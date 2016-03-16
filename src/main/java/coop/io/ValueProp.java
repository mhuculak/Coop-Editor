package coop.io;

public class ValueProp extends Prop {
	private String value;

	public ValueProp(String name) {
		super(name);
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public String toString() {
		return super.getName() + ":" + value;
	}
}