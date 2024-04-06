package kozlov.kirill.pizzeria.workers;

import java.util.Optional;
import java.util.concurrent.TimeoutException;
import kozlov.kirill.pizzeria.RunnablePizzeria;
import kozlov.kirill.pizzeria.data.Baker;
import kozlov.kirill.pizzeria.data.Order;
import kozlov.kirill.queue.OwnBlockingQueue;

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
            potentialOrder = newOrders.poll(RunnablePizzeria.ORDER_WAITING_MS);
        } catch (TimeoutException timeoutException) {
            return;
        } catch (InterruptedException interruptedException) {
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
            warehouse.add(order);
            System.out.println(
                "Baker " + bakerData.name() + " has cooked "
                    + "and placed to warehouse order " + order.id()
            );
        } catch (InterruptedException e) {
            System.out.println(
                "Baker " + bakerData.name() + " was interrupted while cooking"
            );
            // Добавить больше логики для этой ситуации. Возможно, возвращать заказ обратно в новые
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
