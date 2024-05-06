package kozlov.kirill.snake.model.game;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Additional tests for Point class.
 */
public class PointTest {
    @Test
    void testMinXborder() {
        Point point = new Point(0, 1);
        point.move(Vector.LEFT, 0, 1, 0, 1);
        Assertions.assertEquals(new Point(1, 1), point);
    }

    @Test
    void testMaxXborder() {
        Point point = new Point(1, 1);
        point.move(Vector.RIGHT, 0, 1, 0, 1);
        Assertions.assertEquals(new Point(0, 1), point);
    }

    @Test
    void testMinYborder() {
        Point point = new Point(1, 0);
        point.move(Vector.UP, 0, 1, 0, 1);
        Assertions.assertEquals(new Point(1, 1), point);
    }

    @Test
    void testMaxYborder() {
        Point point = new Point(1, 1);
        point.move(Vector.DOWN, 0, 1, 0, 1);
        Assertions.assertEquals(new Point(1, 0), point);
    }
}
