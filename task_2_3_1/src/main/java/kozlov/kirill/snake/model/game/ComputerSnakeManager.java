package kozlov.kirill.snake.model.game;

import lombok.Getter;

import java.util.List;
import java.util.Random;

/**
 * Manager for computer controlled snakes.
 */
public class ComputerSnakeManager implements FieldObject {
    private static final int UPDATES_FOR_RESPAWN = 10;
    private int remainingUpdatesForRespawn = UPDATES_FOR_RESPAWN;

    private static final int UPDATES_FOR_MOVING = 2;
    private int remainingUpdatesForMoving = UPDATES_FOR_MOVING;

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
        snake = new Snake(3, freePoints.get(pointIndex), Vector.RIGHT, field);

        remainingUpdatesForRespawn = UPDATES_FOR_RESPAWN;
        remainingUpdatesForMoving = UPDATES_FOR_MOVING;
    }

    public boolean canBeUpdated() {
        if (snake.isDied()) {
            if (remainingUpdatesForRespawn > 0) {
                remainingUpdatesForRespawn--;
            } else {
                respawnSnake();
            }
            return false;
        }
        if (remainingUpdatesForMoving > 0) {
            remainingUpdatesForMoving--;
            return false;
        }
        remainingUpdatesForMoving = UPDATES_FOR_MOVING;
        return true;
    }

    public void move() {
        // TODO: Счётчик для замедления - штука сомнительная, потому что когда змейка не двигается, она не может умереть. Необходимо либо придумать другой вариант замедления, либо разделить логику движения и смерти (второе лучше)
        if (!canBeUpdated()) {
            return;
        }
        // TODO: можно перенести движение змейки вперёд выбора направления, что добавит её эдакую "инерцию"
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
