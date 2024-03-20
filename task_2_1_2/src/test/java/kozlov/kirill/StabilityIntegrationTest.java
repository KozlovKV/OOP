package kozlov.kirill;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import kozlov.kirill.sockets.BasicTcpSocketOperations;
import kozlov.kirill.sockets.Client;
import kozlov.kirill.sockets.data.ErrorMessage;
import kozlov.kirill.sockets.data.NetworkSendable;
import kozlov.kirill.sockets.data.TaskResult;
import kozlov.kirill.sockets.data.utils.ErrorMessages;
import kozlov.kirill.sockets.exceptions.EndOfStreamException;
import kozlov.kirill.sockets.exceptions.ParsingException;
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
            tasks.add(new Client(list, gateway.getServerHostName(), TEST_PORT));
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
    void testIncorrectData() {
        final int TEST_PORT = 8000;
        Gateway gateway = new Gateway(TEST_PORT, TEST_PORT, 2, 10);
        var gatewayThread = new Thread(gateway);
        gatewayThread.start();
        WorkersPool workersPool = new WorkersPool("230.0.0.0", TEST_PORT);
        workersPool.launchWorkers(TEST_PORT + 1, 10);


        try {
            Socket rawConnection = new Socket(
                gateway.getServerHostName(), gateway.getServerPort()
            );
            rawConnection.getOutputStream().write(0);
            rawConnection.getOutputStream().flush();
            ErrorMessage expected = ErrorMessages.taskDataParsingError;
            Assertions.assertEquals(
                expected,
                BasicTcpSocketOperations.receiveJsonObject(rawConnection, ErrorMessage.class)
            );
            rawConnection.close();
        } catch (IOException | ParsingException | EndOfStreamException e) {
            Assertions.fail();
        }

        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            list.add(UnitTest.BILLION_PRIME);
        }
        FutureTask<NetworkSendable> task = new FutureTask<>(new Client(
                list, gateway.getServerHostName(), TEST_PORT
        ));
        new Thread(task).start();

        try {
            TaskResult expected = new TaskResult(false);
            Assertions.assertEquals(expected, task.get());

            workersPool.shutdown();
            SimpleIntegrationTest.clearingPause();
            Assertions.assertFalse(gatewayThread.isAlive());
        } catch (InterruptedException | ExecutionException e) {
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
        FutureTask<NetworkSendable> task = new FutureTask<>(new Client(
            list, gateway.getServerHostName(), TEST_PORT
        ));
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

    @Test
    void testWorkerFatalInterruption() {
        final int TEST_PORT = 8000;
        boolean internalWorkerErrorHandled = false;
        for (int i = 0; i < 100; ++i) {
            final int msForSleeping = i * 250;
            final int newStartPort = TEST_PORT + i;
            Gateway gateway = new Gateway(newStartPort, newStartPort, 1, 1);
            var gatewayThread = new Thread(gateway);
            gatewayThread.start();
            WorkersPool workersPool = new WorkersPool("230.0.0.0", newStartPort);
            workersPool.launchWorkers(newStartPort + 1, 1);

            ArrayList<Integer> list = new ArrayList<>();
            for (int j = 0; j < 1000000; j++) {
                list.add(UnitTest.BILLION_PRIME);
            }
            FutureTask<NetworkSendable> task = new FutureTask<>(new Client(
                list, gateway.getServerHostName(), newStartPort
            ));
            new Thread(task).start();

            NetworkSendable possibleTaskResult = null;
            try {
                Thread.sleep(msForSleeping);
                workersPool.shutdownNow();

                possibleTaskResult = task.get();
                if (possibleTaskResult.equals(ErrorMessages.workerInternalErrorMessage)) {
                    internalWorkerErrorHandled = true;
                    break;
                }

                workersPool.shutdown();
                SimpleIntegrationTest.clearingPause();
                Assertions.assertFalse(gatewayThread.isAlive());
            } catch (InterruptedException | ExecutionException e) {
                Assertions.fail();
            }
        }
        Assertions.assertTrue(internalWorkerErrorHandled);
    }

//    @Test
//    void testWorkerFatalInterruptionAndNewFound() {
//        final int TEST_PORT = 8000;
//        final int attemptsCnt = 100;
//        Gateway gateway = new Gateway(TEST_PORT, TEST_PORT, attemptsCnt, 10);
//        var gatewayThread = new Thread(gateway);
//        gatewayThread.start();
//        WorkersPool workersPool = new WorkersPool("230.0.0.0", TEST_PORT);
//        boolean realCheckNext = false;
//        for (int i = 0; i < attemptsCnt; ++i) {
//            final int msForSleeping = i * 250;
//            workersPool.launchWorkers(TEST_PORT + 1, 10);
//
//            ArrayList<Integer> list = new ArrayList<>();
//            for (int j = 0; j < 1000; j++) {
//                list.add(UnitTest.BILLION_PRIME);
//            }
//            FutureTask<NetworkSendable> task = new FutureTask<>(new Client(
//                list, gateway.getServerHostName(), TEST_PORT
//            ));
//            new Thread(task).start();
//
//            try {
//                Thread.sleep(msForSleeping);
//                workersPool.shutdownNow();
//
//                Assertions.assertEquals(
//                        10, workersPool.launchWorkers(TEST_PORT + 1, 10)
//                );
//                NetworkSendable possibleTaskResult = task.get();
//                if (possibleTaskResult.equals(ErrorMessages.workerInternalErrorMessage)) {
//                    realCheckNext = true;
//                    workersPool.shutdown();
//                    SimpleIntegrationTest.clearingPause();
//                    continue;
//                }
//                if (realCheckNext) {
//                    TaskResult expected = new TaskResult(false);
//                    Assertions.assertEquals(expected, possibleTaskResult);
//                    break;
//                }
//
//                workersPool.shutdown();
//                SimpleIntegrationTest.clearingPause();
//            } catch (InterruptedException | ExecutionException e) {
//                Assertions.fail();
//            }
//        }
//        workersPool.shutdown();
//        SimpleIntegrationTest.clearingPause();
//        Assertions.assertFalse(gatewayThread.isAlive());
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
        FutureTask<NetworkSendable> task = new FutureTask<>(new Client(
            list, gateway.getServerHostName(), TEST_PORT
        ));
        var interruptedThread = new Thread(task);
        interruptedThread.interrupt();
        interruptedThread.start();
        ArrayList<Integer> listWithUnprime = new ArrayList<>(list);
        listWithUnprime.add(6);
        FutureTask<NetworkSendable> task2 = new FutureTask<>(new Client(
            listWithUnprime, gateway.getServerHostName(), TEST_PORT
        ));
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
            tasks.add(new Client(list, gateway.getServerHostName(), TEST_PORT));
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
            tasks.add(new Client(list, gateway.getServerHostName(), TEST_PORT));
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
