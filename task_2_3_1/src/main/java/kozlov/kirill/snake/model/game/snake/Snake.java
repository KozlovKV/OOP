package kozlov.kirill.snake.model.game.snake;

import java.util.LinkedList;
import java.util.List;

import kozlov.kirill.snake.model.game.Field;
import kozlov.kirill.snake.model.game.FieldObject;
import kozlov.kirill.snake.model.game.Point;
import kozlov.kirill.snake.model.game.Vector;
import lombok.Getter;

/**
 * Snake class.
 */
public class Snake implements FieldObject {
    private final Point head;
    private final LinkedList<Point> body = new LinkedList<>();
    private Point previousTail;
    @Getter
    private Vector direction;
    @Getter
    private boolean died = false;
    private final Field field;

    /**
     * Constructor.
     * <br>
     * To create snake with specified size, `move()` and `grow()` will be called
     * `initialSize` times so for size greater than one `startPoint` is a point of tail
     *
     * @param initialSize initial snake size
     * @param startPoint point where snake will be placed
     * @param initialDirection direction vector
     * @param field game field
     */
    public Snake(
        int initialSize, Point startPoint, Vector initialDirection,
        Field field
    ) {
        if (initialSize < 1) {
            throw new IllegalArgumentException("Snake must have positive initial length");
        }
        this.field = field;

        head = startPoint.copy();
        direction = initialDirection;
        for (int i = 0; i < initialSize - 1; ++i) {
            move();
            grow();
        }
    }

    /**
     * Direction safe setter.
     * <br>
     * Change direction only when new and current one isn't collinear
     *
     * @param newDirection new direction
     *
     * @return true when direction was changed
     */
    public boolean setDirection(Vector newDirection) {
        if (!direction.isCollinear(newDirection)) {
            direction = newDirection;
            return true;
        }
        return false;
    }

    /**
     * Head getter.
     *
     * @return head point link
     */
    public Point head() {
        return head;
    }

    /**
     * Body getter.
     * <br>
     * Body list contains all snake's cells excluding head
     *
     * @return body list link
     */
    public LinkedList<Point> body() {
        return body;
    }

    @Override
    public List<Point> getKillingCells() {
        return body();
    }

    /**
     * Whole body copy-getter.
     * <br>
     * Whole body is a combined head and body
     *
     * @return list with copies of all snake's points
     */
    public LinkedList<Point> wholeBody() {
        LinkedList<Point> wholeBody = new LinkedList<>();
        wholeBody.add(head.copy());
        for (var point : body) {
            wholeBody.add(point.copy());
        }
        return wholeBody;
    }

    @Override
    public List<Point> getOccupiedCells() {
        return wholeBody();
    }

    /**
     * Tail getter.
     *
     * @return the last snake's cell
     */
    public Point tail() {
        if (body.isEmpty()) {
            return head;
        }
        return body.getLast();
    }

    /**
     * Full size getter.
     *
     * @return full snake size which is equivalent to body size + 1
     */
    public int size() {
        return body.size() + 1;
    }

    /**
     * Moving function.
     * <br>
     * Moves snake by internal direction, removes old tail and checks is snake still alive
     */
    public void move() {
        if (died) {
            return;
        }
        body.addFirst(head.copy());
        head.move(
            direction,
            0, field.getWidth() - 1,
            0, field.getHeight() - 1
        );
        previousTail = tail().copy();
        body.removeLast();
        checkAliveStatus();
    }

    /**
     * Growing function.
     * <br>
     * Adds one cell to end of body. This cell is a previous tail point
     * so grow method should be called only after move method invoking
     */
    public void grow() {
        body.addLast(previousTail.copy());
        previousTail = null;
    }

    /**
     * Still alive checker.
     * <br>
     * Use for checking non-killing points' list provided by game field
     */
    public void checkAliveStatus() {
        if (head.isInList(field.getNonKillingCells())) {
            return;
        }
        body.clear();
        died = true;
    }
}
