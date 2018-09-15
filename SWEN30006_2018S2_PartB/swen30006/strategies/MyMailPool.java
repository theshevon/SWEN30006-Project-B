package strategies;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.ListIterator;

import automail.MailItem;
import automail.PriorityMailItem;
import automail.Robot;
import automail.Simulation.RobotType;
import automail.StorageTube;
import automail.WeakRobot;
import exceptions.FragileItemBrokenException;
import exceptions.TubeFullException;

public class MyMailPool implements IMailPool {
	
	private class Item {
		int priority;
		int destination;
		boolean isHeavy;
		boolean isFragile;
		MailItem mailItem;
		
		// Use stable sort to keep arrival time relative positions
		
		public Item(MailItem mailItem) {
			priority = (mailItem instanceof PriorityMailItem) ? ((PriorityMailItem) mailItem).getPriorityLevel() : 1;
			isHeavy = mailItem.getWeight() >= RobotType.Weak.getMaxWeight();
			destination = mailItem.getDestFloor();
			isFragile = mailItem.getFragile();
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
		// Start empty
		normalMailPool = new LinkedList<Item>();
		fragileMailPool = new LinkedList<Item>();
		lightCount = 0;
		robots = new LinkedList<Robot>();
	}

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
		RobotType type = robot.getType();
		
		try { // Get as many items as available or as fit
			
			if (type == RobotType.Careful && fragileMailPool.size() > 0) {
				tube.addItem(fragileMailPool.remove().mailItem, type); //only take one fragile item
			}else {
				if (!(robot instanceof WeakRobot)) {
					while(tube.getSize() < tube.getMaximumCapacity() && !normalMailPool.isEmpty() ) {
						Item item = normalMailPool.remove();
						if (!item.isHeavy) lightCount--;
						tube.addItem(item.mailItem, type);
					}
				} else {
					ListIterator<Item> i = normalMailPool.listIterator();
					while(tube.getSize() < tube.getMaximumCapacity() && lightCount > 0) {
						Item item = i.next();
						if (!item.isHeavy) {
							tube.addItem(item.mailItem, type);
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
		if (robot.getType() != RobotType.Weak){
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
