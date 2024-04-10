package kozlov.kirill;

import java.io.IOException;
import kozlov.kirill.pizzeria.RunnablePizzeria;

@ExcludeClassFromJacocoGeneratedReport
public class Main {
    public static void main(String[] args) {
        try {
            new Thread(
                new RunnablePizzeria(
                    50, "setup.json",
                    "simpleTest.json"
                )
            ).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}