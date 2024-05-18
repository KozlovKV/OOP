package kozlov.kirill.snake.model.game.snake;

import kozlov.kirill.snake.model.game.Field;
import kozlov.kirill.snake.model.game.GameModel;
import kozlov.kirill.snake.model.game.Point;
import kozlov.kirill.snake.model.game.Vector;
import lombok.Getter;

import java.util.List;
import java.util.Random;

/**
 * Snake AI class.
 * <br>
 * Responsible for:
 * <ul>
 *     <li>Snake (re)spawn</li>
 *     <li>Moving cooldowns</li>
 *     <li>Direction choosing</li>
 * </ul>
 */
public abstract class ComputerSnakeAi {
    protected int UPDATES_FOR_RESPAWN = 10;
    protected int remainingUpdatesForRespawn = UPDATES_FOR_RESPAWN;

    protected int UPDATES_FOR_MOVING = 1;
    protected int remainingUpdatesForMoving = UPDATES_FOR_MOVING;

    protected final Snake playerSnake;
    protected final Field field;
    @Getter
    protected Snake snake;

    public ComputerSnakeAi(Field field, Snake playerSnake) {
        this.field = field;
        this.playerSnake = playerSnake;
        respawnSnake();
    }

    private static final int MINIMAL_DISTANCE_FROM_PLAYER_HEAD = 10;

    private Point getSpawnPoint() {
        Random random = new Random();
        List<Point> freePoints = field.getFreeFieldCells(
            playerSnake.head(), MINIMAL_DISTANCE_FROM_PLAYER_HEAD
        );
        int pointIndex = random.nextInt(freePoints.size());
        return freePoints.get(pointIndex);
    }

    private void respawnSnake() {
        this.snake = getNewSnake(getSpawnPoint());
        remainingUpdatesForRespawn = UPDATES_FOR_RESPAWN;
        remainingUpdatesForMoving = UPDATES_FOR_MOVING;
    }

    abstract protected Snake getNewSnake(Point spawnPoint);

    abstract protected Vector getNextDirection();

    private boolean canBeUpdated() {
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
        snake.checkAliveStatus();
        if (!canBeUpdated()) {
            return;
        }
        snake.setDirection(getNextDirection());
        snake.move();
    }
}
