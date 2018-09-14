package automail;

import strategies.IMailPool;

public class BigRobot extends Robot {
	
	public static final int MAX_WEIGHT = Integer.MAX_VALUE;
	public static final int TUBE_SIZE = 6;

	public BigRobot(IMailDelivery delivery, IMailPool mailPool) {
		super(delivery, mailPool, MAX_WEIGHT, TUBE_SIZE, RobotType.Big);
	}
}