package kozlov.kirill.pizzeria.data;

import kozlov.kirill.jsonutil.Serializable;

public record Courier(String name, int capacity) implements Serializable {}
