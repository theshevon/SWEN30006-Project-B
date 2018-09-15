package automail;

import strategies.IMailPool;

public class BigRobot extends Robot {
	
	public static final RobotType TYPE = RobotType.Big;
	public static final int TUBE_SIZE = 6;

	public BigRobot(IMailDelivery delivery, IMailPool mailPool) {
		super(delivery, mailPool, TYPE, TUBE_SIZE);
	}
	
	@Override
	public RobotType getType() {
		return TYPE;
	}
}