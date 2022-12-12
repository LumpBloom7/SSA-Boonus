package nl.maastrichtuniversity.dacs.ssa.g14;

import nl.maastrichtuniversity.dacs.ssa.g14.geometry.Coordinate;
import nl.maastrichtuniversity.dacs.ssa.g14.geometry.Hexagon;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

/**
 * Schematic for locations:
 * <pre>
 *       222
 *      22222
 *  111 22222 333
 * 11111 222 33333
 * 11111 000 33333
 *  111 00000 333
 *  444 00000 666
 * 44444 000 66666
 * 44444 555 66666
 *  444 55555 666
 *      55555
 *       555
 * </pre>
 */
public class Locations {
    private static final double DIAMETER = 10;
    private static final double WIDTH = Hexagon.computeWidth(DIAMETER);
    private static final double HEIGHT = Hexagon.computeHeight(DIAMETER);
    private static final double HALF_WIDTH = Hexagon.computeHalfWidth(DIAMETER);
    private static final double HALF_HEIGHT = Hexagon.computeHalfHeight(DIAMETER);
    private static final double SIDE_LENGTH = Hexagon.computeSideLength(DIAMETER);

    private static Hexagon region(double x, double y) {
        return Hexagon.create(x, y, DIAMETER);
    }

    public static final Hexagon LEFT_UPPER_REGION = region(HALF_WIDTH, HEIGHT);
    public static final Hexagon LEFT_LOWER_REGION = LEFT_UPPER_REGION.translateY(HEIGHT);
    public static final Hexagon TOP_REGION = region(WIDTH + (SIDE_LENGTH / 2), HALF_HEIGHT);
    public static final Hexagon CENTER_REGION = TOP_REGION.translateY(HEIGHT);
    public static final Hexagon BOTTOM_REGION = CENTER_REGION.translateY(HEIGHT);
    public static final Hexagon RIGHT_UPPER_REGION = LEFT_UPPER_REGION.translateX(WIDTH + SIDE_LENGTH);
    public static final Hexagon RIGHT_BOTTOM_REGION = RIGHT_UPPER_REGION.translateY(HEIGHT);

    public static final Hexagon HOSPITAL_REGION = CENTER_REGION;

    public static final int HOSPITAL_LOCATION = 0;
    public static final List<Hexagon> MAP = List.of(
            CENTER_REGION,
            LEFT_UPPER_REGION,
            TOP_REGION,
            RIGHT_UPPER_REGION,
            LEFT_LOWER_REGION,
            BOTTOM_REGION,
            RIGHT_BOTTOM_REGION
    );

    private static final int[][] ADJACENCY_MATRIX = new int[][] {
            // Node 0 dist
            {
                    0, 1, 1, 1, 1, 1, 1
            },
            // Node 1 dist
            {
                    1, 0, 1, 2, 2, 2, 1
            },
            // Node 2
            {
                    1, 1, 0, 1, 2, 2, 2
            },
            // Node 3
            {
                    1, 2, 1, 0, 1, 2, 2
            },
            // Node 4
            {
                    1, 2, 2, 1, 0, 1, 2
            },
            // Node 5
            {
                    1, 2, 2, 2, 1, 0, 1
            },
            // Node 6
            {
                    1, 1, 2, 2, 2, 1, 0
            }
    };

    public static OptionalInt tryGetRegionIndex(Coordinate coordinate) {
        for (int i = 0; i < MAP.size(); i++) {
            if (MAP.get(i).contains(coordinate)) {
                return OptionalInt.of(i);
            }
        }

        return OptionalInt.empty();
    }

    public static int getRegionIndex(Coordinate coordinate) {
        return tryGetRegionIndex(coordinate).orElseThrow(() -> new IllegalArgumentException("No region contains coordinate " + coordinate));
    }

    public static Optional<Hexagon> tryGetRegion(Coordinate coordinate) {
        OptionalInt index = tryGetRegionIndex(coordinate);

        return index.isEmpty() ? Optional.empty() : Optional.of(MAP.get(index.getAsInt()));
    }

    public static Hexagon getRegion(Coordinate coordinate) {
        return MAP.get(getRegionIndex(coordinate));
    }

    private static final double DURATION_PER_UNIT = 1;

    public static double getDistanceBetween(Coordinate alpha, Coordinate beta) {
        return Math.abs(alpha.getX() - beta.getX()) + Math.abs(alpha.getY() - beta.getY());
    }

    public static double timeBetween(Coordinate alpha, Coordinate beta) {
        return getDistanceBetween(alpha, beta) * DURATION_PER_UNIT;
    }
}
