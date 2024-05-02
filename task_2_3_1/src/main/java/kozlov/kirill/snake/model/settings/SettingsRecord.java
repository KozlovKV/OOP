package kozlov.kirill.snake.model.settings;

import kozlov.kirill.util.Serializable;

public record SettingsRecord(
    int fieldWidth,
    int fieldHeight,
    int applesCount
) implements Serializable {}
