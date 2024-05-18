package kozlov.kirill.snake.model.game.snake;

import kozlov.kirill.snake.model.game.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Manager for computer controlled snakes.
 */
public class ComputerSnakeManager implements FieldObject {

    private final ArrayList<ComputerSnakeAi> computerSnakes = new ArrayList<>();

    public ComputerSnakeManager(GameModel model) {
        computerSnakes.add(
            new RandomComputerSnakeAi(model)
        );
    }

    public void move() {
        for (var snakeAi : computerSnakes) {
            snakeAi.move();
        }
    }

    @Override
    public List<Point> getOccupiedCells() {
        List<Point> occupiedCells = new ArrayList<>();
        for (var snakeAi : computerSnakes) {
            occupiedCells.addAll(snakeAi.getSnake().getOccupiedCells());
        }
        return occupiedCells;
    }

    @Override
    public List<Point> getKillingCells() {
        List<Point> killingsCells = new ArrayList<>();
        for (var snakeAi : computerSnakes) {
            killingsCells.addAll(snakeAi.getSnake().getKillingCells());
        }
        return killingsCells;
    }

    public ArrayList<Snake> getSnakes() {
        ArrayList<Snake> snakes = new ArrayList<>();
        for (var snakeAi : computerSnakes) {
            snakes.add(snakeAi.getSnake());
        }
        return snakes;
    }
}
