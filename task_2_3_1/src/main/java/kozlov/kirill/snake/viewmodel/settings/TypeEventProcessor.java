package kozlov.kirill.snake.viewmodel.settings;

import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyEvent;
import kozlov.kirill.snake.ExcludeClassFromJacocoGeneratedReport;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Extended event handler class.
 * <br>
 * Performs value validation after event and storing/getting of valid value
 */
@ExcludeClassFromJacocoGeneratedReport
public class TypeEventProcessor<T> implements EventHandler<KeyEvent> {
    private final Logger logger = LogManager.getLogger("view-model");

    private final TextInputControl fxmlField;
    private final Validator<T> validator;
    private final Label errorLabel;
    private final String errorMessage;
    @Getter
    private T value;

    /**
     * Constructor.
     *
     * @param fxmlField FXML input field for validation
     * @param initialValue initial field value
     * @param validator implementation of Validator interface
     * @param errorLabel label where will be showed error message
     * @param errorMessage message for showing in case of validation failed
     */
    public TypeEventProcessor(
        TextInputControl fxmlField, T initialValue,
        Validator<T> validator,
        Label errorLabel, String errorMessage
    ) {
        this.fxmlField = fxmlField;
        this.value = initialValue;
        fxmlField.setText(initialValue.toString());
        this.validator = validator;
        this.errorLabel = errorLabel;
        this.errorMessage = errorMessage;
    }

    @Override
    public void handle(KeyEvent event) {
        validator.getValidValue(fxmlField, event).ifPresentOrElse(
            successValue -> {
                value = successValue;
                errorLabel.setText("");
            },
            () -> {
                logger.warn("Validation error");
                errorLabel.setText(
                    errorMessage + "\nLast valid value is " + value.toString()
                );
            }
        );
    }
}
