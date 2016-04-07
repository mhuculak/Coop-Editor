package coop.item;

import coop.map.Place;

public class Item {
	private String id;
	private String className;
	private String name;        // name of the specific item object created
	private String type;        // type is associated with the class used to create the item
	private double weight;
	private Place place;
	private String placeID;

	public Item() {
		className = "coop.item.Item";
	}

	public Item(String line) {
		String[] data = line.split(":");
		id = data[0].replaceAll("\\D+","");
		name = data[1];
		type = data[2];
		weight = Double.parseDouble(data[3]);
		placeID = data[4];
		className = "coop.item.Item";
	}

	public String getID() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getClassName() {
		return className;
	}

	public String getType() {
		return type;
	}

	public double getWeight() {
		return weight;
	}

	public Place getPlace() {
		return place;
	}

	public String getPlaceID() {
		return placeID;
	}

	public void setID(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setPlace(Place place) {
		this.place = place;
	}

	public String toString(String className) {
		String value = "item"+id+":"+name+":"+type+":"+Double.toString(weight)+":"+place.getID();
		if (className != null) {
			return value + "#" + className;
		}
		else {
			return value;
		}
		
	}
}