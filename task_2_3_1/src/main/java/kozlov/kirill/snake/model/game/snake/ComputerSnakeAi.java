package kozlov.kirill.snake.model.game.snake;

import kozlov.kirill.snake.model.game.GameModel;
import kozlov.kirill.snake.model.game.Vector;
import lombok.Getter;

public abstract class ComputerSnakeAi {
    protected final int UPDATES_FOR_RESPAWN = 10;
    protected int remainingUpdatesForRespawn = UPDATES_FOR_RESPAWN;

    protected final int UPDATES_FOR_MOVING = 1;
    protected int remainingUpdatesForMoving = UPDATES_FOR_MOVING;

    protected final GameModel model;
    @Getter
    protected Snake snake;

    public ComputerSnakeAi(GameModel model) {
        this.model = model;
        respawnSnake();
    }

    private void respawnSnake() {
        this.snake = getNewSnake();
        remainingUpdatesForRespawn = UPDATES_FOR_RESPAWN;
        remainingUpdatesForMoving = UPDATES_FOR_MOVING;
    }

    abstract protected Snake getNewSnake();

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
