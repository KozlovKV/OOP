package kozlov.kirill;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import kozlov.kirill.sockets.Client;
import kozlov.kirill.sockets.data.NetworkSendable;
import kozlov.kirill.sockets.server.Gateway;
import kozlov.kirill.sockets.worker.WorkersPool;


/**
 * Main entry point.
 */
@ExcludeClassFromJacocoGeneratedReport
public class Main {
    public static final int TEST_SERVER_PORT = 8000;
    private static final int THREADS_TEST_CNT = 0;

    /**
     * Main entry point.
     *
     * @param args cmd's args
     */
    public static void main(String[] args) {
        Gateway gateway = new Gateway(
                TEST_SERVER_PORT, TEST_SERVER_PORT, 3, 1
        );
        new Thread(gateway, "Gateway").start();
        WorkersPool workersPool = new WorkersPool("230.0.0.0", TEST_SERVER_PORT);
        System.out.println(workersPool.launchWorkers(
            TEST_SERVER_PORT + 1, 100
        ));

        final int billionPrime = 1000000007;
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            list.add(billionPrime);
        }
        FutureTask<NetworkSendable> task = new FutureTask<>(new Client(list, gateway.getServerHostName(), gateway.getServerPort()));
        var th = new Thread(task);
        th.start();
        ArrayList<Integer> list2 = new ArrayList<>(list);
        list2.add(6);
        FutureTask<NetworkSendable> task2 = new FutureTask<>(new Client(list2, gateway.getServerHostName(), gateway.getServerPort()));
        new Thread(task2).start();

        try {
            System.out.println(task.get());
            System.out.println(task2.get());
        } catch (InterruptedException | ExecutionException ignored) {}
    }
}