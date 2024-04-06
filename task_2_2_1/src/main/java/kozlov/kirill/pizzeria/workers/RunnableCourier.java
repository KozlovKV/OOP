package kozlov.kirill.pizzeria.workers;

import kozlov.kirill.pizzeria.Pizzeria;
import kozlov.kirill.pizzeria.data.Courier;
import kozlov.kirill.pizzeria.data.Order;
import kozlov.kirill.pizzeria.queue.OwnBlockingQueue;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

public class RunnableCourier implements RunnableWorker {
    private final Courier courierData;
    private final OwnBlockingQueue<Order> warehouse;
    private final LinkedList<Order> trunk;
    private boolean isAboutEnd = false;
    private boolean isEndedJob = false;

    public RunnableCourier(
        Courier courierData,
        OwnBlockingQueue<Order> warehouse
    ) {
        this.courierData = courierData;
        this.warehouse = warehouse;
        this.trunk = new LinkedList<>();
    }

    @Override
    public void offerEndJob() {
        isAboutEnd = true;
    }

    @Override
    public boolean hasEndedJob() {
        return isEndedJob;
    }

    private boolean isTrunkFull() {
        return this.trunk.size() == courierData.capacity();
    }

    private void fillTrunk() {
        while (!(warehouse.isEmpty() || isTrunkFull())) {
            Optional<Order> currentOrder = Optional.empty();
            boolean error = false;
            try {
                currentOrder = warehouse.poll(Pizzeria.ORDER_WAITING_MS);
            } catch (TimeoutException timeoutException) {
                break;
            } catch (InterruptedException interruptedException) {
                error = true;
            }
            if (error || currentOrder.isEmpty()) {
                System.out.println(
                    "Courier " + courierData.name()
                        + " cannot get order from warehouse"
                );
                break;
            }
            Order order = currentOrder.get();
            trunk.add(order);
            System.out.println(
                "Courier " + courierData.name()
                    + " got order " + order.id() + " from warehouse "
            );
        }
    }

    private void deliverOrders() {
        while (!trunk.isEmpty()) {
            Order order = trunk.poll();
            try {
                Thread.sleep(
                    (long) order.distance() * Pizzeria.TIME_MS_QUANTUM
                );
                System.out.println(
                    "Courier " + courierData.name() + " has delivered"
                        + " order " + order.id()
                );
            } catch (InterruptedException e) {
                System.out.println(
                    "Courier " + courierData.name() + " was interrupted while delivering"
                );
                // Добавить больше логики
                return;
            }
        }
    }

    @Override
    public void run() {
        while (!(isAboutEnd && warehouse.isEmpty())) {
            fillTrunk();
            deliverOrders();
        }
        System.out.println(
            "Courier " + courierData.name() + " has ended job"
        );
        isEndedJob = true;
    }
}
