package kozlov.kirill.pizzeria;

import kozlov.kirill.pizzeria.data.Setup;
import kozlov.kirill.jsonutil.JsonUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;

public class PizzeriaTest {
    static void removeFile(String path) {
        File file = new File(path);
        if (!file.delete()) {
            Assertions.fail();
        }
    }

    @Test
    void simpleTest() {
        String outputPath = "test.json";
        long timeForClosing = 50;
        RunnablePizzeria runnablePizzeria = new RunnablePizzeria(
            timeForClosing, "simple.json", outputPath
        );
        Thread pizzeriaThread = new Thread(runnablePizzeria);
        pizzeriaThread.start();
        try {
            Thread.sleep(2 * timeForClosing * RunnablePizzeria.TIME_MS_QUANTUM);
        } catch (InterruptedException interruptedException) {
            Assertions.fail();
        }
        while (!runnablePizzeria.hasFinished());
        Assertions.assertFalse(pizzeriaThread.isAlive());
        try (InputStream resultFileStream = new FileInputStream(outputPath)) {
            Assertions.assertTrue(
                JsonUtils.parse(
                    resultFileStream, Setup.class
                ).orders().isEmpty()
            );
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail();
        }
        removeFile(outputPath);
    }

    @Test
    void emptyTest() {
        String outputPath = "test.json";
        long timeForClosing = 50;
        RunnablePizzeria runnablePizzeria = new RunnablePizzeria(
            timeForClosing, "simple.json", outputPath
        );
        Thread pizzeriaThread = new Thread(runnablePizzeria);
        pizzeriaThread.start();
        try {
            Thread.sleep(2 * timeForClosing * RunnablePizzeria.TIME_MS_QUANTUM);
        } catch (InterruptedException interruptedException) {
            Assertions.fail();
        }
        while (!runnablePizzeria.hasFinished());
        Assertions.assertFalse(pizzeriaThread.isAlive());
        try (InputStream resultFileStream = new FileInputStream(outputPath)) {
            Assertions.assertTrue(
                JsonUtils.parse(
                    resultFileStream, Setup.class
                ).orders().isEmpty()
            );
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail();
        }
        removeFile(outputPath);
    }

    @Test
    void smallWarehouseTest() {
        String outputPath = "test.json";
        long timeForClosing = 50;
        RunnablePizzeria runnablePizzeria = new RunnablePizzeria(
            timeForClosing, "smallWarehouse.json", outputPath
        );
        Thread pizzeriaThread = new Thread(runnablePizzeria);
        pizzeriaThread.start();
        try {
            Thread.sleep(2 * timeForClosing * RunnablePizzeria.TIME_MS_QUANTUM);
        } catch (InterruptedException interruptedException) {
            Assertions.fail();
        }
        while (!runnablePizzeria.hasFinished());
        Assertions.assertFalse(pizzeriaThread.isAlive());
        try (InputStream resultFileStream = new FileInputStream(outputPath)) {
            Assertions.assertTrue(
                JsonUtils.parse(
                    resultFileStream, Setup.class
                ).orders().isEmpty()
            );
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail();
        }
        removeFile(outputPath);
    }

    @Test
    void twoDaysTest() {
        String outputPath = "test.json";
        long timeForClosing = 6;
        RunnablePizzeria runnablePizzeria = new RunnablePizzeria(
            timeForClosing, "twoDays.json", outputPath
        );
        Thread pizzeriaThread = new Thread(runnablePizzeria);
        pizzeriaThread.start();
        try {
            Thread.sleep(2 * timeForClosing * RunnablePizzeria.TIME_MS_QUANTUM);
        } catch (InterruptedException interruptedException) {
            Assertions.fail();
        }
        while (!runnablePizzeria.hasFinished());
        Assertions.assertFalse(pizzeriaThread.isAlive());
        try (InputStream resultFileStream = new FileInputStream(outputPath)) {
            Assertions.assertEquals(1, JsonUtils.parse(
                resultFileStream, Setup.class
            ).orders().size());
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail();
        }
        String outputPath2 = "test2.json";
        runnablePizzeria = new RunnablePizzeria(
            timeForClosing, outputPath, outputPath2
        );
        pizzeriaThread = new Thread(runnablePizzeria);
        pizzeriaThread.start();
        try {
            Thread.sleep(2 * timeForClosing * RunnablePizzeria.TIME_MS_QUANTUM);
        } catch (InterruptedException interruptedException) {
            Assertions.fail();
        }
        while (!runnablePizzeria.hasFinished());
        Assertions.assertFalse(pizzeriaThread.isAlive());
        try (InputStream resultFileStream = new FileInputStream(outputPath2)) {
            Assertions.assertTrue(JsonUtils.parse(
                resultFileStream, Setup.class
            ).orders().isEmpty());
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail();
        }
        removeFile(outputPath);
        removeFile(outputPath2);
    }
}
