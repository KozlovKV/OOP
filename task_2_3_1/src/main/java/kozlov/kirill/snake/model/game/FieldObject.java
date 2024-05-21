package kozlov.kirill.snake.model.game;

import java.util.ArrayList;
import java.util.List;

/**
 * Interface for objects which are placed to field.
 */
public interface FieldObject {
    /**
     * Getter for cells occupied by object.
     *
     * @return occupied cells
     */
    List<Point> getOccupiedCells();

    /**
     * Getter for object's killing cells.
     *
     * @return killing cells
     */
    default List<Point> getKillingCells() {
        return new ArrayList<>();
    }
}
