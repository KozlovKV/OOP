package kozlov.kirill.snake.model.game;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

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

    public void handleObject(FieldObject object) {
        objects.add(object);
    }

    /**
     * Non-killing points getter.
     *
     * @return points' list which won't kill snake by collision
     */
    public List<Point> getNonKillingCells() {
        List<Point> fieldCopy = getFieldPointsCopy();
        for (var object : objects) {
            fieldCopy.removeAll(object.getKillingCells());
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

    public List<Point> getFreeFieldCells(
        Point forbiddenPoint, double minimalDistance
    ) {
        List<Point> fieldCopy = getFreeFieldCells();
        fieldCopy.removeIf(
            point -> point.distance(forbiddenPoint) < minimalDistance
        );
        return fieldCopy;
    }



    public List<Point> getFreeFieldCells(
        List<Point> forbiddenPoints, double minimalDistance
    ) {
        List<Point> fieldCopy = getFreeFieldCells();
        for (var forbiddenPoint : forbiddenPoints) {
            fieldCopy.removeIf(
                point -> point.distance(forbiddenPoint) < minimalDistance
            );
        }
        return fieldCopy;
    }
}
