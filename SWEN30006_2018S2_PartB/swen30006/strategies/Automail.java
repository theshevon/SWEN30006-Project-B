package strategies;

import java.util.ArrayList;
import java.util.List;

import automail.BigRobot;
import automail.CarefulRobot;
import automail.IMailDelivery;
import automail.Robot;
import automail.RobotType;
import automail.StandardRobot;
import automail.WeakRobot;

public class Automail {
	      
    public ArrayList<Robot> robots;
    public IMailPool mailPool;
    
    public Automail(IMailPool mailPool, IMailDelivery delivery, List<RobotType> robotTypes) {
    	    	
    	/** Initialize the MailPool */
    	this.mailPool = mailPool;
    	
    	robots = new ArrayList<Robot>();
    	for (RobotType type : robotTypes) {
    		switch (type) {
    			case Big: 
    				robots.add(new BigRobot(delivery, mailPool));
    				break;
    			case Standard:
    				robots.add(new StandardRobot(delivery, mailPool));
    				break;
    			case Weak:
    				robots.add(new WeakRobot(delivery, mailPool));
    				break;
    			case Careful:
    				robots.add(new CarefulRobot(delivery, mailPool));
    				break;
    		}
    	}
 
    }
    
}
