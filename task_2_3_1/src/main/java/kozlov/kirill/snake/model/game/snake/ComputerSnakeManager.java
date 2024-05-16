package kozlov.kirill.snake.model.game.snake;

import kozlov.kirill.snake.model.game.Field;
import kozlov.kirill.snake.model.game.FieldObject;
import kozlov.kirill.snake.model.game.Point;
import kozlov.kirill.snake.model.game.Vector;

import java.util.ArrayList;
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

    private final ArrayList<ComputerSnakeAi> computerSnakes = new ArrayList<>();

    private final Field field;

    public ComputerSnakeManager(Field field) {
        this.field = field;
        respawnSnake();
    }

    private void respawnSnake() { // TODO: сделать специфичной для конкретной змейки
        computerSnakes.clear();
        // TODO: Сделать более умную логику респеавна
        Random random = new Random();
        List<Point> freePoints = field.getFreeFieldCells();
        int pointIndex = random.nextInt(freePoints.size());
        computerSnakes.add(
            new RandomComputerSnakeAi(
                new Snake(3, freePoints.get(pointIndex), Vector.RIGHT, field)
            )
        );

        remainingUpdatesForRespawn = UPDATES_FOR_RESPAWN;
        remainingUpdatesForMoving = UPDATES_FOR_MOVING;
    }

    public boolean canBeUpdated() {
        if (remainingUpdatesForMoving > 0) {
            remainingUpdatesForMoving--;
            return false;
        }
        remainingUpdatesForMoving = UPDATES_FOR_MOVING;
        for (var snakeAi : computerSnakes) {
            if (snakeAi.getSnake().isDied()) {
                if (remainingUpdatesForRespawn > 0) {
                    remainingUpdatesForRespawn--;
                } else {
                    respawnSnake();
                }
                return false;
            }
        }
        return true;
    }

    public void move() {
        for (var snakeAi : computerSnakes) {
            snakeAi.getSnake().checkAliveStatus();
            if (!canBeUpdated()) {
                return;
            }
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
