package automail;

import automail.Simulation.RobotType;
import strategies.IMailPool;

/**
 * Class used to create a Standard Robot
 */
public class StandardRobot extends Robot {
	
	public StandardRobot(IMailDelivery delivery, IMailPool mailPool) {
		super(delivery, mailPool, RobotType.Standard);
	}
}
