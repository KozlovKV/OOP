package kozlov.kirill.snake.view;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import kozlov.kirill.snake.AppEntryPoint;
import kozlov.kirill.snake.ExcludeClassFromJacocoGeneratedReport;
import kozlov.kirill.snake.viewmodel.SceneManagerAccessible;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Scene manager class.
 * <br>
 * Used for comfortable switching between scenes from other components
 */
@ExcludeClassFromJacocoGeneratedReport
public class SceneManager {
    Logger logger = LogManager.getLogger("view");

    public static final int WIDTH = 640;
    public static final int HEIGHT = 700;

    private final Stage primaryStage;

    /**
     * Constructor.
     *
     * @param primaryStage stage for placing scenes
     */
    public SceneManager(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * Closes primary stage.
     */
    public void close() {
        primaryStage.close();
    }

    /**
     * Scene loading.
     * <br>
     * Loads scene from specified FXML and binds SceneManager
     * with controller (view-model) specified in this file
     *
     * @param fxmlPath path to FXML file
     *
     * @throws IOException when there were some troubles with file's loading
     */
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

    /**
     * Scene changing.
     *
     * @param sceneEnum scene for opening instead of current
     */
    public void changeScene(SceneEnum sceneEnum) {
        try {
            switch (sceneEnum) {
                case MENU:
                    loadScene("menu-scene.fxml");
                    break;
                case SETTINGS:
                    loadScene("settings-scene.fxml");
                    break;
                case GAME:
                    loadScene("game-scene.fxml");
                    break;
                case GAME_OVER:
                    loadScene("game-over-scene.fxml");
                    break;
            }
        } catch (IOException e) {
            logger.error("Error loading scene {}", sceneEnum, e);
            close();
        }
    }

    /**
     * Current scene getter.
     *
     * @return scene which now showed in primary stage
     */
    public Scene getCurrentScene() {
        return primaryStage.getScene();
    }
}
