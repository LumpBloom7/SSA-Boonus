/**
 *	Event class
 *	Events that facilitate changes in the simulation
 *	@author Joel Karel
 *	@version %I%, %G%
 */
package simulation;

public class CEvent {
	/** The object involved with the event */
	private CProcess target;
	/** The type of the event */
	private int type;
	/** The time on which the event will be executed */
	private double timestamp;

	/**
	*	Constructor for objects
	*	@param target	The object that will process the event
	*	@param type	The type of the event
	*	@param time	The time on which the event will be executed
	*/
	public CEvent(CProcess target, int type, double time) {
		this.target = target;
		this.type = type;
		this.timestamp = time;
	}
	
	/**
	*	Method to signal the target to execute an event
	*/
	public void execute()
	{
		target.execute(type, timestamp);
	}
	
	/**
	*	Method to ask the event at which time it will be executed
	*	@return	The time at which the event will be executed
	*/
	public double getTimestamp()
	{
		return timestamp;
	}
}
