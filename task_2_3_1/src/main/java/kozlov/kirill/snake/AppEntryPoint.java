package kozlov.kirill.snake;

import javafx.application.Application;
import javafx.stage.Stage;
import kozlov.kirill.snake.view.SceneEnum;
import kozlov.kirill.snake.view.SceneManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class AppEntryPoint extends Application {
    Logger logger = LogManager.getLogger("main");

    private SceneManager sceneManager;

    @Override
    public void start(Stage stage) {
        stage.setTitle("Snake game");
        stage.setResizable(false);
        sceneManager = new SceneManager(stage);
        sceneManager.changeScene(SceneEnum.MENU);
    }

    public static void main(String[] args) {
        launch();
    }
}