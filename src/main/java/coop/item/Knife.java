package coop.item;

public class Knife extends ActionItem {
	public Knife() {
		init();
	}

	public Knife(String line) {
		super(line);
		init();
	}

	private void init() {
		setType("knife");
		addAction("cut");
	}
}