package kozlov.kirill.snake.model.game;

import lombok.Getter;

import java.util.List;
import java.util.Random;

/**
 * Manager for computer controlled snakes.
 */
public class ComputerSnakeManager implements FieldObject {
    @Getter
    private Snake snake;

    private final Field field;

    public ComputerSnakeManager(Field field) {
        this.field = field;
        respawnSnake();
    }

    private void respawnSnake() {
        // TODO: Сделать более умную логику респеавна
        Random random = new Random();
        List<Point> freePoints = field.getFreeFieldCells();
        int pointIndex = random.nextInt(freePoints.size());
        // TODO: Сделать рост змейки до определённого уровня постепенным (как вариант, дать ей возможность есть, но поставить верхнюю границу роста)
        snake = new Snake(3, freePoints.get(pointIndex), Vector.RIGHT, field);
    }

    private static final int UPDATES_FOR_RESPAWN = 10;
    private int remainingUpdatesForRespawn = UPDATES_FOR_RESPAWN;

    private static final int UPDATES_FOR_MOVING = 2;
    private int remainingUpdatesForMoving = UPDATES_FOR_MOVING;

    public void move() {
        if (snake.isDied()) {
            if (remainingUpdatesForRespawn > 0) {
                remainingUpdatesForRespawn--;
            } else {
                respawnSnake();
                remainingUpdatesForRespawn = UPDATES_FOR_RESPAWN;
            }
            return;
        }
        if (remainingUpdatesForMoving > 0) {
            remainingUpdatesForMoving--;
            return;
        } else {
            remainingUpdatesForMoving = UPDATES_FOR_MOVING;
        }
        int percent = new Random().nextInt(100);
        if (percent < 10) {
            snake.setDirection(Vector.RIGHT);
        } else if (percent < 20) {
            snake.setDirection(Vector.UP);
        } else if (percent < 30) {
            snake.setDirection(Vector.LEFT);
        } else if (percent < 40) {
            snake.setDirection(Vector.DOWN);
        }
        snake.move();
    }

    @Override
    public List<Point> getOccupiedCells() {
        return snake.getOccupiedCells();
    }

    @Override
    public List<Point> getKillingCells() {
        return snake.getKillingCells();
    }
}
