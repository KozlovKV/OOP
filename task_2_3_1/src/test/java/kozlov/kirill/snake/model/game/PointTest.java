package kozlov.kirill.snake.model.game;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PointTest {
    @Test
    void testMinXBorder() {
        Point point = new Point(0, 1);
        point.move(Vector.LEFT, 0, 1, 0, 1);
        Assertions.assertEquals(new Point(1, 1), point);
    }

    @Test
    void testMaxXBorder() {
        Point point = new Point(1, 1);
        point.move(Vector.RIGHT, 0, 1, 0, 1);
        Assertions.assertEquals(new Point(0, 1), point);
    }

    @Test
    void testMinYBorder() {
        Point point = new Point(1, 0);
        point.move(Vector.UP, 0, 1, 0, 1);
        Assertions.assertEquals(new Point(1, 1), point);
    }

    @Test
    void testMaxYBorder() {
        Point point = new Point(1, 1);
        point.move(Vector.DOWN, 0, 1, 0, 1);
        Assertions.assertEquals(new Point(1, 0), point);
    }
}
