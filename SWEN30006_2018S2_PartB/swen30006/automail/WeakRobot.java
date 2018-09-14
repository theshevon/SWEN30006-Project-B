package automail;

import strategies.IMailPool;

public class WeakRobot extends Robot{
	
	public static final int MAX_WEIGHT = 2000;
	public static final int TUBE_SIZE = 4;

	public WeakRobot(IMailDelivery delivery, IMailPool mailPool) {
		super(delivery, mailPool, MAX_WEIGHT, TUBE_SIZE, RobotType.Weak);
	}
}
