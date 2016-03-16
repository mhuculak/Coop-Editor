package coop.map;

import coop.map.Place;
import coop.player.Player;

import java.util.*;

public class GameDef {
	private String gameName;
	private List<Place> places;
	private List<Player> players;	

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
		else {
			System.out.println("ERROR: GameDef cannot parse " + line);
		}	
	}

	public void addPlace(Place place) {
		if (places == null) {
			places = new ArrayList<Place>();
		}		
		places.add(place);
	}

	public void addPlayer(Player player) {
		if	(players == null) {
			players = new ArrayList<Player>();
		}
		System.out.println("Add player " + player.getName());		
		players.add(player);
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

	public String getNextPlayerID() {
		if (players == null || players.size() == 0) {
			return "0";
		}
		else {
			return Integer.toString(players.size());
		}
	}

	private Place getPlace (String id) {
	    for ( Place place : places ) {
    		if (place.getID().equals(id)) {
        		return place;
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
  	}
}