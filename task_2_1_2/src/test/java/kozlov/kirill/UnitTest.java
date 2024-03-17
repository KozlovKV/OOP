package kozlov.kirill;

import java.util.ArrayList;
import kozlov.kirill.primes.ParallelStreamsUnprimeChecker;
import kozlov.kirill.primes.UnprimeChecker;
import kozlov.kirill.sockets.data.TaskData;
import kozlov.kirill.sockets.data.utils.TaskDataUtils;
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

    @Test
    void exampleTest1() {
        ArrayList<Integer> list = getArrayListFromArray(
                new int[]{6, 8, 7, 13, 5, 9, 4}, 7
        );
        UnprimeChecker unprimeChecker = new ParallelStreamsUnprimeChecker().setNumbers(list);
        Assertions.assertTrue(unprimeChecker.isAnyUnprime());
    }

    @Test
    void exampleTest2() {
        ArrayList<Integer> list = getArrayListFromArray(
                new int[]{20319251, 6997901, 6997927, 6997937, 17858849, 6997967,
                        6998009, 6998029, 6998039, 20165149, 6998051, 6998053}, 12
        );
        UnprimeChecker unprimeChecker = new ParallelStreamsUnprimeChecker().setNumbers(list);
        Assertions.assertFalse(unprimeChecker.isAnyUnprime());
    }

    @Test
    void simplePrimesTest() {
        ArrayList<Integer> list = getArrayListFromArray(
                new int[]{2, 3, 5, 7}, 4
        );
        UnprimeChecker unprimeChecker = new ParallelStreamsUnprimeChecker().setNumbers(list);
        Assertions.assertFalse(unprimeChecker.isAnyUnprime());
    }

    @Test
    void simpleUnprimeTest() {
        ArrayList<Integer> list = getArrayListFromArray(
                new int[]{2, 3, 4, 5, 7}, 5
        );
        UnprimeChecker unprimeChecker = new ParallelStreamsUnprimeChecker().setNumbers(list);
        Assertions.assertTrue(unprimeChecker.isAnyUnprime());
    }

    @Test
    void splitDataZeroRest() {
        TaskData originTaskData = new TaskData(getArrayListFromArray(
                new int[]{6, 8, 7, 13, 5, 9, 4, 8}, 8
        ));
        ArrayList<TaskData> expectedTasks = new ArrayList<>();
        expectedTasks.add(new TaskData(getArrayListFromArray(
                new int[]{6, 8, 7, 13}, 4
        )));
        expectedTasks.add(new TaskData(getArrayListFromArray(
                new int[]{5, 9, 4, 8}, 4
        )));
        Assertions.assertEquals(
            expectedTasks, TaskDataUtils.splitTaskData(originTaskData, 2)
        );
    }

    @Test
    void splitDataNonZeroRest() {
        TaskData originTaskData = new TaskData(getArrayListFromArray(
                new int[]{6, 8, 7, 13, 5, 9, 4}, 7
        ));
        ArrayList<TaskData> expectedTasks = new ArrayList<>();
        expectedTasks.add(new TaskData(getArrayListFromArray(
                new int[]{6, 8}, 2
        )));
        expectedTasks.add(new TaskData(getArrayListFromArray(
                new int[]{7, 13}, 2
        )));
        expectedTasks.add(new TaskData(getArrayListFromArray(
                new int[]{5, 9}, 2
        )));
        expectedTasks.add(new TaskData(getArrayListFromArray(
                new int[]{4}, 1
        )));
        Assertions.assertEquals(
                expectedTasks, TaskDataUtils.splitTaskData(originTaskData, 4)
        );
    }

    // TODO: проверка парсера
}

