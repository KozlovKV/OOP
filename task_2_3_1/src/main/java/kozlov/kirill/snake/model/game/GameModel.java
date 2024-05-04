package kozlov.kirill.snake.model.game;

import kozlov.kirill.snake.model.Model;
import kozlov.kirill.snake.model.ModelFragment;
import kozlov.kirill.snake.model.settings.SettingsModel;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;
import java.util.Random;

public class GameModel implements ModelFragment {
    private static final Logger logger = LogManager.getLogger("model");

    @Getter
    private int currentFieldWidth;
    @Getter
    private int currentFieldHeight;
    @Getter
    private int currentMaxApplesCount;

    private static final int INITIAL_SNAKE_SIZE = 1;

    @Getter
    private Snake snake;

    @Getter
    private ApplesList apples;

    @Override
    public GameModel restartModel() {
        var settingsModel = (SettingsModel) Model.SETTINGS.get();
        currentFieldWidth = settingsModel.getFieldWidth();
        currentFieldHeight = settingsModel.getFieldHeight();
        currentMaxApplesCount = settingsModel.getApplesCount();

        snake = new Snake(
            INITIAL_SNAKE_SIZE,
            new Point(currentFieldWidth / 2, currentFieldHeight / 2),
            Vector.RIGHT, currentFieldWidth, currentFieldHeight
        );

        apples = new ApplesList(
            currentMaxApplesCount, currentFieldWidth, currentFieldHeight,
            snake.wholeBody()
        );

        return this;
    }

    public Integer getScores() {
        return this.snake.size();
    }

    public void update() {
        snake.move();
        if (apples.checkSnakeGrowing(snake)) {
            apples.addRandomly(snake.wholeBody());
        }
    }

    public boolean isGameOver() {
        return snake.isDied();
    }
}
