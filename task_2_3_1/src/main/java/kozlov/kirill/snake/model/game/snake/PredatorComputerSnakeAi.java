package kozlov.kirill.snake.model.game.snake;

import java.util.List;
import kozlov.kirill.snake.model.game.Field;
import kozlov.kirill.snake.model.game.Point;
import kozlov.kirill.snake.model.game.Vector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Predator computer snake AI.
 * <br>
 * Follow player snake with some prediction
 */
public class PredatorComputerSnakeAi extends ComputerSnakeAi {
    private final Logger logger = LogManager.getLogger("model");

    /**
     * Predator snake constructor.
     * <br>
     * Literally ComputerSnakeAi constructor
     *
     * @param field game field
     * @param playerSnake player snake
     */
    public PredatorComputerSnakeAi(Field field, Snake playerSnake) {
        super(field, playerSnake);
    }

    @Override
    protected Snake getNewSnake(Point spawnPoint) {
        return new Snake(8, spawnPoint, Vector.RIGHT, field);
    }

    private static final int PREDICTION_STEPS = 3;

    @Override
    protected Vector getNextDirection() {
        Point predictedPlayerHead = playerSnake.head().copy();
        for (int i = 0; i < PREDICTION_STEPS; ++i) {
            predictedPlayerHead.move(
                playerSnake.getDirection(),
                0, field.getWidth() - 1,
                0, field.getHeight() - 1
            );
        }

        Vector nextDirection = Vector.ZERO;
        double distanceToPlayer = Double.MAX_VALUE;
        List<Point> freePoints = field.getFreeFieldCells();
        for (var vector : Vector.values()) {
            Point nextPoint = snake.head().copy();
            nextPoint.move(
                vector,
                0, field.getWidth() - 1,
                0, field.getHeight() - 1
            );
            double nextDistanceToPlayer = nextPoint.distance(predictedPlayerHead);
            if (
                nextPoint.isInList(freePoints)
                && nextDistanceToPlayer < distanceToPlayer
            ) {
                nextDirection = vector;
                distanceToPlayer = nextDistanceToPlayer;
                logger.info(
                    "Change direction to {}. Distance for player is {}",
                    nextDirection, nextDistanceToPlayer
                );
            }
        }
        return nextDirection;
    }
}
