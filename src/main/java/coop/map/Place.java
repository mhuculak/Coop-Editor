package coop.map;

import coop.io.*;
import coop.item.*;
import coop.player.Player;

import java.util.*;

public class Place {
	private String placeName;
	private String className;
	private String desc;		
	private List<Prop> props;
	private String id;
	private CrossPattern crossPattern;
	private List<Player> players;
	private List<Item> items;
	private List<Wall> walls;
	private List<Water> water;
	private Cell cell;

	public Place(String id, String name) {
		this.id = id;
		placeName = name;
		className = "coop.map.Place";
	}

	public Place(String line) {
		//		System.out.println("parse line " + line);
		String[] data = line.split("#");
		String temp = data[0];
		String[] atemp = temp.split(":");
		id = atemp[0].replaceAll("\\D+","");
		String temp2 = atemp[1];
		String[] atemp2 = temp2.split(",");
		placeName = atemp2[0];
		className = atemp2[1];

		String parsing = "";
		for ( int i=1 ; i<data.length ; i++) {
			if (data[i].equals("vprop") || data[i].equals("items") || data[i].equals("actions")) { // skip
				parsing = data[i];
			}
			else {
				if (parsing.equals("vprop")) {
					System.out.println("Parse vprop " + data[i]);
					String[] key_val = data[i].split(":");
					ValueProp vprop = new ValueProp(key_val[0]);
					if (key_val.length==2) {
						vprop.setValue(key_val[1]);						
					}
					else {
						vprop.setValue("");
					}
					addProp(vprop);
				}
				else if (parsing.equals("items")) {
					String[] item_arr = data[i].split(",");
					ItemsProp items = new ItemsProp("items");
					for ( int j=0 ; j<item_arr.length ; j++) {
						items.addItem(item_arr[j]);
					}
					addProp(items);

				}
				else if (parsing.equals("actions")) {
					String[] action_arr = data[i].split(",");
					ActionsProp actions = new ActionsProp("actions");
					for ( int j=0 ; j<action_arr.length ; j++) {
						String[] key_val = action_arr[j].split(":");
						actions.addAction(key_val[0], key_val[1]);
					}
					addProp(actions);
				}
			}
		}
	}

	public void setCell(Cell cell) {
		this.cell = cell;
	}

	public Cell getCell() {
		return cell;
	}

	public void addWall(Wall wall) {
		if (walls == null) {
			walls = new ArrayList<Wall>();
		}
		walls.add(wall);
	}

	public void removeWall(Wall wall) {
		walls.remove(wall);
	}

	public List<Wall> getWalls() {
		return walls;
	}

	public List<Water> getWater() {
		return water;
	}

	public void addWater(Water w) {
		if (water == null) {
			water = new ArrayList<Water>();
		}
		water.add(w);
	}

	public void addItem(Item item) {
		if (items == null) {
			items = new ArrayList<Item>();			
		}
		System.out.println("Add item " + item.getName());
		items.add(item);
	}	

	public void removeItem(Item item) {
		items.remove(item);
	}

	public CrossPattern getCrossPattern() {
		return crossPattern;
	}

	public void setCrossPattern(CrossPattern crossPattern) {
		this.crossPattern = crossPattern;
	}

	public void setProps(List<Prop> props) {
		this.props = props;
	}

	public void addProp(Prop prop) {
		if (props == null) {
			props = new ArrayList<Prop>();
		}
		props.add(prop);
	}

	public String getPlaceName() {
		return placeName;
	}	

	public void setPlaceName(String placeName) {
		this.placeName = placeName;
	}

	public String getClassName() {
		return className;
	}

	public List<Prop> getProps() {
		return props;
	}

	public String getID() {
		return id;
	}

	public void addPlayer(Player player) {
		if (players == null) {
			players = new ArrayList<Player>();			
		}
		players.add(player);
	}

	public void removePlayer(Player player) {
		players.remove(player);
	}

	public void setPlayers(List<Player> players) {
		this.players = players;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public List<Item> getItems() {
		return items;
	}

	public String getDesc() {
/*		
		for (Prop prop : props) {
			if (prop.getName().equals("desc")) {			
				ValueProp vprop = (ValueProp)prop;
				return vprop.getValue();
			}
		}
		return null;
*/		
		return desc;		
	}

	public void setDesc(String desc) {
/*		
		for (Prop prop : props) {
			if (prop.getName().equals("desc")) {			
				ValueProp vprop = (ValueProp)prop;
				vprop.setValue(desc);
			}
		}	
*/
		this.desc = desc;			
	}

	public String getMouseOverInfo() {
		StringBuilder sb = new StringBuilder(100);
		sb.append(placeName+":");
		if (players != null) {			
			for ( Player player : players) {
				sb.append(player.getName()+", ");
			}
			
		}
		if (items != null) {
			for ( Item item : items ) {
				sb.append(item.getName()+", ");
			}
		}
		return sb.toString();
	}

	public String toString() {		
		StringBuilder sb = new StringBuilder(100);
		sb.append("place" + id + ":" + placeName + "," + className);
		sb.append("#vprop#" + desc);
	
/*		
		for (Prop prop : getProps()) {
			if (prop instanceof ValueProp) {
        		ValueProp vprop = (ValueProp)prop;
        		sb.append("#vprop#" + vprop.toString());
        	}
        	else if (prop instanceof ItemsProp) {
        		ItemsProp items = (ItemsProp)prop;
        		sb.append("#items#" + items.toString());
        	}
        	else if (prop instanceof ActionsProp) {
        		ActionsProp actions = (ActionsProp)prop;
        		sb.append("#actions#" + actions.toString());
        	}
		}
*/		
		return sb.toString();
	}

	
}