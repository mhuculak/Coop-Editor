package coop.item;

public class Paper extends ChangeItem {

	public Paper() {
		init();
	}

	public Paper(String line) {
		super(line);
		init();
	}

	private void init() {
		addChange("write", Note.class);
		setType("paper");
	}
	
}