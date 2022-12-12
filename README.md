# SSA course bonus assignment

This is the implementation of the bonus assignment for the SSA course.

## How to run

### Ant

1. Install JDK 11+ and Ant
2. Run `ant` in the project directory
3. Run `java -jar dist/nl.maastrichtuniversity.dacs.ssa.g14.jar`
4. The application will run simulation for 90 days and create `results` 
subdirectory in current location with csv files.

### IDEA and other IDEs

Simply run `nl.maastrichtuniversity.dacs.ssa.g14.Entrypoint#main` 
directly from IDE for the same result

## Internal architecture

The application uses provided framework. The territory is represented
by seven regions, each having five ambulances (machines), one patient
queue (for all three categories, sorted by priority to ensure the 
requested constraints), three patient sources, with the patient sink 
being the only shared resource. There are two process implementations
internally: Ambulance handles its own movement and patient delivery,
and the auxiliary shift change process operates over regions, changing
their capacity at key moments (7:00, 11:00, 15:00, 19:00 and 23:00).
When the capacity is changed, some ambulances may change their status
(from assignable to unassignable and vice versa), and the responsible 
process requests queue to assign ambulances that could have previously
been dormant.

The schedule used for regions is as follows:

- 07:00: 4 crews
- 11:00: 5 crews
- 15:00: 5 crews
- 19:00: 4 crews
- 23:00: 3 crews

Which can be described in a more human-oriented way:

- 23:00 - 07:00: three 8-hour crews
- 07:00 - 11:00: four 8-hour crews
- 11:00 - 15:00: one additional 8-hour crew
- 15:00 - 23:00: four 8-hour crews
