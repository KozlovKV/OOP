package kozlov.kirill.snake.viewmodel.settings;

import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyEvent;
import kozlov.kirill.snake.ExcludeClassFromJacocoGeneratedReport;

/**
 * Lambda interface for checking validness of input text field.
 */
@ExcludeClassFromJacocoGeneratedReport
public interface Validator {
    /**
     * Validness checker.
     *
     * @param field FXML field with value for validation
     * @param event event of it FXML field
     *
     * @return true when current value is invalid
     */
    boolean isValid(TextInputControl field, KeyEvent event);
}
