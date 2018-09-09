package strategies;

import java.util.ArrayList;
import java.util.List;

import automail.IMailDelivery;
import automail.Robot;
import automail.RobotType;

public class Automail {
	      
    public ArrayList<Robot> robots;
    public IMailPool mailPool;
    
    public Automail(IMailPool mailPool, IMailDelivery delivery, List<RobotType> robotTypes) {
    	// Swap between simple provided strategies and your strategies here
    	    	
    	/** Initialize the MailPool */
    	this.mailPool = mailPool;
    	
    	/** Initialize robots */
    	robots = new ArrayList<Robot>();
    	for (RobotType type : robotTypes) {
    		robots.add(new Robot(delivery, mailPool, type));
    	}
 
    }
    
}
