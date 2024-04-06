package kozlov.kirill.pizzeria.data;

import kozlov.kirill.jsonutil.Serializable;

public record Baker(String name, int speed) implements Serializable {}
