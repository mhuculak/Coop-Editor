package coop.io;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class ActionsProp extends Prop {
	private Map<String, String> actions;

	public ActionsProp(String name) {
		super(name);
	}

	public void addAction(String action, String target) {
		if (actions == null) {
			actions = new HashMap<String,String>();
		}
		actions.put(action, target);
	}

	public Map<String,String> getActions() {
		return actions;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder(100);
		
        Iterator entries = actions.entrySet().iterator();
		while ( entries.hasNext()) {
        	Entry entry = (Entry) entries.next();
        	sb.append(entry.getKey().toString()+":"+entry.getValue().toString());
        	if (entries.hasNext()) {
        		sb.append(",");
        	}
        }
        return sb.toString();
	}
}