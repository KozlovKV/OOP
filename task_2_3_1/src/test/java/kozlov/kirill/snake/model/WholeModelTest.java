package kozlov.kirill.snake.model;

import kozlov.kirill.snake.model.game.GameModel;
import kozlov.kirill.snake.model.game.Point;
import kozlov.kirill.snake.model.settings.SettingsModel;
import org.apache.logging.log4j.core.util.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class WholeModelTest {
    @Test
    void testGameProcessWithChangedSettings() {
        // To ensure that settings are differ from set below
        SettingsModel settingsModel = Model.SETTINGS.get().restartModel();
        settingsModel.setSettingsPath("testSettings.json");
        settingsModel = Model.SETTINGS.get().restartModel();

        settingsModel.setFieldWidth(5);
        settingsModel.setFieldHeight(5);
        settingsModel.setApplesCount(1);

        GameModel gameModel = Model.GAME.get().restartModel();
        Assertions.assertEquals(1, gameModel.getApples().list().size());

        Point startPoint = gameModel.getSnake().head().copy();
        for (int i = 0; i < 5; ++i) {
            gameModel.update();
        }
        Assertions.assertEquals(startPoint, gameModel.getSnake().head());
    }
}
