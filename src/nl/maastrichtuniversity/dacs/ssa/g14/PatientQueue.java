package nl.maastrichtuniversity.dacs.ssa.g14;

import simulation.Acceptor;

import java.util.*;

public class PatientQueue implements Acceptor<Patient>
{
    /** List in which the products are kept */
    private final List<Patient> products;
    /** Requests from machine that will be handling the products */
    private final List<Ambulance> requests;

    /**
     *	Initializes the queue and introduces a dummy machine
     *	the machine has to be specified later
     */
    public PatientQueue()
    {
        products = new ArrayList<>();
        requests = new ArrayList<>();
    }

    public boolean hasProduct(){
        return !products.isEmpty();
    }

    /**
     *	Asks a queue to give a product to a machine
     *	True is returned if a product could be delivered; false if the request is queued
     */
    public boolean askProduct(Ambulance machine)
    {
        if (products.isEmpty()) {
            requests.add(machine);
            return false;
        }

        machine.giveProduct(products.get(0));
        products.remove(0);
        return true;
    }

    /**
     *	Offer a product to the queue
     *	It is investigated whether a machine wants the product, otherwise it is stored
     */
    public boolean giveProduct(Patient p) {
        if (products.isEmpty()) {
            var bestAmbulance = tryFindBestAmbulance(p);

            if (bestAmbulance.isPresent()) {
                var index = bestAmbulance.getAsInt();
                var ambulance = requests.get(index);
                ambulance.giveProduct(p);
                requests.remove(index);
                return true;
            }
        }

        products.add(p);
        products.sort(Patient::compareTo);
        return false;
    }

    public boolean tryPush() {
        int processed = 0;

        while (tryAdvance()) {
            processed++;
        }

        return processed != 0;
    }

    private boolean tryAdvance() {
        if (products.isEmpty()) {
            return false;
        }

        Patient patient = products.get(0);
        OptionalInt index = tryFindBestAmbulance(patient);

        if (index.isEmpty()) {
            return false;
        }

        products.remove(0);
        Ambulance ambulance = requests.get(index.getAsInt());
        requests.remove(index.getAsInt());
        ambulance.giveProduct(patient);
        return true;
    }

    private OptionalInt tryFindBestAmbulance(Patient p) {
        double minDistance = Double.POSITIVE_INFINITY;
        int index = -1;
        for (int i = 0; i < requests.size(); i++) {
            Ambulance ambulance = requests.get(i);

            if (!ambulance.isAssignable()) {
                continue;
            }

            double distance = Locations.timeBetween(p.coordinate, ambulance.location);
            if (distance < minDistance) {
                index = i;
                minDistance = distance;
            }
        }

        return index == -1 ? OptionalInt.empty() : OptionalInt.of(index);
    }
}
