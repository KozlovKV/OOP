package kozlov.kirill.snake.viewmodel;

import javafx.fxml.FXML;
import kozlov.kirill.snake.ExcludeClassFromJacocoGeneratedReport;
import kozlov.kirill.snake.view.SceneEnum;
import kozlov.kirill.snake.view.SceneManager;

/**
 * Menu scene view-model class.
 */
@ExcludeClassFromJacocoGeneratedReport
public class MenuViewModel implements SceneManagerAccessible {
    private SceneManager sceneManager;

    @Override
    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    /**
     * Start button handler.
     * <br>
     * Opens GAME scene
     */
    @FXML
    protected void start() {
        sceneManager.changeScene(SceneEnum.GAME);
    }

    /**
     * Settings button handler.
     * <br>
     * Opens SETTINGS scene
     */
    @FXML
    protected void settings() {
        sceneManager.changeScene(SceneEnum.SETTINGS);
    }

    /**
     * Exit button handler.
     * <br>
     * Closes game
     */
    @FXML
    protected void exit() {
        sceneManager.close();
    }
}