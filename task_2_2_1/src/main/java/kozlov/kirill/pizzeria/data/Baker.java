package kozlov.kirill.pizzeria.data;

import kozlov.kirill.jsonutil.Serializable;

/**
 * Baker record-class.
 * 
 * @param name baker name
 * @param cookingTime time for cooking in conventional units
 */
public record Baker(String name, int cookingTime) implements Serializable {}
