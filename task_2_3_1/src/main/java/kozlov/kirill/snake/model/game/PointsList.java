package kozlov.kirill.snake.model.game;

import java.util.LinkedList;

public class PointsList {
    private final LinkedList<Point> points = new LinkedList<>();

    void addFirst(Point point) {
        points.addFirst(point);
    }

    void addLast(Point point) {
        points.addLast(point);
    }

    Point removeFirst() {
        return points.removeFirst();
    }

    Point removeLast() {
        return points.removeLast();
    }


}
