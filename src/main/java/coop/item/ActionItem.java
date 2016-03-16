package coop.item;

import java.util.*;

public class ActionItem extends Item {

	private List<String> actions;

	public List<String> getActions() {
		return actions;
	}

	public void addAction(String action) {
		if (actions == null) {
			actions = new ArrayList<String>();
		}
		actions.add(action);
	}
	
}