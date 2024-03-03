package kozlov.kirill;

import kozlov.kirill.sockets.Client;
import kozlov.kirill.sockets.Gateway;

import java.util.ArrayList;
import java.util.concurrent.*;

public class Main {
    @ExcludeFromJacocoGeneratedReport
    public static void main(String[] args) {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < 1000000; ++i) {
            list.add(3);
        }

        Thread gatewayThread = new Thread(new Gateway());
        gatewayThread.start();

        var clientTask = new FutureTask<>(new Client(list));
        new Thread(clientTask).start();

        try {
            System.out.println(clientTask.get());
        } catch (ExecutionException e) {
            System.err.println("Failed to execute task");
        } catch (InterruptedException e) {
            System.err.println("Result waiting was interrupted");
        }

    }
}