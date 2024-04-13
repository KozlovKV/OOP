package kozlov.kirill.pizzeria;

import java.util.ArrayList;
import kozlov.kirill.pizzeria.data.Baker;
import kozlov.kirill.pizzeria.data.Courier;
import kozlov.kirill.pizzeria.data.Order;
import kozlov.kirill.pizzeria.employees.RunnableBaker;
import kozlov.kirill.pizzeria.employees.RunnableCourier;
import kozlov.kirill.queue.OwnBlockingQueue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for specific situations with employees.
 */
public class EmployeesSpecificTest {
    @Test
    void barkerFailOrderTest() {
        ArrayList<Order> orders = new ArrayList<>();
        orders.add(new Order("a", 1));
        OwnBlockingQueue<Order> newOrders = new OwnBlockingQueue<>(orders);
        OwnBlockingQueue<Order> warehouse = new OwnBlockingQueue<>(1);
        warehouse.prohibitAdding();
        RunnableBaker baker = new RunnableBaker(
            new Baker("Test", 5), newOrders, warehouse
        );
        new Thread(baker).start();
        try {
            Thread.sleep(RunnablePizzeria.TIME_MS_QUANTUM);
            Assertions.assertTrue(newOrders.isEmptyUnreliable());
        } catch (InterruptedException e) {
            Assertions.fail();
        }
        baker.offerToFinishJob();
        try {
            Thread.sleep(5 * RunnablePizzeria.TIME_MS_QUANTUM);
            Assertions.assertFalse(newOrders.isEmptyUnreliable());
        } catch (InterruptedException e) {
            Assertions.fail();
        }
    }

    @Test
    void courierFailOrderTest() {
        ArrayList<Order> orders = new ArrayList<>();
        orders.add(new Order("a", 1000));
        OwnBlockingQueue<Order> newOrders = new OwnBlockingQueue<>(orders);
        OwnBlockingQueue<Order> warehouse = new OwnBlockingQueue<>(1);
        RunnableBaker baker = new RunnableBaker(
            new Baker("Test", 1), newOrders, warehouse
        );
        RunnableCourier courier = new RunnableCourier(
            new Courier("Test", 1), newOrders, warehouse
        );
        new Thread(baker).start();
        Thread courierThread = new Thread(courier);
        courierThread.start();
        try {
            Thread.sleep(5 * RunnablePizzeria.TIME_MS_QUANTUM);
            Assertions.assertTrue(newOrders.isEmptyUnreliable());
            Assertions.assertTrue(warehouse.isEmptyUnreliable());
        } catch (InterruptedException e) {
            Assertions.fail();
        }
        baker.offerToFinishJob();
        newOrders.prohibitPolling();
        courier.offerToFinishJob();
        courierThread.interrupt();
        try {
            Thread.sleep(5 * RunnablePizzeria.TIME_MS_QUANTUM);
            Assertions.assertFalse(newOrders.isEmptyUnreliable());
        } catch (InterruptedException e) {
            Assertions.fail();
        }
    }
}
