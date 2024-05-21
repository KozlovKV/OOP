package kozlov.kirill.snake.model;

import kozlov.kirill.snake.model.game.GameModel;
import kozlov.kirill.snake.model.game.Point;
import kozlov.kirill.snake.model.game.Vector;
import kozlov.kirill.snake.model.settings.SettingsModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for complex usage of all model fragments.
 */
public class WholeModelTest {
    @Test
    void testGameProcessWithChangedSettings() {
        // To ensure that settings are differ from set below
        SettingsModel settingsModel = Model.SETTINGS.get().restartModel();
        settingsModel.setSettingsPath("testSettings.json");
        settingsModel = Model.SETTINGS.get().restartModel();

        settingsModel.setRandomsCount(0);
        settingsModel.setPredatorsCount(0);
        settingsModel.setApplesCount(1);

        GameModel gameModel = Model.GAME.get().restartModel();
        Assertions.assertEquals(1, gameModel.getApples().list().size());

        Point startPoint = gameModel.getSnake().head().copy();
        Point applePoint = startPoint.copy();
        applePoint.move(Vector.RIGHT);
        gameModel.getApples().list().add(applePoint);
        for (int i = 0; i < 5; ++i) {
            gameModel.update();
            startPoint.move(Vector.RIGHT);
        }
        Assertions.assertEquals(startPoint, gameModel.getSnake().head());
        Assertions.assertEquals(2, gameModel.getSnake().size());
        Assertions.assertEquals(1, gameModel.getScores());
    }
}
