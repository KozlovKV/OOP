package kozlov.kirill.snake.viewmodel;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import kozlov.kirill.snake.ExcludeClassFromJacocoGeneratedReport;
import kozlov.kirill.snake.ExcludeMethodFromJacocoTestreport;
import kozlov.kirill.snake.model.Model;
import kozlov.kirill.snake.model.game.GameModel;
import kozlov.kirill.snake.model.game.Point;
import kozlov.kirill.snake.model.game.Snake;
import kozlov.kirill.snake.model.game.Vector;
import kozlov.kirill.snake.view.GameView;
import kozlov.kirill.snake.view.SceneEnum;
import kozlov.kirill.snake.view.SceneManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Game scene view-model class.
 */
@ExcludeClassFromJacocoGeneratedReport
public class GameViewModel implements SceneManagerAccessible {

    private final Logger logger = LogManager.getLogger("model-view");

    private SceneManager sceneManager;

    @FXML
    private Label scores;

    @FXML
    private GridPane fieldGrid;
    private boolean updatedAfterKeyPressed = false;

    private GameModel gameModel;
    private GameView gameView;

    /**
     * Extended animation timer for updating with specified delay.
     */
    private final AnimationTimer animationTimer = new AnimationTimer() {
        public static final long UPDATE_MS = 67;
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
        this.gameModel = Model.GAME.get().restartModel();
        this.gameView = new GameView(
            fieldGrid,
            gameModel.getField().getWidth(), gameModel.getField().getHeight()
        );

        sceneManager.getCurrentScene().setOnKeyPressed(this::keyHandler);
        sceneManager.getCurrentScene().widthProperty();

        animationTimer.start();
    }

    /**
     * Key handler.
     *
     * @param event event with key code. Arrows may produce snake direction changing, ESC ends game
     */
    @ExcludeMethodFromJacocoTestreport
    private void keyHandler(KeyEvent event) {
        if (!updatedAfterKeyPressed) {
            return;
        }
        switch (event.getCode()) {
            case UP:
                if (gameModel.getSnake().setDirection(Vector.UP)) {
                    logger.info("Changed direction to UP");
                }
                break;
            case RIGHT:
                if (gameModel.getSnake().setDirection(Vector.RIGHT)) {
                    logger.info("Changed direction to RIGHT");
                }
                break;
            case DOWN:
                if (gameModel.getSnake().setDirection(Vector.DOWN)) {
                    logger.info("Changed direction to DOWN");
                }
                break;
            case LEFT:
                if (gameModel.getSnake().setDirection(Vector.LEFT)) {
                    logger.info("Changed direction to LEFT");
                }
                break;
            case ESCAPE:
                gameOver();
                break;
            default:
                break;
        }
        updatedAfterKeyPressed = false;
    }

    void addCellsList(List<Point> cells, GameView.Color color) {
        for (Point cell : cells) {
            gameView.setCellColor(cell.getAxisX(), cell.getAxisY(), color);
        }
    }

    void addSnake(Snake snake, GameView.Color bodyColor, GameView.Color headColor) {
        addCellsList(snake.body(), bodyColor);
        gameView.setCellColor(
            snake.head().getAxisX(), snake.head().getAxisY(), headColor
        );
    }

    /**
     * Main update function for animation timer.
     * <br>
     * Repaint some cells, updates game model and maybe finishes the game
     */
    private void updateSnakeCells() {
        gameView.fillAllCells(GameView.Color.FIELD);

        gameModel.update();
        if (gameModel.isGameOver()) {
            gameOver();
            return;
        }
        scores.setText(gameModel.getScores().toString());

        addCellsList(gameModel.getApples().list(), GameView.Color.APPLE);
        // TODO: Исправить этот ужас
        addSnake(
            gameModel.getSnakeManager().getSnakeAi().getSnake(),
            GameView.Color.ENEMY, GameView.Color.ENEMY_HEAD
        );
        addSnake(gameModel.getSnake(), GameView.Color.SNAKE, GameView.Color.SNAKE_HEAD);

        updatedAfterKeyPressed = true;
    }

    /**
     * Game over function.
     * <br>
     * Stops animation timer and opens game over scene
     */
    public void gameOver() {
        animationTimer.stop();
        sceneManager.changeScene(SceneEnum.GAME_OVER);
    }
}
