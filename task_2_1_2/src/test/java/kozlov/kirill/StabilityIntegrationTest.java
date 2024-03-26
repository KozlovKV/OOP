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

/**
 * Integration test for specific situations and heavy load.
 */
public class StabilityIntegrationTest {
    @Test
    void manyRequestsForCommonWorkers() {
        final int testPort = 8000;
        final int threadsTestCnt = 10;
        WorkersPool workersPool;
        try {
            workersPool = new WorkersPool("230.0.0.0", testPort);
        } catch (IOException e) {
            System.err.println("Couldn't create workers' pool: " + e.getMessage());
            Assertions.fail();
            return;
        }
        workersPool.launchWorkers(testPort + 1, 10);
        Gateway gateway = new Gateway(testPort, testPort, threadsTestCnt, 10);
        var gatewayThread = new Thread(gateway);
        gatewayThread.start();

        ArrayList<Callable<NetworkSendable>> tasks = new ArrayList<>();
        for (int i = 0; i < threadsTestCnt; ++i) {
            ArrayList<Integer> list = new ArrayList<>();
            for (int j = 0; j < 1000000; ++j) {
                list.add(i);
            }
            tasks.add(new Client(list, gateway.getServerHostName(), testPort));
        }

        ExecutorService pool = Executors.newFixedThreadPool(threadsTestCnt);
        try {
            List<Future<NetworkSendable>> results = pool.invokeAll(tasks);
            int expectedUnprimesCnt = 4;
            int gotUnprimesCnt = 0;
            for (int i = 0; i < threadsTestCnt; ++i) {
                if (((TaskResult) results.get(i).get()).result()) {
                    gotUnprimesCnt++;
                }
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
        final int testPort = 8000;
        Gateway gateway = new Gateway(testPort, testPort, 2, 10);
        var gatewayThread = new Thread(gateway);
        gatewayThread.start();
        WorkersPool workersPool;
        try {
            workersPool = new WorkersPool("230.0.0.0", testPort);
        } catch (IOException e) {
            System.err.println("Couldn't create workers' pool: " + e.getMessage());
            Assertions.fail();
            return;
        }
        workersPool.launchWorkers(testPort + 1, 10);


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
                list, gateway.getServerHostName(), testPort
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
        final int testPort = 8000;
        Gateway gateway = new Gateway(testPort, testPort, 1, 10);
        var gatewayThread = new Thread(gateway);
        gatewayThread.start();

        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            list.add(UnitTest.BILLION_PRIME);
        }
        FutureTask<NetworkSendable> task = new FutureTask<>(new Client(
            list, gateway.getServerHostName(), testPort
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
        final int testPort = 8000;
        boolean internalWorkerErrorHandled = false;
        for (int i = 0; i < 100; ++i) {
            final int msForSleeping = i * 250;
            final int newStartPort = testPort + i;
            Gateway gateway = new Gateway(newStartPort, newStartPort, 1, 1);
            var gatewayThread = new Thread(gateway);
            gatewayThread.start();
            WorkersPool workersPool;
            try {
                workersPool = new WorkersPool("230.0.0.0", newStartPort);
            } catch (IOException e) {
                System.err.println("Couldn't create workers' pool: " + e.getMessage());
                Assertions.fail();
                return;
            }
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

    @Test
    void testClientInterruption() {
        final int testPort = 8000;
        WorkersPool workersPool;
        try {
            workersPool = new WorkersPool("230.0.0.0", testPort);
        } catch (IOException e) {
            System.err.println("Couldn't create workers' pool: " + e.getMessage());
            Assertions.fail();
            return;
        }
        workersPool.launchWorkers(testPort + 1, 10);
        Gateway gateway = new Gateway(testPort, testPort, 2, 10);
        var gatewayThread = new Thread(gateway);
        gatewayThread.start();

        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            list.add(UnitTest.BILLION_PRIME);
        }
        FutureTask<NetworkSendable> task = new FutureTask<>(new Client(
            list, gateway.getServerHostName(), testPort
        ));
        var interruptedThread = new Thread(task);
        interruptedThread.interrupt();
        interruptedThread.start();
        ArrayList<Integer> listWithUnprime = new ArrayList<>(list);
        listWithUnprime.add(6);
        FutureTask<NetworkSendable> task2 = new FutureTask<>(new Client(
            listWithUnprime, gateway.getServerHostName(), testPort
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
        final int testPort = 8000;
        final int threadsTestCnt = 5;
        WorkersPool workersPool;
        try {
            workersPool = new WorkersPool("230.0.0.0", testPort);
        } catch (IOException e) {
            System.err.println("Couldn't create workers' pool: " + e.getMessage());
            Assertions.fail();
            return;
        }
        Assertions.assertEquals(
                10, workersPool.launchWorkers(
                    WorkersPool.MAX_STATIC_PORT - 9, 100
                )
        );
        Gateway gateway = new Gateway(testPort, testPort, threadsTestCnt, 10);
        var gatewayThread = new Thread(gateway);
        gatewayThread.start();

        ArrayList<Callable<NetworkSendable>> tasks = new ArrayList<>();
        for (int i = 0; i < threadsTestCnt; ++i) {
            ArrayList<Integer> list = new ArrayList<>();
            for (int j = 0; j < 1000000; ++j) {
                list.add(i);
            }
            tasks.add(new Client(list, gateway.getServerHostName(), testPort));
        }

        ExecutorService pool = Executors.newFixedThreadPool(threadsTestCnt);
        try {
            List<Future<NetworkSendable>> results = pool.invokeAll(tasks);
            int expectedUnprimesCnt = 1;
            int gotUnprimesCnt = 0;
            for (int i = 0; i < threadsTestCnt; ++i) {
                if (((TaskResult) results.get(i).get()).result()) {
                    gotUnprimesCnt++;
                }
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
        final int testPort = 8000;
        final int threadsTestCnt = 5;
        WorkersPool workersPool;
        try {
            workersPool = new WorkersPool("230.0.0.0", testPort);
        } catch (IOException e) {
            System.err.println("Couldn't create workers' pool: " + e.getMessage());
            Assertions.fail();
            return;
        }
        Assertions.assertEquals(
                100, workersPool.launchWorkers(testPort + 1, 100)
        );
        Assertions.assertEquals(
                100, workersPool.launchWorkers(testPort + 1, 100)
        );
        Gateway gateway = new Gateway(testPort, testPort, threadsTestCnt, 40);
        var gatewayThread = new Thread(gateway);
        gatewayThread.start();

        ArrayList<Callable<NetworkSendable>> tasks = new ArrayList<>();
        for (int i = 0; i < threadsTestCnt; ++i) {
            ArrayList<Integer> list = new ArrayList<>();
            for (int j = 0; j < 1000000; ++j) {
                list.add(i);
            }
            tasks.add(new Client(list, gateway.getServerHostName(), testPort));
        }

        ExecutorService pool = Executors.newFixedThreadPool(threadsTestCnt);
        try {
            List<Future<NetworkSendable>> results = pool.invokeAll(tasks);
            int expectedUnprimesCnt = 1;
            int gotUnprimesCnt = 0;
            for (int i = 0; i < threadsTestCnt; ++i) {
                if (((TaskResult) results.get(i).get()).result()) {
                    gotUnprimesCnt++;
                }
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
