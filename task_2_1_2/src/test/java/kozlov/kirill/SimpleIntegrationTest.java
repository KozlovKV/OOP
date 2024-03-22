package kozlov.kirill;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import kozlov.kirill.sockets.Client;
import kozlov.kirill.sockets.data.NetworkSendable;
import kozlov.kirill.sockets.data.TaskResult;
import kozlov.kirill.sockets.server.Gateway;
import kozlov.kirill.sockets.worker.Worker;
import kozlov.kirill.sockets.worker.WorkersPool;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Simple integration tests.
 */
public class SimpleIntegrationTest {
    public static final int TEST_PORT = 8000;

    private static final int TESTS_CNT = 7;
    private static final int WORKERS_PER_ONE_TASK = 100;
    private static final int WORKERS_CNT = 100;
    private static WorkersPool pool;

    private static Gateway defaultGateway;
    private static Thread gatewayThread;

    @BeforeAll
    static void startServerAndWorkers() {
        try {
            pool = new WorkersPool("230.0.0.0", TEST_PORT);
        } catch (IOException e) {
            System.err.println("Couldn't create workers' pool: " + e.getMessage());
            Assertions.fail();
            return;
        }
        defaultGateway = new Gateway(TEST_PORT, TEST_PORT, TESTS_CNT, WORKERS_PER_ONE_TASK);
        gatewayThread = new Thread(defaultGateway);
        gatewayThread.start();
        int workersLaunched = pool.launchWorkers(TEST_PORT + 1, WORKERS_CNT);
        Assertions.assertTrue(workersLaunched >= WORKERS_PER_ONE_TASK);
    }

    @AfterAll
    static void killWorkers() {
        pool.shutdown();
        clearingPause();
        Assertions.assertFalse(gatewayThread.isAlive());
    }

    static void clearingPause() {
        try {
            Thread.sleep(
                (Worker.WORKER_SOCKET_TIMEOUT + Gateway.GATEWAY_TIMEOUT) * 2
            );
        } catch (InterruptedException e) {
            Assertions.fail("Failed to wait for correct closing");
        }
    }

    @Test
    void emptyListTest() {
        ArrayList<Integer> list = new ArrayList<>();
        FutureTask<NetworkSendable> task = new FutureTask<>(
            new Client(list, defaultGateway.getServerHostName(), TEST_PORT)
        );
        new Thread(task).start();
        try {
            Assertions.assertFalse(((TaskResult) task.get()).result());
        } catch (ExecutionException | InterruptedException error) {
            Assertions.fail();
        }
    }

    @Test
    void onePrimeTest() {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(5);
        FutureTask<NetworkSendable> task = new FutureTask<>(
            new Client(list, defaultGateway.getServerHostName(), TEST_PORT)
        );
        new Thread(task).start();
        try {
            Assertions.assertFalse(((TaskResult) task.get()).result());
        } catch (ExecutionException | InterruptedException error) {
            Assertions.fail();
        }
    }

    @Test
    void oneUnprimeTest() {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(8);
        FutureTask<NetworkSendable> task = new FutureTask<>(
            new Client(list, defaultGateway.getServerHostName(), TEST_PORT)
        );
        new Thread(task).start();
        try {
            Assertions.assertTrue(((TaskResult) task.get()).result());
        } catch (ExecutionException | InterruptedException error) {
            Assertions.fail();
        }
    }

    @Test
    void largePrimesTest() {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < 1000000; i++) {
            list.add(UnitTest.BILLION_PRIME);
        }
        FutureTask<NetworkSendable> task = new FutureTask<>(
            new Client(list, defaultGateway.getServerHostName(), TEST_PORT)
        );
        new Thread(task).start();
        try {
            Assertions.assertFalse(((TaskResult) task.get()).result());
        } catch (ExecutionException | InterruptedException error) {
            Assertions.fail();
        }
    }

    @Test
    void largeUnprimesTest() {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < 1000000; i++) {
            list.add(100000006);
        }
        FutureTask<NetworkSendable> task = new FutureTask<>(
            new Client(list, defaultGateway.getServerHostName(), TEST_PORT)
        );
        new Thread(task).start();
        try {
            Assertions.assertTrue(((TaskResult) task.get()).result());
        } catch (ExecutionException | InterruptedException error) {
            Assertions.fail();
        }
    }

    @Test
    void largeWithOneUnprimeAtStartTest() {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(8);
        for (int i = 0; i < 1000000; i++) {
            list.add(UnitTest.BILLION_PRIME);
        }
        FutureTask<NetworkSendable> task = new FutureTask<>(
            new Client(list, defaultGateway.getServerHostName(), TEST_PORT)
        );
        new Thread(task).start();
        try {
            Assertions.assertTrue(((TaskResult) task.get()).result());
        } catch (ExecutionException | InterruptedException error) {
            Assertions.fail();
        }
    }

    @Test
    void largeWithOneUnprimeAtEndTest() {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < 1000000; i++) {
            list.add(UnitTest.BILLION_PRIME);
        }
        list.add(8);
        FutureTask<NetworkSendable> task = new FutureTask<>(
            new Client(list, defaultGateway.getServerHostName(), TEST_PORT)
        );
        new Thread(task).start();
        try {
            Assertions.assertTrue(((TaskResult) task.get()).result());
        } catch (ExecutionException | InterruptedException error) {
            Assertions.fail();
        }
    }
}
