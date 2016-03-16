package coop.item;

public class Gold extends ActionItem implements HasValue {

	private double value;

	public Gold() {
		init();
	}

	public Gold(String line) {		
		super(line);		
		init();
		String[] data = line.split("#");		
		value = Double.parseDouble(data[data.length-1]);	
	}

	private void init() {
		setWeight(0.3);
		setType("gold");
		addAction("buy");
		addAction("bribe");		
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public Double getValue() {
		return this.value;
	}

	public void addValue(Double value) {
		this.value += value;
	}

	public boolean removeValue(Double value) {
		if (this.value > value) {
			this.value -= value;
			return true;
		}
		return false;
	}

	@Override
	public String toString(String className) {
		StringBuilder sb = new StringBuilder(100);
		sb.append(super.toString(null));		
		sb.append("#"+Double.toString(value));
		
		if (className != null) {
			sb.append("#"+className);
		}
		return sb.toString();
	}
}