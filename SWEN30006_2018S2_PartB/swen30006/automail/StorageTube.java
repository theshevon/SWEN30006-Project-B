package automail;

import java.util.LinkedList;
import exceptions.FragileItemBrokenException;
import exceptions.TubeFullException;

/**
 * The storage tube carried by the robot.
 */
public class StorageTube {

    private final int maximumCapacity;
    private LinkedList<MailItem> tube;

    /**
     * Constructor for the storage tube
     */
    public StorageTube(int maximumCapacity){
    	this.maximumCapacity = maximumCapacity;
        this.tube = new LinkedList<MailItem>();
    }

    public int getMaximumCapacity() {
    	return maximumCapacity;
    }
    
    /**
     * @return if the storage tube is full
     */
    public boolean isFull(){
        return tube.size() == maximumCapacity;
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
    public void addItem(MailItem item, RobotType robotType) throws TubeFullException, FragileItemBrokenException {
        if(tube.size() < maximumCapacity){
        	if (tube.isEmpty()) {
        		tube.add(item);
        	} else if (robotType != RobotType.Careful && (item.getFragile() || tube.peek().getFragile())) {
        		throw new FragileItemBrokenException();
        	} else {
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
