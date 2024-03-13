package kozlov.kirill;

import kozlov.kirill.sockets.Client;
import kozlov.kirill.sockets.Gateway;
import kozlov.kirill.sockets.multicast.MulticastManager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Main {
    static final private int THREADS_TEST_CNT = 0;

    @ExcludeFromJacocoGeneratedReport
    public static void main(String[] args) {
        new Thread(new Gateway(
            Gateway.FIRST_SERVER_PORT, 2, 4, 5
        ), "Gateway").start();

        final int BILLION_PRIME = 1000000007;
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            list.add(BILLION_PRIME);
        }
        list.add(6);
        FutureTask<Boolean> task = new FutureTask<>(new Client(list));
        new Thread(task).start();

        try {
            System.out.println(task.get());
        } catch (InterruptedException | ExecutionException e) {}

//        if (THREADS_TEST_CNT == 0)
//            return;
//
//        ArrayList<Callable<Boolean>> tasks = new ArrayList<>();
//        for (int i = 0; i < THREADS_TEST_CNT; ++i) {
//            ArrayList<Integer> list = new ArrayList<>();
//            for (int j = 0; j < 1000000; ++j) {
//                list.add(i);
//            }
//            tasks.add(new Client(list));
//        }
//
//        ExecutorService pool = Executors.newFixedThreadPool(THREADS_TEST_CNT);
//        try {
//            List<Future<Boolean>> results = pool.invokeAll(tasks);
//            for (int i = 0; i < THREADS_TEST_CNT; ++i) {
//                System.out.println(i + ": " + results.get(i).get());
//            }
//        } catch (ExecutionException e) {
//            System.err.println("Failed to execute task");
//        } catch (InterruptedException e) {
//            System.err.println("Result waiting was interrupted");
//        }
//
//        pool.shutdown();
    }
}