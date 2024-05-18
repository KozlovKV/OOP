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
    private TextField randomsCountField;
    private IntegerProperty randomsCountProperty;
    @FXML
    private Label randomsCountError;

    @FXML
    private TextField predatorsCountField;
    private IntegerProperty predatorsCountProperty;
    @FXML
    private Label predatorsCountError;

    @FXML
    private TextField applesCountField;
    private IntegerProperty applesCountProperty;
    @FXML
    private Label applesCountError;

    @Override
    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;

        settingsModel = Model.SETTINGS.get().restartModel();
        randomsCountProperty = new SimpleIntegerProperty(settingsModel.getRandomsCount());
        predatorsCountProperty = new SimpleIntegerProperty(settingsModel.getPredatorsCount());
        applesCountProperty = new SimpleIntegerProperty(settingsModel.getApplesCount());

        // TODO: Хорошо бы вынести валидаторы куда-нибудь в отедльное место
        Validator nonNegativeValidator = (field, event) -> {
            if (field.getText().isEmpty()) {
                field.setText("0");
            }
            try {
                if (Integer.parseInt(field.getText()) < 0) {
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }
            return true;
        };

        randomsCountField.onKeyTypedProperty().set(
            new TypeEventProcessor(
                randomsCountField, randomsCountProperty.getValue().toString(),
                nonNegativeValidator,
                randomsCountError,
                "Count of chaotic enemies should be a positive integer number"
            )
        );
        // TODO: И всё же для максимально красивой работы моих валидаторов имеет смысл немного переписать TypeEventProcessor и избавиться от встроенных биндингов вовсе
        randomsCountField.textProperty().bindBidirectional(
            randomsCountProperty, new NumberStringConverter()
        );

        predatorsCountField.onKeyTypedProperty().set(
            new TypeEventProcessor(
                predatorsCountField, predatorsCountProperty.getValue().toString(),
                nonNegativeValidator,
                predatorsCountError,
                "Count of predators should be a positive integer number"
            )
        );
        predatorsCountField.textProperty().bindBidirectional(
            predatorsCountProperty, new NumberStringConverter()
        );

        applesCountField.onKeyTypedProperty().set(
            new TypeEventProcessor(
                applesCountField, applesCountProperty.getValue().toString(),
                nonNegativeValidator,
                applesCountError,
                "Apples count should be a positive integer number"
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
        settingsModel.setRandomsCount(randomsCountProperty.get());
        settingsModel.setPredatorsCount(predatorsCountProperty.get());
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
