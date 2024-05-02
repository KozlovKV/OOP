package kozlov.kirill.snake.model_view;

import javafx.fxml.FXML;
import kozlov.kirill.snake.view.SceneEnum;
import kozlov.kirill.snake.view.SceneManager;

public class MenuController implements SceneManagerAccessible {
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
}