package coop.map;

import coop.mapeditor.MapEditor;

import java.awt.*;

public class CellDef {
	private MyPoint position;
	private Color color;
	private Texture texture;	
	private String id;

	public CellDef(String id, MyPoint pos, Color color) {
		this.position = pos;
		this.color = color;
		this.id = id;
	}

	public CellDef(String id, MyPoint pos, Texture texture) {
		this.position = pos;
		this.texture = texture;
		this.id = id;
	}

	public CellDef(String line) {
		String[] type_data = line.split(":");
		String type = type_data[0];
		id = type.replaceAll("\\D+","");
		line = type_data[1];
//		System.out.println("parse:" + line);
		String[] pos_content = line.split("#");
//		System.out.println("parse:" + pos_color[0]);
		String[] p = pos_content[0].split(",");
		position = new MyPoint(Integer.parseInt(p[0]), Integer.parseInt(p[1]));
		
		if (pos_content.length == 2) {
			System.out.println("parse:" + pos_content[1]);
			String[] c = pos_content[1].split(",");
			if (c.length==3) {
				color = new Color(Integer.parseInt(c[0]), Integer.parseInt(c[1]), Integer.parseInt(c[2])); // RGB
			}
			else {
				texture = MapEditor.textureSelector.getTexture(pos_content[1]);
			}
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("cell" + id + ":" + position.getX() + "," + position.getY());
		if (texture != null) {
			sb.append("#" + texture.getName());
		}
		else if (color != null) {
			sb.append("#" + color.getRed() + "," + color.getGreen() + "," + color.getBlue());
		}
		
		return sb.toString();
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
}