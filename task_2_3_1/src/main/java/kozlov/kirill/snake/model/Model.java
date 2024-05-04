package kozlov.kirill.snake.model;

import kozlov.kirill.snake.model.game.GameModel;
import kozlov.kirill.snake.model.settings.SettingsModel;

/**
 * Enum class for singletones ModelFragments
 */
public enum Model {
    SETTINGS(new SettingsModel()),
    GAME(new GameModel());

    private final ModelFragment modelFragment;

    Model(ModelFragment modelFragment) {
        this.modelFragment = modelFragment;
    }

    /**
     * Model fragment getter.
     *
     * @return link to model fragment instance
     */
    public ModelFragment get() {
        return modelFragment;
    }
}
