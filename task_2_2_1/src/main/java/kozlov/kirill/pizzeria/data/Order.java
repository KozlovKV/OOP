package kozlov.kirill.pizzeria.data;

import kozlov.kirill.jsonutil.Serializable;

public record Order(String id, int distance) implements Serializable {}
