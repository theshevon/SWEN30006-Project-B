package automail;

import strategies.IMailPool;

public class StandardRobot extends Robot {

	public static final RobotType TYPE = RobotType.Standard;
	public static final int TUBE_SIZE = 4;
	
	public StandardRobot(IMailDelivery delivery, IMailPool mailPool) {
		super(delivery, mailPool, TYPE, TUBE_SIZE);
	}
	
	@Override
	public RobotType getType() {
		return TYPE;
	}

}
