package kozlov.kirill.snake.view;

import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import kozlov.kirill.snake.ExcludeClassFromJacocoGeneratedReport;
import kozlov.kirill.snake.model.game.Point;
import lombok.Getter;

import java.util.ArrayList;

@ExcludeClassFromJacocoGeneratedReport
public class GameView {
    public enum Color {
        FIELD("#7CFF7F"),
        SNAKE("#72ADFF"),
        SNAKE_HEAD("#284CFF"),
        APPLE("#FF5B4C");

        @Getter
        private final String hex;

        Color(String hex) {
            this.hex = hex;
        }
    }

    private final GridPane fieldGrid;
    private ArrayList<ArrayList<Rectangle>> fieldRects;
    private final int rowsCnt;
    private final int colsCnt;

    public GameView(GridPane fieldGrid, int rowsCnt, int colsCnt) {
        this.fieldGrid = fieldGrid;
        this.rowsCnt = rowsCnt;
        this.colsCnt = colsCnt;
        constructField();
    }

    private static Rectangle createCell() {
        Rectangle rectangle = new Rectangle(
            30, 30, Paint.valueOf(Color.FIELD.hex)
        );
        rectangle.getStyleClass().clear();
        rectangle.getStyleClass().add("cell");
        return rectangle;
    }

    private void constructField() {
        fieldRects = new ArrayList<>();

        for (int rowIndex = 0; rowIndex < rowsCnt; rowIndex++) {
            ArrayList<Rectangle> fieldRow = new ArrayList<>();
            for (int colIndex = 0; colIndex < colsCnt; colIndex++) {
                Rectangle rectangle = createCell();
                fieldRow.add(rectangle);
                fieldGrid.add(rectangle, colIndex, rowIndex);
            }
            fieldRects.add(fieldRow);
        }
    }

    public void setCellColor(int x, int y, Color color) {
        fieldRects.get(y).get(x).fillProperty().set(Paint.valueOf(color.hex));
    }
}
