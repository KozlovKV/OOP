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

    private final LinkedList<Point> list = new LinkedList<>();

    private final GameModel gameModel;

    public ApplesList(GameModel gameModel) {
        this.gameModel = gameModel;
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

    public void addRandomly(int n) {
        for (int i = 0; i < n; ++i) {
            addRandomly();
        }
    }

    public void addRandomly() {
        Random random = new Random();
        List<Point> freePoints = gameModel.getFreeFieldCells();
        int pointIndex = random.nextInt(freePoints.size());
        Point apple = freePoints.get(pointIndex).copy();
        list.add(apple);
    }
}
