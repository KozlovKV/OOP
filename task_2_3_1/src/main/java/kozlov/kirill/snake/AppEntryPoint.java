package kozlov.kirill.snake;

import javafx.application.Application;
import javafx.stage.Stage;
import kozlov.kirill.snake.view.SceneEnum;
import kozlov.kirill.snake.view.SceneManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Application entry point class.
 */
@ExcludeClassFromJacocoGeneratedReport
public class AppEntryPoint extends Application {
    @Override
    public void start(Stage stage) {
        stage.setTitle("Snake game");
        stage.setResizable(false);
        SceneManager sceneManager = new SceneManager(stage);
        sceneManager.changeScene(SceneEnum.MENU);
    }

    /**
     * Application entry point method.
     *
     * @param args cmds' args
     */
    public static void main(String[] args) {
        launch();
    }
}