package simulation;

import java.util.ArrayList;
import java.util.List;

/**
 *	Product that is send trough the system
 *	@author Joel Karel
 *	@version %I%, %G%
 */
public class Product
{
	/** Stamps for the products */
	private final List<Double> times = new ArrayList<>();
	private final List<String> events = new ArrayList<>();
	private final List<String> stations = new ArrayList<>();
	
	
	public void stamp(double time, String event, String station)
	{
		times.add(time);
		events.add(event);
		stations.add(station);
	}
	
	public List<Double> getTimes()
	{
		return times;
	}

	public List<String> getEvents()
	{
		return events;
	}

	public List<String> getStations()
	{
		return stations;
	}
}
