package kozlov.kirill.graph;

import java.util.Objects;

/**
 * Vertex class.
 *
 * @param <T> type of vertex's value.
 */
public class Vertex<T> {
    private final T value;
    private double distance = Double.MAX_VALUE;

    public Vertex(T value) {
        this.value = value;
    }

    void resetDistance() {
        distance = Double.MAX_VALUE;
    }

    public double getDistance() {
        return distance;
    }

    void setDistance(double distance) {
        this.distance = distance;
    }

    public T getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Vertex<?>)) {
            return false;
        }
        Vertex<T> otherVertex = (Vertex<T>) obj;
        return value.equals(otherVertex.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @ExcludeFromJacocoGeneratedReport
    @Override
    public String toString() {
        return String.format("Vertex %s", value.toString());
    }
}
