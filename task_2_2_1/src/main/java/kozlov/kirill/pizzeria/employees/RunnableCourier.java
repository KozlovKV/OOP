package kozlov.kirill.pizzeria.employees;

import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;
import kozlov.kirill.pizzeria.RunnablePizzeria;
import kozlov.kirill.pizzeria.data.Courier;
import kozlov.kirill.pizzeria.data.Order;
import kozlov.kirill.queue.OwnBlockingQueue;
import kozlov.kirill.queue.ProhibitedQueueActionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Courier class for thread launching.
 */
public class RunnableCourier implements ManagedRunnableEmployee {
    private final static Logger logger = LogManager.getLogger(RunnableCourier.class);

    private final Courier courierData;
    private final OwnBlockingQueue<Order> newOrders;
    private final OwnBlockingQueue<Order> warehouse;
    private final LinkedList<Order> trunk;
    private volatile boolean aboutToFinish = false;

    private CountDownLatch finishLatch = null;

    /**
     * Constructor.
     *
     * @param courierData courier data
     * @param newOrders link to blocking queue with new orders
     * @param warehouse link to blocking queue with warehouse
     */
    public RunnableCourier(
        Courier courierData,
        OwnBlockingQueue<Order> newOrders,
        OwnBlockingQueue<Order> warehouse
    ) {
        this.courierData = courierData;
        this.newOrders = newOrders;
        this.warehouse = warehouse;
        this.trunk = new LinkedList<>();
    }

    @Override
    public void setFinishLatch(CountDownLatch finishLatch) {
        this.finishLatch = finishLatch;
    }

    @Override
    public void offerToFinishJob() {
        aboutToFinish = true;
    }

    private boolean isTrunkFull() {
        return this.trunk.size() == courierData.capacity();
    }

    private void fillTrunk() {
        while (!isTrunkFull()) {
            Order order;
            try {
                order = warehouse.poll(RunnablePizzeria.ORDER_WAITING_MS);
            } catch (TimeoutException timeoutException) {
                return;
            } catch (ProhibitedQueueActionException | InterruptedException e) {
                logger.warn(
                    "Courier {} cannot get order from warehouse",
                    courierData.name(), e
                );
                break;
            }
            trunk.add(order);
            logger.info(
                "Courier {} got order {} from warehouse",
                courierData.name(), order.id()
            );
        }
    }

    private boolean deliverOrders() {
        while (!trunk.isEmpty()) {
            Order order = trunk.poll();
            try {
                Thread.sleep(
                    (long) order.distance() * RunnablePizzeria.TIME_MS_QUANTUM
                );
                logger.info(
                    "Courier {} has delivered order {}",
                    courierData.name(), order.id()
                );
            } catch (InterruptedException e) {
                logger.warn(
                    "Courier {} was interrupted while delivering order {}",
                    courierData.name(), order.id(), e
                );
                trunk.add(order);
                return false;
            }
        }
        return true;
    }

    private void returnOrders() {
        for (Order order : trunk) {
            try {
                newOrders.add(order);
            } catch (ProhibitedQueueActionException | InterruptedException e) {
                logger.error(
                    "Courier {} cannot return failed order {} to new orders list",
                    courierData.name(), order.id(), e
                );
            }
        }
    }

    /**
     * Method for launching in thread.
     * <br>
     * Tries to fill trunk while don't get finishing signal and warehouse isn't empty.
     * It there is any error while delivering orders courier return all orders from trunk
     * to new orders.
     */
    @Override
    public void run() {
        while (! aboutToFinish || ! warehouse.isEmptyUnreliable()) {
            fillTrunk();
            if (!deliverOrders()) {
                returnOrders();
            }
        }
        logger.info("Courier {} has finished job", courierData.name());
        if (finishLatch != null) {
            finishLatch.countDown();
        }
    }
}
