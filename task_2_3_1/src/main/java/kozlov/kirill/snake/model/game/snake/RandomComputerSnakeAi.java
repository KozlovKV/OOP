package kozlov.kirill.snake.model.game.snake;

import java.util.Random;

import kozlov.kirill.snake.ExcludeClassFromJacocoGeneratedReport;
import kozlov.kirill.snake.model.game.Field;
import kozlov.kirill.snake.model.game.Point;
import kozlov.kirill.snake.model.game.Vector;

/**
 * Random computer snake AI.
 * <br>
 * Moves ABSOLUTELY chaotically
 */
public class RandomComputerSnakeAi extends ComputerSnakeAi {
    /**
     * Random snake constructor.
     * <br>
     * Literally ComputerSnakeAi constructor
     *
     * @param field game field
     * @param playerSnake player snake
     */
    public RandomComputerSnakeAi(Field field, Snake playerSnake) {
        super(field, playerSnake);
    }

    @Override
    protected Snake getNewSnake(Point spawnPoint) {
        return new Snake(6, spawnPoint, Vector.RIGHT, field);
    }

    @Override
    protected Vector getNextDirection() {
        int percent = new Random().nextInt(100);
        if (percent < 10) {
            return Vector.RIGHT;
        } else if (percent < 20) {
            return Vector.UP;
        } else if (percent < 30) {
            return Vector.LEFT;
        } else if (percent < 40) {
            return Vector.DOWN;
        }
        return Vector.ZERO;
    }
}
