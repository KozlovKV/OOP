package kozlov.kirill.snake.model.game;

import lombok.Getter;

import java.util.List;
import java.util.Random;

/**
 * Manager for computer controlled snakes.
 */
public class ComputerSnakeManager {
    @Getter
    private Snake snake;

    private final GameModel gameModel;

    public ComputerSnakeManager(GameModel gameModel) {
        this.gameModel = gameModel;
        respawnSnake();
    }

    private void respawnSnake() {
        // TODO: Сделать более умную логику респеавна
        Random random = new Random();
        List<Point> freePoints = gameModel.getFreeFieldCells();
        int pointIndex = random.nextInt(freePoints.size());
        // TODO: Сделать рост змейки до определённого уровня постепенным (как вариант, дать ей возможность есть, но поставить верхнюю границу роста)
        snake = new Snake(8, freePoints.get(pointIndex), Vector.RIGHT, gameModel);
    }

    private static final int UPDATES_FOR_RESPAWN = 10;
    private int remainingUpdatesForRespawn = UPDATES_FOR_RESPAWN;

    public void move() {
        if (snake.isDied()) {
            if (remainingUpdatesForRespawn > 0) {
                remainingUpdatesForRespawn--;
            } else {
                respawnSnake();
                remainingUpdatesForRespawn = UPDATES_FOR_RESPAWN;
            }
            return;
        }
        int percent = new Random().nextInt(100);
        if (percent < 10) {
            snake.setDirection(Vector.RIGHT);
        } else if (percent < 20) {
            snake.setDirection(Vector.UP);
        } else if (percent < 30) {
            snake.setDirection(Vector.LEFT);
        } else if (percent < 40) {
            snake.setDirection(Vector.DOWN);
        }
        snake.move();
    }
}
