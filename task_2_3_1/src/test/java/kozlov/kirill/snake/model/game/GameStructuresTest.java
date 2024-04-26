package kozlov.kirill.snake.model.game;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

public class GameStructuresTest {
    @Test
    void snakeGrowth() {
        GameModel gameModel = new GameModel(4, 0);
        try {
            Field apple = gameModel.getClass()
                .getDeclaredField("apple");
            apple.setAccessible(true);
            apple.set(gameModel, new Point(5, 0));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Assertions.fail();
        }
        gameModel.moveSnake();
        Assertions.assertEquals(5, gameModel.getSnake().size());
    }

    @Test
    void snakeDies() {
        GameModel gameModel = new GameModel(4, 0);
        try {
            Field apple = gameModel.getClass()
                .getDeclaredField("apple");
            apple.setAccessible(true);
            apple.set(gameModel, new Point(5, 0));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Assertions.fail();
        }
        gameModel.moveSnake();
        gameModel.setVector(Vector.UP);
        gameModel.moveSnake();
        gameModel.setVector(Vector.LEFT);
        gameModel.moveSnake();
        gameModel.setVector(Vector.DOWN);
        gameModel.moveSnake();
        Assertions.assertTrue(gameModel.isDied());
    }
}
