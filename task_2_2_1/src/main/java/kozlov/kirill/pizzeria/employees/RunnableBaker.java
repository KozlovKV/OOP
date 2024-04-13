package kozlov.kirill.pizzeria.employees;

import java.util.concurrent.CountDownLatch;
import kozlov.kirill.pizzeria.RunnablePizzeria;
import kozlov.kirill.pizzeria.data.Baker;
import kozlov.kirill.pizzeria.data.Order;
import kozlov.kirill.queue.OwnBlockingQueue;
import kozlov.kirill.queue.ProhibitedQueueActionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Baker class for thread launching.
 */
public class RunnableBaker implements ManagedRunnableEmployee {
    private static final Logger logger = LogManager.getLogger(RunnableBaker.class);

    private final Baker bakerData;
    private final OwnBlockingQueue<Order> newOrders;
    private Order currentOrder;
    private final OwnBlockingQueue<Order> warehouse;
    private volatile boolean aboutToFinish = false;

    private CountDownLatch finishLatch = null;

    /**
     * Constructor.
     *
     * @param bakerData baker data
     * @param newOrders link to blocking queue with new orders
     * @param warehouse link to blocking queue with warehouse
     */
    public RunnableBaker(
        Baker bakerData, OwnBlockingQueue<Order> newOrders,
        OwnBlockingQueue<Order> warehouse
    ) {
        this.bakerData = bakerData;
        this.newOrders = newOrders;
        this.warehouse = warehouse;
    }

    @Override
    public void setFinishLatch(CountDownLatch finishLatch) {
        this.finishLatch = finishLatch;
    }

    @Override
    public void offerToFinishJob() {
        aboutToFinish = true;
    }

    private boolean acceptOrder() {
        try {
            currentOrder = newOrders.poll();
        } catch (ProhibitedQueueActionException prohibitedQueueActionException) {
            return false;
        } catch (InterruptedException e) {
            logger.warn(
                "Baker {} was interrupted while getting order",
                bakerData.name(), e
            );
            return false;
        }
        logger.info(
            "Baker {} has accepted order {}",
            bakerData.name(), currentOrder.id()
        );
        return true;
    }

    private void cookOrder() {
        boolean error = false;
        try {
            Thread.sleep(
                (long) bakerData.cookingTime() * RunnablePizzeria.TIME_MS_QUANTUM
            );
        } catch (InterruptedException interruptedException) {
            logger.warn(
                "Baker {} was interrupted while cooking",
                bakerData.name(), interruptedException
            );
            error = true;
        }
        if (!error) {
            logger.info(
                "Baker {} has cooked order {}",
                bakerData.name(), currentOrder.id()
            );
            try {
                warehouse.add(currentOrder);
                logger.info(
                    "Baker {} has placed order {} to warehouse",
                    bakerData.name(), currentOrder.id()
                );
            } catch (ProhibitedQueueActionException | InterruptedException e) {
                logger.warn(
                    "Baker {} cannot place order {} to warehouse",
                    bakerData.name(), currentOrder.id(), e
                );
                error = true;
            }
        }
        if (error) {
            try {
                newOrders.add(currentOrder);
            } catch (ProhibitedQueueActionException | InterruptedException e) {
                logger.error(
                    "Baker {} cannot return failed order {} to new orders list",
                    bakerData.name(), currentOrder.id(), e
                );
            }
        }
    }

    /**
     * Method for launching in thread.
     * <br>
     * Tries to accept new order, cooks it and then tries to place it to warehouse.
     * Finishes job when has got signal for finishing and (perhaps) failed to accept order.
     */
    @Override
    public void run() {
        while (! aboutToFinish) {
            if (acceptOrder()) {
                cookOrder();
            }
        }
        logger.info("Baker {} has finished", bakerData.name());
        if (finishLatch != null) {
            finishLatch.countDown();
        }
    }
}
