import java.util.*;

public class Items extends Prop {
	private List<String> items;

	public Items(String name) {
		super(name);
	}

	public void addItem(String item) {
		if (items == null) {
			items = new ArrayList<String>();
		}
		items.add(item);
	}

	public List<String> getItems() {
		return items;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder(100);
		Iterator<String> itr = items.iterator();		
        while (itr.hasNext()) {
        	sb.append(itr.next());
        	if (itr.hasNext()) {        	
       			sb.append(",");
        	}
        }
        return sb.toString();
	}
}