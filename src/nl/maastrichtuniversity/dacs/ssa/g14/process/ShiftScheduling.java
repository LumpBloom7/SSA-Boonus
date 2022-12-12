package nl.maastrichtuniversity.dacs.ssa.g14.process;

import nl.maastrichtuniversity.dacs.ssa.g14.PatientQueue;
import nl.maastrichtuniversity.dacs.ssa.g14.domain.Region;
import nl.maastrichtuniversity.dacs.ssa.g14.domain.Schedule;
import simulation.CEventList;
import simulation.CProcess;

import java.util.List;

public class ShiftScheduling implements CProcess {
    private static final int FOUR_HOURS = 4 * 60;

    private final CEventList timeline;
    private final Schedule schedule;
    private final List<Region> regions;
    private final PatientQueue queue;

    public ShiftScheduling(CEventList timeline, Schedule schedule, List<Region> regions, PatientQueue queue) {
        this.timeline = timeline;
        this.schedule = schedule;
        this.regions = regions;
        this.queue = queue;
    }

    public void scheduleAt(double time) {
        timeline.add(this, EventTypes.SHIFT_CHANGE, time);
    }

    @Override
    public void execute(int type, double time) {
        int capacity = schedule.apply((int) time);
        System.out.printf("[%f] Changing shifts, new capacity: %d%n", time, capacity);

        for (Region region : regions) {
            region.setCapacity(capacity);
        }
        queue.tryPush();

        scheduleAt(time + FOUR_HOURS);
    }
}
