package kozlov.kirill.finders;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.LinkedList;
import java.util.stream.Stream;

public class FindersTest {
    static class StringGraphsArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of(new SimpleStringFinder())
            );
        }
    }

    @ParameterizedTest
    @ArgumentsSource(StringGraphsArgumentsProvider.class)
    void testGraph(StringFinder finder) {
        finder.setSearchTarget("бра");
        finder.openFile("input.txt");
        finder.find();
        LinkedList<Long> predictedList = new LinkedList<>();
        predictedList.add((long)1);
        predictedList.add((long)8);
        Assertions.assertEquals(finder.getTargetsFoundPositions(), predictedList);
    }
}
