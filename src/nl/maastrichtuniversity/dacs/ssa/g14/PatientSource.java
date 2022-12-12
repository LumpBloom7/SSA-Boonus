package nl.maastrichtuniversity.dacs.ssa.g14;

import nl.maastrichtuniversity.dacs.ssa.g14.distribution.PatientParameterProvider;
import nl.maastrichtuniversity.dacs.ssa.g14.domain.Region;
import nl.maastrichtuniversity.dacs.ssa.g14.domain.RegionMap;
import nl.maastrichtuniversity.dacs.ssa.g14.geometry.Coordinate;
import nl.maastrichtuniversity.dacs.ssa.g14.process.Stamps;
import simulation.Acceptor;
import simulation.CEventList;
import simulation.CProcess;

public class PatientSource implements CProcess {
    /** Eventlist that will be requested to construct events */
    private final CEventList timeline;

    /** Queue that buffers products for the machine */
    private final Acceptor<Patient> queue;

    /** Name of the source */
    private final String name;

    private final PatientType patientType;
    private final RegionMap regions;

    public PatientSource(PatientType type, Acceptor<Patient> q, CEventList l, RegionMap regions) {
        timeline = l;
        queue = q;
        name = type.toString() + " source";
        patientType = type;
        this.regions = regions;

        scheduleNextArrival(0);
    }

    @Override
    public void execute(int type, double time) {
        Region region = regions.getRandomRegion();
        Coordinate coordinate = region.getRandomPoint();
        System.out.printf("[%f] New %s patient at coordinate = %s (region=%d)%n", time, patientType, coordinate, region.getId());
        // give arrived product to queue
        Patient p = new Patient(patientType, coordinate, time);
        p.stamp(time, Stamps.PATIENT_CREATED, name);
        queue.giveProduct(p);
        scheduleNextArrival(time);
    }

    private void scheduleNextArrival(double time) {
        timeline.add(this, 0, time + drawRandomExponential(time));
    }

    public static double drawRandomExponential(double t) {
        // Rate is measured in patients / hour, but the internal computation is
        // done in minutes, so the rate is divided by 60.
        double mean = 1 / (PatientParameterProvider.compute(t) / 60);
        // draw a [0,1] uniform distributed number
        double u = Math.random();
        // Convert it into a exponentially distributed random variate with selected mean
        return -mean * Math.log(u);
    }
}
