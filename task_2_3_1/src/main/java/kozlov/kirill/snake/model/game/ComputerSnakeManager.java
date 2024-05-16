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

    private static final int UPDATES_FOR_MOVING = 1;
    private int remainingUpdatesForMoving = UPDATES_FOR_MOVING;

    @Getter
    private ComputerSnakeAi snakeAi;

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
        snakeAi = new RandomComputerSnakeAi(
            new Snake(3, freePoints.get(pointIndex), Vector.RIGHT, field)
        );

        remainingUpdatesForRespawn = UPDATES_FOR_RESPAWN;
        remainingUpdatesForMoving = UPDATES_FOR_MOVING;
    }

    public boolean canBeUpdated() {
        // TODO: Как-то не очень красиво получать из ИИ змейки саму змейку, но пока что пусть будет так
        if (snakeAi.getSnake().isDied()) {
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
        snakeAi.getSnake().checkAliveStatus();
        if (!canBeUpdated()) {
            return;
        }
        // TODO: можно перенести движение змейки вперёд выбора направления, что добавит её эдакую "инерцию"
        snakeAi.move();
    }

    @Override
    public List<Point> getOccupiedCells() {
        return snakeAi.getSnake().getOccupiedCells();
    }

    @Override
    public List<Point> getKillingCells() {
        return snakeAi.getSnake().getKillingCells();
    }
}
