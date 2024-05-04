package kozlov.kirill.snake.model.game;

import kozlov.kirill.snake.model.Model;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for GameModel model fragment.
 * <br>
 * Contains almost whole methods of internal objects
 */
public class GameStructuresTest {
    @Test
    void testSnakeValidDirectionChanging() {
        GameModel gameModel = Model.GAME.get().restartModel();
        gameModel.getSnake().setDirection(Vector.RIGHT);
        Assertions.assertEquals(Vector.RIGHT, gameModel.getSnake().getDirection());
        gameModel.getSnake().setDirection(Vector.UP);
        Assertions.assertEquals(Vector.UP, gameModel.getSnake().getDirection());
        gameModel.getSnake().setDirection(Vector.LEFT);
        Assertions.assertEquals(Vector.LEFT, gameModel.getSnake().getDirection());
        gameModel.getSnake().setDirection(Vector.DOWN);
        Assertions.assertEquals(Vector.DOWN, gameModel.getSnake().getDirection());
    }

    @Test
    void testSnakeInvalidDirectionChanging() {
        GameModel gameModel = Model.GAME.get().restartModel();

        gameModel.getSnake().setDirection(Vector.RIGHT);
        gameModel.getSnake().setDirection(Vector.LEFT);
        Assertions.assertEquals(Vector.RIGHT, gameModel.getSnake().getDirection());

        gameModel.getSnake().setDirection(Vector.UP);
        gameModel.getSnake().setDirection(Vector.DOWN);
        Assertions.assertEquals(Vector.UP, gameModel.getSnake().getDirection());

        gameModel.getSnake().setDirection(Vector.LEFT);
        gameModel.getSnake().setDirection(Vector.RIGHT);
        Assertions.assertEquals(Vector.LEFT, gameModel.getSnake().getDirection());

        gameModel.getSnake().setDirection(Vector.DOWN);
        gameModel.getSnake().setDirection(Vector.UP);
        Assertions.assertEquals(Vector.DOWN, gameModel.getSnake().getDirection());
    }
    @Test
    void testGrowing() {
        GameModel gameModel = Model.GAME.get().restartModel();
        Point nextPoint = gameModel.getSnake().head().copy();
        nextPoint.move(gameModel.getSnake().getDirection());
        gameModel.getApples().list().add(nextPoint);

        Assertions.assertEquals(1, gameModel.getScores());
        Assertions.assertEquals(
            gameModel.getSnake().head(),
            gameModel.getSnake().tail()
        );

        gameModel.update();
        Assertions.assertEquals(2, gameModel.getScores());
        Assertions.assertEquals(
            gameModel.getSnake().body().get(0),
            gameModel.getSnake().tail()
        );

        nextPoint = gameModel.getSnake().head().copy();
        nextPoint.move(gameModel.getSnake().getDirection());
        gameModel.getApples().list().add(nextPoint);

        gameModel.update();
        Assertions.assertEquals(3, gameModel.getScores());
        Assertions.assertNotEquals(
            gameModel.getSnake().body().get(0),
            gameModel.getSnake().head()
        );
        Assertions.assertNotEquals(
            gameModel.getSnake().body().get(0),
            gameModel.getSnake().tail()
        );
    }

    @Test
    void testGameOver() {
        GameModel gameModel = Model.GAME.get().restartModel();
        gameModel.getSnake().setDirection(Vector.RIGHT);
        for (int i = 0; i < 5; ++i) {
            gameModel.getSnake().move();
            gameModel.getSnake().grow();
        }
        gameModel.getSnake().setDirection(Vector.UP);
        gameModel.getSnake().move();
        gameModel.getSnake().setDirection(Vector.LEFT);
        gameModel.getSnake().move();
        gameModel.getSnake().setDirection(Vector.DOWN);
        gameModel.getSnake().move();
        Assertions.assertTrue(gameModel.isGameOver());
    }
}
