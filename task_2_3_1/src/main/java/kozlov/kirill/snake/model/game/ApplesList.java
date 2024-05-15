package kozlov.kirill.snake.model.game;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;


/**
 * Apples wrapper class.
 */
public class ApplesList implements FieldObject {
    private final LinkedList<Point> list = new LinkedList<>();

    private final Field field;

    /**
     * Constructor.
     *
     * @param field game field
     */
    public ApplesList(Field field) {
        this.field = field;
    }

    /**
     * Apples' list getter.
     *
     * @return apples' list
     */
    public LinkedList<Point> list() {
        return list;
    }

    @Override
    public List<Point> getOccupiedCells() {
        return list();
    }

    /**
     * Snake growing checker.
     * <br>
     * If snake's head collides with any apple, method grow is called
     *
     * @param snake snake for checking
     * @return true when snake ate apple
     */
    public boolean checkSnakeGrowing(Snake snake) {
        var collision = snake.head().getListCollision(list);
        if (collision.isEmpty()) {
            return false;
        }
        collision.ifPresent(index -> {
            snake.grow();
            list.remove((int) index);
        });
        return true;
    }

    /**
     * Adds n apples using `addRandomly()`.
     *
     * @param n apples count to add
     */
    public void addRandomly(int n) {
        for (int i = 0; i < n; ++i) {
            addRandomly();
        }
    }

    /**
     * Apple random adder.
     * <br>
     * Gets free field points from gameModel and chooses one of them randomly
     */
    public void addRandomly() {
        Random random = new Random();
        List<Point> freePoints = field.getFreeFieldCells();
        int pointIndex = random.nextInt(freePoints.size());
        Point apple = freePoints.get(pointIndex).copy();
        list.add(apple);
    }
}
