package simulation;

/**
 *	Blueprint for processes
 *	Classes that implement this interface can process events
 *	@author Joel Karel
 *	@version %I%, %G%
 */
public interface CProcess
{
	/**
	*	Method to have this object process an event
	*	@param type	The type of the event that has to be executed
	*	@param time	The current time
	*/
	void execute(int type, double time);
}
