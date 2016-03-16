package coop.item;

public class Pen extends ActionItem {
	public Pen() {
		init();
	}

	public Pen(String line) {
		super(line);
		init();
	}

	private void init() {
		setType("pen");
		addAction("write");
	}
}