package kozlov.kirill.pizzeria.workers;

import java.util.Optional;
import java.util.concurrent.TimeoutException;
import kozlov.kirill.pizzeria.RunnablePizzeria;
import kozlov.kirill.pizzeria.data.Baker;
import kozlov.kirill.pizzeria.data.Order;
import kozlov.kirill.queue.OwnBlockingQueue;
import kozlov.kirill.queue.ProhibitedQueueActionException;

public class RunnableBaker implements RunnableEmployee {
    private final Baker bakerData;
    private final OwnBlockingQueue<Order> newOrders;
    private final OwnBlockingQueue<Order> warehouse;
    private volatile boolean aboutToFinish = false;
    private volatile boolean finishedJob = false;

    public RunnableBaker(
        Baker bakerData, OwnBlockingQueue<Order> newOrders,
        OwnBlockingQueue<Order> warehouse
    ) {
        this.bakerData = bakerData;
        this.newOrders = newOrders;
        this.warehouse = warehouse;
    }

    @Override
    public void offerToFinishJob() {
        aboutToFinish = true;
    }

    @Override
    public boolean hasFinishedJob() {
        return finishedJob;
    }

    private void cookOrder() {
        Optional<Order> potentialOrder = Optional.empty();
        boolean error = false;
        try {
            potentialOrder = newOrders.poll();
        } catch (ProhibitedQueueActionException prohibitedQueueActionException) {
            return;
        } catch (InterruptedException e) {
            error = true;
        }
        if (error || potentialOrder.isEmpty()) {
            System.out.println(
                "Baker " + bakerData.name() + " cannot get order"
            );
            return;
        }
        Order order = potentialOrder.get();
        System.out.println(
            "Baker " + bakerData.name() + " has accepted "
                + "order " + order.id()
        );
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
                    + "order " + order.id()
            );
            try {
                warehouse.add(order);
                System.out.println(
                    "Baker " + bakerData.name() + " has placed "
                        + "to warehouse order " + order.id()
                );
            } catch (ProhibitedQueueActionException | InterruptedException e) {
                System.out.println(
                    "Baker " + bakerData.name() + " cannot place "
                        + "order " + order.id() + " to warehouse"
                );
                error = true;
            }
        }
        if (error) {
            try {
                newOrders.add(order);
            } catch (ProhibitedQueueActionException | InterruptedException e) {
                System.out.println(
                    "Baker " + bakerData.name() + " cannot return "
                        + "failed order " + order.id() + " to new orders list"
                );
            }
        }
    }

    @Override
    public void run() {
        while (! aboutToFinish) {
            cookOrder();
        }
        System.out.println(
            "Baker " + bakerData.name() + " has finished job"
        );
        finishedJob = true;
    }
}
