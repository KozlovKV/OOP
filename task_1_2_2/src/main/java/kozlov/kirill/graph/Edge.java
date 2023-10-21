package kozlov.kirill.graph;

import java.util.Objects;

public class Edge<T> {
    private final T from;
    private final T to;
    private double weight = 0;

    public Edge(T from, T to) {
        this.from = from;
        this.to = to;
    }

    public Edge(T from, T to, double weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    public T getFrom() {
        return from;
    }

    public T getTo() {
        return to;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Edge<?>)) {
            return false;
        }
        Edge<T> otherEdge = (Edge<T>) obj;
        return from.equals(otherEdge.from) && to.equals(otherEdge.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }

    @Override
    public String toString() {
        return String.format("%s - %f -> %s", from.toString(), weight, to.toString());
    }
}
