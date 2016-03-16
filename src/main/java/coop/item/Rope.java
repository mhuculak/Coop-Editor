package coop.item;

public class Rope extends BinderItem {
	public Rope() {
		init();
	}

	public Rope(String line) {
		super(line);
		init();
	}

	private void init() {
		setType("rope");
		addAction("tie");
	}
}