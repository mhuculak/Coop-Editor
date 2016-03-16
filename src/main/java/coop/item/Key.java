package coop.item;

public class Key extends ActionItem {

	public Key() {
		init();
	}

	public Key(String line) {		
		super(line);		
		init();	
	}

	private void init() {
		setType("key");
		addAction("lock");
		addAction("unlock");
	}
	
}