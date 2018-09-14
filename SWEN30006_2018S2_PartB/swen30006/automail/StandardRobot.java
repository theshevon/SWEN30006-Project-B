package automail;

import strategies.IMailPool;

public class StandardRobot extends Robot {

	public static final int MAX_WEIGHT = Integer.MAX_VALUE;
	public static final int TUBE_SIZE = 4;
	
	public StandardRobot(IMailDelivery delivery, IMailPool mailPool) {
		super(delivery, mailPool, MAX_WEIGHT, TUBE_SIZE, RobotType.Standard);
	}
}
