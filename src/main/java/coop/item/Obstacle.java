package coop.item;

public class Obstacle extends Item implements HasText {

	private String obstacleType;

	public Obstacle() {
		init();
	}

	public Obstacle(String line) {		
		super(line.split("#")[0]);
		String[] data = line.split("#");		
		obstacleType = data[data.length-1];				
		init();	
	}
	
	private void init() {
		setType("obstacle");
	}

	public void setText(String obstacleType) {
		System.out.println("Setting note text to " + obstacleType);
		this.obstacleType = obstacleType;
	}

	public String getText() {
		return obstacleType;
	}

	@Override
	public String toString(String className) {
		StringBuilder sb = new StringBuilder(100);
		sb.append(super.toString(null));
		if (obstacleType != null) {
			sb.append("#"+obstacleType);
		}
		if (className != null) {
			sb.append("#"+className);
		}
		return sb.toString();
	}
}