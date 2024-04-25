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

    private static final int INITIAL_SNAKE_SIZE = 4;

    @Getter
    private final LinkedList<Point> snake = new LinkedList<>();

    @Getter
    @Setter
    private Vector vector = Vector.RIGHT;

    @Getter
    private Point apple;

    public GameModel() {
        Point head = new Point(fieldWidth / 2, fieldHeight / 2);
        for (int i = 0; i < INITIAL_SNAKE_SIZE; ++i) {
            snake.addFirst(head.copy());
            head.move(vector);
        }
        updateApple();
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
        if (newHead.equals(apple)) {
            updateApple();
            logger.info("Snake grew up");
        } else {
            snake.removeLast();
        }
    }

    private void updateApple() {
        Random random = new Random();
        do {
            apple = new Point(random.nextInt(fieldWidth), random.nextInt(fieldHeight));
        } while (snake.stream().anyMatch(point -> point.equals(apple)));
    }
}
