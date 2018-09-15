package automail;

import automail.Simulation.RobotType;
import strategies.IMailPool;

public class BigRobot extends Robot {
	
	public BigRobot(IMailDelivery delivery, IMailPool mailPool) {
		super(delivery, mailPool, RobotType.Big);
	}

}