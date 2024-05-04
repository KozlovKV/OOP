package kozlov.kirill.snake.view_model;

import javafx.fxml.FXML;
import kozlov.kirill.snake.ExcludeClassFromJacocoGeneratedReport;
import kozlov.kirill.snake.view.SceneEnum;
import kozlov.kirill.snake.view.SceneManager;

@ExcludeClassFromJacocoGeneratedReport
public class MenuViewModel implements SceneManagerAccessible {
    private SceneManager sceneManager;

    @Override
    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    @FXML
    protected void start() {
        sceneManager.changeScene(SceneEnum.GAME);
    }

    @FXML
    protected void settings() {
        sceneManager.changeScene(SceneEnum.SETTINGS);
    }

    @FXML
    protected void exit() {
        sceneManager.close();
    }
}