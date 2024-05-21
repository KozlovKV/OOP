package kozlov.kirill.snake.model.game.snake;

import java.util.List;
import java.util.Random;
import kozlov.kirill.snake.model.game.Field;
import kozlov.kirill.snake.model.game.Point;
import kozlov.kirill.snake.model.game.Vector;
import lombok.Getter;

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

    /**
     * Constructor.
     * <br>
     * Binds game object with new computer snake and spawns it
     *
     * @param field game field
     * @param playerSnake player snake
     */
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

    /**
     * New snake getter.
     *
     * @param spawnPoint point for snake's spawn
     * @return new snake which will be controlled by AI
     */
    abstract protected Snake getNewSnake(Point spawnPoint);

    /**
     * Next direction getter.
     *
     * @return next direction which is chosen by current strategy
     */
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

    /**
     * Moving method.
     * <br>
     * Check aliveness, possibly changes direction and moves controlled snake
     */
    public void move() {
        snake.checkAliveStatus();
        if (!canBeUpdated()) {
            return;
        }
        snake.setDirection(getNextDirection());
        snake.move();
    }
}
