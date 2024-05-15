package kozlov.kirill.snake.model.game;

import java.util.ArrayList;
import java.util.List;
import kozlov.kirill.snake.model.Model;
import kozlov.kirill.snake.model.ModelFragment;
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
    private int currentFieldWidth;
    @Getter
    private int currentFieldHeight;

    private final List<Point> fieldPoints = new ArrayList<>();

    private static final int INITIAL_SNAKE_SIZE = 1;
    @Getter
    private Snake snake;

    @Getter
    private ComputerSnakeManager snakeManager;

    @Getter
    private ApplesList apples;

    @Override
    public GameModel restartModel() {
        var settingsModel = (SettingsModel) Model.SETTINGS.get();
        currentFieldWidth = settingsModel.getFieldWidth();
        currentFieldHeight = settingsModel.getFieldHeight();

        constructFieldPoints();

        snake = new Snake(
            INITIAL_SNAKE_SIZE,
            new Point(currentFieldWidth / 2, currentFieldHeight / 2),
            Vector.RIGHT, this
        );

        apples = new ApplesList(this);
        apples.addRandomly(settingsModel.getApplesCount());

        snakeManager = new ComputerSnakeManager(this);

        return this;
    }

    /**
     * Creates field's list with all points.
     */
    private void constructFieldPoints() {
        fieldPoints.clear();
        for (int x = 0; x < currentFieldWidth; ++x) {
            for (int y = 0; y < currentFieldHeight; ++y) {
                fieldPoints.add(new Point(x, y));
            }
        }
    }

    /**
     * Copy-getter for field's list.
     *
     * @return field's list with points' copies
     */
    private List<Point> getFieldPointsCopy() {
        List<Point> copy = new ArrayList<>();
        for (var point : fieldPoints) {
            copy.add(point.copy());
        }
        return copy;
    }

    /**
     * Non-killing points getter.
     *
     * @param snake snake for which returned points will be safe
     * @return points' list which won't kill snake by collision
     */
    public List<Point> getNonKillingCells(Snake snake) {
        List<Point> fieldCopy = getFieldPointsCopy();
//        fieldCopy.removeAll(snake.body());
        fieldCopy.removeAll(this.snake.body());
        // TODO: придумать хорошее решение для получения данных о ещё неинициализированных змейках
//        fieldCopy.removeAll(this.snakeManager.getSnake().body());
        return fieldCopy;
    }

    /**
     * Free points getter.
     *
     * @return points' list which can be used for placing some objects
     */
    public List<Point> getFreeFieldCells() {
        List<Point> fieldCopy = getFieldPointsCopy();
        fieldCopy.removeAll(snake.wholeBody());
        fieldCopy.removeAll(apples.list());
        return fieldCopy;
    }

    /**
     * Scores' getter.
     *
     * @return current game scores which now is equivalent to snake size
     */
    public Integer getScores() {
        // TODO: Сделать независимый от длины подсчёт очков
        return this.snake.size();
    }

    /**
     * Update function.
     * <br>
     * Performs all action which must happen during game iteration
     */
    public void update() {
        snake.move();
        if (apples.checkSnakeGrowing(snake)) {
            apples.addRandomly();
        }
        snakeManager.move();
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
