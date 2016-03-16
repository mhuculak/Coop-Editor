package coop.item;

public class Note extends ActionItem {

	private String noteText;	

	public Note() {
		this.noteText = noteText;
		setWeight(0.0);
		setType("note");
	}

	public void setText(String noteText) {
		this.noteText = noteText;
	}

	public String getText() {
		return noteText;
	}
	
}