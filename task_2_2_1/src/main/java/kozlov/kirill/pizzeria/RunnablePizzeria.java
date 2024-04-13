package kozlov.kirill.pizzeria;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Pizzeria class for thread launching.
 */
public class RunnablePizzeria implements Runnable {
    public final static long TIME_MS_QUANTUM = 100; //< One time step in pizzeria's world
    public final static long ORDER_WAITING_MS = 1000;

    private final static Logger logger = LogManager.getLogger(RunnablePizzeria.class);

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
    ) throws IOException, IllegalSetupDataException {
        logger.info("Initializing Pizzeria");
        this.timeForClosing = timeForClosing;
        this.setupSavePath = setupSavePath;
        Setup setup = getSetup(setupLoadPath);
        checkSetupValidness(setup);
        bakers = setup.bakers();
        couriers = setup.couriers();
        warehouse = new OwnBlockingQueue<>(setup.warehouseCapacity());
        newOrders = new OwnBlockingQueue<>(setup.orders());
        logger.info("Pizzeria initialized");
    }

    public boolean hasNotFinished() {
        return ! finished;
    }

    private Setup getSetup(
        String setupPath
    ) throws IOException {
        logger.info("Loading setup from {}", setupPath);
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
            var e = new FileNotFoundException(
                "Couldn't find setup JSON file either in specified path "
                + "either in this path in resources using path " + setupPath
            );
            logger.error("Failed to load setup", e);
            throw e;
        }
        try {
            loadedSetup = JsonUtils.parse(inputStream, Setup.class);
        } catch (ParsingException e) {
            logger.error("Failed to parse setup", e);
            throw e;
        }
        inputStream.close();
        logger.info("Loaded setup from {}", setupPath);
        return loadedSetup;
    }

    private void saveSetup() {
        logger.info("Saving setup in {}", setupSavePath);
        Setup setup = new Setup(
            bakers, couriers, warehouse.maxSize(), newOrders.getListCopyUnreliable()
        );
        try (
            OutputStream outputStream = new FileOutputStream(setupSavePath)
        ) {
            JsonUtils.serialize(setup, outputStream);
            logger.info("Saved setup in {}", setupSavePath);
        } catch (IOException streamSerializeException) {
            logger.error("Failed to save setup", streamSerializeException);
            try {
                System.out.println(JsonUtils.serialize(setup));
            } catch (IOException stringSerializeException) {
                logger.error("Failed to serialize setup", stringSerializeException);
            }
        }
    }

    private void checkSetupValidness(
        Setup setup
    ) throws IllegalSetupDataException {
        if (setup.bakers().isEmpty()) {
            logger.error("Invalid setup: no bakers found");
            throw new IllegalSetupDataException("There are no bakers");
        }
        if (setup.couriers().isEmpty()) {
            logger.error("Invalid setup: no couriers found");
            throw new IllegalSetupDataException("There are no couriers");
        }
        if (setup.warehouseCapacity() <= 0) {
            logger.error("Invalid setup: non-positive warehouse capacity");
            throw new IllegalSetupDataException("There is invalid warehouse capacity");
        }
    }

    @Override
    public void run() {
        logger.info("Starting Pizzeria");
        runEmployees();
        logger.info("All employees have started");
        try {
            Thread.sleep(timeForClosing * TIME_MS_QUANTUM);
        } catch (InterruptedException ignored) {}
        logger.info("Finishing work day");
        finishWorkDay();
        logger.info("Work day finished");
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
    }

    private void finishWorkDay() {
        bakersManager.offerEmployeesFinishJob();
        newOrders.prohibitPolling();
        try {
            bakersManager.waitForAllEmployeesFinished();
        } catch (InterruptedException interruptedException) {
            logger.warn("Bakers waiting interrupted");
        }
        warehouse.prohibitAdding();
        logger.info("All bakers have finished their job");
        couriersManager.offerEmployeesFinishJob();
        try {
            couriersManager.waitForAllEmployeesFinished();
        } catch (InterruptedException interruptedException) {
            logger.warn("Couriers waiting interrupted");
        }
        logger.info("All couriers finished their job");
        saveSetup();
        finished = true;
    }
}
