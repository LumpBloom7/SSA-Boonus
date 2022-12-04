package nl.maastrichtuniversity.dacs.ssa.g14;

import simulation.Product;

public class Patient extends Product implements Comparable<Patient>{
    public final PatientType type;
    public final double appointmentTime;

    public final int location;

    public Patient(PatientType type, int loc, double appointmentTime){
        this.type = type;
        this.appointmentTime = appointmentTime;
        this.location = loc;
    }


    @Override
    public int compareTo(Patient o) {
        if (o == this)
            return 0;

        int initial = Integer.compare(type.priorityLevel, o.type.priorityLevel);

        if(initial == 0)
            return Double.compare(appointmentTime, o.appointmentTime);

        return initial;
    }
}
