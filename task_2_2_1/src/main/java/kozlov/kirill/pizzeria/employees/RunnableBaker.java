package kozlov.kirill.pizzeria.employees;

import java.util.concurrent.CountDownLatch;
import kozlov.kirill.pizzeria.RunnablePizzeria;
import kozlov.kirill.pizzeria.data.Baker;
import kozlov.kirill.pizzeria.data.Order;
import kozlov.kirill.queue.OwnBlockingQueue;
import kozlov.kirill.queue.ProhibitedQueueActionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RunnableBaker implements ManagedRunnableEmployee {
    private final static Logger logger = LogManager.getLogger(RunnableBaker.class);

    private final Baker bakerData;
    private final OwnBlockingQueue<Order> newOrders;
    private Order currentOrder;
    private final OwnBlockingQueue<Order> warehouse;
    private volatile boolean aboutToFinish = false;

    private CountDownLatch finishLatch = null;

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
                (long) bakerData.speed() * RunnablePizzeria.TIME_MS_QUANTUM
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
