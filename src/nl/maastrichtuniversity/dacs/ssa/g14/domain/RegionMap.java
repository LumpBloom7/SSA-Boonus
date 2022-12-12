package nl.maastrichtuniversity.dacs.ssa.g14.domain;

import nl.maastrichtuniversity.dacs.ssa.g14.Locations;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RegionMap {
    private static final Random RANDOM = new Random();

    private final List<Region> regions;

    public RegionMap(List<Region> regions) {
        this.regions = regions;
    }

    public List<Region> getRegions() {
        return new ArrayList<>(regions);
    }

    public Region getRandomRegion() {
        int index = RANDOM.nextInt(0, regions.size());
        return regions.get(index);
    }

    public static RegionMap standard(int capacity) {
        List<Region> regions = new ArrayList<>();
        for (int i = 0; i < Locations.MAP.size(); i++) {
            regions.add(new Region(i, Locations.MAP.get(i), capacity));
        }

        return new RegionMap(regions);
    }
}
