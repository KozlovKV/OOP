package kozlov.kirill.snake.model;

import kozlov.kirill.snake.model.game.GameModel;
import kozlov.kirill.snake.model.settings.SettingsModel;
import lombok.Getter;

public enum Model {
    SETTINGS(new SettingsModel()),
    GAME(new GameModel());

    private final ModelFragment modelFragment;

    Model(ModelFragment modelFragment) {
        this.modelFragment = modelFragment;
    }

    public ModelFragment get() {
        return modelFragment;
    }
}
