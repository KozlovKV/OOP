package kozlov.kirill.pizzeria;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import kozlov.kirill.pizzeria.data.Setup;
import kozlov.kirill.jsonutil.JsonUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
        RunnablePizzeria runnablePizzeria = null;
        try {
            runnablePizzeria = new RunnablePizzeria(
                timeForClosing, "simple.json", outputPath
            );
        } catch (IOException e) {
            Assertions.fail();
        }
        Thread pizzeriaThread = new Thread(runnablePizzeria);
        pizzeriaThread.start();
        try {
            Thread.sleep(2 * timeForClosing * RunnablePizzeria.TIME_MS_QUANTUM);
        } catch (InterruptedException interruptedException) {
            Assertions.fail();
        }
        while (runnablePizzeria.hasNotFinished());
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
        RunnablePizzeria runnablePizzeria = null;
        try {
            runnablePizzeria = new RunnablePizzeria(
                timeForClosing, "simple.json", outputPath
            );
        } catch (IOException e) {
            Assertions.fail();
        }
        Thread pizzeriaThread = new Thread(runnablePizzeria);
        pizzeriaThread.start();
        try {
            Thread.sleep(2 * timeForClosing * RunnablePizzeria.TIME_MS_QUANTUM);
        } catch (InterruptedException interruptedException) {
            Assertions.fail();
        }
        while (runnablePizzeria.hasNotFinished());
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
        RunnablePizzeria runnablePizzeria = null;
        try {
            runnablePizzeria = new RunnablePizzeria(
                timeForClosing, "smallWarehouse.json", outputPath
            );
        } catch (IOException e) {
            Assertions.fail();
        }
        Thread pizzeriaThread = new Thread(runnablePizzeria);
        pizzeriaThread.start();
        try {
            Thread.sleep(2 * timeForClosing * RunnablePizzeria.TIME_MS_QUANTUM);
        } catch (InterruptedException interruptedException) {
            Assertions.fail();
        }
        while (runnablePizzeria.hasNotFinished());
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
        RunnablePizzeria runnablePizzeria = null;
        try {
            runnablePizzeria = new RunnablePizzeria(
                timeForClosing, "twoDays.json", outputPath
            );
        } catch (IOException e) {
            Assertions.fail();
        }
        Thread pizzeriaThread = new Thread(runnablePizzeria);
        pizzeriaThread.start();
        try {
            Thread.sleep(2 * timeForClosing * RunnablePizzeria.TIME_MS_QUANTUM);
        } catch (InterruptedException interruptedException) {
            Assertions.fail();
        }
        while (runnablePizzeria.hasNotFinished());
        try (InputStream resultFileStream = new FileInputStream(outputPath)) {
            Assertions.assertFalse(JsonUtils.parse(
                resultFileStream, Setup.class
            ).orders().isEmpty());
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail();
        }
        Assertions.assertFalse(pizzeriaThread.isAlive());
        String outputPath2 = "test2.json";

        try {
            runnablePizzeria = new RunnablePizzeria(
                timeForClosing, outputPath, outputPath2
            );
        } catch (IOException e) {
            Assertions.fail();
        }
        pizzeriaThread = new Thread(runnablePizzeria);
        pizzeriaThread.start();
        try {
            Thread.sleep(2 * timeForClosing * RunnablePizzeria.TIME_MS_QUANTUM);
        } catch (InterruptedException interruptedException) {
            Assertions.fail();
        }
        while (runnablePizzeria.hasNotFinished());
        try (InputStream resultFileStream = new FileInputStream(outputPath2)) {
            Assertions.assertTrue(JsonUtils.parse(
                resultFileStream, Setup.class
            ).orders().isEmpty());
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail();
        }
        Assertions.assertFalse(pizzeriaThread.isAlive());
        removeFile(outputPath);
        removeFile(outputPath2);
    }
}
