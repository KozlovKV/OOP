//package kozlov.kirill.snake.model.game;
//
//import lombok.Getter;
//import lombok.NonNull;
//
//@Getter
//public class ClosedPoint {
//    Integer x;
//    Integer y;
//    private final int minX;
//    private final int maxX;
//    private final int minY;
//    private final int maxY;
//
//    public ClosedPoint(int x, int minX, int maxX, int y, int minY, int maxY) {
//        this.x = x;
//        this.minX = minX;
//        this.maxX = maxX;
//        this.y = y;
//        this.minY = minY;
//        this.maxY = maxY;
//    }
//
//    public void move(
//        Vector vector
//    ) {
//        x += vector.getDirection().x;
//        if (x < minX) {
//            x = maxX;
//        } else if (x > maxX) {
//            x = minX;
//        }
//
//        y += vector.getDirection().y;
//        if (y < minY) {
//            y = maxY;
//        } else if (y > maxY) {
//            y = minY;
//        }
//    }
//
//    public boolean equals(Point point) {
//        return point.equals(this);
//    }
//}
