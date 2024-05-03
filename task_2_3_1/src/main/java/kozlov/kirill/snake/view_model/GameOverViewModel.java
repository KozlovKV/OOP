package kozlov.kirill.snake.view_model;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import kozlov.kirill.snake.ExcludeClassFromJacocoGeneratedReport;
import kozlov.kirill.snake.model.Model;
import kozlov.kirill.snake.model.game.GameModel;
import kozlov.kirill.snake.view.SceneEnum;
import kozlov.kirill.snake.view.SceneManager;

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

    @FXML
    protected void back() {
        sceneManager.changeScene(SceneEnum.MENU);
    }
}
