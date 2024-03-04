package kozlov.kirill;

import kozlov.kirill.sockets.Client;
import kozlov.kirill.sockets.Gateway;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Main {
    @ExcludeFromJacocoGeneratedReport
    public static void main(String[] args) {


        Thread gatewayThread = new Thread(new Gateway());
        gatewayThread.start();

        ArrayList<Callable<Boolean>> tasks = new ArrayList<>();
        for (int i = 0; i < 100; ++i) {
            ArrayList<Integer> list = new ArrayList<>();
            for (int j = 0; j < 1000000; ++j) {
                list.add(i);
            }
            tasks.add(new Client(list));
        }

        ExecutorService pool = Executors.newCachedThreadPool();
        try {
            List<Future<Boolean>> results = pool.invokeAll(tasks);
            for (int i = 0; i < 100; ++i) {
                System.out.println(i + ": " + results.get(i).get());
            }
        } catch (ExecutionException e) {
            System.err.println("Failed to execute task");
        } catch (InterruptedException e) {
            System.err.println("Result waiting was interrupted");
        }

    }
}