package kozlov.kirill.snake.model_view;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import kozlov.kirill.snake.model.Model;
import kozlov.kirill.snake.model.game.GameModel;
import kozlov.kirill.snake.model.game.Point;
import kozlov.kirill.snake.model.game.Vector;
import kozlov.kirill.snake.view.GameView;
import kozlov.kirill.snake.view.SceneEnum;
import kozlov.kirill.snake.view.SceneManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameController implements SceneManagerAccessible {

    private final Logger logger = LogManager.getLogger("model-view");

    private SceneManager sceneManager;

    @FXML
    private Label scores;

    @FXML
    private GridPane fieldGrid;
    private boolean updatedAfterKeyPressed = false;

    private GameModel gameModel;
    private GameView gameView;

    private final AnimationTimer animationTimer = new AnimationTimer() {
        public static final long UPDATE_MS = 50;
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
        this.gameModel = (GameModel) Model.GAME.get().restartModel();
        this.gameView = new GameView(
            fieldGrid,
            gameModel.getCurrentFieldHeight(), gameModel.getCurrentFieldWidth()
        );

        sceneManager.getCurrentScene().setOnKeyPressed(this::keyHandler);
        sceneManager.getCurrentScene().widthProperty();

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

    private void updateSnakeCells() {
        Point currentHead = gameModel.getSnakeHead();
        gameView.setCellColor(currentHead.getX(), currentHead.getY(), GameView.Color.SNAKE);
        Point tail = gameModel.getSnakeTail();
        gameView.setCellColor(tail.getX(), tail.getY(), GameView.Color.FIELD);

        gameModel.moveSnake();
        scores.setText(gameModel.getScores().toString());
        if (gameModel.isDied()) {
            animationTimer.stop();
            sceneManager.changeScene(SceneEnum.GAME_OVER);
            return;
        }

        Point newHead = gameModel.getSnakeHead();
        gameView.setCellColor(newHead.getX(), newHead.getY(), GameView.Color.SNAKE_HEAD);

        for (Point apple : gameModel.getApples()) {
            gameView.setCellColor(apple.getX(), apple.getY(), GameView.Color.APPLE);
        }

        updatedAfterKeyPressed = true;
    }
}
