package kozlov.kirill.snake.view;

import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import kozlov.kirill.snake.AppEntryPoint;
import kozlov.kirill.snake.model_view.SceneManagerAccessible;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class SceneManager {
    Logger logger = LogManager.getLogger("view");

    public static final int WIDTH = 640;
    public static final int HEIGHT = 640;

    private final Stage primaryStage;

    public SceneManager(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    private void loadScene(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(
            AppEntryPoint.class.getResource(fxmlPath)
        );

        Scene scene = new Scene(loader.load(), WIDTH, HEIGHT);
        scene.getStylesheets().add("style.css");
        primaryStage.setScene(scene);
        primaryStage.show();

        SceneManagerAccessible controller = loader.getController();
        controller.setSceneManager(this);

        logger.info("Set scene from {}", fxmlPath);
    }

    public void changeScene(SceneEnum sceneEnum) {
        try {
            switch (sceneEnum) {
                case MENU: loadScene("menu-scene.fxml"); break;
                case GAME: loadScene("game-scene.fxml"); break;
                case GAME_OVER: loadScene("game-over-scene.fxml"); break;
            }
        } catch (IOException e) {
            logger.error("Error loading scene {}", sceneEnum, e);
            // TODO: Add more logic
            primaryStage.close();
        }
    }

    public Scene getCurrentScene() {
        return primaryStage.getScene();
    }
}
