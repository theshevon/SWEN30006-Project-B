package automail;

import exceptions.ExcessiveDeliveryException;
import exceptions.FragileItemBrokenException;
import exceptions.ItemTooHeavyException;
import strategies.IMailPool;

public class CarefulRobot extends Robot {
	
	public static final RobotType TYPE = RobotType.Careful;
	public static final int TUBE_SIZE = 3;
	private boolean skipNextStep = false;

	public CarefulRobot(IMailDelivery delivery, IMailPool mailPool) {
		super(delivery, mailPool, TYPE, TUBE_SIZE);
	}
	
	@Override
	public RobotType getType() {
		return TYPE;
	}
	
	public void step() throws ItemTooHeavyException, ExcessiveDeliveryException, FragileItemBrokenException {
		
		if ((current_state == RobotState.DELIVERING || current_state == RobotState.RETURNING) && getCurrentFloor() != getDestinationFloor()) {
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
