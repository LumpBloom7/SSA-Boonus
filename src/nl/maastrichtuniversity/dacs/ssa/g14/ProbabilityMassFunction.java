package nl.maastrichtuniversity.dacs.ssa.g14;

public interface ProbabilityMassFunction {
    double calculate(int k);

    /**
     * This probably should be separately defined in cumulative
     * probability function.
     */
    default int getMatchingK(double probability) {
        int k = 1;
        double accumulator = calculate(1);
        while (accumulator < probability) {
            k++;
            accumulator += calculate(k);
        }
        return k - 1;
    }

    class Poisson implements ProbabilityMassFunction {
        private final double lambda;

        public Poisson(double lambda) {
            this.lambda = lambda;
        }

        @Override
        public double calculate(int k) {
            return Math.pow(lambda, k) * Math.pow(Math.E, -lambda) / factorial(k);
        }

        private static long factorial(long k) {
            long accumulator = 1;

            for (int i = 2; i < k; i++) {
                accumulator *= i;
            }

            return accumulator;
        }
    }
}
