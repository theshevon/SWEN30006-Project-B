package automail;

import automail.Simulation.RobotType;
import exceptions.ExcessiveDeliveryException;
import exceptions.FragileItemBrokenException;
import exceptions.ItemTooHeavyException;
import strategies.IMailPool;

public class CarefulRobot extends Robot {

	private boolean skipNextStep = false;

	public CarefulRobot(IMailDelivery delivery, IMailPool mailPool) {
		super(delivery, mailPool, RobotType.Careful);
	}
	
	public void step() throws ItemTooHeavyException, ExcessiveDeliveryException, FragileItemBrokenException {
		
		if ((current_state == RobotState.DELIVERING || current_state == RobotState.RETURNING) 
				&& getCurrentFloor() != getDestinationFloor()) {
			
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
