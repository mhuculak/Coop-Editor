package coop.item;

public class Phone extends ActionItem  {
	public Phone() {
		init();
	}

	public Phone(String line) {		
		super(line);		
		init();	
	}

	private void init() {
		setType("phone");
		setWeight(0.15);
		addAction("talk");		
	}
}