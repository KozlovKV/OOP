package kozlov.kirill;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import kozlov.kirill.sockets.Client;
import kozlov.kirill.sockets.data.NetworkSendable;
import kozlov.kirill.sockets.server.Gateway;
import kozlov.kirill.sockets.worker.WorkersPool;


@ExcludeClassFromJacocoGeneratedReport
public class Main {
    public static final int TEST_SERVER_PORT = 8000;
    static final private int THREADS_TEST_CNT = 0;

    public static void main(String[] args) {
        new Thread(new Gateway(
                TEST_SERVER_PORT, TEST_SERVER_PORT, 3, 1
        ), "Gateway").start();
        WorkersPool workersPool = new WorkersPool("230.0.0.0", TEST_SERVER_PORT);
        System.out.println(workersPool.launchWorkers(
            TEST_SERVER_PORT + 1, 100
        ));

        final int BILLION_PRIME = 1000000007;
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            list.add(BILLION_PRIME);
        }
        FutureTask<NetworkSendable> task = new FutureTask<>(new Client(list, TEST_SERVER_PORT));
        var th = new Thread(task);
        th.start();
        ArrayList<Integer> list2 = new ArrayList<>(list);
        list2.add(6);
        FutureTask<NetworkSendable> task2 = new FutureTask<>(new Client(list2, TEST_SERVER_PORT));
        new Thread(task2).start();

        try {
            System.out.println(task.get());
            System.out.println(task2.get());
        } catch (InterruptedException | ExecutionException e) {}
    }
}