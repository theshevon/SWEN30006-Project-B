package automail;

import java.util.LinkedList;

import exceptions.FragileItemBrokenException;
import exceptions.TubeFullException;

/**
 * Class used to create a storage tube for a robot
 */
public class StorageTube {

    private final int CAPACITY;
    private final boolean CAN_TAKE_FRAGILE; // stores whether or not the robot can carrying fragile items
    private LinkedList<MailItem> tube;

    /**
     * Constructor for the storage tube
     */
    public StorageTube(int capacity, boolean canTakeFragile){
    	this.CAPACITY = capacity;
        this.CAN_TAKE_FRAGILE = canTakeFragile;
        this.tube = new LinkedList<MailItem>();
    }

    /**
     * @return the maximum capacity of the tube
     */
    public int getMaximumCapacity() {
    	return CAPACITY;
    }
    
    /**
     * @return if the storage tube is full
     */
    public boolean isFull(){
        return tube.size() == CAPACITY;
    }

    /**
     * @return if the storage tube is empty
     */
    public boolean isEmpty(){
        return tube.isEmpty();
    }
    
    /**
     * @return the first item in the storage tube (without removing it)
     */
    public MailItem peek() {
    	return tube.peek();
    }

    /**
     * Add an item to the tube
     * @param item The item being added
     * @throws TubeFullException thrown if an item is added which exceeds the capacity
     */
    public void addItem(MailItem item) throws TubeFullException, FragileItemBrokenException {
    	
    	// throw an exception if a non-Careful robot attempts to take a fragile item
    	if (item.isFragile() && !CAN_TAKE_FRAGILE) {
    		throw new FragileItemBrokenException();
    	}
    	
        if (tube.size() < CAPACITY){
        	
        	if (tube.isEmpty()) {
        		tube.add(item);
        	} 
        	// if the robot already has a fragile item in its tube and attempts to take another, 
        	// throw an exception
        	else if (item.isFragile() || tube.peek().isFragile()) {
        		throw new FragileItemBrokenException();
        	}
        	else {
        		tube.add(item);
        	}
        	
        } else {
            throw new TubeFullException();
        }
    }

    /** @return the size of the tube **/
    public int getSize(){
    	return tube.size();
    }
    
    /** 
     * @return the first item in the storage tube (after removing it)
     */
    public MailItem removeItem(){
        return tube.removeFirst();
    }

}
