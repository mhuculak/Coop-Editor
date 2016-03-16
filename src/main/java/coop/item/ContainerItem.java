package coop.item;

import java.util.*;

// FIXME: need to add the plumbing to support adding and removing items
public class ContainerItem extends Item {
	
	private List<Item> items;
	private List<String> actions;

	public ContainerItem() {

	}	

	public ContainerItem(String line) {

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