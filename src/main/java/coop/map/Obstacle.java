package coop.map;

public class Obstacle {
	private String id;
	private int direction;

	public Obstacle(String id, int direction) {
		this.id = id;
		this.direction = direction;
	}

	public Obstacle(String line) {
		System.out.println("Obstacle(" + line + ")");
		String[] c = line.split(":");
		id = c[0].replaceAll("\\D+","");
		direction = Integer.parseInt(c[1]);
	}

	public String getID() {
		return id;
	}

	public int getDirection() {
		return direction;
	}

	public String toString() {
		return "obstacle"+id+":"+Integer.toString(direction);
	}
}