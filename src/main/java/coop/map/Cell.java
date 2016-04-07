package coop.map;

import java.awt.*;

public class Cell {
	private MyPoint position;
	private MyPoint center;
	private Polygon hexagon;
	private Color color;
	private Texture texture;
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

	public Cell(String id, Polygon polygon, MyPoint pos, Texture texture) {
		this.position = pos;
		this.hexagon = polygon;
		this.texture = texture;
		this.id = id;
	}

    // FIXME: this sucks...better for the hexagon to be relative to the center
	public void setCenter(double edgeSize, double yLen) {
		int e = (int)(edgeSize+0.5);
		int y = (int)(yLen/2.0+0.5);
		center = new MyPoint(position.getX()+e, position.getY()+y);		
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
	}

	public void setColor(Color color, String name) {
		this.color = color;
		this.colorName = name;
	}

	public void setPlace(Place place) {
		this.place = place;
	}

	public MyPoint getCenter() {
		return center;
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

	public Texture getTexture() {
		return texture;
	}

	public String getID() {
		return id;
	}

	public String toString() {
		Point p = hexagon.getBounds().getLocation();
		Color pc = (color == null) ? Color.white : color;
		return "Location = (" + p.getX() + "," + p.getY() + ") color = " + pc.toString();
	}
}
