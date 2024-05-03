package kozlov.kirill.snake.view_model.settings;

import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyEvent;

public interface Validator {
    boolean isValid(TextInputControl field, KeyEvent event);
}
