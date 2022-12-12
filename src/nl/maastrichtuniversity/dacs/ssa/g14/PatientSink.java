
package nl.maastrichtuniversity.dacs.ssa.g14;

import nl.maastrichtuniversity.dacs.ssa.g14.process.Stamps;
import simulation.Acceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
                    case Stamps.PATIENT_CREATED -> createdAt = time;
                    case Stamps.PATIENT_ACCEPTED -> acceptedAt = time;
                    case Stamps.PATIENT_PICKED_UP -> pickedUpAt = time;
                    case Stamps.PATIENT_DELIVERED -> deliveredAt = time;
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

    public record Results(
            Map<PatientType, List<Double>> acceptanceTimes,
            Map<PatientType, List<Double>> pickUpTimes,
            Map<PatientType, List<Double>> deliveryTimes,
            double a1FifteenMinutesFraction
    ) {}
}
