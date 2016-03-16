package coop.item;

import java.util.*;

class Bond {
	private Class c1;
	private Class c2;

	public Bond(Class c1, Class c2) {
		this.c1 = c1;
		this.c2 = c2;
	}
}

public class BinderItem extends Item {

	private List<Bond> bonds;

	public void addBond(Class c1, Class c2) {
		if (bonds == null) {
			bonds = new ArrayList<Bond>();
		}
		bonds.add(new Bond(c1, c2));
	}
	
}