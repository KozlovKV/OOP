package kozlov.kirill.snake.model.game.snake;

import kozlov.kirill.snake.model.game.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Manager for computer controlled snakes.
 */
public class ComputerSnakeManager implements FieldObject {
    @Getter
    private final ArrayList<ComputerSnakeAi> computerSnakes = new ArrayList<>();

    /**
     * Computer snakes constructor.
     * <br>
     * Creates AI snakes and gives them game data
     *
     * @param randomsCount count of chaotic enemies
     * @param predatorsCount count of predators
     * @param field game field
     * @param playerSnake player snake
     */
    public ComputerSnakeManager(
        int randomsCount, int predatorsCount,
        Field field, Snake playerSnake
    ) {
        field.handleObject(this);
        for (int i = 0; i < randomsCount; ++i) {
            computerSnakes.add(
                new RandomComputerSnakeAi(field, playerSnake)
            );
        }
        for (int i = 0; i < predatorsCount; ++i) {
            computerSnakes.add(
                new PredatorComputerSnakeAi(field, playerSnake)
            );
        }
    }

    /**
     * Calls move method for all computer snakes.
     */
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
}
