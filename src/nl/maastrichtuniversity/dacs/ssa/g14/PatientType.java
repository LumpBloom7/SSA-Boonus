package nl.maastrichtuniversity.dacs.ssa.g14;

public enum PatientType {
    A1(-1),
    B(0),
    A2(1);
    public final int priorityLevel;

    PatientType(int priorityLevel){
        this.priorityLevel = priorityLevel;
    }
}
