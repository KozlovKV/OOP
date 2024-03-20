package kozlov.kirill.sockets.worker;

import java.util.ArrayList;
import java.util.concurrent.ThreadFactory;
import kozlov.kirill.sockets.exceptions.WorkerCreationException;
import kozlov.kirill.sockets.multicast.MulticastManager;

/**
 * Workers' poll class.
 */
public final class WorkersPool {
    public static final int MAX_STATIC_PORT = 32767;

    private final MulticastManager multicastManager;
    private final ThreadFactory workersThreadsFactory = Thread.ofVirtual().factory();
    private final ArrayList<Worker> activeWorkers = new ArrayList<>();
    private final ArrayList<Thread> workersThreads = new ArrayList<>();

    /**
     * WorkersPool constructor.
     * <br>
     * Creates MulticastManager for creation workers available in local network
     *
     * @param multicastIP address for multicast group
     * @param multicastPort port for multicast group
     */
    public WorkersPool(String multicastIP, int multicastPort) {
        multicastManager = new MulticastManager(multicastIP, multicastPort);
    }

    /**
     * Worker's creator.
     * <br>
     * Trie to create and create worker on port up to MAX_STATIC_PORT,
     * launches successfully created workers in virtual threads and adds
     * them to lists of workers and threads
     *
     * @param startPort the first port for creation tries
     *
     * @return next port after acquired by this worker or -1 if worker wasn't created
     */
    public int launchWorker(int startPort) {
        if (startPort > MAX_STATIC_PORT)
            return -1;
        int currentPort = startPort;
        Worker worker = null;
        do {
            try {
                worker = new Worker(currentPort, multicastManager);
            } catch (WorkerCreationException e) {
                System.err.println(
                    "Failed to create worker on port " + currentPort + e.getMessage()
                );
                currentPort++;
            }
        } while (worker == null && currentPort <= MAX_STATIC_PORT);
        if (worker == null)
            return -1;
        activeWorkers.add(worker);
        Thread workerThread = workersThreadsFactory.newThread(worker);
        workersThreads.add(workerThread);
        workerThread.start();
        System.out.println("Worker on port " + currentPort + " successfully created and launched");
        return currentPort + 1;
    }

    /**
     * Workers' launcher.
     *
     * @param startPort the first desired port for workers
     * @param workersCount desired count of workers
     *
     * @return count of successfully created and launched workers
     */
    public int launchWorkers(
            int startPort, int workersCount
    ) {
        int currentPort = startPort;
        int workersLaunched;
        for (workersLaunched = 0; workersLaunched < workersCount; ++workersLaunched) {
            currentPort = launchWorker(currentPort);
            if (currentPort == -1) {
                break;
            }
        }
        return workersLaunched;
    }

    /**
     * Soft workers pool shutdown.
     * <br>
     * Shutdown is performed by setting on close flag in workers' instances
     */
    public void shutdown() {
        for (var worker : activeWorkers) {
            worker.setCloseFlag();
        }
        activeWorkers.clear();
        workersThreads.clear();
    }

    /**
     * Hard workers pool shutdown.
     * <br>
     * Shutdown is performed by sending interruption to workers' threads
     */
    public void shutdownNow() {
        for (var worker : workersThreads) {
            worker.interrupt();
        }
        activeWorkers.clear();
        workersThreads.clear();
    }
}
