package automail;

import exceptions.ExcessiveDeliveryException;
import exceptions.ItemTooHeavyException;
import exceptions.MailAlreadyDeliveredException;
import exceptions.FragileItemBrokenException;
import strategies.Automail;
import strategies.IMailPool;

import java.util.stream.Stream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class used to simulate the behaviour of automail
 */
public class Simulation {
	
	/**
	 * Enumerations for different robot types
	 */
	public static enum RobotType {
    	
    	Big(Integer.MAX_VALUE, 6, false), 
    	Standard(Integer.MAX_VALUE, 4, false), 
    	Careful(Integer.MAX_VALUE, 3, true), 
    	Weak(2000, 4, false);
    	
    	private int maxCarryingWeight;
    	private int tubeCapacity;
    	private boolean canCarryFragile;
    	
    	private RobotType(int maxCarryingWeight, int tubeCapacity, boolean canCarryFragile) {
    		this.maxCarryingWeight = maxCarryingWeight;
    		this.tubeCapacity = tubeCapacity;
    		this.canCarryFragile = canCarryFragile;
    	}
    	
    	/**
    	 * @return The maximum weight of an item that the robot can carry 
    	 */
    	public int getMaxWeight() {
    		return maxCarryingWeight;
    	}
    	
    	/**
    	 * @return The maximum number of items that the robot can carry in its tube
    	 */
    	public int getTubeCapacity() {
    		return tubeCapacity;
    	}
    	
    	/**
    	 * @return True if the robot can carry fragile items
    	 */
    	public boolean canCarryFragile() {
    		return canCarryFragile;
    	}
    }
    
    private static ArrayList<MailItem> MAIL_DELIVERED;
    private static double total_score = 0;

    public static void main(String[] args) throws IOException, InstantiationException, 
    														IllegalAccessException, ClassNotFoundException {
    	
    	Properties automailProperties = new Properties();
		
    	// Default properties
    	automailProperties.setProperty("Robots", "Big, Careful, Standard, Weak");
    	automailProperties.setProperty("MailPool", "strategies.SimpleMailPool");
    	automailProperties.setProperty("Floors", "10");
    	automailProperties.setProperty("Fragile", "false");
    	automailProperties.setProperty("Mail_to_Create", "80");
    	automailProperties.setProperty("Last_Delivery_Time", "100");

    	// Read properties
		FileReader inStream = null;
		try {
			inStream = new FileReader("automail.properties");
			automailProperties.load(inStream);
		} finally {
			 if (inStream != null) {
				 inStream.close();
	         }
		}
		
		// MailPool
		String mailPoolName = automailProperties.getProperty("MailPool");
		IMailPool mailPool = (IMailPool) Class.forName(mailPoolName).newInstance();
		//Seed
		String seedProp = automailProperties.getProperty("Seed");
		// Floors
		Building.FLOORS = Integer.parseInt(automailProperties.getProperty("Floors"));
        System.out.printf("Floors: %5d%n", Building.FLOORS);
        // Fragile
        boolean fragile = Boolean.parseBoolean(automailProperties.getProperty("Fragile"));
        System.out.printf("Fragile: %5b%n", fragile);
		// Mail_to_Create
		int noMailToCreate = Integer.parseInt(automailProperties.getProperty("Mail_to_Create"));
        System.out.printf("Mail_to_Create: %5d%n", noMailToCreate);
		// Last_Delivery_Time
		Clock.LAST_DELIVERY_TIME = Integer.parseInt(automailProperties.getProperty("Last_Delivery_Time"));
        System.out.printf("Last_Delivery_Time: %5d%n", Clock.LAST_DELIVERY_TIME);
		// Robots
		String robotsProp = automailProperties.getProperty("Robots");
		List<RobotType> robotTypes = 
				Stream.of(robotsProp.split(",")).map(RobotType::valueOf).collect(Collectors.toList());
		System.out.print("Robots: "); System.out.println(robotTypes);

		// End properties
		
        MAIL_DELIVERED = new ArrayList<MailItem>();
                
        /** Used to see whether a seed is initialized or not */
        HashMap<Boolean, Integer> seedMap = new HashMap<>();
        
        /** Read the first argument and save it as a seed if it exists */
        if (args.length == 0 ) { 			// No arg
        	if (seedProp == null) { 		// and no property
        		seedMap.put(false, 0); 		// so randomise
        	} else { 					    // Use property seed
        		seedMap.put(true, Integer.parseInt(seedProp));
        	}
        } else { // Use arg seed - overrides property
        	seedMap.put(true, Integer.parseInt(args[0]));
        }
        Integer seed = seedMap.get(true);
        System.out.printf("Seed: %s%n", seed == null ? "null" : seed.toString());
        Automail automail = new Automail(mailPool, new ReportDelivery(), robotTypes);
        MailGenerator mailGenerator = new MailGenerator(noMailToCreate, automail.getMailPool(), 
        																				seedMap, fragile);
        
        /** Initiate all the mail */
        mailGenerator.generateAllMail();
        
        // PriorityMailItem priority;  // Not used in this version
        while(MAIL_DELIVERED.size() != mailGenerator.getNoMailToCreate()) {
        	//System.out.println("-- Step: " + Clock.Time());
            /* priority = */ mailGenerator.step();
            try {
                automail.getMailPool().step();
				for (int i=0; i<robotTypes.size(); i++) automail.getRobots().get(i).step();
			} catch (ExcessiveDeliveryException|ItemTooHeavyException|FragileItemBrokenException e) {
				e.printStackTrace();
				System.out.println("Simulation unable to complete.");
				System.exit(0);
			}
            Clock.Tick();
        }
        
        printResults();
    }
    
    static class ReportDelivery implements IMailDelivery {
    	
    	/** Confirm the delivery and calculate the total score */
    	public void deliver(MailItem deliveryItem){
    		if(!MAIL_DELIVERED.contains(deliveryItem)){
                System.out.printf("T: %3d > Delivered [%s]%n", Clock.Time(), deliveryItem.toString());
    			MAIL_DELIVERED.add(deliveryItem);
    			// Calculate delivery score
    			total_score += calculateDeliveryScore(deliveryItem);
    		}
    		else{
    			try {
    				throw new MailAlreadyDeliveredException();
    			} catch (MailAlreadyDeliveredException e) {
    				e.printStackTrace();
    			}
    		}
    	}

    }
    
    private static double calculateDeliveryScore(MailItem deliveryItem) {
    	
    	// Penalty for longer delivery times
    	final double penalty = 1.2;
    	double priority_weight = 0;
    	
        // Take (delivery time - arrivalTime)**penalty * (1+ sqrt(priority_weight))
    	if (deliveryItem instanceof PriorityMailItem){
    		priority_weight = ((PriorityMailItem) deliveryItem).getPriorityLevel();
    	}
    	
        return Math.pow(Clock.Time() - deliveryItem.getArrivalTime(),penalty)*(1+Math.sqrt(priority_weight));
    }

    /**
     * Prints the results of the simulation, ie. Completion time, final delivery time and final score.
     */
    public static void printResults(){
        System.out.println("T: "+Clock.Time()+" | Simulation complete!");
        System.out.println("Final Delivery time: "+Clock.Time());
        System.out.printf("Final Score: %.2f%n", total_score);
    }
}
