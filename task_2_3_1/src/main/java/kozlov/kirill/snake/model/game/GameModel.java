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
    private final LinkedList<Point> snake = new LinkedList<>();
    private boolean died = false;

    @Getter
    @Setter
    private Vector vector = Vector.RIGHT;

    @Getter
    private final LinkedList<Point> apples = new LinkedList<>();

    @Override
    public GameModel restartModel() {
        var settingsModel = (SettingsModel) Model.SETTINGS.get();
        currentFieldWidth = settingsModel.getFieldWidth();
        currentFieldHeight = settingsModel.getFieldHeight();
        currentMaxApplesCount = settingsModel.getApplesCount();

        snake.clear();
        apples.clear();
        died = false;
        vector = Vector.RIGHT;

        Point head = new Point(currentFieldWidth / 2, currentFieldHeight / 2);
        for (int i = 0; i < INITIAL_SNAKE_SIZE; ++i) {
            snake.addFirst(head.copy());
            head.move(vector);
        }
        for (int i = 0; i < currentMaxApplesCount; ++i) {
            addAppleRandomly();
        }
        return this;
    }

    public Point getSnakeHead() {
        return snake.getFirst();
    }

    public Point getSnakeTail() {
        return snake.getLast();
    }

    public Integer getScores() {
        return this.snake.size();
    }

    public void moveSnake() {
        Point newHead = getSnakeHead().copy();
        newHead.move(vector, 0, currentFieldWidth - 1, 0, currentFieldHeight - 1);
        snake.addFirst(newHead);
        int appleIndex = getAteApple();
        if (appleIndex > -1) {
            apples.remove(appleIndex);
            addAppleRandomly();
            logger.info("Snake grew up"); // TODO: Хорошо бы вынести рост в отдельный метод
        } else {
            snake.removeLast();
        }
        checkSnakeLives();
    }

    private void checkSnakeLives() {
        Point head = getSnakeHead();
        for (int i = 1; i < snake.size(); ++i) {
            if (head.equals(snake.get(i))) {
                died = true;
                return;
            }
        }
    }

    public boolean isDied() {
        return died;
    }

    private int getAteApple() {
        Point head = getSnakeHead();
        int i = 0;
        for (var apple : apples) {
            if (apple.equals(head)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    private void addAppleRandomly() {
        Random random = new Random();
        Point apple;
        do {
            apple = new Point(random.nextInt(currentFieldWidth), random.nextInt(currentFieldHeight));
        } while (
            apple.isInList(snake) || apple.isInList(apples)
        );
        apples.add(apple);
    }
}
