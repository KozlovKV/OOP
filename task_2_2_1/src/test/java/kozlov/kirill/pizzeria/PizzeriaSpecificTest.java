package kozlov.kirill.pizzeria;

import kozlov.kirill.jsonutil.ParsingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * Tests for specific pizzeria situations.
 */
public class PizzeriaSpecificTest {
    @Test
    void setupNotFoundTest() {
        Assertions.assertThrows(IOException.class, () -> {
            new Thread(
                new RunnablePizzeria(
                    50, "fakeFile.json",
                    "noNeedIndeed"
                )
            ).start();
        });
    }

    @Test
    void parsingExceptionTest() {
        Assertions.assertThrows(ParsingException.class, () -> {
            new Thread(
                new RunnablePizzeria(
                    50, "invalid.json",
                    "noNeedIndeed"
                )
            ).start();
        });
    }

    @Test
    void noBakersTest() {
        Assertions.assertThrows(IllegalSetupDataException.class, () -> {
            new Thread(
                new RunnablePizzeria(
                    50, "noBakers.json",
                    "noNeedIndeed"
                )
            ).start();
        });
    }

    @Test
    void noCouriersTest() {
        Assertions.assertThrows(IllegalSetupDataException.class, () -> {
            new Thread(
                new RunnablePizzeria(
                    50, "noCouriers.json",
                    "noNeedIndeed"
                )
            ).start();
        });
    }

    @Test
    void zeroWarehouseCapacityTest() {
        Assertions.assertThrows(IllegalSetupDataException.class, () -> {
            new Thread(
                new RunnablePizzeria(
                    50, "zeroWarehouseCapacity.json",
                    "noNeedIndeed"
                )
            ).start();
        });
    }
}
