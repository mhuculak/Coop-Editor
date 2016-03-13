import java.awt.*;

public class CellDef {
	private MyPoint position;
	private Color color;
	private String id;

	public CellDef(String id, MyPoint pos, Color color) {
		this.position = pos;
		this.color = color;
		this.id = id;
	}

	public CellDef(String line) {
		String[] type_data = line.split(":");
		String type = type_data[0];
		id = type.replaceAll("\\D+","");
		line = type_data[1];
//		System.out.println("parse:" + line);
		String[] pos_color = line.split("#");
//		System.out.println("parse:" + pos_color[0]);
		String[] p = pos_color[0].split(",");
		position = new MyPoint(Integer.parseInt(p[0]), Integer.parseInt(p[1]));
//		System.out.println("parse:" + pos_color[1]);
		if (pos_color.length == 2) {
			String[] c = pos_color[1].split(",");
			color = new Color(Integer.parseInt(c[0]), Integer.parseInt(c[1]), Integer.parseInt(c[2])); // RGB
		}
	}

	public String toString() {
		String v = null;
		if (color == null) {
			v =  "cell" + id + ":" + position.getX() + "," + position.getY();
		}
		else {
			v =  "cell" + id + ":" + position.getX() + "," + position.getY() + "#" + color.getRed() + "," + color.getGreen() + "," + color.getBlue();
		}
		return v;
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
}