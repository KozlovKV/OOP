package kozlov.kirill.sockets.worker;

import kozlov.kirill.sockets.multicast.MulticastManager;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class WorkersFactory {
    static final int MAX_PORT = 65535;
    public static ArrayList<Worker> launchAndGetWorkers(
            int startPort, int workersCount, String multicastIP, int multicastPort
    ) {
        MulticastManager multicastManager = new MulticastManager(multicastIP, multicastPort);
        ArrayList<Worker> newWorkers = new ArrayList<>();
        ExecutorService workersThreadPool = Executors.newFixedThreadPool(workersCount);
        int currentPort = startPort;
        while (currentPort <+ MAX_PORT && newWorkers.size() < workersCount) {
            Worker worker = new Worker(currentPort, multicastManager);
            currentPort++;
            if (!worker.isCreatedSuccessfully())
                continue;
            newWorkers.add(worker);
            workersThreadPool.submit(worker);
            System.out.println("Worker on port " + (currentPort - 1) + " successfully created and launched");
        }
        workersThreadPool.shutdown();
        return newWorkers;
    }
}
