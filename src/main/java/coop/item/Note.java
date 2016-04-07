package coop.item;

public class Note extends ActionItem implements HasText {

	private String text;	

	public Note() {
		init();		
	}

	public Note(String line) {
		super(line);
		init();
		String[] data = line.split("#");		
		text = data[data.length-1];		
	}

	private void init() {
		setWeight(0.0);
		setType("note");
	}

	public void setText(String noteText) {
		System.out.println("Setting note text to " + noteText);
		this.text = noteText;
	}

	public String getText() {
		return text;
	}

	@Override
	public String toString(String className) {
		StringBuilder sb = new StringBuilder(100);
		sb.append(super.toString(null));
		if (text != null) {
			sb.append("#"+text);
		}
		if (className != null) {
			sb.append("#"+className);
		}
		return sb.toString();
	}
	
}