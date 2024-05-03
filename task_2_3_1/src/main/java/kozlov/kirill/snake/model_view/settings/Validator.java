package kozlov.kirill.snake.model_view.settings;

import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyEvent;

public interface Validator {
    boolean isValid(TextInputControl field, KeyEvent event);
}
