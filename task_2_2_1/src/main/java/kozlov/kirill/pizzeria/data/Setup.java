package kozlov.kirill.pizzeria.data;

import java.util.ArrayList;

public record Setup(
    ArrayList<Baker> bakers,
    ArrayList<Courier> couriers,
    int warehouseCapacity,
    ArrayList<Order> orders
) implements Serializable {}
