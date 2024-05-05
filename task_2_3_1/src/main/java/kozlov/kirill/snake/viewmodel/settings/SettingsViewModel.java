package kozlov.kirill.snake.viewmodel.settings;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.converter.NumberStringConverter;
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
    private TextField widthField;
    private IntegerProperty widthProperty;
    @FXML
    private Label widthError;

    @FXML
    private TextField heightField;
    private IntegerProperty heightProperty;
    @FXML
    private Label heightError;

    @FXML
    private TextField applesCountField;
    private IntegerProperty applesCountProperty;
    @FXML
    private Label applesCountError;

    @Override
    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;

        settingsModel = Model.SETTINGS.get().restartModel();
        widthProperty = new SimpleIntegerProperty(settingsModel.getFieldWidth());
        heightProperty = new SimpleIntegerProperty(settingsModel.getFieldHeight());
        applesCountProperty = new SimpleIntegerProperty(settingsModel.getApplesCount());

        Validator positiveIntValidator = (field, event) -> {
            try {
                if (Integer.parseInt(field.getText()) < 1) {
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }
            return true;
        };

        widthField.onKeyTypedProperty().set(
            new TypeEventProcessor(
                widthField, widthProperty.getValue().toString(),
                positiveIntValidator,
                widthError, "Width should be a positive integer number"
            )
        );
        widthField.textProperty().bindBidirectional(
            widthProperty, new NumberStringConverter()
        );

        heightField.onKeyTypedProperty().set(
            new TypeEventProcessor(
                heightField, heightProperty.getValue().toString(),
                positiveIntValidator,
                heightError, "Width should be a positive integer number"
            )
        );
        heightField.textProperty().bindBidirectional(
            heightProperty, new NumberStringConverter()
        );

        applesCountField.onKeyTypedProperty().set(
            new TypeEventProcessor(
                applesCountField, applesCountProperty.getValue().toString(),
                positiveIntValidator,
                applesCountError, "Width should be a positive integer number"
            )
        );
        applesCountField.textProperty().bindBidirectional(
            applesCountProperty, new NumberStringConverter()
        );
    }

    /**
     * Save button handler.
     * <br>
     * Saves value to settings model fragment and to JSON, then opens MENU scene
     */
    @FXML
    protected void save() {
        settingsModel.setFieldWidth(widthProperty.get());
        settingsModel.setFieldHeight(heightProperty.get());
        settingsModel.setApplesCount(applesCountProperty.get());
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
