package coop.item;

public class Shovel extends ActionItem {
	public Shovel() {
		init();
	}

	public Shovel(String line) {
		super(line);
		init();
	}

	private void init() {
		setType("shovel");
		addAction("dig");
		setWeight(3.0);
	}
}