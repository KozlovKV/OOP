package kozlov.kirill.snake.model_view;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import kozlov.kirill.snake.model.game.GameModel;
import kozlov.kirill.snake.model.game.Point;
import kozlov.kirill.snake.model.game.Vector;
import kozlov.kirill.snake.view.SceneEnum;
import kozlov.kirill.snake.view.SceneManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class GameController implements SceneManagerAccessible {

    private static final String FIELD_HEX_CODE = "#7CFF7F";
    private static final String SNAKE_HEX_CODE = "#72ADFF";
    private static final String SNAKE_HEAD_HEX_CODE = "#284CFF";
    private static final String APPLE_HEX_CODE = "#FF5B4C";

    private final Logger logger = LogManager.getLogger("model-view");

    private SceneManager sceneManager;

    @FXML
    private GridPane fieldGrid;
    private ArrayList<ArrayList<Rectangle>> fieldRects;
    private boolean updatedAfterKeyPressed = false;

    private GameModel gameModel;

    private final AnimationTimer animationTimer = new AnimationTimer() {
        public static final long UPDATE_MS = 33;
        private long lastUpdateTimestamp = 0;

        @Override
        public void handle(long nanoSecTimestamp) {
            long msTimestamp = nanoSecTimestamp / 1000000;
            if (msTimestamp - lastUpdateTimestamp < UPDATE_MS) {
                return;
            }
            updateSnakeCells();
            lastUpdateTimestamp = msTimestamp;
        }
    };

    @Override
    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
        this.gameModel = new GameModel();

        constructField();

        sceneManager.getCurrentScene().setOnKeyPressed(this::keyHandler);

        animationTimer.start();
    }

    private void keyHandler(KeyEvent event) {
        if (!updatedAfterKeyPressed) {
            return;
        }
        Vector currentVector = gameModel.getVector();
        switch (event.getCode()) {
            case UP:
                if (!currentVector.equals(Vector.DOWN)) {
                    gameModel.setVector(Vector.UP);
                    logger.info("Changed direction to UP");
                }
                break;
            case RIGHT:
                if (!currentVector.equals(Vector.LEFT)) {
                    gameModel.setVector(Vector.RIGHT);
                    logger.info("Changed direction to RIGHT");
                }
                break;
            case DOWN:
                if (!currentVector.equals(Vector.UP)) {
                    gameModel.setVector(Vector.DOWN);
                    logger.info("Changed direction to DOWN");
                }
                break;
            case LEFT:
                if (!currentVector.equals(Vector.RIGHT)) {
                    gameModel.setVector(Vector.LEFT);
                    logger.info("Changed direction to LEFT");
                }
                break;
        }
        updatedAfterKeyPressed = false;
    }

    // TODO: Вынести чисто стилевые статические штуки в отдельный класс
    private static Rectangle getCell() {
        Rectangle rectangle = new Rectangle(
            30, 30, Paint.valueOf(FIELD_HEX_CODE)
        );
        rectangle.getStyleClass().clear();
        rectangle.getStyleClass().add("cell");
        return rectangle;
    }

    private void setCellColor(int x, int y, String htmlColor) {
        fieldRects.get(y).get(x).fillProperty().set(Paint.valueOf(htmlColor));
    }

    private void constructField() {
        fieldRects = new ArrayList<>();

        for (int rowIndex = 0; rowIndex < gameModel.getFieldWidth(); rowIndex++) {
            ArrayList<Rectangle> fieldRow = new ArrayList<>();
            for (int colIndex = 0; colIndex < gameModel.getFieldHeight(); colIndex++) {
                Rectangle rectangle = getCell();
                fieldRow.add(rectangle);
                fieldGrid.add(rectangle, colIndex, rowIndex);
            }
            fieldRects.add(fieldRow);
        }
        for (var point : gameModel.getSnake()) {
            if (point.equals(gameModel.getSnakeHead())) {
                setCellColor(point.getX(), point.getY(), SNAKE_HEAD_HEX_CODE);
            } else {
                setCellColor(point.getX(), point.getY(), SNAKE_HEX_CODE);
            }
        }
        Point apple = gameModel.getApple();
        setCellColor(apple.getX(), apple.getY(), APPLE_HEX_CODE);
    }

    private void updateSnakeCells() {
        Point tail = gameModel.getSnakeTail();
        setCellColor(tail.getX(), tail.getY(), FIELD_HEX_CODE);
        Point currentHead = gameModel.getSnakeHead();
        setCellColor(currentHead.getX(), currentHead.getY(), SNAKE_HEX_CODE);

        gameModel.moveSnake();
        if (gameModel.shouldDie()) {
            animationTimer.stop();
            sceneManager.changeScene(SceneEnum.GAME_OVER);
            // TODO: проверить, надо ли чистить какие-то данные в этот момент
            return;
        }

        Point newHead = gameModel.getSnakeHead();
        setCellColor(newHead.getX(), newHead.getY(), SNAKE_HEAD_HEX_CODE);

        Point apple = gameModel.getApple();
        setCellColor(apple.getX(), apple.getY(), APPLE_HEX_CODE);

        updatedAfterKeyPressed = true;
    }
}
