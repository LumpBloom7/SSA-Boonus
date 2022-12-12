package nl.maastrichtuniversity.dacs.ssa.g14.geometry;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * This project implies that hexagons are always placed so that two
 * sides are parallel to horizontal axis.
 */
public class Hexagon {
    private static final Random RANDOM = new Random();
    /**
     * See <a href="https://en.wikipedia.org/wiki/Hexagon#Parameters">wikipedia</a>
     * for source.
     */
    public static final double CIRCUMCIRCLE_RADIUS_APOTHEM_RATIO = 2.0 / Math.sqrt(3);
    public static final double WIDTH_HEIGHT_RATIO = CIRCUMCIRCLE_RADIUS_APOTHEM_RATIO;

    public static final double SLOPE = Math.tan(Math.PI / 3);

    private final Coordinate center;
    private final double outerDiameter;

    /**
     * @param center Center point.
     * @param outerDiameter Outer diameter of hexagon (diameter of circle in
     * which hexagon is inscribed).
     */
    public Hexagon(Coordinate center, double outerDiameter) {
        this.center = center;
        this.outerDiameter = outerDiameter;
    }

    public Coordinate getCenter() {
        return center;
    }

    public double getOuterDiameter() {
        return outerDiameter;
    }

    public double getInnerDiameter() {
        return computeInnerDiameter(outerDiameter);
    }

    public double getOuterRadius() {
        return computeOuterRadius(outerDiameter);
    }

    public double getInnerRadius() {
        return computeInnerRadius(outerDiameter);
    }

    public double getWidth() {
        return computeWidth(outerDiameter);
    }

    public double getHeight() {
        return computeHeight(outerDiameter);
    }

    public double getHalfWidth() {
        return computeHalfWidth(outerDiameter);
    }

    public double getHalfHeight() {
        return computeHalfHeight(outerDiameter);
    }

    public double getSideLength() {
        return computeSideLength(outerDiameter);
    }

    public Hexagon translate(double deltaX, double deltaY) {
        return create(center.translate(deltaX, deltaY), outerDiameter);
    }

    public Hexagon translateX(double deltaX) {
        return translate(deltaX, 0);
    }

    public Hexagon translateY(double deltaY) {
        return translate(0, deltaY);
    }

    public boolean contains(Coordinate coordinate) {
        return contains(coordinate.getX(), coordinate.getY());
    }

    public boolean contains(double x, double y) {
        // if it's vertically out of bounding rectangle, then instantly disapprove it
        if (!within(y, center.getY() - getHalfHeight(), center.getY() + getHalfHeight())) {
            return false;
        }

        double minimumX = center.getX() - getHalfWidth();
        double maximumX = center.getX() + getHalfWidth();

        double leftUpperBound = center.getY() + (x - minimumX) * SLOPE;
        double leftLowerBound = center.getY() + (x - minimumX) * -SLOPE;
        double rightUpperBound = center.getY() + (x - maximumX) * -SLOPE;
        double rightLowerBound = center.getY() + (x - maximumX) * SLOPE;

        return within(y, leftLowerBound, leftUpperBound) && within(y, rightLowerBound, rightUpperBound);
    }

    /**
     * Not the most effective, but simplest approach.
     * Sample a uniformly distributed point inside the bounding
     * rectangle until it falls within the hexagon.
     *
     * @return A random point within the hexagon.
     */
    public Coordinate getRandomPoint() {
        while (true) {
            double xFactor = RANDOM.nextDouble();
            double yFactor = RANDOM.nextDouble();

            double scaledX = (center.getX() - getHalfWidth()) + getWidth() * xFactor;
            double scaledY = (center.getY() - getHalfHeight()) + getHeight() * yFactor;

            if (contains(scaledX, scaledY)) {
                return Coordinate.of(scaledX, scaledY);
            }
        }
    }

    private static boolean within(double value, double lower, double upper) {
        return value >= lower && value <= upper;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Hexagon hexagon = (Hexagon) o;
        return Double.compare(hexagon.outerDiameter, outerDiameter) == 0 && center.equals(hexagon.center);
    }

    @Override
    public int hashCode() {
        return Objects.hash(center, outerDiameter);
    }

    @Override
    public String toString() {
        return "Hexagon{" +
                "center=" + center +
                ", outerDiameter=" + outerDiameter +
                '}';
    }

    public static double computeInnerDiameter(double outerDiameter) {
        return outerDiameter / CIRCUMCIRCLE_RADIUS_APOTHEM_RATIO;
    }

    public static double computeOuterRadius(double outerDiameter) {
        return outerDiameter / 2;
    }

    public static double computeInnerRadius(double outerDiameter) {
        return computeInnerDiameter(outerDiameter) / 2;
    }

    public static double computeHeight(double outerDiameter) {
        return computeInnerDiameter(outerDiameter);
    }

    public static double computeWidth(double outerDiameter) {
        return outerDiameter;
    }

    public static double computeHalfWidth(double outerDiameter) {
        return computeWidth(outerDiameter) / 2;
    }

    public static double computeHalfHeight(double outerDiameter) {
        return computeHeight(outerDiameter) / 2;
    }

    public static double computeSideLength(double outerDiameter) {
        return computeOuterRadius(outerDiameter);
    }

    public static Hexagon create(Coordinate center, double diameter) {
        return new Hexagon(center, diameter);
    }

    public static Hexagon create(double x, double y, double diameter) {
        return create(Coordinate.of(x, y), diameter);
    }

    public static void main(String[] args) {
        double epsilon = 1E-4;

        Map<Coordinate, Boolean> testCoordinates = new LinkedHashMap<>(Map.ofEntries(
                Map.entry(Coordinate.of(0, 0), false),
                Map.entry(Coordinate.of(0, 1), false),
                Map.entry(Coordinate.of(1, 0), false),
                Map.entry(Coordinate.of(1, 1), false),

                Map.entry(Coordinate.of(0.5, 0), false),
                Map.entry(Coordinate.of(0.5, 1), false),
                Map.entry(Coordinate.of(0, 0.5), true),
                Map.entry(Coordinate.of(1, 0.5), true),

                Map.entry(Coordinate.of(0.5, 0.5), true),
                Map.entry(Coordinate.of(0.4, 0.4), true),
                Map.entry(Coordinate.of(0.4, 0.6), true),
                Map.entry(Coordinate.of(0.6, 0.4), true),
                Map.entry(Coordinate.of(0.6, 0.6), true),

                Map.entry(Coordinate.of(0.25, (1 / SLOPE) - epsilon), true),
                Map.entry(Coordinate.of(0.25, 1 - (1 / SLOPE) + epsilon), true),
                Map.entry(Coordinate.of(0.75, (1 / SLOPE) - epsilon), true),
                Map.entry(Coordinate.of(0.75, 1 - (1 / SLOPE) + epsilon), true),

                Map.entry(Coordinate.of(0.125, (0.5 / SLOPE) - epsilon), true),
                Map.entry(Coordinate.of(0.125, 1 - (0.5 / SLOPE) + epsilon), true),
                Map.entry(Coordinate.of(0.875, (0.5 / SLOPE) - epsilon), true),
                Map.entry(Coordinate.of(0.875, 1 - (0.5 / SLOPE) + epsilon), true)
        ));

        Hexagon subject = Hexagon.create(0.5, 0.5, 1);

        testCoordinates.forEach((coordinate, expectation) -> {
            boolean result = subject.contains(coordinate);
            System.out.printf(
                    "Testing coordinate %s: %s - expectation=%s, result=%s%n",
                    Boolean.compare(result, expectation) == 0 ? "SUCCESS" : "ERROR",
                    coordinate,
                    expectation,
                    result
            );
        });

        System.out.println();

        IntStream.range(0, 10).forEach(any -> {
            Coordinate sample = subject.getRandomPoint();

            System.out.println("Randomly selected point: " + sample);
        });
    }
}
