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
    private final LinkedList<Point> apples = new LinkedList<>();

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

        apples.clear();
        for (int i = 0; i < currentMaxApplesCount; ++i) {
            addAppleRandomly();
        }
        return this;
    }

    public Integer getScores() {
        return this.snake.size();
    }

    public void update() {
        snake.move();
        int appleIndex = snake.head().getListCollision(apples);
        if (appleIndex != -1) {
            snake.grow();
            apples.remove(appleIndex);
            addAppleRandomly();
        }
    }

    public boolean isGameOver() {
        return snake.isDied();
    }

    private void addAppleRandomly() {
        Random random = new Random();
        Point apple;
        do {
            apple = new Point(random.nextInt(currentFieldWidth), random.nextInt(currentFieldHeight));
        } while (
            apple.equals(snake.head()) || apple.isInList(snake.body()) || apple.isInList(apples)
        );
        apples.add(apple);
    }
}
