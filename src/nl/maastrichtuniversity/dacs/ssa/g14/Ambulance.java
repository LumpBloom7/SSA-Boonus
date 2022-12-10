package nl.maastrichtuniversity.dacs.ssa.g14;

import simulation.*;

public class Ambulance implements CProcess, Acceptor<Patient> {
    private static final int RETURN_TO_DOCK_T = 1;
    private static final int SHIFT_CHANGE_T = 2;
    private static final int SHIFT_CHANGE_COMPLETE_T = 3;

    private int currentLoc;

    public final int dockLocation;

    /** Patient that is being handled */
    private Patient product;

    /** Eventlist that will manage events */
    private final CEventList eventlist;

    /** Queue from which the machine has to take products */
    private PatientQueue queue;

    /** Sink to dump products */
    private Acceptor<Patient> sink;

    /** Status of the ambulance (d=at dock, h = at ambulance, b=busy) */
    private char status;

    /** Machine name */
    private final String name;

    /** Mean processing time */
    private final double meanProcTime;

    private double shiftStartTime = 0;

    /**
     * Constructor
     * Service times are exponentially distributed with mean 30
     *
     * @param dockLocation The location of the dock for this ambulance
     * @param q            Queue from which the machine has to take products
     * @param s            Where to send the completed products
     * @param e            Eventlist that will manage events
     * @param n            The name of the machine
     */
    public Ambulance(int dockLocation, PatientQueue q, Acceptor<Patient> s, CEventList e, String n) {
        this.dockLocation = currentLoc = dockLocation;

        status = 'i';
        queue = q;
        sink = s;
        eventlist = e;
        name = n;
        meanProcTime = 0.083;
        queue.askProduct(this);

        onShiftChangeEnd(0);
    }

    /**
     * Method to have this object execute an event
     *
     * @param type The type of the event that has to be executed
     * @param tme  The current time
     */
    public void execute(int type, double tme) {
        if (type == SHIFT_CHANGE_T) {
            if (status == 'b')
                return;

            onShiftChangeBegin(tme);
            return;
        }

        if (type == SHIFT_CHANGE_COMPLETE_T) {
            onShiftChangeEnd(tme);
            return;
        }

        if (type == RETURN_TO_DOCK_T) {
            onReturnToDock(tme);
            return;
        }

        // show arrival
        System.out.printf("[%s] Patient brought to hospital ; Time: %f\n", name, tme);
        // Remove product from system
        product.stamp(tme, "Production complete", name);
        sink.giveProduct(product);
        product = null;
        // set machine status to idle
        status = 'i';
        currentLoc = 0;

        if (shiftChangeImminent(tme) || !queue.hasProduct())
            returnToDock(tme, shiftChangeImminent(tme));

        queue.askProduct(this);

    }

    private boolean shiftChangeImminent(double t) {
        return (t - shiftStartTime) >= 8 - 0.1f - meanProcTime;
    }

    private void returnToDock(double tme, boolean changeShifts) {
        var returnTime = Locations.timeBetween(0, dockLocation);

        if (changeShifts) {
            eventlist.add(this, RETURN_TO_DOCK_T, tme + returnTime);
            System.out.printf("[%s] Shift change imminent, returning to dock @ %d ; Time: %f \n", name, dockLocation,
                    tme);
        } else {
            eventlist.add(this, RETURN_TO_DOCK_T, tme + returnTime);
            System.out.printf("[%s] No patients, returning to dock @ % d; Time: %f \n", name, dockLocation, tme);
        }

        status = 'b';
    }

    private void onReturnToDock(double tme) {

        System.out.printf("[%s] Returned to dock ; Time: %f \n", name, tme);
        currentLoc = dockLocation;

        if (shiftChangeImminent(tme)) {
            onShiftChangeBegin(tme);
        } else {
            status = 'i';
            queue.askProduct(this);
        }
    }

    private void onShiftChangeBegin(double tme) {
        System.out.printf("[%s] Changing crews ; Time: %f\n", name, tme);

        eventlist.add(this, SHIFT_CHANGE_COMPLETE_T, tme + drawRandomExponential(meanProcTime));
        status = 'b';
    }

    private void onShiftChangeEnd(double tme) {

        shiftStartTime = Math.round(tme / 8) * 8;

        // We can't start a shift after during the night shift
        // Assuming hour 0 is 7 AM
        if (shiftStartTime > 16) {
            System.out.printf("[%s] Night shift, no crew assigned; Time: %f\n", name, tme);
            return;
        }

        queue.askProduct(this); // Remind the queue that we are active again

        // Queue the next shift change
        eventlist.add(this, SHIFT_CHANGE_T, shiftStartTime + 8 - 0.1f);

        status = 'i';
    }

    /**
     * Let the machine accept a product and let it start handling it
     *
     * @param p The product that is offered
     * @return true if the product is accepted and started, false in all other cases
     */
    @Override
    public boolean giveProduct(Patient p) {
        // Only accept something if the machine is idle
        if (status == 'i') {
            // accept the product
            product = p;
            // mark starting time
            product.stamp(eventlist.getTime(), "Production started", name);
            System.out.printf("[%s] Going to pick up patient @ %d\n", name, p.location);
            // start production
            startProduction();
            // Flag that the product has arrived
            return true;
        }
        // Flag that the product has been rejected
        else
            return false;
    }

    /**
     * Starting routine for the production
     * Start the handling of the current product with an exponentionally distributed
     * processingtime with average 0.083
     * This time is placed in the eventlist
     */
    private void startProduction() {
        // duration = processingTime + Time to get there + time to get back;
        double duration = drawRandomExponential(meanProcTime) + Locations.timeBetween(product.location, currentLoc)
                + Locations.timeBetween(product.location, 0);

        // Create a new event in the eventlist
        double tme = eventlist.getTime();
        eventlist.add(this, 0, tme + duration); // target,type,time
        // set status to busy
        status = 'b';
    }

    public static double drawRandomExponential(double mean) {
        // draw a [0,1] uniform distributed number
        double u = Math.random();
        // Convert it into a exponentially distributed random variate with mean 33
        double res = -mean * Math.log(u);
        return res;
    }
}
