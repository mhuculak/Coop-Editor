import java.io.*;

public class GameWrapper {
	private GameDef gameDef;
	private MapDef mapDef;

	public GameWrapper(GameDef gameDef, MapDef mapDef) {
		this.gameDef = gameDef;
		this.mapDef = mapDef;
	}

	static GameWrapper readGameWrapperFile(String fname) {
		try {			
			FileInputStream fis = new FileInputStream(fname);	
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));			
			int i=0;
			MapDef mapDef = new MapDef();
			GameDef gameDef = new GameDef(fname);
			String line = null;
			while ((line = br.readLine()) != null) {
				String[] type_data = line.split(":");
				String type = type_data[0];
				if (type.contains("place")) {
					gameDef.parseLine(line);
				}
				else {
					mapDef.parseLine(line);
				}
				i++;
			}
			br.close();
			System.out.println("Read " + i + " lines from " + fname);
			return new GameWrapper(gameDef, mapDef);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	

	public GameDef getGameDef() {
		return gameDef;
	}

	public MapDef getMapDef() {
		return mapDef;
	}
}