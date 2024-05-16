package kozlov.kirill.snake.model.game;

import java.util.Random;

public class RandomComputerSnakeAi extends ComputerSnakeAi {
    public RandomComputerSnakeAi(Snake snake) {
        super(snake);
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
