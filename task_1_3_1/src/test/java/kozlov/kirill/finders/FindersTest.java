package kozlov.kirill.finders;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
        finder.find("russian.txt", "бра", true);
        LinkedList<Long> predictedList = new LinkedList<>();
        predictedList.add((long) 1);
        predictedList.add((long) 8);
        Assertions.assertEquals(predictedList, finder.getTargetsFoundPositions());
    }

    @ParameterizedTest
    @ArgumentsSource(MainStringFinderArgumentsProvider.class)
    void germanTest(StringFinder finder) {
        finder.find("german.txt", "ü", true);
        LinkedList<Long> predictedList = new LinkedList<>();
        predictedList.add((long) 22);
        Assertions.assertEquals(predictedList, finder.getTargetsFoundPositions());
    }

    @ParameterizedTest
    @ArgumentsSource(MainStringFinderArgumentsProvider.class)
    void chineseTest(StringFinder finder) {
        finder.find("chinese.txt", "飘", true);
        LinkedList<Long> predictedList = new LinkedList<>();
        predictedList.add((long) 25);
        Assertions.assertEquals(predictedList, finder.getTargetsFoundPositions());
    }

    @ParameterizedTest
    @ArgumentsSource(MainStringFinderArgumentsProvider.class)
    void emptyTargetTest(StringFinder finder) {
        Assertions.assertThrows(UnsupportedOperationException.class, () -> {
            finder.find("russian.txt", "", true);
            Assertions.assertTrue(finder.getTargetsFoundPositions().isEmpty());
        });
    }

    @ParameterizedTest
    @ArgumentsSource(MainStringFinderArgumentsProvider.class)
    void multiLinesTest(StringFinder finder) {
        finder.find("multilines.txt", "test", true);
        LinkedList<Long> predictedList = new LinkedList<>();
        predictedList.add((long) 50);
        Assertions.assertEquals(predictedList, finder.getTargetsFoundPositions());
    }

    @ParameterizedTest
    @ArgumentsSource(MainStringFinderArgumentsProvider.class)
    void targetNotFoundTest(StringFinder finder) {
        finder.find("russian.txt", "hello", true);
        LinkedList<Long> predictedList = new LinkedList<>();
        Assertions.assertEquals(predictedList, finder.getTargetsFoundPositions());
    }

    @ParameterizedTest
    @ArgumentsSource(MainStringFinderArgumentsProvider.class)
    void fileNotFoundTest(StringFinder finder) {
        finder.find("not_exist.txt", "hello", true);
        Assertions.assertTrue(finder.getTargetsFoundPositions().isEmpty());
    }

    @ParameterizedTest
    @ArgumentsSource(MainStringFinderArgumentsProvider.class)
    void fullFileTargetTest(StringFinder finder) {
        finder.find("russian.txt", "абракадабра", true);
        LinkedList<Long> predictedList = new LinkedList<>();
        predictedList.add((long) 0);
        Assertions.assertEquals(predictedList, finder.getTargetsFoundPositions());
    }

    @ParameterizedTest
    @ArgumentsSource(MainStringFinderArgumentsProvider.class)
    void twoTargetsTest(StringFinder finder) {
        finder.find("russian.txt", "абракадабра", true);
        LinkedList<Long> predictedList = new LinkedList<>();
        predictedList.add((long) 0);
        Assertions.assertEquals(predictedList, finder.getTargetsFoundPositions());
        finder.find("russian.txt", "б", true);
        predictedList.clear();
        predictedList.add((long) 1);
        predictedList.add((long) 8);
        Assertions.assertEquals(predictedList, finder.getTargetsFoundPositions());
    }

    @ParameterizedTest
    @ArgumentsSource(MainStringFinderArgumentsProvider.class)
    void twoMbTest(StringFinder finder) {
        finder.find("2MB.txt", "aa", true);
        LinkedList<Long> predictedList = new LinkedList<>();
        predictedList.add((long) 1048575);
        Assertions.assertEquals(predictedList, finder.getTargetsFoundPositions());
    }

    /**
     * 16GB file generator.
     * Created file is fulfilled by 'b's excluding "aa" on positions 1048575 and 17178820607
     */
    static void write16gbTestFile() {
        try (FileWriter writer = new FileWriter("16GB.txt")) {
            int twoMbSize = 1048576 * 2;
            StringBuilder twoMbString = new StringBuilder("b");
            for (int i = twoMbSize; i > 1; i /= 2) {
                twoMbString.append(twoMbString);
            }
            String twoMbBs = twoMbString.toString();
            twoMbString.replace(1048575, 1048577, "aa");
            writer.write(twoMbString.toString());
            writer.flush();
            for (int i = 0; i < 8190; ++i) {
                writer.write(twoMbBs);
                writer.flush();
            }
            writer.write(twoMbString.toString());
            writer.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 16GB file deleter.
     */
    static void delete16gbTestFile() {
        try {
            Files.delete(Paths.get("16GB.txt"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @ParameterizedTest
    @ArgumentsSource(MainStringFinderArgumentsProvider.class)
    void insaneLargeTest(StringFinder finder) {
        write16gbTestFile();
        finder.find("16GB.txt", "aa", false);
        LinkedList<Long> predictedList = new LinkedList<>();
        predictedList.add((long) 1048575);
        predictedList.add((long) 16 * 1024 * 1024 * 1024 - 1048577);
        Assertions.assertEquals(predictedList, finder.getTargetsFoundPositions());
        delete16gbTestFile();
    }
}
