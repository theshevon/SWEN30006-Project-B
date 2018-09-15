package automail;

import strategies.IMailPool;

public class WeakRobot extends Robot{
	
	public static final RobotType TYPE = RobotType.Weak;
	public static final int TUBE_SIZE = 4;

	public WeakRobot(IMailDelivery delivery, IMailPool mailPool) {
		super(delivery, mailPool, TYPE, TUBE_SIZE);
	}
	
	@Override
	public RobotType getType() {
		return TYPE;
	}

}
