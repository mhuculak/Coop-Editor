package coop.item;

public class Bag extends ContainerItem {

	public Bag() {
		init();
	}

	public Bag(String line) {		
		super(line);		
		init();	
	}

	private void init() {
		setType("bag");
		addAction("put");
		addAction("remove");
	}
	
}