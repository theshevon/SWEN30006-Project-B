package automail;

import exceptions.ExcessiveDeliveryException;
import exceptions.ItemTooHeavyException;
import exceptions.FragileItemBrokenException;
import strategies.IMailPool;
import java.util.Map;
import java.util.TreeMap;

import automail.Simulation.RobotType;

/**
 * Abstract class used to create a robot
 */
public abstract class Robot {

	/** Possible states the robot can be in */
    public enum RobotState { DELIVERING, WAITING, RETURNING }
    
	private final String id;
	
	private StorageTube tube;
    private IMailDelivery delivery;
    private IMailPool mailPool;
    private MailItem deliveryItem;
    
    private RobotState current_state;
    private RobotType type;
    private int current_floor;
    private int destination_floor;
    private boolean receivedDispatch;    
    private int deliveryCounter;
    
    /**
     * Initiates the robot's location at the start to be at the mailroom
     * also set it to be waiting for mail.
     * @param behaviour governs selection of mail items for delivery and behaviour on priority arrivals
     * @param delivery governs the final delivery
     * @param mailPool is the source of mail items
     * @param strong is whether the robot can carry heavy items
     */
    public Robot(IMailDelivery delivery, IMailPool mailPool, RobotType type){
    	
    	id = "R" + hashCode();
    	
        // current_state = RobotState.WAITING;
    	current_state = RobotState.RETURNING;
        current_floor = Building.MAILROOM_LOCATION;
        tube = new StorageTube(type.getTubeCapacity(), type.canCarryFragile());
        
        this.type = type;
        this.delivery = delivery;
        this.mailPool = mailPool;
        this.receivedDispatch = false;
        this.deliveryCounter = 0;
    }
    
    /**
     * Sets the robots to dispatch
     */
    public void dispatch() {
    	receivedDispatch = true;
    }

    /**
     * Moves the robot towards its destination depending on the number of clock ticks. This is called
     * every time step
     * @throws ExcessiveDeliveryException if robot delivers more than the capacity of the tube without 
     * refilling
     * @throws ItemTooHeavyException if a robot takes a MailItem from its StorageTube which is too heavy
     * @throws FragileItemBrokenException if the robot moves too fast or other items are put in the tube 
	 * when it already contains a fragile item
     */
    public void step() throws ExcessiveDeliveryException, ItemTooHeavyException, FragileItemBrokenException {    	
    	
    	switch (current_state) {
    		
    		/** This state is triggered when the robot is returning to the mailroom after a delivery */
    		case RETURNING:
    			
    			/** If its current position is at the mailroom, then the robot should change state */
                if (current_floor == Building.MAILROOM_LOCATION){
                	while(!tube.isEmpty()) {
                		MailItem mailItem = tube.removeItem();
                		mailPool.addToPool(mailItem);
                        System.out.printf("T: %3d > old addToPool [%s]%n", Clock.Time(),mailItem.toString());
                	}
        			/** Tell the sorter the robot is ready */
        			mailPool.registerWaiting(this);
                	changeState(RobotState.WAITING);
                } else {
                	/** If the robot is not at the mailroom floor yet, then move towards it! */
                    moveTowards(Building.MAILROOM_LOCATION);
                	break;
                }
    		case WAITING:
                
    			/** If the StorageTube is ready and the Robot is waiting in the mailroom then start the 
                 * delivery */
                if (!tube.isEmpty() && receivedDispatch){
                	receivedDispatch = false;
                	deliveryCounter = 0; // reset delivery counter
        			setRoute();
        			mailPool.deregisterWaiting(this);
                	changeState(RobotState.DELIVERING);
                }
                break;
    		
    		case DELIVERING:
    			
    			if (current_floor == destination_floor){ // If already here drop off either way
                    /** Delivery complete, report this to the simulator! */
                    delivery.deliver(deliveryItem);
                    deliveryCounter++;
                    if (deliveryCounter > tube.getMaximumCapacity()){  // Implies a simulation bug
                    	throw new ExcessiveDeliveryException();
                    }
                    /** Check if want to return, i.e. if there are no more items in the tube*/
                    if (tube.isEmpty()){
                    	changeState(RobotState.RETURNING);
                    }
                    else{
                        /** If there are more items, set the robot's route to the location to deliver the item */
                        setRoute();
                        changeState(RobotState.DELIVERING);
                    }
    			} else {
	        		/** The robot is not at the destination yet, move towards it! */
	                moveTowards(destination_floor);
    			}
                break;
    	}
    }
    
    /**
     * @return the current state of the robot
     */
    public RobotState getState() {
    	return current_state;
    }
    
    /**
     * @return the current floor that the robot's one
     */
    public int getCurrentFloor() {
    	return current_floor;
    }
    
    /**
     * @param currentFloor the floor that the robot should be on 
     */
    public void setCurrentFloor(int currentFloor) {
    	this.current_floor = currentFloor;
    }
    
    /**
     * @return the destination floor for the robot
     */
    public int getDestinationFloor() {
    	return destination_floor;
    }

    /**
     * Sets the route for the robot
     */
    private void setRoute() throws ItemTooHeavyException{
        /** Pop the item from the StorageUnit */
        deliveryItem = tube.removeItem();
        if (deliveryItem.getWeight() > type.getMaxWeight()) throw new ItemTooHeavyException(); 
        /** Set the destination floor */
        destination_floor = deliveryItem.getDestFloor();
    }
    
    /**
     * Generic function that moves the robot towards the destination
     * @param destination the floor towards which the robot is moving
     */
    private void moveTowards(int destination) throws FragileItemBrokenException {
    	
    	if (type == RobotType.Careful) {
    		if (!tube.isEmpty() && tube.peek().isFragile()) throw new FragileItemBrokenException();
    	}else {
    		if (deliveryItem != null && deliveryItem.isFragile() || !tube.isEmpty() && 
    										tube.peek().isFragile()) throw new FragileItemBrokenException();
    	}
    	
    	
        if(current_floor < destination){
            current_floor++;
        }
        else{
            current_floor--;
        }
    }
    
    private String getIdTube() {
    	return String.format("%s(%1d/%1d)", id, tube.getSize(), tube.getMaximumCapacity());
    }
    
    /**
     * Prints out the change in state
     * @param nextState the state to which the robot is transitioning
     */
    private void changeState(RobotState nextState){
    	if (current_state != nextState) {
            System.out.printf("T: %3d > %7s changed from %s to %s%n", Clock.Time(), getIdTube(), 
            																	current_state, nextState);
    	}
    	current_state = nextState;
    	if(nextState == RobotState.DELIVERING){
            System.out.printf("T: %3d > %7s-> [%s]%n", Clock.Time(), getIdTube(), deliveryItem.toString());
    	}
    }

    /**
     * @return the storage tube of the robot
     */
	public StorageTube getTube() {
		return tube;
	}
    
	static private int count = 0;
	static private Map<Integer, Integer> hashMap = new TreeMap<Integer, Integer>();

	@Override
	public int hashCode() {
		Integer hash0 = super.hashCode();
		Integer hash = hashMap.get(hash0);
		if (hash == null) { hash = count++; hashMap.put(hash0, hash); }
		return hash;
	}
}
