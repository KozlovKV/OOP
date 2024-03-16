package kozlov.kirill;

import kozlov.kirill.sockets.Client;
import kozlov.kirill.sockets.data.ErrorMessage;
import kozlov.kirill.sockets.data.NetworkSendable;
import kozlov.kirill.sockets.data.TaskResult;
import kozlov.kirill.sockets.server.Gateway;
import kozlov.kirill.sockets.worker.Worker;
import kozlov.kirill.sockets.worker.WorkersFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class IntegrationTest {

    @Test
    void manyRequestsForCommonWorkers() {
        final int THREADS_TEST_CNT = 10;
        final int TEST_PORT = 8000;
        WorkersFactory.launchAndGetWorkers(TEST_PORT + 1, 10, "230.0.0.0", TEST_PORT);
        Gateway gateway = new Gateway(TEST_PORT, TEST_PORT, 10, 10);
        var gatewayThread = new Thread(gateway);
        gatewayThread.start();

        ArrayList<Callable<NetworkSendable>> tasks = new ArrayList<>();
        for (int i = 0; i < THREADS_TEST_CNT; ++i) {
            ArrayList<Integer> list = new ArrayList<>();
            for (int j = 0; j < 1000000; ++j) {
                list.add(i);
            }
            tasks.add(new Client(list, TEST_PORT));
        }

        ExecutorService pool = Executors.newFixedThreadPool(THREADS_TEST_CNT);
        try {
            List<Future<NetworkSendable>> results = pool.invokeAll(tasks);
            int expectedUnprimesCnt = 4, gotUnprimesCnt = 0;
            for (int i = 0; i < THREADS_TEST_CNT; ++i) {
                if (((TaskResult)results.get(i).get()).result())
                    gotUnprimesCnt++;
            }
            Assertions.assertEquals(expectedUnprimesCnt, gotUnprimesCnt);
            pool.shutdown();
            Thread.sleep(Worker.WORKER_SOCKET_TIMEOUT*2);
            Assertions.assertFalse(gatewayThread.isAlive());
        } catch (ExecutionException | InterruptedException e) {
            Assertions.fail();
        }
    }

    @Test
    void workersNotFound() {final int THREADS_TEST_CNT = 10;
        final int TEST_PORT = 7000;
        Gateway gateway = new Gateway(TEST_PORT, TEST_PORT, 10, 10);
        var gatewayThread = new Thread(gateway);
        gatewayThread.start();

        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            list.add(UnitTest.BILLION_PRIME);
        }
        FutureTask<NetworkSendable> task = new FutureTask<>(new Client(list, TEST_PORT));
        new Thread(task).start();

        try {
            ErrorMessage expected = new ErrorMessage("Server couldn't find calculation node");
            Assertions.assertEquals(expected, task.get());
        } catch (InterruptedException | ExecutionException e) {}
    }

    // TODO: Падение воркера в процессе

    // TODO: Откллючение клиента во время вычисления
}
