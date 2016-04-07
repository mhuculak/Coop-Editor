package coop.map;

public class Water extends Obstacle {
	private double depth;

	public Water(String id, int direction) {
		super(id, direction);
		depth = 10.0;
	}	
}