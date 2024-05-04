package kozlov.kirill.snake.model.game;

import kozlov.kirill.snake.model.Model;
import kozlov.kirill.snake.model.ModelFragment;
import kozlov.kirill.snake.model.settings.SettingsModel;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

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
    private int currentMaxApplesCount;
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

        currentMaxApplesCount = settingsModel.getApplesCount();
        apples = new ApplesList(this);
        apples.addRandomly(currentMaxApplesCount);

        return this;
    }

    private void constructFieldPoints() {
        fieldPoints.clear();
        for (int x = 0; x < currentFieldWidth; ++x) {
            for (int y = 0; y < currentFieldHeight; ++y) {
                fieldPoints.add(new Point(x, y));
            }
        }
    }

    private List<Point> getFieldPointsCopy() {
        List<Point> copy = new ArrayList<>();
        for (var point : fieldPoints) {
            copy.add(point.copy());
        }
        return copy;
    }

    public List<Point> getNonKillingCells(Snake snake) {
        List<Point> fieldCopy = getFieldPointsCopy();
        fieldCopy.removeAll(snake.body());
        return fieldCopy;
    }

    public List<Point> getFreeFieldCells() {
        List<Point> fieldCopy = getFieldPointsCopy();
        fieldCopy.removeAll(snake.wholeBody());
        fieldCopy.removeAll(apples.list());
        return fieldCopy;
    }

    public Integer getScores() {
        return this.snake.size();
    }

    public void update() {
        snake.move();
        if (apples.checkSnakeGrowing(snake)) {
            apples.addRandomly();
        }
    }

    public boolean isGameOver() {
        return snake.isDied();
    }
}
