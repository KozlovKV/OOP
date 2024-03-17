package kozlov.kirill.sockets.worker;

import java.util.ArrayList;
import java.util.concurrent.ThreadFactory;
import kozlov.kirill.sockets.multicast.MulticastManager;

public final class WorkersPool {
    static final int MAX_PORT = 65535;

    final private MulticastManager multicastManager;
    final private ThreadFactory workersThreadsFactory = Thread.ofVirtual().factory();
    final private ArrayList<Worker> activeWorkers = new ArrayList<>();

    public WorkersPool(String multicastIP, int multicastPort) {
        multicastManager = new MulticastManager(multicastIP, multicastPort);
    }

    public int launchWorker(int startPort) {
        if (startPort > MAX_PORT)
            return -1;
        int currentPort = startPort;
        Worker worker;
        do {
            worker = new Worker(currentPort++, multicastManager);
        } while (!worker.isCreatedSuccessfully() && currentPort <= MAX_PORT);
        if (!worker.isCreatedSuccessfully())
            return -1;
        activeWorkers.add(worker);
        workersThreadsFactory.newThread(worker).start();
        System.out.println("Worker on port " + (currentPort - 1) + " successfully created and launched");
        return currentPort;
    }

    public int launchWorkers(
            int startPort, int workersCount
    ) {
        int currentPort = startPort;
        int workersCreated;
        for (workersCreated = 0; workersCreated < workersCount; ++workersCreated) {
            currentPort = launchWorker(currentPort);
            if (currentPort == -1) {
                break;
            }
        }
        return workersCreated;
    }

    public void shutdown() {
        for (var worker : activeWorkers) {
            worker.setCloseFlag();
        }
    }
}
