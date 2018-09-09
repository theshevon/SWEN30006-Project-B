package automail;

public enum RobotType {
	Big(6), 
	Careful(3), 
	Standard(4), 
	Weak(4);
	
	private int tubeSize;
	
	private RobotType(int tubeSize) {
		this.tubeSize = tubeSize;
	}
	
	public int getTubeSize() {
		return tubeSize;
	}
	
}
