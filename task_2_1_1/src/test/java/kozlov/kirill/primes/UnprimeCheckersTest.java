package kozlov.kirill.primes;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.ArrayList;
import java.util.stream.Stream;

public class UnprimeCheckersTest {
    static class CheckersProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
            return Stream.of(
                Arguments.of(new SimpleUnprimeChecker()),
                Arguments.of(new ParallelStreamsUnprimeChecker()),
                Arguments.of(new ThreadUnprimeChecker(4)),
                Arguments.of(new ThreadUnprimeChecker(8)),
                Arguments.of(new ThreadUnprimeChecker(12))
            );
        }
    }

    @ParameterizedTest
    @ArgumentsSource(CheckersProvider.class)
    void simplePrimesTest(UnprimeChecker checker) {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(2);
        list.add(3);
        list.add(5);
        list.add(7);
        checker.setNumbers(list);
        Assertions.assertFalse(checker.isAnyUnprime());
    }

    @ParameterizedTest
    @ArgumentsSource(CheckersProvider.class)
    void simpleUnprimeTest(UnprimeChecker checker) {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        list.add(7);
        checker.setNumbers(list);
        Assertions.assertTrue(checker.isAnyUnprime());
    }
}
