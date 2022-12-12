package nl.maastrichtuniversity.dacs.ssa.g14.domain;

import nl.maastrichtuniversity.dacs.ssa.g14.Ambulance;
import nl.maastrichtuniversity.dacs.ssa.g14.geometry.Coordinate;
import nl.maastrichtuniversity.dacs.ssa.g14.geometry.Hexagon;

import java.util.ArrayList;
import java.util.List;

public class Region {
    private final int id;
    private final Hexagon area;
    private final List<Ambulance> ambulances = new ArrayList<>();
    private int capacity;

    public Region(int id, Hexagon area, int crews) {
        this.id = id;
        this.area = area;
        this.capacity = crews;
    }

    public int getId() {
        return id;
    }

    public Hexagon getArea() {
        return area;
    }

    public Coordinate getRandomPoint() {
        return area.getRandomPoint();
    }

    public Coordinate getCenter() {
        return area.getCenter();
    }

    public int getCapacity() {
        return capacity;
    }

    public Region setCapacity(int crews) {
        this.capacity = crews;
        synchronizeAvailability();
        return this;
    }

    public Region addAmbulance(Ambulance ambulance) {
        ambulances.add(ambulance);
        synchronizeAvailability();
        return this;
    }

    private void synchronizeAvailability() {
        for (int i = 0; i < ambulances.size(); i++) {
            boolean assignable = i < capacity;
            ambulances.get(i).setAssignable(assignable);
        }
    }
}
