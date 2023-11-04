package kozlov.kirill.finders;

import java.util.LinkedList;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;


/**
 * Test class for string finders.
 */
public class FindersTest {
    static class MainStringFinderArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of(new SimpleStringFinder())
            );
        }
    }

    @ParameterizedTest
    @ArgumentsSource(MainStringFinderArgumentsProvider.class)
    void russianTest(StringFinder finder) {
        finder.find("russian.txt", "бра");
        LinkedList<Long> predictedList = new LinkedList<>();
        predictedList.add((long) 1);
        predictedList.add((long) 8);
        Assertions.assertEquals(predictedList, finder.getTargetsFoundPositions());
    }

    @ParameterizedTest
    @ArgumentsSource(MainStringFinderArgumentsProvider.class)
    void germanTest(StringFinder finder) {
        finder.find("german.txt", "ü");
        LinkedList<Long> predictedList = new LinkedList<>();
        predictedList.add((long) 22);
        Assertions.assertEquals(predictedList, finder.getTargetsFoundPositions());
    }

    @ParameterizedTest
    @ArgumentsSource(MainStringFinderArgumentsProvider.class)
    void chineseTest(StringFinder finder) {
        finder.find("chinese.txt", "飘");
        LinkedList<Long> predictedList = new LinkedList<>();
        predictedList.add((long) 25);
        Assertions.assertEquals(predictedList, finder.getTargetsFoundPositions());
    }

    @ParameterizedTest
    @ArgumentsSource(MainStringFinderArgumentsProvider.class)
    void emptyTargetTest(StringFinder finder) {
        Assertions.assertThrows(UnsupportedOperationException.class, () -> {
            finder.find("russian.txt", "");
            Assertions.assertTrue(finder.getTargetsFoundPositions().isEmpty());
        });
    }

    @ParameterizedTest
    @ArgumentsSource(MainStringFinderArgumentsProvider.class)
    void multiLinesTest(StringFinder finder) {
        finder.find("multilines.txt", "test");
        LinkedList<Long> predictedList = new LinkedList<>();
        predictedList.add((long) 50);
        Assertions.assertEquals(predictedList, finder.getTargetsFoundPositions());
    }

    @ParameterizedTest
    @ArgumentsSource(MainStringFinderArgumentsProvider.class)
    void notFoundTest(StringFinder finder) {
        finder.find("russian.txt", "hello");
        LinkedList<Long> predictedList = new LinkedList<>();
        Assertions.assertEquals(predictedList, finder.getTargetsFoundPositions());
    }

    @ParameterizedTest
    @ArgumentsSource(MainStringFinderArgumentsProvider.class)
    void fullFileTargetTest(StringFinder finder) {
        finder.find("russian.txt", "абракадабра");
        LinkedList<Long> predictedList = new LinkedList<>();
        predictedList.add((long) 0);
        Assertions.assertEquals(predictedList, finder.getTargetsFoundPositions());
    }

    @ParameterizedTest
    @ArgumentsSource(MainStringFinderArgumentsProvider.class)
    void twoMbTest(StringFinder finder) {
        finder.find("2MB.txt", "aa");
        LinkedList<Long> predictedList = new LinkedList<>();
        predictedList.add((long) 1048575);
        Assertions.assertEquals(predictedList, finder.getTargetsFoundPositions());
    }

    /*
      Test for file generated by script.

      @param finder string finder super abstract class
     */
    //        @ParameterizedTest
    //        @ArgumentsSource(MainStringFinderArgumentsProvider.class)
    //        void insaneLargeTest(StringFinder finder) {
    //            finder.find("16GB.txt", "aa");
    //            LinkedList<Long> predictedList = new LinkedList<>();
    //            predictedList.add((long) 1048575);
    //            predictedList.add((long) 16 * 1024 * 1024 * 1024 - 1048577);
    //            Assertions.assertEquals(predictedList, finder.getTargetsFoundPositions());
    //        }
}
