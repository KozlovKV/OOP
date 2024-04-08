package kozlov.kirill.pizzeria.workers;

import java.util.LinkedList;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import kozlov.kirill.pizzeria.RunnablePizzeria;
import kozlov.kirill.pizzeria.data.Courier;
import kozlov.kirill.pizzeria.data.Order;
import kozlov.kirill.queue.OwnBlockingQueue;
import kozlov.kirill.queue.ProhibitedQueueActionException;

public class RunnableCourier implements RunnableEmployee {
    private final Courier courierData;
    private final OwnBlockingQueue<Order> warehouse;
    private final LinkedList<Order> trunk;
    private volatile boolean aboutToFinish = false;
    private volatile boolean finishedJob = false;

    public RunnableCourier(
        Courier courierData,
        OwnBlockingQueue<Order> warehouse
    ) {
        this.courierData = courierData;
        this.warehouse = warehouse;
        this.trunk = new LinkedList<>();
    }

    @Override
    public void offerToFinishJob() {
        aboutToFinish = true;
    }

    @Override
    public boolean hasFinishedJob() {
        return finishedJob;
    }

    private boolean isTrunkFull() {
        return this.trunk.size() == courierData.capacity();
    }

    private void fillTrunk() {
        while (!(warehouse.isEmptyUnreliable() || isTrunkFull())) {
            Optional<Order> potentialOrder = Optional.empty();
            boolean error = false;
            try {
                potentialOrder = warehouse.poll();
            } catch (ProhibitedQueueActionException | InterruptedException e) {
                error = true;
            }
            if (error || potentialOrder.isEmpty()) {
                System.out.println(
                    "Courier " + courierData.name()
                        + " cannot get order from warehouse"
                );
                break;
            }
            Order order = potentialOrder.get();
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
                    (long) order.distance() * RunnablePizzeria.TIME_MS_QUANTUM
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
        while (!(aboutToFinish && warehouse.isEmptyUnreliable())) {
            fillTrunk();
            deliverOrders();
        }
        System.out.println(
            "Courier " + courierData.name() + " has finished job"
        );
        finishedJob = true;
    }
}
