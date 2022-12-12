package nl.maastrichtuniversity.dacs.ssa.g14.geometry;

import java.util.Objects;

/**
 * Just a helper class to never mistake x for y again
 */
public class Coordinate {
    private final double x;
    private final double y;

    public Coordinate(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Coordinate translate(double deltaX, double deltaY) {
        return new Coordinate(x + deltaX, y + deltaY);
    }

    public Coordinate translateX(double delta) {
        return translate(delta, 0);
    }

    public Coordinate translateY(double delta) {
        return translate(0, delta);
    }

    public static Coordinate of(double x, double y) {
        return new Coordinate(x, y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Coordinate that = (Coordinate) o;
        return Double.compare(that.getX(), getX()) == 0 && Double.compare(that.getY(), getY()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getX(), getY());
    }

    @Override
    public String toString() {
        return "Coordinate{x=" + x + ", y=" + y + "}";
    }
}
