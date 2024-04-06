package kozlov.kirill.pizzeria;

import kozlov.kirill.pizzeria.data.Baker;
import kozlov.kirill.pizzeria.data.Courier;
import kozlov.kirill.pizzeria.data.Order;
import kozlov.kirill.pizzeria.data.Setup;
import kozlov.kirill.pizzeria.data.utils.EndOfStreamException;
import kozlov.kirill.pizzeria.data.utils.JsonUtils;
import kozlov.kirill.pizzeria.data.utils.ParsingException;
import kozlov.kirill.pizzeria.queue.OwnBlockingQueue;
import kozlov.kirill.pizzeria.workers.RunnableBaker;
import kozlov.kirill.pizzeria.workers.RunnableCourier;

import java.io.*;
import java.util.ArrayList;

public class Pizzeria implements Runnable {
    public final static long TIME_MS_QUANTUM = 100; //< One time step in pizzeria's world
    public final static long ORDER_WAITING_MS = 1000;

    private final long timeForClosing;
    private final ArrayList<Baker> bakers;
    private final ArrayList<RunnableBaker> runnableBakers = new ArrayList<>();
    private final ArrayList<Courier> couriers;
    private final ArrayList<RunnableCourier> runnableCouriers = new ArrayList<>();
    private final OwnBlockingQueue<Order> warehouse;
    private final OwnBlockingQueue<Order> newOrders;
    private final String setupSavePath;

    public Pizzeria(
        int timeForClosing, String setupLoadPath, String setupSavePath
    ) throws FileNotFoundException {
        this.timeForClosing = timeForClosing;
        this.setupSavePath = setupSavePath;
        Setup setup = getSetup(setupLoadPath);
        bakers = setup.bakers();
        couriers = setup.couriers();
        warehouse = new OwnBlockingQueue<>(setup.warehouseCapacity());
        newOrders = new OwnBlockingQueue<>(setup.orders());
    }

    private Setup getSetup(String setupPath) throws FileNotFoundException {
        InputStream inputStream = null;
        Setup loadedSetup = null;
        try {
            inputStream = new FileInputStream(setupPath);
        } catch (IOException notFoundException) {
            inputStream = getClass().getClassLoader().getResourceAsStream(
                setupPath
            );
        }
        if (inputStream == null)
            throw new FileNotFoundException();
        try {
            loadedSetup = JsonUtils.parse(inputStream, Setup.class);
        } catch (ParsingException | EndOfStreamException e) {
            System.err.println("Parsing failed");
        }
        try {
            inputStream.close();
        } catch (IOException ignored) {
            System.err.println("Failed to close file input stream");
        }
        return loadedSetup;
    }

    private void saveSetup() {
        Setup setup = new Setup(
            bakers, couriers, warehouse.maxSize(), newOrders.getListCopy()
        );
        try (
            OutputStream outputStream = new FileOutputStream(setupSavePath)
        ) {
            JsonUtils.serialize(setup, outputStream);
        } catch (IOException e) {
            System.err.println("Failed to save setup");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        runWorkers();
        System.out.println("All workers have started");
        try {
            Thread.sleep(timeForClosing * TIME_MS_QUANTUM);
        } catch (InterruptedException ignored) {}
        endWorkDay();
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
    }

    private void endWorkDay() {
        for (var runnableBaker : runnableBakers) {
            runnableBaker.offerEndJob();
        }
        while (runnableCouriers.stream().filter(RunnableCourier::hasEndedJob).count() == runnableBakers.size());
        for (var runnableCourier : runnableCouriers) {
            runnableCourier.offerEndJob();
        }
        saveSetup();
    }
}
