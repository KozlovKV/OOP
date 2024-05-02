package kozlov.kirill.snake.model_view;

import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.util.converter.NumberStringConverter;
import kozlov.kirill.snake.model.Model;
import kozlov.kirill.snake.model.settings.SettingsModel;
import kozlov.kirill.snake.view.SceneEnum;
import kozlov.kirill.snake.view.SceneManager;

public class SettingsController implements SceneManagerAccessible {

    private SceneManager sceneManager;
    private SettingsModel settingsModel;

    @FXML
    private TextField widthField;
    private IntegerProperty widthProperty;
    @FXML
    private TextField heightField;
    private IntegerProperty heightProperty;
    @FXML
    private TextField applesCountField;
    private IntegerProperty applesCountProperty;

    @Override
    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;

        settingsModel = (SettingsModel) Model.SETTINGS.get().restartModel();
        widthProperty = new SimpleIntegerProperty(settingsModel.getFieldWidth());
        heightProperty = new SimpleIntegerProperty(settingsModel.getFieldHeight());
        applesCountProperty = new SimpleIntegerProperty(settingsModel.getApplesCount());

        widthField.textProperty().bindBidirectional(widthProperty, new NumberStringConverter());
        heightField.textProperty().bindBidirectional(heightProperty, new NumberStringConverter());
        applesCountField.textProperty().bindBidirectional(applesCountProperty, new NumberStringConverter());
    }

    @FXML
    protected void save() {
        settingsModel.setFieldWidth(widthProperty.get());
        settingsModel.setFieldHeight(heightProperty.get());
        settingsModel.setApplesCount(applesCountProperty.get());
        sceneManager.changeScene(SceneEnum.MENU);
    }

    @FXML
    protected void discard() {
        sceneManager.changeScene(SceneEnum.MENU);
    }
}
