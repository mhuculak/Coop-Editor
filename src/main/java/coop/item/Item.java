package coop.item;

public class Item {
	private String name;        // name of the specific item object created
	private String type;        // type is associated with the class used to create the item
	private double weight;

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public double getWeight() {
		return weight;
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
}