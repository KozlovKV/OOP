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

    public boolean isCollinear(Vector otherVector) {
        if (this.equals(UP) || this.equals(DOWN)) {
            return otherVector.equals(UP) || otherVector.equals(DOWN);
        }
        return otherVector.equals(LEFT) || otherVector.equals(RIGHT);
    }
}
