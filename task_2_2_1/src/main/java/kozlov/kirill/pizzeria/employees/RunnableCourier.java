package kozlov.kirill.pizzeria.employees;

import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;
import kozlov.kirill.pizzeria.RunnablePizzeria;
import kozlov.kirill.pizzeria.data.Courier;
import kozlov.kirill.pizzeria.data.Order;
import kozlov.kirill.queue.OwnBlockingQueue;
import kozlov.kirill.queue.ProhibitedQueueActionException;


public class RunnableCourier implements ManagedRunnableEmployee {
    private final Courier courierData;
    private final OwnBlockingQueue<Order> newOrders;
    private final OwnBlockingQueue<Order> warehouse;
    private final LinkedList<Order> trunk;
    private volatile boolean aboutToFinish = false;

    private CountDownLatch finishLatch = null;

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
                System.out.println(
                    "Courier " + courierData.name()
                        + " cannot get order from warehouse"
                );
                break;
            }
            trunk.add(order);
            System.out.println(
                "Courier " + courierData.name()
                    + " got order " + order.id() + " from warehouse "
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
                System.out.println(
                    "Courier " + courierData.name() + " has delivered"
                        + " order " + order.id()
                );
            } catch (InterruptedException e) {
                System.out.println(
                    "Courier " + courierData.name() + " was interrupted while delivering"
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
                System.out.println(
                    "Courier " + courierData.name() + " cannot return "
                        + "failed order " + order.id() + " to new orders list"
                );
            }
        }
    }

    @Override
    public void run() {
        while (! aboutToFinish || ! warehouse.isEmptyUnreliable()) {
            fillTrunk();
            if (!deliverOrders()) {
                returnOrders();
            }
        }
        System.out.println(
            "Courier " + courierData.name() + " has finished job"
        );
        if (finishLatch != null) {
            finishLatch.countDown();
        }
    }
}
