package kozlov.kirill.snake.model.game.snake;

import kozlov.kirill.snake.model.game.GameModel;
import kozlov.kirill.snake.model.game.Point;
import kozlov.kirill.snake.model.game.Vector;

import java.util.List;
import java.util.Random;

public class RandomComputerSnakeAi extends ComputerSnakeAi {
    public RandomComputerSnakeAi(GameModel model) {
        super(model);
    }

    @Override
    protected Snake getNewSnake() {
        // TODO: Сделать более умную логику респеавна
        Random random = new Random();
        List<Point> freePoints = model.getField().getFreeFieldCells();
        int pointIndex = random.nextInt(freePoints.size());
        return new Snake(3, freePoints.get(pointIndex), Vector.RIGHT, model.getField());
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
