package nl.maastrichtuniversity.dacs.ssa.g14;

import simulation.*;

public class Ambulance implements CProcess, Acceptor<Patient> {
    private static final int RETURN_TO_DOCK_T = 1;

    private int currentLoc ;

    public final int dockLocation;

    /** Patient that is being handled  */
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

    /**
     *	Constructor
     *        Service times are exponentially distributed with mean 30
     *  @param dockLocation The location of the dock for this ambulance
     *	@param q	Queue from which the machine has to take products
     *	@param s	Where to send the completed products
     *	@param e	Eventlist that will manage events
     *	@param n	The name of the machine
     */
    public Ambulance(int dockLocation, PatientQueue q, Acceptor<Patient> s, CEventList e, String n)
    {
        this.dockLocation = currentLoc= dockLocation;

        status='i';
        queue=q;
        sink=s;
        eventlist=e;
        name=n;
        meanProcTime=30;
        queue.askProduct(this);
    }

    /**
     *	Method to have this object execute an event
     *	@param type	The type of the event that has to be executed
     *	@param tme	The current time
     */
    public void execute(int type, double tme)
    {
        if(type == RETURN_TO_DOCK_T){
            onReturnToDock(tme);
            return;
        }

        // show arrival
        System.out.printf("[%s] Patient brought to hospital, time %f\n", name, tme);
        // Remove product from system
        product.stamp(tme,"Production complete",name);
        sink.giveProduct(product);
        product=null;
        // set machine status to idle
        status='i';
        currentLoc = 0;

        // Ask the queue for products
        // TODO: or if shift change imminent
        if(queue.hasProduct())
            queue.askProduct(this);
        else
            returnToDock(tme);
    }

    private void returnToDock(double tme){
        var returnTime = Locations.timeBetween(0,dockLocation);

        eventlist.add(this, RETURN_TO_DOCK_T, tme + returnTime);
        System.out.printf("[%s] No patients, returning to dock @ %d\n", name,  dockLocation);
        status = 'b';
    }

    private void onReturnToDock(double tme){
        // TODO: IF SHIFT CHANGE

        System.out.printf("[%s] Returned to dock\n", name);
        currentLoc = dockLocation;

        status = 'i';
        queue.askProduct(this);
    }

    /**
     *	Let the machine accept a product and let it start handling it
     *	@param p	The product that is offered
     *	@return	true if the product is accepted and started, false in all other cases
     */
    @Override
    public boolean giveProduct(Patient p)
    {
        // Only accept something if the machine is idle
        if(status=='i')
        {
            // accept the product
            product=p;
            // mark starting time
            product.stamp(eventlist.getTime(),"Production started",name);
            System.out.printf("[%s] Going to pick up patient @ %d\n",name,  p.location);
            // start production
            startProduction();
            // Flag that the product has arrived
            return true;
        }
        // Flag that the product has been rejected
        else return false;
    }

    /**
     *	Starting routine for the production
     *	Start the handling of the current product with an exponentionally distributed processingtime with average 30
     *	This time is placed in the eventlist
     */
    private void startProduction()
    {
            // TODO: Processing time
            double duration = drawRandomExponential(meanProcTime) + Locations.timeBetween(product.location,currentLoc) + Locations.timeBetween(product.location, 0);
            // Create a new event in the eventlist
            double tme = eventlist.getTime();
            eventlist.add(this,0,tme+duration); //target,type,time
            // set status to busy
            status='b';
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
