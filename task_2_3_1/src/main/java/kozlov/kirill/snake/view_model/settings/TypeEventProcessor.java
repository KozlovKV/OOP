package kozlov.kirill.snake.view_model.settings;

import javafx.event.EventHandler;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyEvent;

public class TypeEventProcessor implements EventHandler<KeyEvent> {
    private final TextInputControl fxmlField;
    private final Validator validator;
    private String previousValue;

    public TypeEventProcessor(
        TextInputControl fxmlField, String initialValue,
        Validator validator
    ) {
        this.fxmlField = fxmlField;
        fxmlField.setText(initialValue);
        previousValue = initialValue;
        this.validator = validator;
    }

    @Override
    public void handle(KeyEvent event) {
        if (validator.isValid(fxmlField, event)) {
            previousValue = fxmlField.getText();
            return;
        }
        // TODO: добавить какие-нибудь сообщения об ошибках. Возможно, добавить цепочку валидаторов или ещё как-нибудь
        System.err.println("Error while validating");
        fxmlField.setText(previousValue);
    }
}