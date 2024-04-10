package kozlov.kirill.pizzeria.employees;

import java.util.concurrent.CountDownLatch;
import kozlov.kirill.pizzeria.RunnablePizzeria;
import kozlov.kirill.pizzeria.data.Baker;
import kozlov.kirill.pizzeria.data.Order;
import kozlov.kirill.queue.OwnBlockingQueue;
import kozlov.kirill.queue.ProhibitedQueueActionException;

public class RunnableBaker implements ManagedRunnableEmployee {
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
            System.out.println(
                "Baker " + bakerData.name() + " cannot get order"
            );
            return false;
        }
        System.out.println(
            "Baker " + bakerData.name() + " has accepted "
                + "order " + currentOrder.id()
        );
        return true;
    }

    private void cookOrder() {
        boolean error = false;
        try {
            Thread.sleep(
                (long) bakerData.speed() * RunnablePizzeria.TIME_MS_QUANTUM
            );
        } catch (InterruptedException e) {
            System.out.println(
                "Baker " + bakerData.name() + " was interrupted while cooking"
            );
            error = true;
        }
        if (!error) {
            System.out.println(
                "Baker " + bakerData.name() + " has cooked order "
                    + "order " + currentOrder.id()
            );
            try {
                warehouse.add(currentOrder);
                System.out.println(
                    "Baker " + bakerData.name() + " has placed "
                        + "to warehouse order " + currentOrder.id()
                );
            } catch (ProhibitedQueueActionException | InterruptedException e) {
                System.out.println(
                    "Baker " + bakerData.name() + " cannot place "
                        + "order " + currentOrder.id() + " to warehouse"
                );
                error = true;
            }
        }
        if (error) {
            try {
                newOrders.add(currentOrder);
            } catch (ProhibitedQueueActionException | InterruptedException e) {
                System.out.println(
                    "Baker " + bakerData.name() + " cannot return "
                        + "failed order " + currentOrder.id() + " to new orders list"
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
        System.out.println(
            "Baker " + bakerData.name() + " has finished job"
        );
        if (finishLatch != null) {
            finishLatch.countDown();
        }
    }
}
