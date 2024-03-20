package kozlov.kirill;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import kozlov.kirill.sockets.Client;
import kozlov.kirill.sockets.data.ErrorMessage;
import kozlov.kirill.sockets.data.NetworkSendable;
import kozlov.kirill.sockets.data.TaskResult;
import kozlov.kirill.sockets.data.utils.ErrorMessages;
import kozlov.kirill.sockets.server.Gateway;
import kozlov.kirill.sockets.worker.WorkersPool;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class StabilityIntegrationTest {

    @Test
    void manyRequestsForCommonWorkers() {
        final int TEST_PORT = 8000;
        final int THREADS_TEST_CNT = 10;
        WorkersPool workersPool = new WorkersPool("230.0.0.0", TEST_PORT);
        workersPool.launchWorkers(TEST_PORT + 1, 10);
        Gateway gateway = new Gateway(TEST_PORT, TEST_PORT, THREADS_TEST_CNT, 10);
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
            workersPool.shutdown();
            SimpleIntegrationTest.clearingPause();
            Assertions.assertFalse(gatewayThread.isAlive());
        } catch (ExecutionException | InterruptedException e) {
            Assertions.fail();
        }
    }

    @Test
    void workersNotFound() {
        final int TEST_PORT = 8000;
        Gateway gateway = new Gateway(TEST_PORT, TEST_PORT, 1, 10);
        var gatewayThread = new Thread(gateway);
        gatewayThread.start();

        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            list.add(UnitTest.BILLION_PRIME);
        }
        FutureTask<NetworkSendable> task = new FutureTask<>(new Client(list, TEST_PORT));
        new Thread(task).start();

        try {
            ErrorMessage expected = ErrorMessages.workerNotFoundMessage;
            Assertions.assertEquals(expected, task.get());
            SimpleIntegrationTest.clearingPause();
            Assertions.assertFalse(gatewayThread.isAlive());
        } catch (InterruptedException | ExecutionException e) {
            Assertions.fail();
        }
    }

//    @Test
//    void testWorkerFatalInterruption() {
//        final int TEST_PORT = 8000;
//        WorkersPool workersPool = new WorkersPool("230.0.0.0", TEST_PORT);
//        workersPool.launchWorkers(TEST_PORT + 1, 10);
//        Gateway gateway = new Gateway(TEST_PORT, TEST_PORT, 1, 10);
//        var gatewayThread = new Thread(gateway);
//        gatewayThread.start();
//
//        ArrayList<Integer> list = new ArrayList<>();
//        for (int i = 0; i < 1000000; i++) {
//            list.add(UnitTest.BILLION_PRIME);
//        }
//        FutureTask<NetworkSendable> task = new FutureTask<>(new Client(list, TEST_PORT));
//        new Thread(task).start();
//
//        try {
//            Thread.sleep(2000);
//            workersPool.shutdownNow();
//            ErrorMessage expected = ErrorMessages.workerInternalErrorMessage;
//            Assertions.assertEquals(expected, task.get());
//
//            SimpleIntegrationTest.clearingPause();
//            Assertions.assertFalse(gatewayThread.isAlive());
//        } catch (InterruptedException | ExecutionException e) {
//            Assertions.fail();
//        }
//    }
//
//    @Test
//    void testWorkerFatalInterruptionAndNewFound() {
//        final int TEST_PORT = 8000;
//        WorkersPool workersPool = new WorkersPool("230.0.0.0", TEST_PORT);
//        workersPool.launchWorkers(TEST_PORT + 1, 10);
//        Gateway gateway = new Gateway(TEST_PORT, TEST_PORT, 1, 10);
//        var gatewayThread = new Thread(gateway);
//        gatewayThread.start();
//
//        ArrayList<Integer> list = new ArrayList<>();
//        for (int i = 0; i < 1000000; i++) {
//            list.add(UnitTest.BILLION_PRIME);
//        }
//        FutureTask<NetworkSendable> task = new FutureTask<>(new Client(list, TEST_PORT));
//        new Thread(task).start();
//
//        try {
//            Thread.sleep(1000);
//            workersPool.shutdownNow();
//
//            Assertions.assertEquals(
//                    10, workersPool.launchWorkers(TEST_PORT + 1, 10)
//            );
//            TaskResult expected = new TaskResult(false);
//            Assertions.assertEquals(expected, task.get());
//
//            workersPool.shutdown();
//            SimpleIntegrationTest.clearingPause();
//            Assertions.assertFalse(gatewayThread.isAlive());
//        } catch (InterruptedException | ExecutionException e) {
//            Assertions.fail();
//        }
//    }

    @Test
    void testClientInterruption() {
        final int TEST_PORT = 8000;
        WorkersPool workersPool = new WorkersPool("230.0.0.0", TEST_PORT);
        workersPool.launchWorkers(TEST_PORT + 1, 10);
        Gateway gateway = new Gateway(TEST_PORT, TEST_PORT, 2, 10);
        var gatewayThread = new Thread(gateway);
        gatewayThread.start();

        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            list.add(UnitTest.BILLION_PRIME);
        }
        FutureTask<NetworkSendable> task = new FutureTask<>(new Client(list, TEST_PORT));
        var interruptedThread = new Thread(task);
        interruptedThread.interrupt();
        interruptedThread.start();
        ArrayList<Integer> listWithUnprime = new ArrayList<>(list);
        listWithUnprime.add(6);
        FutureTask<NetworkSendable> task2 = new FutureTask<>(new Client(listWithUnprime, TEST_PORT));
        new Thread(task2).start();

        try {
            TaskResult expected = new TaskResult(true);
            Assertions.assertEquals(expected, task2.get());

            workersPool.shutdown();
            SimpleIntegrationTest.clearingPause();
            Assertions.assertFalse(gatewayThread.isAlive());
        } catch (InterruptedException | ExecutionException e) {
            Assertions.fail();
        }
    }

    @Test
    void testWorkersPoolNearToPortsUpperBound() {
        final int TEST_PORT = 8000;
        final int THREADS_TEST_CNT = 5;
        WorkersPool workersPool = new WorkersPool("230.0.0.0", TEST_PORT);
        Assertions.assertEquals(
                10, workersPool.launchWorkers(
                    WorkersPool.MAX_STATIC_PORT - 9, 100
                )
        );
        Gateway gateway = new Gateway(TEST_PORT, TEST_PORT, THREADS_TEST_CNT, 10);
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
            int expectedUnprimesCnt = 1, gotUnprimesCnt = 0;
            for (int i = 0; i < THREADS_TEST_CNT; ++i) {
                if (((TaskResult)results.get(i).get()).result())
                    gotUnprimesCnt++;
            }
            Assertions.assertEquals(expectedUnprimesCnt, gotUnprimesCnt);
            pool.shutdown();
            workersPool.shutdown();
            SimpleIntegrationTest.clearingPause();
            Assertions.assertFalse(gatewayThread.isAlive());
        } catch (ExecutionException | InterruptedException e) {
            Assertions.fail();
        }
    }

    @Test
    void test2WorkersPoolsOnSamePorts() {
        final int TEST_PORT = 8000;
        final int THREADS_TEST_CNT = 5;
        WorkersPool workersPool = new WorkersPool("230.0.0.0", TEST_PORT);
        Assertions.assertEquals(
                100, workersPool.launchWorkers(TEST_PORT + 1, 100)
        );
        Assertions.assertEquals(
                100, workersPool.launchWorkers(TEST_PORT + 1, 100)
        );
        Gateway gateway = new Gateway(TEST_PORT, TEST_PORT, THREADS_TEST_CNT, 40);
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
            int expectedUnprimesCnt = 1, gotUnprimesCnt = 0;
            for (int i = 0; i < THREADS_TEST_CNT; ++i) {
                if (((TaskResult)results.get(i).get()).result())
                    gotUnprimesCnt++;
            }
            Assertions.assertEquals(expectedUnprimesCnt, gotUnprimesCnt);
            pool.shutdown();
            workersPool.shutdown();
            SimpleIntegrationTest.clearingPause();
            Assertions.assertFalse(gatewayThread.isAlive());
        } catch (ExecutionException | InterruptedException e) {
            Assertions.fail();
        }
    }
}
