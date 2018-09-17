package automail;

import automail.Simulation.RobotType;
import strategies.IMailPool;

/**
 * Class to create a Weak Robot
 */
public class WeakRobot extends Robot{

	public WeakRobot(IMailDelivery delivery, IMailPool mailPool) {
		super(delivery, mailPool, RobotType.Weak);
	}
}
