package kozlov.kirill.pizzeria.workers;

import kozlov.kirill.pizzeria.Pizzeria;
import kozlov.kirill.pizzeria.data.Baker;
import kozlov.kirill.pizzeria.data.Order;
import kozlov.kirill.pizzeria.queue.OwnBlockingQueue;

import java.util.Optional;
import java.util.concurrent.TimeoutException;

public class RunnableBaker implements RunnableWorker {
    private final Baker bakerData;
    private final OwnBlockingQueue<Order> newOrders;
    private final OwnBlockingQueue<Order> warehouse;
    private boolean isAboutEnd = false;
    private boolean isEndedJob = false;

    public RunnableBaker(
        Baker bakerData, OwnBlockingQueue<Order> newOrders,
        OwnBlockingQueue<Order> warehouse
    ) {
        this.bakerData = bakerData;
        this.newOrders = newOrders;
        this.warehouse = warehouse;
    }

    @Override
    public void offerEndJob() {
        isAboutEnd = true;
    }

    @Override
    public boolean hasEndedJob() {
        return isEndedJob;
    }

    @Override
    public void run() {
        Optional<Order> currentOrder = Optional.empty();
        while (!isAboutEnd) {
            boolean error = false;
            try {
                currentOrder = newOrders.poll(Pizzeria.ORDER_WAITING_MS);
            } catch (TimeoutException timeoutException) {
                continue;
            } catch (InterruptedException interruptedException) {
                error = true;
            }
            if (error || currentOrder.isEmpty()) {
                System.out.println(
                    "Baker " + bakerData.name() + " cannot get order"
                );
                continue;
            }
            Order order = currentOrder.get();
            System.out.println(
                "Baker " + bakerData.name() + " has accepted "
                + "order " + order.id()
            );
            try {
                Thread.sleep(
                    (long) bakerData.speed() * Pizzeria.TIME_MS_QUANTUM
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
                return;
            }
        }
        System.out.println(
            "Baker " + bakerData.name() + " has ended job"
        );
        isEndedJob = true;
    }
}
