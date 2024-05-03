package kozlov.kirill.snake.model_view.settings;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyEvent;
import javafx.util.converter.NumberStringConverter;
import kozlov.kirill.snake.ExcludeClassFromJacocoGeneratedReport;
import kozlov.kirill.snake.model.Model;
import kozlov.kirill.snake.model.settings.SettingsModel;
import kozlov.kirill.snake.model_view.SceneManagerAccessible;
import kozlov.kirill.snake.view.SceneEnum;
import kozlov.kirill.snake.view.SceneManager;

@ExcludeClassFromJacocoGeneratedReport
public class SettingsController implements SceneManagerAccessible {

    private SceneManager sceneManager;
    private SettingsModel settingsModel;

    @FXML
    private TextField widthField;
    private IntegerProperty widthProperty; // TODO: в перспективе от этих штук можно будет полностью избавиться, заменив их функционал логикой TypeEventProcessor
    @FXML
    private TextField heightField;
    private IntegerProperty heightProperty;
    @FXML
    private TextField applesCountField;
    private IntegerProperty applesCountProperty;

    @Override
    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;

        settingsModel = Model.SETTINGS.get().restartModel();
        widthProperty = new SimpleIntegerProperty(settingsModel.getFieldWidth());
        heightProperty = new SimpleIntegerProperty(settingsModel.getFieldHeight());
        applesCountProperty = new SimpleIntegerProperty(settingsModel.getApplesCount());

        Validator positiveIntValidator = (field, event) -> {
            try {
                if (Integer.parseInt(field.getText()) < 1)
                    return false;
            } catch (NumberFormatException e) {
                return false;
            }
            return true;
        };

        widthField.onKeyTypedProperty().set(
            new TypeEventProcessor(
                widthField, widthProperty.getValue().toString(),
                positiveIntValidator
            )
        );
        widthField.textProperty().bindBidirectional(widthProperty, new NumberStringConverter());

        heightField.onKeyTypedProperty().set(
            new TypeEventProcessor(
                heightField, heightProperty.getValue().toString(),
                positiveIntValidator
            )
        );
        heightField.textProperty().bindBidirectional(heightProperty, new NumberStringConverter());

        applesCountField.onKeyTypedProperty().set(
            new TypeEventProcessor(
                applesCountField, applesCountProperty.getValue().toString(),
                positiveIntValidator
            )
        );
        applesCountField.textProperty().bindBidirectional(applesCountProperty, new NumberStringConverter());
    }

    @FXML
    protected void save() {
        settingsModel.setFieldWidth(widthProperty.get());
        settingsModel.setFieldHeight(heightProperty.get());
        settingsModel.setApplesCount(applesCountProperty.get());
        settingsModel.saveToJson();
        sceneManager.changeScene(SceneEnum.MENU);
    }

    @FXML
    protected void discard() {
        sceneManager.changeScene(SceneEnum.MENU);
    }
}
