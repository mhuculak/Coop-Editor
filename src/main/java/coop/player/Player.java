package coop.player;

import coop.map.Place;

public class Player {

	private String id;
	private String name;
	private String className;
	private String desc;	
	private Place place;
	private String placeID;		
	private double height;
	private double weight;
	private double strength; // max strength is 1.0
		
	public Player(String id, String name, String desc, Place place, double height, double weight, double strength) {
		this.id = id;
		this.name = name;
		this.desc = desc;
		this.place = place;
		this.height = height;
		this.weight = weight;
		this.strength = strength;
		this.className = "coop.player.Player";
	}

	public Player(String line) {				
		String[] d = line.split(":");
		id = d[0].replaceAll("\\D+","");
		name = d[1];
		desc = d[2];
		placeID = d[3];
		height = Double.parseDouble(d[4]);
		weight = Double.parseDouble(d[5]);
		strength = Double.parseDouble(d[6]);
		this.className = "coop.player.Player";
	}

	public void setPlace(Place place) {
		this.place = place;
	}

	public Place getPlace() {
		return place;
	}

	public String getPlaceID() {
		return placeID;
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

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public double getStrength() {
		return strength;
	}

	public void setStrength(double strength) {
		this.strength = strength;
	}

	public String toString() {		
		return "player"+id+":"+name+":"+desc+":"+place.getID()+":"+height+":"+weight+":"+strength;
	}
}