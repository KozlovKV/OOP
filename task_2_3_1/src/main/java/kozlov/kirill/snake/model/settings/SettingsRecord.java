package kozlov.kirill.snake.model.settings;

import kozlov.kirill.util.Serializable;

/**
 * Settings record class.
 *
 * @param fieldWidth field's width
 * @param fieldHeight field's height
 * @param applesCount apples count on the field
 */
public record SettingsRecord(
    int fieldWidth,
    int fieldHeight,
    int applesCount
) implements Serializable {}
