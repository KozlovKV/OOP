package kozlov.kirill.pizzeria.data;

import kozlov.kirill.jsonutil.Serializable;

/**
 * Order record-class.
 *
 * @param id order id
 * @param distance distance to destination in conventional units (aka time for delivering)
 */
public record Order(String id, int distance) implements Serializable {}
