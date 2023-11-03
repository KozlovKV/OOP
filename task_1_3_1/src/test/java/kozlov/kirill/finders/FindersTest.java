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

    static class SecondConstructorStringFinderArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of(new SimpleStringFinder("./data/russian.txt"))
            );
        }
    }

    static class ThirdConstructorStringFinderArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of(new SimpleStringFinder("./data/russian.txt", "бра"))
            );
        }
    }

    @ParameterizedTest
    @ArgumentsSource(MainStringFinderArgumentsProvider.class)
    void russianTest(StringFinder finder) {
        finder.setSearchTarget("бра");
        finder.openFile("./data/russian.txt");
        finder.find();
        LinkedList<Long> predictedList = new LinkedList<>();
        predictedList.add((long) 1);
        predictedList.add((long) 8);
        Assertions.assertEquals(predictedList, finder.getTargetsFoundPositions());
    }

    @ParameterizedTest
    @ArgumentsSource(MainStringFinderArgumentsProvider.class)
    void germanTest(StringFinder finder) {
        finder.setSearchTarget("ü");
        finder.openFile("./data/german.txt");
        finder.find();
        LinkedList<Long> predictedList = new LinkedList<>();
        predictedList.add((long) 22);
        Assertions.assertEquals(predictedList, finder.getTargetsFoundPositions());
    }

    @ParameterizedTest
    @ArgumentsSource(MainStringFinderArgumentsProvider.class)
    void chineseTest(StringFinder finder) {
        finder.setSearchTarget("飘");
        finder.openFile("./data/chinese.txt");
        finder.find();
        LinkedList<Long> predictedList = new LinkedList<>();
        predictedList.add((long) 25);
        Assertions.assertEquals(predictedList, finder.getTargetsFoundPositions());
    }

    @ParameterizedTest
    @ArgumentsSource(MainStringFinderArgumentsProvider.class)
    void emptyTargetTest(StringFinder finder) {
        Assertions.assertThrows(UnsupportedOperationException.class, () -> {
            finder.setSearchTarget("");
            finder.openFile("./data/russian.txt");
            finder.find();
            Assertions.assertTrue(finder.getTargetsFoundPositions().isEmpty());
        });
    }

    @ParameterizedTest
    @ArgumentsSource(MainStringFinderArgumentsProvider.class)
    void multiLinesTest(StringFinder finder) {
        finder.setSearchTarget("test");
        finder.openFile("./data/multilines.txt");
        finder.find();
        LinkedList<Long> predictedList = new LinkedList<>();
        predictedList.add((long) 50);
        Assertions.assertEquals(predictedList, finder.getTargetsFoundPositions());
    }

    @ParameterizedTest
    @ArgumentsSource(MainStringFinderArgumentsProvider.class)
    void notFoundTest(StringFinder finder) {
        finder.setSearchTarget("hello");
        finder.openFile("./data/russian.txt");
        finder.find();
        LinkedList<Long> predictedList = new LinkedList<>();
        Assertions.assertEquals(predictedList, finder.getTargetsFoundPositions());
    }

    @ParameterizedTest
    @ArgumentsSource(MainStringFinderArgumentsProvider.class)
    void fullFileTargetTest(StringFinder finder) {
        finder.setSearchTarget("абракадабра");
        finder.openFile("./data/russian.txt");
        finder.find();
        LinkedList<Long> predictedList = new LinkedList<>();
        predictedList.add((long) 0);
        Assertions.assertEquals(predictedList, finder.getTargetsFoundPositions());
    }

    @ParameterizedTest
    @ArgumentsSource(SecondConstructorStringFinderArgumentsProvider.class)
    void secondConstructorTest(StringFinder finder) {
        finder.setSearchTarget("бра");
        finder.find();
        LinkedList<Long> predictedList = new LinkedList<>();
        predictedList.add((long) 1);
        predictedList.add((long) 8);
        Assertions.assertEquals(predictedList, finder.getTargetsFoundPositions());
    }

    @ParameterizedTest
    @ArgumentsSource(ThirdConstructorStringFinderArgumentsProvider.class)
    void thirdConstructorTest(StringFinder finder) {
        finder.find();
        LinkedList<Long> predictedList = new LinkedList<>();
        predictedList.add((long) 1);
        predictedList.add((long) 8);
        Assertions.assertEquals(predictedList, finder.getTargetsFoundPositions());
    }

    @ParameterizedTest
    @ArgumentsSource(MainStringFinderArgumentsProvider.class)
    void twoMbTest(StringFinder finder) {
        finder.setSearchTarget("aa");
        finder.openFile("./data/2MB.txt");
        finder.find();
        LinkedList<Long> predictedList = new LinkedList<>();
        predictedList.add((long) 1048575);
        Assertions.assertEquals(predictedList, finder.getTargetsFoundPositions());
    }

    /*
      Test for file generated by script.

      @param finder string finder super abstract class
     */
    //    @ParameterizedTest
    //    @ArgumentsSource(StringGraphsArgumentsProvider.class)
    //    void insaneLargeTest(StringFinder finder) {
    //        finder.setSearchTarget("aa");
    //        finder.openFile("./data/16GB.txt");
    //        finder.find();
    //        LinkedList<Long> predictedList = new LinkedList<>();
    //        predictedList.add((long) 1048575);
    //        predictedList.add((long) 16 * 1024 * 1024 * 1024 - 1048577);
    //        Assertions.assertEquals(predictedList, finder.getTargetsFoundPositions());
    //    }
}
