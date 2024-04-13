package kozlov.kirill.pizzeria.data;

import java.util.ArrayList;
import kozlov.kirill.jsonutil.Serializable;

/**
 * Record-class with all data for pizzeria.
 *
 * @param bakers bakers' list
 * @param couriers couriers' list
 * @param warehouseCapacity warehouse size
 * @param orders orders' list
 */
public record Setup(
    ArrayList<Baker> bakers,
    ArrayList<Courier> couriers,
    int warehouseCapacity,
    ArrayList<Order> orders
) implements Serializable {}
