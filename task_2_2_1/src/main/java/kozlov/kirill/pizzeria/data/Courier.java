package kozlov.kirill.pizzeria.data;

import kozlov.kirill.jsonutil.Serializable;

/**
 * Courier record-class.
 *
 * @param name courier name
 * @param capacity trunk size
 */
public record Courier(String name, int capacity) implements Serializable {}
