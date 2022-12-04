package nl.maastrichtuniversity.dacs.ssa.g14;

import simulation.Acceptor;
import simulation.CEventList;
import simulation.CProcess;

import java.util.Random;

public class PatientSource implements CProcess {
    /** Eventlist that will be requested to construct events */
    private final CEventList list;

    /** Queue that buffers products for the machine */
    private final Acceptor<Patient> queue;

    /** Name of the source */
    private final String name;

    private final PatientType patientType;

    private final static Random rng = new Random();


    public PatientSource(PatientType type, Acceptor<Patient> q, CEventList l){
        list = l;
        queue = q;
        name = type.toString() + " source";
        patientType = type;

        list.add(this, 0, 0+ drawRandomExponential(33));
    }

    @Override
    public void execute(int type, double tme)
    {
        // show arrival
        int location = rng.nextInt(1, 7);
        System.out.printf("New %s patient at location %d, time %f\n", patientType, location, tme);
        // give arrived product to queue
        Patient p = new Patient(patientType,location , tme);
        p.stamp(tme,"Creation",name);
        queue.giveProduct(p);
        // generate duration

        // TODO: This needs to change
        double nextDuration = drawRandomExponential(33);

        list.add(this,0,tme+nextDuration); //target,type,time
    }

    public static double drawRandomExponential(double mean)
    {
        // draw a [0,1] uniform distributed number
        double u = Math.random();
        // Convert it into a exponentially distributed random variate with mean 33
        double res = -mean*Math.log(u);
        return res;
    }
}
