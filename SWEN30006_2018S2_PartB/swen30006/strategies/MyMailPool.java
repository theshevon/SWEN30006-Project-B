package strategies;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.ListIterator;

import automail.CarefulRobot;
import automail.MailItem;
import automail.PriorityMailItem;
import automail.Robot;
import automail.Simulation.RobotType;
import automail.StorageTube;
import automail.WeakRobot;
import exceptions.FragileItemBrokenException;
import exceptions.TubeFullException;

/**
 * Class to manage the mail pool
 */
public class MyMailPool implements IMailPool {
	
	private class Item {
		
		private int priority;
		private int destination;
		private boolean isHeavy;
		private boolean isFragile;
		private MailItem mailItem;
		
		public Item(MailItem mailItem) {
			priority = (mailItem instanceof PriorityMailItem) ? 
													((PriorityMailItem) mailItem).getPriorityLevel() : 1;
			isHeavy = mailItem.getWeight() >= RobotType.Weak.getMaxWeight();
			destination = mailItem.getDestFloor();
			isFragile = mailItem.isFragile();
			this.mailItem = mailItem;
		}
	}
	
	private class ItemComparator implements Comparator<Item> {
		@Override
		public int compare(Item i1, Item i2) {
			
			int order = 0;
			if (i1.priority < i2.priority) {
				order = 1;
			} else if (i1.priority > i2.priority) {
				order = -1;
			} else if (i1.destination < i2.destination) {
				order = 1;
			} else if (i1.destination > i2.destination) {
				order = -1;
			}
			return order;
		}
	}
	
	private LinkedList<Item> normalMailPool;
	private LinkedList<Item> fragileMailPool;
	private LinkedList<Robot> robots;
	private int lightCount;

	public MyMailPool(){
		normalMailPool = new LinkedList<Item>();
		fragileMailPool = new LinkedList<Item>();
		robots = new LinkedList<Robot>();
		lightCount = 0;
	}

	/**
	 * Adds a new mail item to the pool
	 * @param mailItem mail item to add
	 */
	public void addToPool(MailItem mailItem) {
		Item item = new Item(mailItem);
		
		if (item.isFragile) {
			fragileMailPool.add(item);
		}
		else{
			normalMailPool.add(item);
			if (!item.isHeavy) lightCount++;
		}
		
		normalMailPool.sort(new ItemComparator());
		fragileMailPool.sort(new ItemComparator());
	}
	
	@Override
	public void step() throws FragileItemBrokenException {
		for (Robot robot: (Iterable<Robot>) robots::iterator) { fillStorageTube(robot); }
	}
	
	private void fillStorageTube(Robot robot) throws FragileItemBrokenException {
		
		StorageTube tube = robot.getTube();
		
		// Get as many items as available or as fit
		try { 
			
			if (robot instanceof CarefulRobot && fragileMailPool.size() > 0) {
				tube.addItem(fragileMailPool.remove().mailItem); //only take one fragile item
			}
			else {
				
				if (!(robot instanceof WeakRobot)) {
					while(tube.getSize() < tube.getMaximumCapacity() && !normalMailPool.isEmpty() ) {
						Item item = normalMailPool.remove();
						if (!item.isHeavy) lightCount--;
						tube.addItem(item.mailItem);
					}
				} 
				else {
					ListIterator<Item> i = normalMailPool.listIterator();
					while(tube.getSize() < tube.getMaximumCapacity() && lightCount > 0) {
						Item item = i.next();
						if (!item.isHeavy) {
							tube.addItem(item.mailItem);
							i.remove();
							lightCount--;
						}
					}
				}
			}
			
			if (tube.getSize() > 0) {
				robot.dispatch();
			}
		}
		catch(TubeFullException e){
			e.printStackTrace();
		}
	}

	@Override
	public void registerWaiting(Robot robot) { // assumes won't be there
		if (!(robot instanceof WeakRobot)){
			robots.add(robot); 
		} else {
			robots.addLast(robot); // weak robot last as want more efficient delivery with highest priorities
		}
	}

	@Override
	public void deregisterWaiting(Robot robot) {
		robots.remove(robot);
	}

}
