package kozlov.kirill.snake.model.game;

import lombok.Getter;

/**
 * Vectors' extended enum class.
 */
public enum Vector {
    UP(new Point(0, -1)),
    RIGHT(new Point(1, 0)),
    DOWN(new Point(0, 1)),
    LEFT(new Point(-1, 0));

    @Getter
    private final Point direction;

    /**
     * Enum's constructor.
     *
     * @param p point which represents x,y deltas of vector
     */
    Vector(Point p) {
        this.direction = p;
    }

    /**
     * Collinear predicate.
     *
     * @param otherVector vector for checking
     *
     * @return true when two vectors are collinear
     */
    public boolean isCollinear(Vector otherVector) {
        if (this.equals(UP) || this.equals(DOWN)) {
            return otherVector.equals(UP) || otherVector.equals(DOWN);
        }
        return otherVector.equals(LEFT) || otherVector.equals(RIGHT);
    }
}
