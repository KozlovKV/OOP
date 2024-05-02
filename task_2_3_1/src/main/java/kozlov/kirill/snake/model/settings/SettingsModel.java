package kozlov.kirill.snake.model.settings;

import kozlov.kirill.snake.model.ModelFragment;
import lombok.Data;

@Data
public class SettingsModel implements ModelFragment {
    private int fieldWidth = 20;
    private int fieldHeight = 20;
    private int applesCount = 3;

    @Override
    public SettingsModel restartModel() {
        return this;
    }
}
