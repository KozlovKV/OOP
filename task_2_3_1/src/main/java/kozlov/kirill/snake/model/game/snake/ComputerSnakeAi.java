package kozlov.kirill.snake.model.game.snake;

import kozlov.kirill.snake.model.game.Vector;
import lombok.Getter;

public abstract class ComputerSnakeAi {
    @Getter
    private final Snake snake;

    public ComputerSnakeAi(Snake snake) {
        this.snake = snake;
    }

    abstract protected Vector getNextDirection();

    public void move() {
        snake.setDirection(getNextDirection());
        snake.move();
    }
}
