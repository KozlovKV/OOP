package kozlov.kirill.snake.model.game;

import kozlov.kirill.snake.model.Model;
import kozlov.kirill.snake.model.settings.SettingsModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class PredatorTest {
    @BeforeAll
    static void setSettings() {
        SettingsModel settingsModel = (SettingsModel) Model.SETTINGS.get();
        settingsModel.setPredatorsCount(1);
        settingsModel.setRandomsCount(0);
    }

    @Test
    void testPlayerKilling() {
        GameModel gameModel = Model.GAME.get().restartModel();
        for (int i = 0; i < 150; ++i) {
            gameModel.update();
            if (gameModel.isGameOver()) {
                break;
            }
        }
        Assertions.assertTrue(gameModel.isGameOver());
    }
}
