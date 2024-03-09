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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {
    static final private int GATEWAYS_CNT = 1;
    static final private int THREADS_TEST_CNT = 1;

    @ExcludeFromJacocoGeneratedReport
    public static void main(String[] args) {
        ArrayList<Thread> gateways = new ArrayList<>();
        for (int i = 0; i < GATEWAYS_CNT; ++i) {
            Thread gatewayThread = new Thread(new Gateway(
                Gateway.FIRST_SERVER_PORT + i, 2, 100
            ), "Gateway " + i);
            gatewayThread.start();
            gateways.add(gatewayThread);
        }

        ArrayList<Callable<Boolean>> tasks = new ArrayList<>();
        for (int i = 0; i < THREADS_TEST_CNT; ++i) {
            ArrayList<Integer> list = new ArrayList<>();
            for (int j = 0; j < 1000000; ++j) {
                list.add(i);
            }
            tasks.add(new Client(list));
        }

        ExecutorService pool = Executors.newFixedThreadPool(THREADS_TEST_CNT);
        try {
            List<Future<Boolean>> results = pool.invokeAll(tasks);
            for (int i = 0; i < THREADS_TEST_CNT; ++i) {
                System.out.println(i + ": " + results.get(i).get());
            }
        } catch (ExecutionException e) {
            System.err.println("Failed to execute task");
        } catch (InterruptedException e) {
            System.err.println("Result waiting was interrupted");
        }

        pool.shutdown();
    }
}