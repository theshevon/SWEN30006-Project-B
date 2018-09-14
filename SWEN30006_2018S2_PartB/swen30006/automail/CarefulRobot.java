package automail;

import exceptions.ExcessiveDeliveryException;
import exceptions.FragileItemBrokenException;
import exceptions.ItemTooHeavyException;
import strategies.IMailPool;

public class CarefulRobot extends Robot {
	
	public static final int MAX_WEIGHT = Integer.MAX_VALUE;
	public static final int TUBE_SIZE = 3;
	private boolean skipNextStep = false;

	public CarefulRobot(IMailDelivery delivery, IMailPool mailPool) {
		super(delivery, mailPool, MAX_WEIGHT, TUBE_SIZE, RobotType.Careful);
	}
	
	public void step() throws ItemTooHeavyException, ExcessiveDeliveryException, FragileItemBrokenException {
		
		if ((current_state == RobotState.DELIVERING) || (current_state == RobotState.RETURNING)) {
			if (!skipNextStep) {
				super.step();
			}
			skipNextStep = !skipNextStep;
		}else {
			if (skipNextStep) {
				skipNextStep = false;
			}
			super.step();
		}
	}
}
