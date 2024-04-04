package kozlov.kirill.pizzeria;

import kozlov.kirill.pizzeria.data.Baker;
import kozlov.kirill.pizzeria.data.Courier;
import kozlov.kirill.pizzeria.data.Order;
import kozlov.kirill.pizzeria.data.Setup;
import kozlov.kirill.pizzeria.data.utils.EndOfStreamException;
import kozlov.kirill.pizzeria.data.utils.JsonUtils;
import kozlov.kirill.pizzeria.data.utils.ParsingException;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Pizzeria {
    final static int TIME_MS_QUANTUM = 100; //< One time step in pizzeria's world

    private final int timeForClosing;
    private final ArrayList<Baker> bakers;
    private final ExecutorService bakersThreadPool;
    private final ArrayList<Courier> couriers;
    private final ExecutorService couriersThreadPool;
    private final OwnBlockingQueue<Order> warehouse;
    private final OwnBlockingQueue<Order> newOrders;

    public Pizzeria(int timeForClosing, String setupPath) throws FileNotFoundException {
        this.timeForClosing = timeForClosing;
        Setup setup = getSetup(setupPath);
        bakers = setup.bakers();
        bakers.add(new Baker("Ivan", 99));
        bakersThreadPool = Executors.newFixedThreadPool(bakers.size());
        couriers = setup.couriers();
        couriersThreadPool = Executors.newFixedThreadPool(couriers.size());
        warehouse = new OwnBlockingQueue<>(setup.warehouseCapacity());
        newOrders = new OwnBlockingQueue<>(setup.orders());
        saveSetup("./testData/newSetup.json");
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

    private void saveSetup(String setupPath) throws FileNotFoundException {
        Setup setup = new Setup(
            bakers, couriers, warehouse.maxSize(), newOrders.getListCopy()
        );
        try (
            OutputStream outputStream = new FileOutputStream(setupPath)
        ) {
            JsonUtils.serialize(setup, outputStream);
        } catch (IOException e) {
            System.err.println("Failed to save setup");
            e.printStackTrace();
        }
    }
}
