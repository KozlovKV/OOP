package kozlov.kirill.snake.viewmodel.settings;

import java.util.Optional;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyEvent;
import kozlov.kirill.snake.ExcludeClassFromJacocoGeneratedReport;

/**
 * Lambda interface for checking validness of input text field.
 */
@ExcludeClassFromJacocoGeneratedReport
public interface Validator<T> {
    /**
     * Valid value getter.
     *
     * @param field FXML field with value for validation
     * @param event event of it FXML field
     *
     * @return Optional.empty() when current value in field is invalid or Optional.of(T) else
     */
    Optional<T> getValidValue(TextInputControl field, KeyEvent event);
}
