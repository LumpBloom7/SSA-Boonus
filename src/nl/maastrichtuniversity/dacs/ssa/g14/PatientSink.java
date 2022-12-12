
package nl.maastrichtuniversity.dacs.ssa.g14;

import nl.maastrichtuniversity.dacs.ssa.g14.process.Stamps;
import simulation.Acceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A sink
 *
 * @author Joel Karel
 * @version %I%, %G%
 */
public class PatientSink implements Acceptor<Patient> {
    /** All products are kept */
    private ArrayList<Patient> products;
    /** All properties of products are kept */
    private ArrayList<Integer> numbers;
    private ArrayList<Double> times;
    private ArrayList<String> events;
    private ArrayList<String> stations;
    /** Counter to number products */
    private int number;
    /** Name of the sink */
    private String name;

    /**
     * Constructor, creates objects
     */
    public PatientSink(String n) {
        name = n;
        products = new ArrayList<>();
        numbers = new ArrayList<>();
        times = new ArrayList<>();
        events = new ArrayList<>();
        stations = new ArrayList<>();
        number = 0;
    }

    @Override
    public boolean giveProduct(Patient p) {
        number++;
        products.add(p);
        // store stamps
        List<Double> t = p.getTimes();
        List<String> e = p.getEvents();
        List<String> s = p.getStations();
        for (int i = 0; i < t.size(); i++) {
            numbers.add(number);
            times.add(t.get(i));
            events.add(e.get(i));
            stations.add(s.get(i));
        }
        return true;
    }

    private static Map<PatientType, List<Double>> setupMetric() {
        return Map.of(
                PatientType.A1, new ArrayList<>(),
                PatientType.A2, new ArrayList<>(),
                PatientType.B, new ArrayList<>()
        );
    }

    public Results getResults() {
        Map<PatientType, List<Double>> acceptanceTimes = setupMetric();
        Map<PatientType, List<Double>> pickUpTimes = setupMetric();
        Map<PatientType, List<Double>> deliveryTimes = setupMetric();
        int timelyA1 = 0;
        int overdueA1 = 0;

        for (Patient product : products) {
            double createdAt = -1;
            double acceptedAt = -1;
            double pickedUpAt = -1;
            double deliveredAt = -1;

            for (int i = 0; i < product.getEvents().size(); i++) {
                String event = product.getEvents().get(i);
                double time = product.getTimes().get(i);
                switch (event) {
                    case Stamps.PATIENT_CREATED:
                        createdAt = time;
                        break;
                    case Stamps.PATIENT_ACCEPTED:
                        acceptedAt = time;
                        break;
                    case Stamps.PATIENT_PICKED_UP:
                        pickedUpAt = time;
                        break;
                    case Stamps.PATIENT_DELIVERED:
                        deliveredAt = time;
                        break;
                }
            }

            if (acceptedAt == -1 || pickedUpAt == -1 || deliveredAt == -1) {
                // then the patient was not delivered by the end of the simulation
                continue;
            }

            double acceptanceTime = acceptedAt - createdAt;
            double pickUpTime = pickedUpAt - createdAt;
            double deliveryTime = deliveredAt - createdAt;

            acceptanceTimes.get(product.type).add(acceptanceTime);
            pickUpTimes.get(product.type).add(pickUpTime);
            deliveryTimes.get(product.type).add(deliveryTime);

            if (product.type == PatientType.A1) {
                if (pickUpTime <= 15) {
                    timelyA1++;
                } else {
                    overdueA1++;
                }
            }
        }

        int totalA1 = timelyA1 + overdueA1;
        double fraction = totalA1 == 0 ? 1 : timelyA1 / (double) totalA1;

        return new Results(
                acceptanceTimes,
                pickUpTimes,
                deliveryTimes,
                fraction
        );
    }

    public static final class Results {
        private final Map<PatientType, List<Double>> acceptanceTimes;
        private final Map<PatientType, List<Double>> pickUpTimes;
        private final Map<PatientType, List<Double>> deliveryTimes;
        private final double a1FifteenMinutesFraction;

        public Results(
                Map<PatientType, List<Double>> acceptanceTimes,
                Map<PatientType, List<Double>> pickUpTimes,
                Map<PatientType, List<Double>> deliveryTimes,
                double a1FifteenMinutesFraction
        ) {
            this.acceptanceTimes = acceptanceTimes;
            this.pickUpTimes = pickUpTimes;
            this.deliveryTimes = deliveryTimes;
            this.a1FifteenMinutesFraction = a1FifteenMinutesFraction;
        }

        public Map<PatientType, List<Double>> acceptanceTimes() {
            return acceptanceTimes;
        }

        public Map<PatientType, List<Double>> pickUpTimes() {
            return pickUpTimes;
        }

        public Map<PatientType, List<Double>> deliveryTimes() {
            return deliveryTimes;
        }

        public double a1FifteenMinutesFraction() {
            return a1FifteenMinutesFraction;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj == null || obj.getClass() != this.getClass()) {
                return false;
            }
            var that = (Results) obj;
            return Objects.equals(this.acceptanceTimes, that.acceptanceTimes) &&
                    Objects.equals(this.pickUpTimes, that.pickUpTimes) &&
                    Objects.equals(this.deliveryTimes, that.deliveryTimes) &&
                    Double.doubleToLongBits(this.a1FifteenMinutesFraction) == Double.doubleToLongBits(that.a1FifteenMinutesFraction);
        }

        @Override
        public int hashCode() {
            return Objects.hash(acceptanceTimes, pickUpTimes, deliveryTimes, a1FifteenMinutesFraction);
        }

        @Override
        public String toString() {
            return "Results[" +
                    "acceptanceTimes=" + acceptanceTimes + ", " +
                    "pickUpTimes=" + pickUpTimes + ", " +
                    "deliveryTimes=" + deliveryTimes + ", " +
                    "a1FifteenMinutesFraction=" + a1FifteenMinutesFraction + ']';
        }
    }
}
