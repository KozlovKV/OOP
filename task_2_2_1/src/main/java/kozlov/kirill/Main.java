package kozlov.kirill;

import java.io.IOException;
import kozlov.kirill.pizzeria.RunnablePizzeria;

/**
 * Main entry point class.
 */
@ExcludeClassFromJacocoGeneratedReport
public class Main {
    /**
     * Main entry point method.
     *
     * @param args cmd's args
     */
    public static void main(String[] args) {
        try {
            new Thread(
                new RunnablePizzeria(
                    50, "setup.json",
                    "./testData/simpleTest.json"
                )
            ).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}