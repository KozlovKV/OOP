package kozlov.kirill.sockets.worker;

import java.util.ArrayList;
import java.util.concurrent.ThreadFactory;
import kozlov.kirill.sockets.multicast.MulticastManager;

public final class WorkersPool {
    public static final int MAX_STATIC_PORT = 32767;

    final private MulticastManager multicastManager;
    final private ThreadFactory workersThreadsFactory = Thread.ofVirtual().factory();
    final private ArrayList<Worker> activeWorkers = new ArrayList<>();
    final private ArrayList<Thread> workersThreads = new ArrayList<>();

    public WorkersPool(String multicastIP, int multicastPort) {
        multicastManager = new MulticastManager(multicastIP, multicastPort);
    }

    public int launchWorker(int startPort) {
        if (startPort > MAX_STATIC_PORT)
            return -1;
        int currentPort = startPort;
        Worker worker;
        do {
            worker = new Worker(currentPort++, multicastManager);
        } while (!worker.isCreatedSuccessfully() && currentPort <= MAX_STATIC_PORT);
        if (!worker.isCreatedSuccessfully())
            return -1;
        activeWorkers.add(worker);
        Thread workerThread = workersThreadsFactory.newThread(worker);
        workersThreads.add(workerThread);
        workerThread.start();
        System.out.println("Worker on port " + (currentPort - 1) + " successfully created and launched");
        return currentPort;
    }

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

    public void shutdown() {
        for (var worker : activeWorkers) {
            worker.setCloseFlag();
        }
    }

    public void shutdownNow() {
        for (var worker : workersThreads) {
            worker.interrupt();
        }
    }
}
