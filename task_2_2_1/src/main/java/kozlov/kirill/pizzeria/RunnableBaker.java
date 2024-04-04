package kozlov.kirill.pizzeria;

import kozlov.kirill.pizzeria.data.Baker;
import kozlov.kirill.pizzeria.data.Order;

import java.util.Optional;

public class RunnableBaker implements Runnable {
    private final Baker baker;
    private final OwnBlockingQueue<Order> newOrders;
    private final OwnBlockingQueue<Order> warehouse;

    public RunnableBaker(
        Baker baker, OwnBlockingQueue<Order> newOrders,
        OwnBlockingQueue<Order> warehouse
    ) {
        this.baker = baker;
        this.newOrders = newOrders;
        this.warehouse = warehouse;
    }

    @Override
    public void run() {
        Optional<Order> currentOrder = Optional.empty();
        try {
            currentOrder = newOrders.poll();
        } catch (InterruptedException interruptedException) {
            System.out.println(
                "Baker " + baker.name() + " cannot get order"
            );
        }
        while (currentOrder.isPresent()) {
            Order order = currentOrder.get();
            System.out.println(
                "Baker " + baker.name() + " has accepted "
                + "order " + order.id()
            );
            try {
                Thread.sleep(
                    (long) baker.speed() * Pizzeria.TIME_MS_QUANTUM
                );
                // Если склад уже закрыт для пополнения навечно, вернуть в новые ордеры и завершиться
                warehouse.add(order);
                System.out.println(
                    "Baker " + baker.name() + " has cooked "
                        + "and placed to warehouse order " + order.id()
                );
            } catch (InterruptedException e) {
                System.out.println(
                    "Baker " + baker.name() + " was interrupted while cooking"
                );
                return;
            }
            try {
                currentOrder = newOrders.poll();
            } catch (InterruptedException interruptedException) {
                System.out.println(
                    "Baker " + baker.name() + " cannot get order"
                );
                return;
            }
        }
    }
}
