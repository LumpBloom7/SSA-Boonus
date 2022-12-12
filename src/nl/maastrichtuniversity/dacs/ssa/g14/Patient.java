package nl.maastrichtuniversity.dacs.ssa.g14;

import nl.maastrichtuniversity.dacs.ssa.g14.geometry.Coordinate;
import simulation.Product;

public class Patient extends Product implements Comparable<Patient> {
    public final PatientType type;
    public final double appointmentTime;
    public final Coordinate coordinate;

    public Patient(PatientType type, Coordinate coordinate, double appointmentTime) {
        this.type = type;
        this.coordinate = coordinate;
        this.appointmentTime = appointmentTime;
    }

    public PatientType getType() {
        return type;
    }

    @Override
    public int compareTo(Patient o) {
        if (o == this)
            return 0;

        int initial = Integer.compare(type.priorityLevel, o.type.priorityLevel);

        if (initial != 0) {
            return initial;
        }

        return Double.compare(appointmentTime, o.appointmentTime);
    }
}
