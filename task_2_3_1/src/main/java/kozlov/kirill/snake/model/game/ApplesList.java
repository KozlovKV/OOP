package kozlov.kirill.snake.model.game;

import lombok.Getter;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class ApplesList {
    public enum Type {
        REB,
        GREEN,
        BLUE,
    }

    private final int applesCount;
    private final LinkedList<Point> list = new LinkedList<>();

    private final int fieldWidth;
    private final int fieldHeight;

    public ApplesList(
        int applesCount, int fieldWidth, int fieldHeight,
        List<Point> occupiedPoints
    ) {
        this.applesCount = applesCount;
        this.fieldWidth = fieldWidth;
        this.fieldHeight = fieldHeight;
        for (int i = 0; i < applesCount; ++i) {
            addRandomly(occupiedPoints);
        }
    }

    public LinkedList<Point> list() {
        return list;
    }

    public boolean checkSnakeGrowing(Snake snake) {
        int appleIndex = snake.head().getListCollision(list);
        if (appleIndex != -1) {
            snake.grow();
            list.remove(appleIndex);
            return true;
        }
        return false;
    }

    public void addRandomly(List<Point> occupiedPoints) {
        Random random = new Random();
        Point apple;
        do {
            apple = new Point(random.nextInt(fieldWidth), random.nextInt(fieldHeight));
        } while (
            apple.isInList(occupiedPoints) || apple.isInList(list)
        );
        list.add(apple);
    }
}
