package kozlov.kirill.pizzeria;

import kozlov.kirill.ExcludeClassFromJacocoGeneratedReport;

import java.io.FileNotFoundException;

@ExcludeClassFromJacocoGeneratedReport
public class Main {
    public static void main(String[] args) {

        try {
            new Thread(
                new Pizzeria(
                    50, "setup.json",
                    "./testData/save.json"
                )
            ).start();
        } catch (FileNotFoundException ignored) {}

    }
}