package automail;

public enum RobotType {
	Big(Integer.MAX_VALUE), 
	Careful(Integer.MAX_VALUE), 
	Standard(Integer.MAX_VALUE), 
	Weak(2000);
	
	private int maxCarryingWeight;
	
	private RobotType(int maxCarryingWeight) {
		this.maxCarryingWeight = maxCarryingWeight;
	}
	
	public int getMaxWeight() {
		return maxCarryingWeight;
	}
}
