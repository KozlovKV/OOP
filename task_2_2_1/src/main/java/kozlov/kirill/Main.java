package kozlov.kirill;

import kozlov.kirill.pizzeria.RunnablePizzeria;

@ExcludeClassFromJacocoGeneratedReport
public class Main {
    public static void main(String[] args) {
        new Thread(
            new RunnablePizzeria(
                50, "simple.json",
                "simpleTest.json"
            )
        ).start();
    }
}