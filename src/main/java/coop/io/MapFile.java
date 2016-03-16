package coop.io;

import coop.map.MapDef;
import coop.map.GameDef;
import coop.map.Place;
import coop.map.CellDef;
import coop.map.Wall;
import coop.map.Door;
import coop.player.Player;

import java.io.*;
import java.util.Map;
import java.awt.*;

public class MapFile {
	
	public static void writeMapFile(String path, GameWrapper gameWrapper) {
		int i =0;
		MapDef mapDef = gameWrapper.getMapDef();
		GameDef gameDef = gameWrapper.getGameDef();
		try {

			File fout = new File(path);
			FileOutputStream fos = new FileOutputStream(fout);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			
//			bw.write(Double.toString(mapDef.getGridFactor()));
//			bw.newLine();
			bw.write("mapDim:" + mapDef.getDim().toString());
			bw.newLine();
			Color c = mapDef.getDefaultColor();
			if (c != null) {
				bw.write("defaultColor:"+c.getRed() + "," + c.getGreen() + "," + c.getBlue());
				bw.newLine();
			}
			if (gameDef.getPlaces() != null) {
				for (Place place : gameDef.getPlaces()) {
					bw.write(place.toString());
					bw.newLine();
				}
			}
			if (gameDef.getPlayers() != null) {
				for (Player player : gameDef.getPlayers()) {
					bw.write(player.toString());
					bw.newLine();
				}
			}
			if (mapDef.getConfigParams() != null) {
				if (mapDef.getConfigParams().getStringParams() != null) {
					for (Map.Entry<String, String> entry : mapDef.getConfigParams().getStringParams().entrySet()) {
						bw.write("sParam:" + entry.getKey() + ":" + entry.getValue());
						bw.newLine();
					}
				}
				if (mapDef.getConfigParams().getIntParams() != null) {
					for (Map.Entry<String, Integer> entry : mapDef.getConfigParams().getIntParams().entrySet()) {
						bw.write("iParam:" + entry.getKey() + ":" + Integer.toString(entry.getValue()));
						bw.newLine();
					}
				}
				if (mapDef.getConfigParams().getDoubleParams() != null) {
					for (Map.Entry<String, Double> entry : mapDef.getConfigParams().getDoubleParams().entrySet()) {
						bw.write("dParam:" + entry.getKey() + ":" + Double.toString(entry.getValue()));
						bw.newLine();
					}
				}
			}			
			for (CellDef cellDef : mapDef.getCells()) {
				bw.write(cellDef.toString());
				bw.newLine();
				i++;
			}
			if (mapDef.getWalls() != null) {
				for (Wall wall : mapDef.getWalls()) {
					bw.write(wall.toString());
					bw.newLine();
					i++;
				}
			}
			if (mapDef.getDoors() != null) {
				for (Door door : mapDef.getDoors()) {
					bw.write(door.toString());
					bw.newLine();
					i++;
				}
			}
			bw.close();			
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}