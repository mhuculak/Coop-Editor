package coop.map;

public class MyPoint {
	private int x;
	private int y;	

	public MyPoint(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public MyPoint(String x, String y) {
		this.x = Integer.parseInt(x);
		this.y = Integer.parseInt(y);
	}

	public MyPoint(String line) {
		String[] xy = line.split(",");
		x = Integer.parseInt(xy[0]);
		y = Integer.parseInt(xy[1]);
	}

	public int getX() { // y dimension (up/down the screen)
		return x;
	}

	public int getY() { // x dimension (across the screen)
		return y;
	}

	public boolean equals(MyPoint p) {
		if (p.getX() == x && p.getY() == y) {
			return true;
		}
		return false;
	}

	public String toString() {
		return x + "," + y;
	}
}
