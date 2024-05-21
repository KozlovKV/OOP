package kozlov.kirill.snake.model.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Getter;

/**
 * Field class.
 */
public class Field {
    @Getter
    private final int width;
    @Getter
    private final int height;

    private final List<Point> allCells = new ArrayList<>();

    private final List<FieldObject> objects = new ArrayList<>();

    /**
     * Constructor.
     * <br>
     * Creates field's list with all points
     */
    public Field(int width, int height) {
        this.width = width;
        this.height = height;
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                allCells.add(new Point(x, y));
            }
        }
    }

    /**
     * Copy-getter for field's list.
     *
     * @return field's list with points' copies
     */
    private List<Point> getFieldPointsCopy() {
        List<Point> copy = new ArrayList<>();
        for (var point : allCells) {
            copy.add(point.copy());
        }
        return copy;
    }

    /**
     * Handles field object for further getting occupied and killing cells.
     *
     * @param object implementation of FieldObject interface
     */
    public void handleObject(FieldObject object) {
        objects.add(object);
    }

    /**
     * Non-killing points getter.
     *
     * @param additionalPoint additional non-killing point. Usually it's a current snake head
     * @return points' list which won't kill given snake by collision
     */
    public List<Point> getNonKillingCells(Point additionalPoint) {
        List<Point> fieldCopy = getFieldPointsCopy();
        boolean noAdditionalPointCollision = true;
        for (var object : objects) {
            List<Point> objectKillingPoints = object.getKillingCells();
            if (noAdditionalPointCollision) {
                Optional<Integer> collision = additionalPoint.getListCollision(objectKillingPoints);
                if (collision.isPresent()) {
                    objectKillingPoints.remove((int) collision.get());
                    noAdditionalPointCollision = false;
                }
            }
            fieldCopy.removeAll(objectKillingPoints);
        }
        return fieldCopy;
    }

    /**
     * Free points getter.
     *
     * @return points' list which can be used for placing some objects
     */
    public List<Point> getFreeFieldCells() {
        List<Point> fieldCopy = getFieldPointsCopy();
        for (var object : objects) {
            fieldCopy.removeAll(object.getOccupiedCells());
        }
        return fieldCopy;
    }

    /**
     * Free points getter with forbidden area.
     *
     * @param forbiddenPoint center of forbidden area
     * @param minimalDistance euclid radius of forbidden area
     * @return free points not included in forbidden area
     */
    public List<Point> getFreeFieldCells(
        Point forbiddenPoint, double minimalDistance
    ) {
        List<Point> fieldCopy = getFreeFieldCells();
        fieldCopy.removeIf(
            point -> point.distance(forbiddenPoint) < minimalDistance
        );
        return fieldCopy;
    }
}
