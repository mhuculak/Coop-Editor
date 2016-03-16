package coop.item;

public class Bucket extends ContainerItem {

	public Bucket() {
		init();
	}

	public Bucket(String line) {		
		super(line);		
		init();	
	}

	private void init() {
		setType("bucket");
		addAction("add");
		addAction("remove");
		addAction("pour");
		addAction("fill");
	}
	
}