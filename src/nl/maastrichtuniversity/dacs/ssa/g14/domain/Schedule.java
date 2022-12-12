package nl.maastrichtuniversity.dacs.ssa.g14.domain;

import java.util.Map;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.TreeMap;

public interface Schedule {
    int apply(double minutes);

    class Standard implements Schedule {
        private static final int DAY_MINUTES = 1440;
        private static final NavigableMap<Double, Integer> DISTRIBUTION = new TreeMap<>(
                Map.of(
                        7 * 60.0, 4,
                        11 * 60.0, 5,
                        15 * 60.0, 5,
                        19 * 60.0, 4,
                        23 * 60.0, 3
                )
        );

        @Override
        public int apply(double minutes) {
            double dayTime = minutes % DAY_MINUTES;
            return Optional.ofNullable(DISTRIBUTION.floorEntry(dayTime))
                    .orElse(DISTRIBUTION.floorEntry(dayTime + DAY_MINUTES))
                    .getValue();
        }
    }

    static Schedule standard() {
        return new Standard();
    }
}
