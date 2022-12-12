package nl.maastrichtuniversity.dacs.ssa.g14.distribution;

public interface ProbabilityDistributionFunction {
    double compute(double uniform);

    class Erlang implements ProbabilityDistributionFunction {
        private final int k;
        private final double lambda;

        public Erlang(int k, double lambda) {
            if (k <= 0) {
                throw new IllegalArgumentException("k parameter can't be less than 1, " + k + " given");
            }

            if (lambda <= 0) {
                throw new IllegalArgumentException("lambda parameter must be greater than zero");
            }

            this.k = k;
            this.lambda = lambda;
        }

        @Override
        public double compute(double uniform) {
            double numerator = Math.pow(lambda, k) * Math.pow(uniform, k - 1) * Math.exp(-lambda * uniform);
            double denominator = factorial(k - 1);
            return numerator / denominator;
        }

        private static long factorial(int value) {
            if (value < 0) {
                throw new IllegalArgumentException("value can't be less than zero, " + value + " given");
            }

            long accumulator = 1;

            for (int multiplier = 2; multiplier <= value; multiplier++) {
                accumulator *= multiplier;
            }

            return accumulator;
        }

        public static ProbabilityDistributionFunction of(int k, double lambda) {
            return new Erlang(k, lambda);
        }

        public static ProbabilityDistributionFunction three(double lambda) {
            return new Erlang(3, lambda);
        }
    }
}
