package nl.maastrichtuniversity.dacs.ssa.g14;

import simulation.Acceptor;

import java.util.ArrayList;
import java.util.Comparator;

public class PatientQueue implements Acceptor<Patient>
{
    /** List in which the products are kept */
    private ArrayList<Patient> row;
    /** Requests from machine that will be handling the products */
    private ArrayList<Ambulance> requests;

    /**
     *	Initializes the queue and introduces a dummy machine
     *	the machine has to be specified later
     */
    public PatientQueue()
    {
        row = new ArrayList<>();
        requests = new ArrayList<>();
    }

    public boolean hasProduct(){
        return !row.isEmpty();
    }

    /**
     *	Asks a queue to give a product to a machine
     *	True is returned if a product could be delivered; false if the request is queued
     */
    public boolean askProduct(Ambulance machine)
    {
        if(row.isEmpty()){
            requests.add(machine);
            return true;
        }

        var bestPatient = findBestPatientFor(machine);
        machine.giveProduct(bestPatient);
        row.remove(bestPatient);
        return true;
    }

    /**
     *	Offer a product to the queue
     *	It is investigated whether a machine wants the product, otherwise it is stored
     */
    public boolean giveProduct(Patient p)
    {
        // Check if the machine accepts it
        if(requests.isEmpty()) {
            row.add(p);
            return true;
        }

        var bestAmbulance = findBestAmbulanceFor(p);
        bestAmbulance.giveProduct(p);
        requests.remove(bestAmbulance);

        return true;
    }

    private Ambulance findBestAmbulanceFor(Patient p){
        return requests.stream().min(Comparator.comparingDouble(lhs -> Locations.timeBetween(p.location, lhs.dockLocation))).get();
    }

    private Patient findBestPatientFor(Ambulance a){
        // TODO: Maybe take distance into account?
        return row.stream().min(Patient::compareTo).get();
    }
}