package coop.item;

import java.util.*;

public class ActionItem extends Item {

	private List<String> actions;

	public ActionItem() {

	}

	public ActionItem(String line) {
		super(line.split("#")[0]);
		String[] data = line.split("#");		
		if (data.length >= 3) { //  the tail will be read by the child class
			String[] a = data[2].split(",");
			for ( int i=0 ; i<a.length ; i++ ) {
				addAction(a[i]);
			}
		}
	}

	public List<String> getActions() {
		return actions;
	}

	public void addAction(String action) {
		if (actions == null) {
			actions = new ArrayList<String>();
		}
		actions.add(action);
	}

	@Override
	public String toString(String className) {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString(null));
		if (actions != null) {
			sb.append("#actions#");
			for ( String action : actions) {
				sb.append(action+",");
			}
			sb.append("#");
		}
		if (className != null) {
			sb.append("#"+className);
		}
		return sb.toString();
	}
	
}