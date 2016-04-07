package coop.map;
/*
   A Wall is defined aa segment on the map which seperates 2 cells
*/
import java.awt.*;

public class Wall extends Obstacle {	
	private String className;
	private MyPoint endpoint1;
	private MyPoint endpoint2;
	private Cell innerCell;
	private Cell outerCell;
	private String cellID;		
	private double height;
	private boolean hasRoof;
	private Color color;
	private String doorID;

	Wall(String id, int direction, MyPoint e1, MyPoint e2, Cell c, double h, boolean roof, Color color) {
		super(id, direction);
		this.color = color;
		endpoint1 = e1;
		endpoint2 = e2;
		innerCell = c;		
		height = h;
		hasRoof = roof;
		cellID = innerCell.getID();
		className = "coop.map.Wall";
	}

	Wall(String line) {
		super(line);
		String[] c = line.split(":");
		endpoint1 = new MyPoint(c[2]);
		endpoint2 = new MyPoint(c[3]);
		cellID = c[4];		
		height = Double.parseDouble(c[5]);
		hasRoof = Boolean.parseBoolean(c[6]);
		String[] rgb = c[7].split(",");
		color = new Color(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2]));
		if (c.length == 9) {
			doorID = c[8];
		}
		className = "coop.map.Wall";
	}

	public boolean contains(MyLine line) {
		if (line.getEndp1().equals(endpoint1) && line.getEndp2().equals(endpoint2)) {
			return true;
		}
		else if (line.getEndp1().equals(endpoint2) && line.getEndp2().equals(endpoint1)) {
			return true;
		}
		return false;
	}

	public String getClassName() {
		return className;
	}

	public Double getHeight() {
		return height;
	}

	public String getCellID() {
		return cellID;
	}

	public void setOuterCell(Cell cell) {
		outerCell = cell;
	}

	public boolean hasRoof() {
		return hasRoof;
	}

	public void setDoorID(String doorID) {
		this.doorID = doorID;
	}

	public void removeDoorID() {
		this.doorID = null;
	}

	public String getDoorID() {
		return doorID;
	}

	public MyPoint getEndp1() {
		return endpoint1;
	}

	public MyPoint getEndp2() {
		return endpoint2;
	}

	public Color getColor() {
		return color;
	}

	public Cell getCell() {
		return innerCell;
	}

	public Cell getOuterCell() {
		return outerCell;
	}

	public String toString()  {
		StringBuilder sb = new StringBuilder(100);
		sb.append(super.toString());
		sb.append(":"+endpoint1.toString()+":"+endpoint2.toString()+":");
		sb.append(cellID+":"+Double.toString(height)+":"+Boolean.toString(hasRoof));
		sb.append(":"+color.getRed() + "," + color.getGreen() + "," + color.getBlue());
		if (doorID != null) {
			sb.append(":" + doorID);
		}
		return sb.toString();
	}

}