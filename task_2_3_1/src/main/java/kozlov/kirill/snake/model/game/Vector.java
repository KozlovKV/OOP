package kozlov.kirill.snake.model.game;

import lombok.Getter;
import lombok.NonNull;

public enum Vector {
    UP(new Point(0, -1)),
    RIGHT(new Point(1, 0)),
    DOWN(new Point(0, 1)),
    LEFT(new Point(-1, 0));

    @Getter
    private final Point direction;

    Vector(Point p) {
        this.direction = p;
    }

    @NonNull
    public Vector getInvertedVector() {
        switch (this) {
            case UP -> {
                return DOWN;
            }
            case DOWN -> {
                return UP;
            }
            case LEFT -> {
                return RIGHT;
            }
            case RIGHT -> {
                return LEFT;
            }
        }
        return null;
    }
}
