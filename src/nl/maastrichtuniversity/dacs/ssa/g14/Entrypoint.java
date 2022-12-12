package nl.maastrichtuniversity.dacs.ssa.g14;

import nl.maastrichtuniversity.dacs.ssa.g14.domain.Region;
import nl.maastrichtuniversity.dacs.ssa.g14.domain.RegionMap;
import nl.maastrichtuniversity.dacs.ssa.g14.domain.Schedule;
import nl.maastrichtuniversity.dacs.ssa.g14.process.ShiftScheduling;
import simulation.CEventList;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

public class Entrypoint {
    private static final Path RESULTS_DIRECTORY = Path.of("results");
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        List<PatientSink.Results> results = IntStream.range(0, 90)
                .mapToObj(any -> simulateDay())
                .toList();

        Files.createDirectories(RESULTS_DIRECTORY);

        for (PatientType type : PatientType.values()) {
            record(type.name() + ".acceptance.csv", results, single -> single.acceptanceTimes().get(type));
            record(type.name() + ".delivery.csv", results, single -> single.deliveryTimes().get(type));
            record(type.name() + ".pickup.csv", results, single -> single.pickUpTimes().get(type));
        }

        record("A1.15-minutes-fraction.csv", results, single -> List.of(single.a1FifteenMinutesFraction()));
    }

    private static void record(String name, List<PatientSink.Results> results, Function<PatientSink.Results, List<Double>> extractor) throws Exception {
        Iterator<CharSequence> aggregate = results.stream()
                .flatMap(single -> extractor.apply(single).stream())
                .map(value -> (CharSequence) value.toString())
                .iterator();

        Path path = RESULTS_DIRECTORY.resolve(name);
        Files.deleteIfExists(path);
        Files.createFile(path);
        Files.write(path, () -> aggregate);
    }

    public static PatientSink.Results simulateDay() {
        CEventList timeline = new CEventList();
        PatientQueue queue = new PatientQueue();

        Schedule schedule = Schedule.standard();
        RegionMap map = RegionMap.standard(schedule.apply(0));

        new PatientSource(PatientType.A1, queue, timeline, map);
        new PatientSource(PatientType.A2, queue, timeline, map);
        new PatientSource(PatientType.B, queue, timeline, map);

        PatientSink sink = new PatientSink("patients");

        List<Region> regions = map.getRegions();
        new ShiftScheduling(timeline, schedule, regions, queue).scheduleAt(7 * 60);

        for (int i = 0; i < regions.size(); i++) {
            Region region = regions.get(i);
            for (int j = 0; j < 5; j++) {
                Ambulance ambulance = new Ambulance(region, queue, sink, timeline, String.format("Ambulance %d-%d", i, j));
                region.addAmbulance(ambulance);
                queue.askProduct(ambulance);
            }
        }

        timeline.start(1440);

        return sink.getResults();
    }
}
