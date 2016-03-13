import java.util.*;

public class Place {
	private String placeName;
	private String className;		
	private List<Prop> props;
	private String id;

	public Place(String id, String name) {
		this.id = id;
		placeName = name;
		className = "cooperation.PositionState";
	}

	public Place(String line) {
		//		System.out.println("parse line " + line);
		String[] data = line.split("#");
		String temp = data[0];
		String[] atemp = temp.split(":");
		id = atemp[0].replaceAll("\\D+","");
		String temp2 = atemp[1];
		String[] atemp2 = temp2.split(",");
		placeName = atemp[0];
		className = atemp2[1];

		String parsing = "";
		for ( int i=1 ; i<data.length ; i++) {
			if (data[i].equals("vprop") || data[i].equals("items") || data[i].equals("actions")) { // skip
				parsing = data[i];
			}
			else {
				if (parsing.equals("vprop")) {
					String[] key_val = data[i].split(":");
					ValueProp vprop = new ValueProp(key_val[0]);
					vprop.setValue(key_val[1]);
					addProp(vprop);
				}
				else if (parsing.equals("items")) {
					String[] item_arr = data[i].split(",");
					Items items = new Items("items");
					for ( int j=0 ; j<item_arr.length ; j++) {
						items.addItem(item_arr[j]);
					}
					addProp(items);

				}
				else if (parsing.equals("actions")) {
					String[] action_arr = data[i].split(",");
					ActionsProp actions = new ActionsProp("actions");
					for ( int j=0 ; j<action_arr.length ; j++) {
						String[] key_val = action_arr[j].split(":");
						actions.addAction(key_val[0], key_val[1]);
					}
					addProp(actions);
				}
			}
		}
	}	

	public void setProps(List<Prop> props) {
		this.props = props;
	}

	public void addProp(Prop prop) {
		if (props == null) {
			props = new ArrayList<Prop>();
		}
		props.add(prop);
	}

	public String getPlaceName() {
		return placeName;
	}	

	public String getClassName() {
		return className;
	}

	public List<Prop> getProps() {
		return props;
	}

	public String toString() {		
		StringBuilder sb = new StringBuilder(100);
		sb.append("place" + id + ":" + placeName + "," + className);
		for (Prop prop : getProps()) {
			if (prop instanceof ValueProp) {
        		ValueProp vprop = (ValueProp)prop;
        		sb.append("#vprop#" + vprop.toString());
        	}
        	else if (prop instanceof Items) {
        		Items items = (Items)prop;
        		sb.append("#items#" + items.toString());
        	}
        	else if (prop instanceof ActionsProp) {
        		ActionsProp actions = (ActionsProp)prop;
        		sb.append("#actions#" + actions.toString());
        	}
		}
		return sb.toString();
	}

	
}