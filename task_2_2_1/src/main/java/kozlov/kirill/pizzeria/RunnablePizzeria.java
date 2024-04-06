package kozlov.kirill.pizzeria;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import kozlov.kirill.jsonutil.EndOfStreamException;
import kozlov.kirill.jsonutil.JsonUtils;
import kozlov.kirill.jsonutil.ParsingException;
import kozlov.kirill.pizzeria.data.Baker;
import kozlov.kirill.pizzeria.data.Courier;
import kozlov.kirill.pizzeria.data.Order;
import kozlov.kirill.pizzeria.data.Setup;
import kozlov.kirill.pizzeria.workers.RunnableBaker;
import kozlov.kirill.pizzeria.workers.RunnableCourier;
import kozlov.kirill.queue.OwnBlockingQueue;

public class RunnablePizzeria implements Runnable {
    public final static long TIME_MS_QUANTUM = 100; //< One time step in pizzeria's world
    public final static long ORDER_WAITING_MS = 1000;

    private final long timeForClosing;
    private ArrayList<Baker> bakers;
    private final ArrayList<RunnableBaker> runnableBakers = new ArrayList<>();
    private ArrayList<Courier> couriers;
    private final ArrayList<RunnableCourier> runnableCouriers = new ArrayList<>();
    private OwnBlockingQueue<Order> warehouse;
    private OwnBlockingQueue<Order> newOrders;
    private final String setupSavePath;
    private volatile boolean finished = false;

    public RunnablePizzeria(
        long timeForClosing, String setupLoadPath, String setupSavePath
    ) {
        this.timeForClosing = timeForClosing;
        this.setupSavePath = setupSavePath;
        Setup setup = getSetup(setupLoadPath);
        if (setup == null) {
            return;
        }
        bakers = setup.bakers();
        couriers = setup.couriers();
        warehouse = new OwnBlockingQueue<>(setup.warehouseCapacity());
        newOrders = new OwnBlockingQueue<>(setup.orders());
    }

    public boolean hasFinished() {
        return finished;
    }

    private Setup getSetup(String setupPath) {
        InputStream inputStream;
        Setup loadedSetup = null;
        try {
            inputStream = new FileInputStream(setupPath);
        } catch (IOException notFoundException) {
            inputStream = getClass().getClassLoader().getResourceAsStream(
                setupPath
            );
        }
        if (inputStream == null) {
            System.err.println(
                "Couldn't find setup JSON file either in specified path "
                + "either in this path in resources"
            );
            return null;
        }
        try {
            loadedSetup = JsonUtils.parse(inputStream, Setup.class);
        } catch (ParsingException | EndOfStreamException e) {
            System.err.println("Parsing failed");
            return null;
        }
        try {
            inputStream.close();
        } catch (IOException ignored) {
            System.err.println("Failed to close file input stream");
        }
        return loadedSetup;
    }

    private void saveSetup() {
        System.out.println("Saving setup...");
        Setup setup = new Setup(
            bakers, couriers, warehouse.maxSize(), newOrders.getListCopy()
        );
        try (
            OutputStream outputStream = new FileOutputStream(setupSavePath)
        ) {
            JsonUtils.serialize(setup, outputStream);
            System.out.println("Setup saved");
        } catch (IOException streamSerializeException) {
            System.err.println("Failed to save setup: " + streamSerializeException.getMessage());
            System.err.println("Printing setup to STDERR:");
            try {
                System.err.println(JsonUtils.serialize(setup));
            } catch (IOException stringSerializeException) {
                System.err.println("Failed to serialize setup");
            }
        }
    }

    @Override
    public void run() {
        runWorkers();
        try {
            Thread.sleep(timeForClosing * TIME_MS_QUANTUM);
        } catch (InterruptedException ignored) {}
        finishWorkDay();
    }

    private void runWorkers() {
        for (var baker : bakers) {
            RunnableBaker runnableBaker = new RunnableBaker(baker, newOrders, warehouse);
            new Thread(runnableBaker).start();
            runnableBakers.add(runnableBaker);
        }
        for (var courier : couriers) {
            RunnableCourier runnableCourier = new RunnableCourier(courier, warehouse);
            new Thread(runnableCourier).start();
            runnableCouriers.add(runnableCourier);
        }
        System.out.println("All employees have started");
    }

    private void finishWorkDay() {
        System.out.println("Finishing work day...");
        for (var runnableBaker : runnableBakers) {
            runnableBaker.offerToFinishJob();
        }
        while (
            runnableCouriers.stream()
                .filter(RunnableCourier::hasFinishedJob)
                .count() == runnableBakers.size()
        ) {
            try {
                System.out.println("Waiting while all bakers finish their job...");
                Thread.sleep(TIME_MS_QUANTUM);
            } catch (InterruptedException interruptedException) {
                System.err.println("Bakers waiting interrupted");
            }
        }
        System.out.println("All bakers finished their job. \nWaiting couriers...");
        for (var runnableCourier : runnableCouriers) {
            runnableCourier.offerToFinishJob();
        }
        System.out.println("All couriers finished their job");
        saveSetup();
        finished = true;
    }
}
