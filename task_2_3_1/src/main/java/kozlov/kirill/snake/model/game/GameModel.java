package kozlov.kirill.snake.model.game;

import kozlov.kirill.snake.model.Model;
import kozlov.kirill.snake.model.ModelFragment;
import kozlov.kirill.snake.model.game.snake.ComputerSnakeManager;
import kozlov.kirill.snake.model.game.snake.Snake;
import kozlov.kirill.snake.model.settings.SettingsModel;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Model fragment for game.
 */
public class GameModel implements ModelFragment {
    private static final Logger logger = LogManager.getLogger("model");

    @Getter
    private Field field;

    private static final int INITIAL_SNAKE_SIZE = 1;
    @Getter
    private Snake snake;

    @Getter
    private ComputerSnakeManager snakeManager;

    @Getter
    private ApplesList apples;

    @Getter
    private Integer scores;

    @Override
    public GameModel restartModel() {
        var settingsModel = (SettingsModel) Model.SETTINGS.get();

        scores = 0;

        field = new Field(
            settingsModel.getFieldWidth(),
            settingsModel.getFieldHeight()
        );

        snake = new Snake(
            INITIAL_SNAKE_SIZE,
            new Point(field.getWidth() / 2, field.getHeight() / 2),
            Vector.RIGHT, field
        );
        field.handleObject(snake);

        apples = new ApplesList(field);
        apples.addRandomly(settingsModel.getApplesCount());
        field.handleObject(apples);

        snakeManager = new ComputerSnakeManager(this);
        field.handleObject(snakeManager);

        return this;
    }

    /**
     * Update function.
     * <br>
     * Performs all action which must happen during game iteration
     */
    public void update() {
        snakeManager.move();
        snake.move();
        if (apples.checkSnakeGrowing(snake)) {
            apples.addRandomly();
            scores++;
        }
    }

    /**
     * Game over predicate.
     *
     * @return true when game is over (now it's equivalent to main snake death)
     */
    public boolean isGameOver() {
        return snake.isDied();
    }
}
