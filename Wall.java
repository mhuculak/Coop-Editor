
/*
   A Wall is defined aa segment on the map which seperates 2 cells
*/
import java.awt.*;

public class Wall {
	private String id;
	private MyPoint endpoint1;
	private MyPoint endpoint2;
	private String cellID;	
	private double height;
	private boolean hasRoof;
	private Color color;
	private String doorID;

	Wall(String id, MyPoint e1, MyPoint e2, String c, double h, boolean roof, Color color) {
		this.id = id;
		this.color = color;
		endpoint1 = e1;
		endpoint2 = e2;
		cellID = c;		
		height = h;
		hasRoof = roof;
	}

	Wall(String line) {
		String[] c = line.split(":");
		id = c[0].replaceAll("\\D+","");
		endpoint1 = new MyPoint(c[1]);
		endpoint2 = new MyPoint(c[2]);
		cellID = c[3];		
		height = Double.parseDouble(c[4]);
		hasRoof = Boolean.parseBoolean(c[4]);
		String[] rgb = c[6].split(",");
		color = new Color(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2]));
		if (c.length == 8) {
			doorID = c[7];
		}
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

	public void setDoorID(String doorID) {
		this.doorID = doorID;
	}

	public void removeDoorID() {
		this.doorID = null;
	}

	public String getDoorID() {
		return doorID;
	}

	public String getID() {
		return id;
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

	public String toString()  {
		StringBuilder sb = new StringBuilder(100);
		sb.append("wall"+id+":"+endpoint1.toString()+":"+endpoint2.toString()+":");
		sb.append(cellID+":"+Double.toString(height)+":"+Boolean.toString(hasRoof));
		sb.append(":"+color.getRed() + "," + color.getGreen() + "," + color.getBlue());
		if (doorID != null) {
			sb.append(":" + doorID);
		}
		return sb.toString();
	}

}