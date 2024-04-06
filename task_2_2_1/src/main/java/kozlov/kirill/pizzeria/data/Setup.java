package kozlov.kirill.pizzeria.data;

import java.util.ArrayList;
import kozlov.kirill.jsonutil.Serializable;

public record Setup(
    ArrayList<Baker> bakers,
    ArrayList<Courier> couriers,
    int warehouseCapacity,
    ArrayList<Order> orders
) implements Serializable {}
