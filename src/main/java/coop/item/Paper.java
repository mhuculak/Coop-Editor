package coop.item;

public class Paper extends ChangeItem {

	public Paper() {
		addChange("write", Note.class);
		setType("paper");
	}
	
}