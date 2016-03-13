import java.awt.*;

public class Cell {
	private MyPoint position;
	private Polygon hexagon;
	private Color color;
	private String colorName;
	private Place place;
	private String id;

	public Cell(String id, Polygon polygon, MyPoint pos) {
		this.position = pos;
		this.hexagon = polygon;
		this.color = null; // requires use of default color
		this.id = id;
	}

	public Cell(String id, Polygon polygon, MyPoint pos, Color color) {
		this.position = pos;
		this.hexagon = polygon;
		this.color = color;
		this.id = id;
	}

	public void setColor(Color color, String name) {
		this.color = color;
		this.colorName = name;
	}

	public void setPlace(Place place) {
		this.place = place;
	}

	public Place getPlace() {
		return place;
	}

	public Polygon getHexagon() {
		return hexagon;
	}

	public MyPoint getPosition() {
		return position;
	}

	public Color getColor() {
		return color;
	}

	public String getID() {
		return id;
	}

	public String toString() {
		Point p = hexagon.getBounds().getLocation();
		return "Location = (" + p.getX() + "," + p.getY() + ") color = " + color.toString();
	}
}
