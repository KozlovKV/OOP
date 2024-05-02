package kozlov.kirill.snake.model.game;

import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;
import java.util.Random;

public class GameModel {
    private static final Logger logger = LogManager.getLogger("model");

    @Getter
    private final int fieldWidth = 20;
    @Getter
    private final int fieldHeight = 20;
    @Getter
    private final int applesCount = 5;

    private static final int INITIAL_SNAKE_SIZE = 1;

    @Getter
    private final LinkedList<Point> snake = new LinkedList<>();
    private boolean died = false;

    @Getter
    @Setter
    private Vector vector = Vector.RIGHT;

    @Getter
    private final LinkedList<Point> apples = new LinkedList<>();

    public GameModel() {
        Point head = new Point(fieldWidth / 2, fieldHeight / 2);
        for (int i = 0; i < INITIAL_SNAKE_SIZE; ++i) {
            snake.addFirst(head.copy());
            head.move(vector);
        }
        for (int i = 0; i < applesCount; ++i) {
            addAppleRandomly();
        }
    }

    public GameModel(int startX, int startY) {
        Point head = new Point(startX, startY);
        Vector invertedVector = vector.getInvertedVector();
        for (int i = 0; i < INITIAL_SNAKE_SIZE; ++i) {
            snake.add(head.copy());
            head.move(invertedVector);
        }
        addAppleRandomly();
    }

    public Point getSnakeHead() {
        return snake.getFirst();
    }

    public Point getSnakeTail() {
        return snake.getLast();
    }

    public void moveSnake() {
        Point newHead = getSnakeHead().copy();
        newHead.move(vector, 0, fieldWidth - 1, 0, fieldHeight - 1);
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
            apple = new Point(random.nextInt(fieldWidth), random.nextInt(fieldHeight));
        } while (
            apple.isInList(snake) || apple.isInList(apples)
        );
        apples.add(apple);
    }
}
