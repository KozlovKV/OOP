package kozlov.kirill.pizzeria;

import java.io.*;
import java.util.ArrayList;

import kozlov.kirill.jsonutil.JsonUtils;
import kozlov.kirill.jsonutil.ParsingException;
import kozlov.kirill.pizzeria.data.Baker;
import kozlov.kirill.pizzeria.data.Courier;
import kozlov.kirill.pizzeria.data.Order;
import kozlov.kirill.pizzeria.data.Setup;
import kozlov.kirill.pizzeria.employees.EmployeesManager;
import kozlov.kirill.pizzeria.employees.RunnableBaker;
import kozlov.kirill.pizzeria.employees.RunnableCourier;
import kozlov.kirill.queue.OwnBlockingQueue;

public class RunnablePizzeria implements Runnable {
    public final static long TIME_MS_QUANTUM = 100; //< One time step in pizzeria's world
    public final static long ORDER_WAITING_MS = 1000;

    private final long timeForClosing;
    private volatile boolean finished = false;

    private ArrayList<Baker> bakers;
    private EmployeesManager<RunnableBaker> bakersManager;
    private ArrayList<Courier> couriers;
    private EmployeesManager<RunnableCourier> couriersManager;
    private OwnBlockingQueue<Order> warehouse;
    private OwnBlockingQueue<Order> newOrders;

    private final String setupSavePath;

    public RunnablePizzeria(
        long timeForClosing, String setupLoadPath, String setupSavePath
    ) throws IOException {
        this.timeForClosing = timeForClosing;
        this.setupSavePath = setupSavePath;
        Setup setup = getSetup(setupLoadPath);
        bakers = setup.bakers();
        couriers = setup.couriers();
        warehouse = new OwnBlockingQueue<>(setup.warehouseCapacity());
        newOrders = new OwnBlockingQueue<>(setup.orders());
    }

    public boolean hasNotFinished() {
        return ! finished;
    }

    private Setup getSetup(
        String setupPath
    ) throws IOException {
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
            throw new FileNotFoundException(
                "Couldn't find setup JSON file either in specified path "
                + "either in this path in resources using path " + setupPath
            );
        }
        loadedSetup = JsonUtils.parse(inputStream, Setup.class);
        inputStream.close();
        return loadedSetup;
    }

    private void saveSetup() {
        System.out.println("Saving setup...");
        Setup setup = new Setup(
            bakers, couriers, warehouse.maxSize(), newOrders.getListCopyUnreliable()
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
        runEmployees();
        try {
            Thread.sleep(timeForClosing * TIME_MS_QUANTUM);
        } catch (InterruptedException ignored) {}
        finishWorkDay();
    }

    private void runEmployees() {
        ArrayList<RunnableBaker> runnableBakers = new ArrayList<>();
        for (var baker : bakers) {
            runnableBakers.add(new RunnableBaker(baker, newOrders, warehouse));
        }
        bakersManager = new EmployeesManager<>(runnableBakers);
        bakersManager.startEmployees();
        ArrayList<RunnableCourier> runnableCouriers = new ArrayList<>();
        for (var courier : couriers) {
            runnableCouriers.add(new RunnableCourier(courier, newOrders, warehouse));
        }
        couriersManager = new EmployeesManager<>(runnableCouriers);
        couriersManager.startEmployees();
        System.out.println("All employees have started");
    }

    private void finishWorkDay() {
        System.out.println("Finishing work day...");
        bakersManager.offerEmployeesFinishJob();
        newOrders.prohibitPolling();
        try {
            bakersManager.waitForAllEmployeesFinished();
        } catch (InterruptedException interruptedException) {
            System.err.println("Bakers waiting interrupted");
        }
        warehouse.prohibitAdding();
        System.out.println("All bakers finished their job. \nWaiting couriers...");
        couriersManager.offerEmployeesFinishJob();
        try {
            couriersManager.waitForAllEmployeesFinished();
        } catch (InterruptedException interruptedException) {
            System.err.println("Couriers waiting interrupted");
        }
        System.out.println("All couriers finished their job");
        saveSetup();
        finished = true;
    }
}
