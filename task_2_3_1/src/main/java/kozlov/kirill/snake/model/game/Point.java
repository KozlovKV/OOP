package kozlov.kirill.snake.model.game;

import java.util.List;
import java.util.Optional;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;


/**
 * Point data-class.
 * <br>
 * Contains constructor Point(int x, int y) and getters/setters for x and y fields
 */
@Data
@RequiredArgsConstructor
public class Point {
    @NonNull
    Integer x;
    @NonNull
    Integer y;

    /**
     * Simple moving.
     *
     * @param vector direction vector
     */
    public void move(Vector vector) {
        x += vector.getDirection().x;
        y += vector.getDirection().y;
    }

    /**
     * Closed moving.
     * <br>
     * After movement point must get into specified borders (works like math rings)
     *
     * @param vector direction vector
     * @param minX minimal X value
     * @param maxX maximal X value
     * @param minY minimal Y value
     * @param maxY maximal Y value
     */
    public void move(
        Vector vector,
        int minX, int maxX,
        int minY, int maxY
    ) {
        x += vector.getDirection().x;
        if (x < minX) {
            x = maxX;
        } else if (x > maxX) {
            x = minX;
        }

        y += vector.getDirection().y;
        if (y < minY) {
            y = maxY;
        } else if (y > maxY) {
            y = minY;
        }

    }

    /**
     * Copy-getter.
     *
     * @return new Point instance with same coordinates
     */
    public Point copy() {
        return new Point(x, y);
    }

    /**
     * Affiliation predicate.
     *
     * @param list list for checking
     *
     * @return true when this point is equivalent to some point in list
     */
    public boolean isInList(List<Point> list) {
        if (list == null) {
            return false;
        }
        for (var point : list) {
            if (equals(point)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Affiliation index getter.
     *
     * @param list list for checking
     *
     * @return index from list which is the first entry of this point wrapped in Optional type
     *     or Optional.empty() when there are no entries
     */
    public Optional<Integer> getListCollision(List<Point> list) {
        if (list == null) {
            return Optional.empty();
        }
        for (int i = 0; i < list.size(); ++i) {
            if (equals(list.get(i))) {
                return Optional.of(i);
            }
        }
        return Optional.empty();
    }
}
