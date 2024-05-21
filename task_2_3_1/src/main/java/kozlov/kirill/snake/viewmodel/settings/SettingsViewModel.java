package kozlov.kirill.snake.viewmodel.settings;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import kozlov.kirill.snake.ExcludeClassFromJacocoGeneratedReport;
import kozlov.kirill.snake.model.Model;
import kozlov.kirill.snake.model.settings.SettingsModel;
import kozlov.kirill.snake.view.SceneEnum;
import kozlov.kirill.snake.view.SceneManager;
import kozlov.kirill.snake.viewmodel.SceneManagerAccessible;

/**
 * Settings scene view-model class.
 * <br>
 * Bind to every setting value own field with TypeEventProcessor instance for additional validation
 */
@ExcludeClassFromJacocoGeneratedReport
public class SettingsViewModel implements SceneManagerAccessible {

    private SceneManager sceneManager;
    private SettingsModel settingsModel;

    @FXML
    private TextField randomsCountField;
    private TypeEventProcessor<Integer> randomsCountHandler;
    @FXML
    private Label randomsCountError;

    @FXML
    private TextField predatorsCountField;
    private TypeEventProcessor<Integer> predatorsCountHandler;
    @FXML
    private Label predatorsCountError;

    @FXML
    private TextField applesCountField;
    private TypeEventProcessor<Integer> applesCountHandler;
    @FXML
    private Label applesCountError;

    @Override
    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;

        settingsModel = Model.SETTINGS.get().restartModel();

        randomsCountHandler = new TypeEventProcessor<Integer>(
            randomsCountField, settingsModel.getRandomsCount(),
            ImplementedValidators.nonNegativeValidator,
            randomsCountError,
            "Count of chaotic enemies should be a non-negative integer number"
        );
        randomsCountField.onKeyTypedProperty().set(
            randomsCountHandler
        );

        predatorsCountHandler = new TypeEventProcessor<Integer>(
            predatorsCountField, settingsModel.getPredatorsCount(),
            ImplementedValidators.nonNegativeValidator,
            predatorsCountError,
            "Count of predators should be a non-negative integer number"
        );
        predatorsCountField.onKeyTypedProperty().set(
            predatorsCountHandler
        );

        applesCountHandler = new TypeEventProcessor<Integer>(
            applesCountField, settingsModel.getApplesCount(),
            ImplementedValidators.positiveValidator,
            applesCountError,
            "Apples count should be a positive integer number"
        );
        applesCountField.onKeyTypedProperty().set(
            applesCountHandler
        );
    }

    /**
     * Save button handler.
     * <br>
     * Saves value to settings model fragment and to JSON, then opens MENU scene
     */
    @FXML
    protected void save() {
        settingsModel.setRandomsCount(randomsCountHandler.getValue());
        settingsModel.setPredatorsCount(predatorsCountHandler.getValue());
        settingsModel.setApplesCount(applesCountHandler.getValue());
        settingsModel.saveToJson();
        sceneManager.changeScene(SceneEnum.MENU);
    }

    /**
     * Discard button handler.
     * <br>
     * Opens MENU scene without saving
     */
    @FXML
    protected void discard() {
        sceneManager.changeScene(SceneEnum.MENU);
    }
}
