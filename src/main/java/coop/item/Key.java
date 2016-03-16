package coop.item;

public class Key extends ActionItem {

	public Key() {
		setType("key");
		addAction("lock");
		addAction("unlock");
	}
	
}