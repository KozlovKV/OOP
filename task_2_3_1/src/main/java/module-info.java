module kozlov.kirill.snake.task_2_3_1 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires org.apache.logging.log4j.core;

    opens kozlov.kirill.snake.task_2_3_1 to javafx.fxml;
    exports kozlov.kirill.snake.task_2_3_1;
    exports kozlov.kirill.snake.task_2_3_1.view;
    opens kozlov.kirill.snake.task_2_3_1.view to javafx.fxml;
    exports kozlov.kirill.snake.task_2_3_1.model_view;
    opens kozlov.kirill.snake.task_2_3_1.model_view to javafx.fxml;
}