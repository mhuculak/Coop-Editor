import java.util.*;
import java.awt.*;

public class MapDef {
	private ConfigParams configParams;
	private MyPoint mapDim;
	private java.util.List<CellDef> cells;
	private java.util.List<Wall> walls;
	private java.util.List<Door> doors;
	private Color defaultColor;

	public MapDef() {

	}

	public MapDef(MyPoint mapDim, ConfigParams configParams) {
		this.mapDim = mapDim;
		this.configParams = configParams;
	}	

	public void parseLine(String line) {
//		System.out.println("parse line " + line);
		String[] type_data = line.split(":");
		String type = type_data[0];
		String tail = type_data[1];
		if (type.equals("mapDim")) {
			mapDim = new MyPoint(tail);
		}
		else if (type.equals("sParam")) {
			if (configParams == null) {
				configParams = new ConfigParams();
			}
			configParams.addStringParam(type_data[1], type_data[2]);
		}
		else if (type.equals("iParam")) {
			if (configParams == null) {
				configParams = new ConfigParams();
			}
			configParams.addIntParam(type_data[1], Integer.parseInt(type_data[2]));
		}
		else if (type.equals("dParam")) {
			if (configParams == null) {
				configParams = new ConfigParams();
			}
			configParams.addDoubleParam(type_data[1], Double.parseDouble(type_data[2]));
			
		}
		else if (type.contains("cell")) {
			addCell(new CellDef(line));
		}
		else if (type.contains("wall")) {
			addWall(new Wall(line));
		}
		else if (type.contains("door")) {
			addDoor(new Door(line));
		}
		else if (type.contains("defaultColor")) {
			setDefaultColor(line);
		}
	}

	public void addWall(Wall wall) {
		if (walls == null) {
			walls = new ArrayList<Wall>();
		}
		walls.add(wall);
	}

	public void addDoor(Door door) {
		if (doors == null) {
			doors = new ArrayList<Door>();			
		}
		doors.add(door);
	}

	public void setWalls(java.util.List<Wall> walls) {
		this.walls = walls;
	}

	public void setDefaultColor(Color color) {
		defaultColor = color;
	}

	public void setDoors(java.util.List<Door> doors) {
		this.doors = doors;
	}

	public void setDefaultColor(String line) {
		String[] name_color = line.split(":");
        String n = name_color[0];
        String[] c = name_color[1].split(",");
        defaultColor = new Color(Integer.parseInt(c[0]), Integer.parseInt(c[1]), Integer.parseInt(c[2])); 		
	}

	public Color getDefaultColor() {
		return defaultColor;
	}

	public void addCell(CellDef cell) {
		if (cells == null) {
			cells = new ArrayList<CellDef>();
		}
		cells.add(cell); 
	}

	public java.util.List<CellDef> getCells() {
		return cells;
	}

	public java.util.List<Wall> getWalls() {
		return walls;
	}

	public java.util.List<Door> getDoors() {
		return doors;
	}

	public MyPoint getDim() {
		return mapDim;
	}

	public ConfigParams getConfigParams() {
		return configParams;
	}
}
