package coop.item;

import java.util.*;

class Bond {
	private Class c1;
	private Class c2;

	public Bond(Class c1, Class c2) {
		this.c1 = c1;
		this.c2 = c2;
	}

	public Bond(String line) {
		String data[] = line.split(":");
		try {
			c1 = Class.forName(data[0]);
			c2 = Class.forName(data[1]);
		}
		catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		} 
	}

	public String toString() {
		return c1.getName() + ":" + c2.getName();
	}
}

public class BinderItem extends Item {	

	private List<Bond> bonds;
	private List<String> actions;

	public BinderItem() {

	}

	public BinderItem(String line) {
		super(line.split("#")[0]);
		String[] data = line.split("#");		
		for ( int i=1 ; i<4 && i<data.length ; i+=2 ) {
			if (data[i].equals("bonds")) {
				bonds = new ArrayList<Bond>();
				String[] b = data[i+1].split(",");
				for ( int j=0 ; j<b.length ; j++ ) {
					bonds.add(new Bond(b[j]));
				}
			}
			else if (data[i].equals("actions")) {
				String[] a = data[i+1].split(",");
				for ( int j=0 ; j<a.length ; j++ ) {
					addAction(a[j]);
				}
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

	public void addBond(Class c1, Class c2) {
		if (bonds == null) {
			bonds = new ArrayList<Bond>();
		}
		bonds.add(new Bond(c1, c2));
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
		if (bonds != null) {
			sb.append("#bonds#");
			for (Bond bond : bonds) {
				sb.append(bond.toString()+ ",");
			}
			sb.append("#");
		}
		if (className != null) {
			sb.append("#"+className);
		}
		return sb.toString();
	}
	
}