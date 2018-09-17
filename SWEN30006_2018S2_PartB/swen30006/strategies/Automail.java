package strategies;

import java.util.ArrayList;
import java.util.List;

import automail.BigRobot;
import automail.CarefulRobot;
import automail.IMailDelivery;
import automail.Robot;
import automail.Simulation.RobotType;
import automail.StandardRobot;
import automail.WeakRobot;

/**
 * Class used to store the mail pool and create the robots that will deliver the mail
 */
public class Automail {
	      
    private ArrayList<Robot> robots;
    private IMailPool mailPool;
    
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
    
    /**
     * @return the mail pool
     */
    public IMailPool getMailPool() {
    	return mailPool;
    }
    
    /**
     * @return the list of robots being used
     */
    public ArrayList<Robot> getRobots(){
    	return robots;
    }
    
}
