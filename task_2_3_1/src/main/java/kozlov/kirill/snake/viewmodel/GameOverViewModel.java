package kozlov.kirill.snake.viewmodel;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import kozlov.kirill.snake.ExcludeClassFromJacocoGeneratedReport;
import kozlov.kirill.snake.model.Model;
import kozlov.kirill.snake.model.game.GameModel;
import kozlov.kirill.snake.view.SceneEnum;
import kozlov.kirill.snake.view.SceneManager;

/**
 * Game over scene view-model class.
 */
@ExcludeClassFromJacocoGeneratedReport
public class GameOverViewModel implements SceneManagerAccessible {
    private SceneManager sceneManager;

    @FXML
    private Label score;

    @Override
    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
        score.setText(
            ((GameModel)Model.GAME.get()).getScores().toString()
        );
    }

    /**
     * Back button handler.
     * <br>
     * Returns to MENU scene
     */
    @FXML
    protected void back() {
        sceneManager.changeScene(SceneEnum.MENU);
    }
}
