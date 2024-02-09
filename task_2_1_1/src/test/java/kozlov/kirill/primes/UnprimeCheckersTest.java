package kozlov.kirill.primes;

import java.util.ArrayList;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

/**
 * Tests' class for unprime checkers.
 */
public class UnprimeCheckersTest {
    static class CheckersProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                Arguments.of(new SimpleUnprimeChecker()),
                Arguments.of(new ParallelStreamsUnprimeChecker()),
                Arguments.of(new ThreadUnprimeChecker(4)),
                Arguments.of(new ThreadUnprimeChecker(8)),
                Arguments.of(new ThreadUnprimeChecker(12))
            );
        }
    }

    private static ArrayList<Integer> getArrayListFromArray(int [] arr, int n) {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            list.add(arr[i]);
        }
        return list;
    }

    @ParameterizedTest
    @ArgumentsSource(CheckersProvider.class)
    void exampleTest1(UnprimeChecker checker) {
        ArrayList<Integer> list = getArrayListFromArray(
                new int[]{6, 8, 7, 13, 5, 9, 4}, 7
        );
        checker.setNumbers(list);
        Assertions.assertTrue(checker.isAnyUnprime());
    }

    @ParameterizedTest
    @ArgumentsSource(CheckersProvider.class)
    void exampleTest2(UnprimeChecker checker) {
        ArrayList<Integer> list = getArrayListFromArray(
                new int[]{20319251, 6997901, 6997927, 6997937, 17858849, 6997967,
                          6998009, 6998029, 6998039, 20165149, 6998051, 6998053}, 12
        );
        checker.setNumbers(list);
        Assertions.assertFalse(checker.isAnyUnprime());
    }

    @ParameterizedTest
    @ArgumentsSource(CheckersProvider.class)
    void simplePrimesTest(UnprimeChecker checker) {
        ArrayList<Integer> list = getArrayListFromArray(
                new int[]{2, 3, 5, 7}, 4
        );
        checker.setNumbers(list);
        Assertions.assertFalse(checker.isAnyUnprime());
    }

    @ParameterizedTest
    @ArgumentsSource(CheckersProvider.class)
    void simpleUnprimeTest(UnprimeChecker checker) {
        ArrayList<Integer> list = getArrayListFromArray(
                new int[]{2, 3, 4, 5, 7}, 5
        );
        checker.setNumbers(list);
        Assertions.assertTrue(checker.isAnyUnprime());
    }

    @ParameterizedTest
    @ArgumentsSource(CheckersProvider.class)
    void emptyListTest(UnprimeChecker checker) {
        ArrayList<Integer> list = new ArrayList<>();
        checker.setNumbers(list);
        Assertions.assertFalse(checker.isAnyUnprime());
    }

    @ParameterizedTest
    @ArgumentsSource(CheckersProvider.class)
    void onePrimeTest(UnprimeChecker checker) {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(5);
        checker.setNumbers(list);
        Assertions.assertFalse(checker.isAnyUnprime());
    }

    @ParameterizedTest
    @ArgumentsSource(CheckersProvider.class)
    void oneUnprimeTest(UnprimeChecker checker) {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(8);
        checker.setNumbers(list);
        Assertions.assertTrue(checker.isAnyUnprime());
    }

    @ParameterizedTest
    @ArgumentsSource(CheckersProvider.class)
    void largePrimesTest(UnprimeChecker checker) {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < 1000000; i++) {
            list.add(100000007);
        }
        checker.setNumbers(list);
        Assertions.assertFalse(checker.isAnyUnprime());
    }

    @ParameterizedTest
    @ArgumentsSource(CheckersProvider.class)
    void largeUnprimesTest(UnprimeChecker checker) {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < 1000000; i++) {
            list.add(100000006);
        }
        checker.setNumbers(list);
        Assertions.assertTrue(checker.isAnyUnprime());
    }

    @ParameterizedTest
    @ArgumentsSource(CheckersProvider.class)
    void largeWithOneUnprimeAtStartTest(UnprimeChecker checker) {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(8);
        for (int i = 0; i < 1000000; i++) {
            list.add(100000007);
        }
        checker.setNumbers(list);
        Assertions.assertTrue(checker.isAnyUnprime());
    }

    @ParameterizedTest
    @ArgumentsSource(CheckersProvider.class)
    void largeWithOneUnprimeAtEndTest(UnprimeChecker checker) {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < 1000000; i++) {
            list.add(100000007);
        }
        list.add(8);
        checker.setNumbers(list);
        Assertions.assertTrue(checker.isAnyUnprime());
    }
}
