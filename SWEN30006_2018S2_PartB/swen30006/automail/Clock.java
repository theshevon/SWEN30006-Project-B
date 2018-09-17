package automail;

/**
 * Class that represents the clock for the simulation
 */
public class Clock {
	
	/** Represents the current time **/
    private static int Time = 0;
    
    /** The threshold for the latest time for mail to arrive **/
    public static int LAST_DELIVERY_TIME;

    /** 
     * @return The current time (in terms of clock ticks)
     */
    public static int Time() {
    	return Time;
    }
    
    /**
     * Increments the clock time
     */
    public static void Tick() {
    	Time++;
    }
}
