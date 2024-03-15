package kozlov.kirill;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import kozlov.kirill.sockets.Client;
import kozlov.kirill.sockets.Gateway;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests' class for unprime checkers.
 */
public class UnitTest {
    public static final int BILLION_PRIME = 1000000007;

    static ArrayList<Integer> getArrayListFromArray(int [] arr, int n) {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            list.add(arr[i]);
        }
        return list;
    }

    static {
        Gateway defaultGateway = new Gateway(8000, -1, 100, 10);
        new Thread(defaultGateway).start();
    }

    @Test
    void exampleTest1() {
        
        ArrayList<Integer> list = getArrayListFromArray(
                new int[]{6, 8, 7, 13, 5, 9, 4}, 7
        );
        FutureTask<Boolean> task = new FutureTask<>(new Client(list));
        new Thread(task).start();
        try {
            Assertions.assertTrue(task.get());
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
        FutureTask<Boolean> task = new FutureTask<>(new Client(list));
        new Thread(task).start();
        try {
            Assertions.assertFalse(task.get());
        } catch (ExecutionException | InterruptedException error) {
            Assertions.fail();
        }
    }

    @Test
    void simplePrimesTest() {
        
        ArrayList<Integer> list = getArrayListFromArray(
                new int[]{2, 3, 5, 7}, 4
        );
        FutureTask<Boolean> task = new FutureTask<>(new Client(list));
        new Thread(task).start();
        try {
            Assertions.assertFalse(task.get());
        } catch (ExecutionException | InterruptedException error) {
            Assertions.fail();
        }
    }

    @Test
    void simpleUnprimeTest() {
        
        ArrayList<Integer> list = getArrayListFromArray(
                new int[]{2, 3, 4, 5, 7}, 5
        );
        FutureTask<Boolean> task = new FutureTask<>(new Client(list));
        new Thread(task).start();
        try {
            Assertions.assertTrue(task.get());
        } catch (ExecutionException | InterruptedException error) {
            Assertions.fail();
        }
    }

    @Test
    void emptyListTest() {
        
        ArrayList<Integer> list = new ArrayList<>();
        FutureTask<Boolean> task = new FutureTask<>(new Client(list));
        new Thread(task).start();
        try {
            Assertions.assertFalse(task.get());
        } catch (ExecutionException | InterruptedException error) {
            Assertions.fail();
        }
    }

    @Test
    void onePrimeTest() {
        
        ArrayList<Integer> list = new ArrayList<>();
        list.add(5);
        FutureTask<Boolean> task = new FutureTask<>(new Client(list));
        new Thread(task).start();
        try {
            Assertions.assertFalse(task.get());
        } catch (ExecutionException | InterruptedException error) {
            Assertions.fail();
        }
    }

    @Test
    void oneUnprimeTest() {
        
        ArrayList<Integer> list = new ArrayList<>();
        list.add(8);
        FutureTask<Boolean> task = new FutureTask<>(new Client(list));
        new Thread(task).start();
        try {
            Assertions.assertTrue(task.get());
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
        FutureTask<Boolean> task = new FutureTask<>(new Client(list));
        new Thread(task).start();
        try {
            Assertions.assertFalse(task.get());
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
        FutureTask<Boolean> task = new FutureTask<>(new Client(list));
        new Thread(task).start();
        try {
            Assertions.assertTrue(task.get());
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
        FutureTask<Boolean> task = new FutureTask<>(new Client(list));
        new Thread(task).start();
        try {
            Assertions.assertTrue(task.get());
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
        FutureTask<Boolean> task = new FutureTask<>(new Client(list));
        new Thread(task).start();
        try {
            Assertions.assertTrue(task.get());
        } catch (ExecutionException | InterruptedException error) {
            Assertions.fail();
        }
    }
}

