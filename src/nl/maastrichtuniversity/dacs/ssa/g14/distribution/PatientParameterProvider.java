package nl.maastrichtuniversity.dacs.ssa.g14.distribution;

import java.util.function.DoubleUnaryOperator;

public class PatientParameterProvider {
    public static double compute(double elapsedMinutes) {
        double elapsedHours = elapsedMinutes / 60;
        return 3 - 2 * Math.sin(5 * (Math.PI + elapsedHours) / (6 * Math.PI));
    }

    public static DoubleUnaryOperator asProvider() {
        return PatientParameterProvider::compute;
    }
}
