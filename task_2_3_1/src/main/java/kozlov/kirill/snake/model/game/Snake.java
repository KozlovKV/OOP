package kozlov.kirill.snake.model.game;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Snake {
    private final Point head;
    private final LinkedList<Point> body = new LinkedList<>();
    private Point previousTail;
    @Getter
    private Vector direction;
    private final int fieldWidth;
    private final int fieldHeight;
    private boolean died = false;

    public Snake(
        int initialSize, Point startPoint, Vector initialDirection,
        int fieldWidth, int fieldHeight
    ) {
        if (initialSize < 1) {
            throw new IllegalArgumentException("Snake must have positive initial length");
        }
        head = startPoint.copy();
        direction = initialDirection;
        this.fieldWidth = fieldWidth;
        this.fieldHeight = fieldHeight;
        for (int i = 0; i < initialSize - 1; ++i) {
            move();
            grow();
        }
    }

    public boolean setDirection(Vector newDirection) {
        if (!direction.isCollinear(newDirection)) {
            direction = newDirection;
            return true;
        }
        return false;
    }

    public boolean isDied() {
        return died;
    }

    public Point head() {
        return head;
    }

    public LinkedList<Point> wholeBody() {
        LinkedList<Point> wholeBody = new LinkedList<>();
        wholeBody.add(head.copy());
        for (var point : body) {
            wholeBody.add(point.copy());
        }
        return wholeBody;
    }

    public Point tail() {
        if (body.isEmpty()) {
            return head;
        }
        return body.getLast();
    }

    public int size() {
        return body.size() + 1;
    }

    public void move() {
        body.addFirst(head.copy());
        head.move(direction, 0, fieldWidth - 1, 0, fieldHeight - 1);
        previousTail = tail().copy();
        body.removeLast();
        isStillAlive(null);
    }

    public void grow() {
        body.addLast(previousTail.copy());
        previousTail = null; // TODO: проверить, не создаёт ли это проблем
    }

    public boolean isStillAlive(List<Point> additionalDeadPoints) {
        if (
            head.isInList(body) || head.isInList(additionalDeadPoints)
        ) {
            died = true;
            return false;
        }
        return true;
    }
}
