package kozlov.kirill.snake.model.settings;

import kozlov.kirill.util.Serializable;

/**
 * Settings record class.
 *
 * @param randomsCount count of random enemy snakes
 * @param predatorsCount count of predator enemy snakes
 * @param applesCount apples count on the field
 */
public record SettingsRecord(
    int randomsCount,
    int predatorsCount,
    int applesCount
) implements Serializable {}
