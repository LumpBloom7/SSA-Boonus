package nl.maastrichtuniversity.dacs.ssa.g14;

import nl.maastrichtuniversity.dacs.ssa.g14.distribution.ProbabilityDistributionFunction;
import nl.maastrichtuniversity.dacs.ssa.g14.domain.Region;
import nl.maastrichtuniversity.dacs.ssa.g14.geometry.Coordinate;
import nl.maastrichtuniversity.dacs.ssa.g14.process.Stamps;
import nl.maastrichtuniversity.dacs.ssa.g14.process.EventTypes;
import simulation.Acceptor;
import simulation.CEventList;
import simulation.CProcess;

public class Ambulance implements CProcess, Acceptor<Patient> {
    // Erlang-3 with lambda = 1 as per document
    private static final ProbabilityDistributionFunction PROCESSING_TIME_GENERATOR = ProbabilityDistributionFunction.Erlang.of(3, 1);
    private static final char STATUS_IDLE = 'i';
    private static final char STATUS_BUSY = 'b';
    private static final char STATUS_RETURNING = 'r';

    /** Eventlist that will manage events */
    private final CEventList timeline;

    /**
     * Dock ambulance belongs to
     */
    public final Region dock;
    public Coordinate location;

    /** Patient that is being handled */
    private Patient product;

    /** Queue from which the machine has to take products */
    private PatientQueue queue;

    /** Sink to dump products */
    private Acceptor<Patient> sink;

    /** Status of the ambulance (d=at dock, h = at ambulance, b=busy) */
    private char status;

    /** Machine name */
    private final String name;

    private boolean assignable = false;

    /**
     * Constructor
     * Service times are exponentially distributed with mean 30
     *
     * @param dock The location of the dock for this ambulance
     * @param q    Queue from which the machine has to take products
     * @param s    Where to send the completed products
     * @param e    Eventlist that will manage events
     * @param n    The name of the machine
     */
    public Ambulance(Region dock, PatientQueue q, Acceptor<Patient> s, CEventList e, String n) {
        this.dock = dock;
        location = dock.getCenter();
        status = STATUS_IDLE;
        queue = q;
        sink = s;
        timeline = e;
        name = n;
    }

    /**
     * Method to have this object execute an event
     *
     * @param type The type of the event that has to be executed
     * @param time  The current time
     */
    public void execute(int type, double time) {
        switch (type) {
            case EventTypes.PATIENT_PICKED_UP -> {
                System.out.printf("[%f | %s] Patient picked up%n", time, name);
                product.stamp(time, Stamps.PATIENT_PICKED_UP, name);
                status = STATUS_BUSY;
                double deliveryTime = Locations.timeBetween(product.coordinate, Locations.HOSPITAL_REGION.getCenter());

                timeline.add(this, EventTypes.PATIENT_DELIVERED, time + deliveryTime);
            }
            case EventTypes.PATIENT_DELIVERED -> {
                location = Locations.HOSPITAL_REGION.getCenter();
                // Remove product from system
                product.stamp(time, Stamps.PATIENT_DELIVERED, name);
                sink.giveProduct(product);
                product = null;
                // set machine status to returning
                status = STATUS_RETURNING;
                if (queue.askProduct(this)) {
                    System.out.printf(
                            "[%f | %s] Patient brought to hospital, instantly got new request for x=%f y=%f (region: %d)%n",
                            time,
                            name,
                            product.coordinate.getX(),
                            product.coordinate.getY(),
                            Locations.tryGetRegionIndex(product.coordinate).orElse(-1)
                    );
                    return;
                }
                // show arrival
                double returnTime = Locations.timeBetween(Locations.HOSPITAL_REGION.getCenter(), dock.getCenter());
                System.out.printf("[%f | %s] Patient brought to hospital, going back, eta: %f%n", time, name, returnTime);

                if (returnTime == 0) {
                    timeline.add(this, EventTypes.AMBULANCE_RETURNED, time);
                } else {
                    timeline.add(this, EventTypes.AMBULANCE_ADVANCED, time + 1);
                }
            }
            case EventTypes.AMBULANCE_ADVANCED -> {
                if (isBusy()) {
                    // there was a patient request satisfied
                    return;
                }

                double xOffset = location.getX() - dock.getCenter().getX();
                double yOffset = location.getY() - dock.getCenter().getY();

                double xMovementSign = xOffset == 0 ? 1 : -(xOffset / Math.abs(xOffset));
                double xMovementDistance = Math.min(Math.abs(xOffset), 1);
                double yMovementSign = yOffset == 0 ? 1 : -(yOffset / Math.abs(yOffset));
                double yMovementDistance = Math.min(Math.abs(yOffset), 1 - xMovementDistance);

                location = location.translate(xMovementSign * xMovementDistance, yMovementSign * yMovementDistance);

                if (xMovementDistance + yMovementDistance < 1) {
                    timeline.add(this, EventTypes.AMBULANCE_RETURNED, time);
                } else {
                    timeline.add(this, EventTypes.AMBULANCE_ADVANCED, time + 1);
                }
            }
            case EventTypes.AMBULANCE_RETURNED -> {
                if (isBusy()) {
                    // there was a patient request satisfied
                    return;
                }

                System.out.printf("[%f | %s] Returned to dock%n", time, name);
                location = dock.getCenter();
                status = STATUS_IDLE;
            }
        }
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
        if (status != STATUS_BUSY) {
            // accept the product
            product = p;
            // mark starting time
            product.stamp(timeline.getTime(), Stamps.PATIENT_ACCEPTED, name);
            System.out.printf(
                    "[%f | %s] Going to pick up patient at x=%f y=%f (region: %d)%n",
                    timeline.getTime(),
                    name,
                    p.coordinate.getX(),
                    p.coordinate.getY(),
                    Locations.tryGetRegionIndex(p.coordinate).orElse(-1)
            );
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
     * Start the handling of the current product with an erlang-3 based
     * processing time.
     * This time is placed in the eventlist
     */
    private void startProduction() {
        double processingTime = PROCESSING_TIME_GENERATOR.compute(Math.random());
        double movementTime = Locations.timeBetween(product.coordinate, dock.getCenter());
        double pickupTime = processingTime + movementTime;

        // Create a new event in the eventlist
        double time = timeline.getTime();
        timeline.add(this, EventTypes.PATIENT_PICKED_UP, time + pickupTime); // target,type,time
        // set status to busy
        status = STATUS_BUSY;
    }

    public int getStatus() {
        return status;
    }

    public boolean isBusy() {
        return status == STATUS_BUSY;
    }

    public Ambulance setAssignable(boolean status) {
        assignable = status;
        return this;
    }

    public boolean isAssignable() {
        return assignable;
    }
}
