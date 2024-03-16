package kozlov.kirill;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import kozlov.kirill.sockets.Client;
import kozlov.kirill.sockets.data.NetworkSendable;
import kozlov.kirill.sockets.data.TaskResult;
import kozlov.kirill.sockets.server.Gateway;
import kozlov.kirill.sockets.worker.WorkersFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests' class for unprime checkers.
 */
public class UnitTest {
    public static final int BILLION_PRIME = 1000000007;
    public static final int TEST_PORT = 8000;

    static ArrayList<Integer> getArrayListFromArray(int [] arr, int n) {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            list.add(arr[i]);
        }
        return list;
    }

    static {
        Gateway defaultGateway = new Gateway(TEST_PORT, TEST_PORT, -1, 10);
        new Thread(defaultGateway).start();
        WorkersFactory.launchAndGetWorkers(TEST_PORT + 1, 100, "230.0.0.0", TEST_PORT);
    }

    @Test
    void exampleTest1() {

        ArrayList<Integer> list = getArrayListFromArray(
                new int[]{6, 8, 7, 13, 5, 9, 4}, 7
        );
        FutureTask<NetworkSendable> task = new FutureTask<>(new Client(list, TEST_PORT));
        new Thread(task).start();
        try {
            Assertions.assertTrue(((TaskResult)task.get()).result());
        } catch (ExecutionException | InterruptedException error) {
            Assertions.fail();
        }
    }

    @Test
    void exampleTest2() {

        ArrayList<Integer> list = getArrayListFromArray(
                new int[]{20319251, 6997901, 6997927, 6997937, 17858849, 6997967,
                        6998009, 6998029, 6998039, 20165149, 6998051, 6998053}, 12
        );
        FutureTask<NetworkSendable> task = new FutureTask<>(new Client(list, TEST_PORT));
        new Thread(task).start();
        try {
            Assertions.assertFalse(((TaskResult)task.get()).result());
        } catch (ExecutionException | InterruptedException error) {
            Assertions.fail();
        }
    }

    @Test
    void simplePrimesTest() {

        ArrayList<Integer> list = getArrayListFromArray(
                new int[]{2, 3, 5, 7}, 4
        );
        FutureTask<NetworkSendable> task = new FutureTask<>(new Client(list, TEST_PORT));
        new Thread(task).start();
        try {
            Assertions.assertFalse(((TaskResult)task.get()).result());
        } catch (ExecutionException | InterruptedException error) {
            Assertions.fail();
        }
    }

    @Test
    void simpleUnprimeTest() {

        ArrayList<Integer> list = getArrayListFromArray(
                new int[]{2, 3, 4, 5, 7}, 5
        );
        FutureTask<NetworkSendable> task = new FutureTask<>(new Client(list, TEST_PORT));
        new Thread(task).start();
        try {
            Assertions.assertTrue(((TaskResult)task.get()).result());
        } catch (ExecutionException | InterruptedException error) {
            Assertions.fail();
        }
    }

    @Test
    void emptyListTest() {

        ArrayList<Integer> list = new ArrayList<>();
        FutureTask<NetworkSendable> task = new FutureTask<>(new Client(list, TEST_PORT));
        new Thread(task).start();
        try {
            Assertions.assertFalse(((TaskResult)task.get()).result());
        } catch (ExecutionException | InterruptedException error) {
            Assertions.fail();
        }
    }

    @Test
    void onePrimeTest() {

        ArrayList<Integer> list = new ArrayList<>();
        list.add(5);
        FutureTask<NetworkSendable> task = new FutureTask<>(new Client(list, TEST_PORT));
        new Thread(task).start();
        try {
            Assertions.assertFalse(((TaskResult)task.get()).result());
        } catch (ExecutionException | InterruptedException error) {
            Assertions.fail();
        }
    }

    @Test
    void oneUnprimeTest() {

        ArrayList<Integer> list = new ArrayList<>();
        list.add(8);
        FutureTask<NetworkSendable> task = new FutureTask<>(new Client(list, TEST_PORT));
        new Thread(task).start();
        try {
            Assertions.assertTrue(((TaskResult)task.get()).result());
        } catch (ExecutionException | InterruptedException error) {
            Assertions.fail();
        }
    }

    @Test
    void largePrimesTest() {

        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < 1000000; i++) {
            list.add(BILLION_PRIME);
        }
        FutureTask<NetworkSendable> task = new FutureTask<>(new Client(list, TEST_PORT));
        new Thread(task).start();
        try {
            Assertions.assertFalse(((TaskResult)task.get()).result());
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
        FutureTask<NetworkSendable> task = new FutureTask<>(new Client(list, TEST_PORT));
        new Thread(task).start();
        try {
            Assertions.assertTrue(((TaskResult)task.get()).result());
        } catch (ExecutionException | InterruptedException error) {
            Assertions.fail();
        }
    }

    @Test
    void largeWithOneUnprimeAtStartTest() {

        ArrayList<Integer> list = new ArrayList<>();
        list.add(8);
        for (int i = 0; i < 1000000; i++) {
            list.add(BILLION_PRIME);
        }
        FutureTask<NetworkSendable> task = new FutureTask<>(new Client(list, TEST_PORT));
        new Thread(task).start();
        try {
            Assertions.assertTrue(((TaskResult)task.get()).result());
        } catch (ExecutionException | InterruptedException error) {
            Assertions.fail();
        }
    }

    @Test
    void largeWithOneUnprimeAtEndTest() {

        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < 1000000; i++) {
            list.add(BILLION_PRIME);
        }
        list.add(8);
        FutureTask<NetworkSendable> task = new FutureTask<>(new Client(list, TEST_PORT));
        new Thread(task).start();
        try {
            Assertions.assertTrue(((TaskResult)task.get()).result());
        } catch (ExecutionException | InterruptedException error) {
            Assertions.fail();
        }
    }

    // TODO: перенести тесты выше в интеграционные

    // TODO: проверка парсера

    // TODO: проверка splitTaskData

    // TODO: проверка считателя
}

