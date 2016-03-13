import java.util.*;

public class GameDef {
	private String gameName;
	private List<Place> places;

	public GameDef(String name) {
		System.out.println("GameDef checking name " + name);
		String[] comp = name.split("\\."); // remove file extension
		if (comp == null) {
			System.out.println("Failed to find basename from filename" + name);
		}
		else {
			System.out.println(name + " has " + comp.length + " components");
		}
		gameName = comp[0];
		places = new ArrayList<Place>();
	}
	public void parseLine(String line) {
		Place p = new Place(line, "dummy");
		addPlace(p);		
	}

	public void addPlace(Place place) {		
		places.add(place);
	}

	public String getName() {
		return gameName;
	}

	public List<Place> getPlaces() {
		return places;
	}
}