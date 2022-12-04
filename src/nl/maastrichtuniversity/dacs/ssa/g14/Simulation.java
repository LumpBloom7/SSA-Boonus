package nl.maastrichtuniversity.dacs.ssa.g14;

import simulation.*;

public class Simulation {

    public CEventList list;
    public PatientQueue queue;
    public Source sourceA1;
    public Source sourceA2;
    public Source sourceB;
    public Sink sink;
    public Machine mach;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Create an eventlist, keeps track of events happen in the universe
        CEventList l = new CEventList();

        // A queue for the machine
        PatientQueue q = new PatientQueue();

        // A source
        PatientSource A1S = new PatientSource(PatientType.A1, q, l);
        PatientSource A2S = new PatientSource(PatientType.A2, q, l);
        PatientSource BS = new PatientSource(PatientType.B, q, l);

        // A sink
        PatientSink si = new PatientSink("Sink 1");

        for(int i = 0; i < 7; ++i){
            for(int j = 0; j < 5; ++j){
                new Ambulance(i, q, si,l, String.format("Ambulance %d-%d", i, j));
            }
        }
        // start the eventlist
        l.start(1000); // 2000 is maximum time
    }

}
