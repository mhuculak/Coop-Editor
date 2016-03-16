package coop.item;
import java.util.*;
import java.util.Map.Entry;

public class ChangeItem extends Item {

	private Map<String, Class<?>> changes;   // each change is a verb -> class mapping

	public ChangeItem() {

	}

	public ChangeItem(String line) {
		super(line.split("#")[0]);
		String[] data = line.split("#");		
		if (data.length == 3) {
			changes = new HashMap<String, Class<?>>();
			String[] d = data[2].split(",");
			for ( int i=0 ; i<d.length ; i++ ) {
				String[] verb_class = d[i].split(":");
				try {
					System.out.println("add change verb:" + verb_class[0] + " class:" + verb_class[1]);
					changes.put(verb_class[0], Class.forName(verb_class[1]));
				}
				catch (ClassNotFoundException ex) {
					ex.printStackTrace();
				} 
			}
		}		
	}

	public Map<String, Class<?>> getChanges() {
		return changes;
	}

	public void addChange(String action, Class c ) {
		if (changes == null) {
			changes = new HashMap<String, Class<?>>();
		}
		changes.put(action, c);
	}

	@Override
	public String toString(String className) {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString(null));
		if (changes != null) {
			sb.append("#achanges#");
			Iterator entries = changes.entrySet().iterator();
			while ( entries.hasNext()) {
        		Entry entry = (Entry) entries.next();
        		Class c = (Class)entry.getValue();		
				sb.append(entry.getKey().toString()+":"+c.getName()+",");
			}
			sb.append("#");
		}
		if (className != null) {
			sb.append("#"+className);
		}
		return sb.toString();
	}
}