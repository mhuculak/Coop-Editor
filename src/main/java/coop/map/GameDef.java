package coop.map;

import coop.map.Place;
import coop.player.Player;
import coop.item.Item;

import java.util.*;
import java.lang.reflect.*;

public class GameDef {
	private String gameName;
	private List<Place> places;
	private List<Player> players;
	private List<Item> items;

	public GameDef() {
		places = new ArrayList<Place>();
		players = new ArrayList<Player>();
		items = new ArrayList<Item>();
	}	

	public void setName(String name) {
		System.out.println("GameDef checking name " + name);
		String[] comp = name.split("\\."); // remove file extension
		if (comp == null) {
			System.out.println("Failed to find basename from filename" + name);
		}
		else {
			System.out.println(name + " has " + comp.length + " components");
		}
		gameName = comp[0];
	}

	public void parseLine(String line) {
		System.out.println("parsing " + line);
		if (line.contains("place")) {
			Place p = new Place(line);
			addPlace(p);
		}
		else if (line.contains("player")) {
			Player p = new Player(line);
			addPlayer(p);
		}
		else if (line.contains("item")) {
			String[] z = line.split("#");
			String className = z[z.length-1];
			line = line.substring(0, line.length() - className.length() - 1); // trims #className
			Object obj = null;
			try {
				
				Class c = Class.forName(className);
				Constructor ctor = c.getConstructor(String.class);
				obj = ctor.newInstance(line);
			}
			catch (NoSuchMethodException | InstantiationException | ClassNotFoundException | IllegalAccessException | InvocationTargetException ex) { // Live dnagerously!
				ex.printStackTrace();
			}
			Item item = (Item)obj;
			System.out.println("Adding item " + item.getName() + " of type " + item.getType());
			addItem(item);
		}
		else {
			System.out.println("ERROR: GameDef cannot parse " + line);
		}	
	}

	public void addPlace(Place place) {				
		places.add(place);
	}

	public void addPlayer(Player player) {		
		System.out.println("Add player " + player.getName());		
		players.add(player);
	}

	public void addItem(Item item) {		
		items.add(item);
	}

	public String getName() {
		return gameName;
	}

	public List<Place> getPlaces() {
		return places;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public List<Item> getItems() {
		return items;
	}

	public String getNextPlayerID() {
		if (players == null || players.size() == 0) {
			return "0";
		}
		else {
			return Integer.toString(players.size());
		}
	}

	public String getNextItemID() {
		if (items == null || items.size() == 0) {
			return "0";			
		}
		else {
			return Integer.toString(items.size());
		}
	}

	public Place getPlace (String id) {
	    for ( Place place : places ) {
    		if (place.getID().equals(id)) {
        		return place;
      		}
      	}
      	return null;
    }

    public Item findItem(String name, Class c) {
    	for (Item item : items) {
    		if (item.getName().equals(name) && item.getClass().equals(c)) {
    			return item;
    		}
    	}
    	return null;
    }
   
   	// call this after loading GameDef data from a file
  	public void resolve() {
  		System.out.println("Resolving ...");
  		if (players != null) {
  			System.out.println("Resolving players...");
	  		for ( Player player : players) {
	  			System.out.println("Resolving player " + player.getName());
  				Place place = getPlace(player.getPlaceID());
  				if (place == null) {
  					System.out.println("ERROR: could not resolve place ID " + player.getPlaceID());
  				}
  				else {
  					System.out.println("Setting place for player " + player.getName() + " to " + place.getPlaceName());
  					player.setPlace(place);
  					place.addPlayer(player); 
  				}
  			}  			
  		}
  		if (items != null) {
  			System.out.println("Resolving items...");
	  		for ( Item item : items) {
	  			System.out.println("Resolving item " + item.getName());
  				Place place = getPlace(item.getPlaceID());
  				if (place == null) {
  					System.out.println("ERROR: could not resolve place ID " + item.getPlaceID());
  				}
  				else {
  					System.out.println("Setting place for item " + item.getName() + " to " + place.getPlaceName());
  					item.setPlace(place);
  					place.addItem(item); 
  				}
  			}  			
  		}  		
  	}
}