package automail;

import automail.Simulation.RobotType;
import exceptions.ExcessiveDeliveryException;
import exceptions.FragileItemBrokenException;
import exceptions.ItemTooHeavyException;
import strategies.IMailPool;

/**
 * Class used to create a Careful Robot
 */
public class CarefulRobot extends Robot {

	private boolean skipNextStep = true;

	public CarefulRobot(IMailDelivery delivery, IMailPool mailPool) {
		super(delivery, mailPool, RobotType.Careful);
	}
	
	/**
	 * Moves the robot towards its destination depending on the number of clock ticks
	 */
	public void step() throws ItemTooHeavyException, ExcessiveDeliveryException, FragileItemBrokenException {
		
		// If the robot is moving, ensure that it takes a step during alternating clock ticks. This enforces
		// it to move at half the speed of the standard robot
		if ((getState() == RobotState.DELIVERING || getState() == RobotState.RETURNING) 
														&& getCurrentFloor() != getDestinationFloor()) {
			
			if (!skipNextStep) {
				super.step();
			}
			skipNextStep = !skipNextStep;
		}else {
			
			// if the robot is waiting, ensure that it skips the first step it takes when its state changes
			if (!skipNextStep) {
				skipNextStep = true;
			}
			super.step();
		}
	}
}
