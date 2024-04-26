package kozlov.kirill.snake.model.game;

import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;


@Data
@RequiredArgsConstructor
public class Point {
    @NonNull
    Integer x;
    @NonNull
    Integer y;

    public void move(Vector vector) {
        x += vector.getDirection().x;
        y += vector.getDirection().y;
    }

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

    public Point copy() {
        return new Point(x, y);
    }
}
