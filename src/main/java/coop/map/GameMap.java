package coop.map;

import coop.io.*;
import coop.mapeditor.*;
import coop.util.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import java.util.*;


class DrawLine extends JPanel {
  private MyLine line;
  private float width;
  DrawLine(MyLine line, float width) {
    this.line = line;
    this.width = width;
  }
  public void paintComponent(Graphics g) {
    Graphics2D g2 = (Graphics2D) g;
    g2.setStroke(new BasicStroke(width));
    g2.setColor(line.getColor());    
//    System.out.println("Draw line"); 
    g2.drawLine(line.getEndp1().getX(), line.getEndp1().getY(), line.getEndp2().getX(), line.getEndp2().getY());    
  }
}

class FillCell extends JPanel {
  private Cell cell;
  FillCell(Cell cell) {
    this.cell = cell;
  }
  public void paintComponent(Graphics g) {
    g.setColor(cell.getColor());
//    System.out.println("Fill cell with color " + cell.getColor().toString());     
    g.fillPolygon(cell.getHexagon());               
  }
}

class HighlightCell extends JPanel {
  private Cell cell;
  private float width;
  private String text;
  HighlightCell(Cell cell, float width) {
    this.cell = cell;
    this.width = width;
  }
  HighlightCell(Cell cell, float width, String text) {
    this.cell = cell;
    this.width = width;
    this.text = text;
  }
  public void paintComponent(Graphics g) {
    Graphics2D g2 = (Graphics2D) g;
    g2.setStroke(new BasicStroke(width));
    Color borderColor;
    if (cell.getColor() == Color.black || cell.getColor() == Color.darkGray) {
      borderColor = Color.lightGray;
    }
    else {
      borderColor = Color.darkGray;
    }
    g2.setColor(borderColor); 
//    System.out.println("Highlight cell with color " + borderColor.toString());    
    g2.drawPolygon(cell.getHexagon());
    if (text != null) {
      g2.drawString(text, cell.getPosition().getX(), cell.getPosition().getY());         
    }
  }
}

public class GameMap extends JPanel {

	private JFrame m_frame;
//	private ColorSelector m_colorSelector;
	private MyPoint m_mapDim;
//	private double m_gridFactor;
	private ConfigParams m_configParams;
  private GameDef m_gameDef;
	private double m_cellSize;	
	private double m_edgeSize;
	private double m_yLen;
  private int m_cellsPerRow;
  private int m_numRows;
  private int m_numCells;
	private java.util.List<Cell> m_cells;
  private java.util.List<Wall> m_walls;
  private java.util.List<Door> m_doors;
	private double m_xoff;
	private double m_yoff;
	private boolean m_clickListenerEnabled;
  private boolean m_motionListenerEnabled;
	private Cell mouseOverCell;
  private MyLine mouseOverLine;
  private String m_editMode;        // cell, highlight, line, door
  private String m_editAction;      // add, modify, remove
  private MyLine m_selectedLine;
  private Cell m_selectedCell;
  private Door m_selectedDoor;
  private double m_currentWallHeight;
  private boolean m_currentWallHasRoof;
  private boolean m_currentDoorIsLocked;
  private String m_currentDoorKeyName;
  private Color m_defaultCellColor;
  private Texture m_defaultCellTexture;
  private String m_viewMode;        // all, game
  private static int m_waterID;
  private static int m_wallID;
    
    // ctor for new game map
	public GameMap(JFrame frame, MyPoint mapDim, double edgeSize) {
//		m_colorSelector = colorSelector;
		m_frame = frame;
		m_configParams = new ConfigParams();
    initParams();
    m_gameDef = new GameDef(); 
    
		setupGrid( mapDim, edgeSize);
		int xoff = (int)(m_xoff + 0.5);
		int yoff = (int)(m_yoff + 0.5);
		int id = 0;

    for ( int i=0 ; i<m_numRows ; i++) {
      double y = m_yoff*i;
      int ys = (int)(y + 0.5);
      for ( int j=0 ; j<m_cellsPerRow ; j++) {
        double x = 3*m_edgeSize*j;
        if ( (i % 2) == 1) {
          x += m_xoff;
        }
        int xs = (int)(x + 0.5);
        MyPoint position = new MyPoint(xs, ys);
        Polygon hexagon = getHexagon(position);
        Cell cell = new Cell(Integer.toString(id++), hexagon, position);
        cell.setCenter(m_edgeSize, m_yLen);
        m_cells.add(cell);
      }
    }    
		start();
	}

    // ctor for loading a saved game/map
	public GameMap(GameWrapper gameWrapper, JFrame frame) {
//	 	m_colorSelector = colorSelector;
		m_frame = frame;
		m_configParams = gameWrapper.getMapDef().getConfigParams();
    m_gameDef = gameWrapper.getGameDef();   
    m_defaultCellColor = gameWrapper.getMapDef().getDefaultColor();
    m_defaultCellTexture = gameWrapper.getMapDef().getDefaultTexture();
		setupGrid(gameWrapper.getMapDef().getDim(), m_configParams.getDoubleParam("edgeSize"));
		m_cells = new ArrayList<Cell>();		
		for (CellDef cellDef : gameWrapper.getMapDef().getCells()) {			
			Polygon hexagon = getHexagon(cellDef.getPosition());
      Cell cell = null;
      if (cellDef.getTexture() != null) {
//        System.out.println("Creating cell with texture" + cellDef.getTexture().getName());
        cell = new Cell(cellDef.getID(), hexagon, cellDef.getPosition(), cellDef.getTexture());        
      }
      else {
        cell = new Cell(cellDef.getID(), hexagon, cellDef.getPosition(), cellDef.getColor());
      }
      cell.setCenter(m_edgeSize, m_yLen);
			m_cells.add(cell);
      
      if (m_gameDef.getPlaces() != null) {
        for ( Place place : m_gameDef.getPlaces() ) {
            if ( place.getID().equals(cellDef.getID())) {
                cell.setPlace(place);
                place.setCrossPattern(getCrossPattern(cell.getPosition()));
                place.setCell(cell);
            }
        }
      }
		}
    for (Cell cell : m_cells) {
      if (cell.getTexture() != null && cell.getTexture().getName().contains("water")) {
          addWater(cell);
        }
    }
    m_walls = gameWrapper.getMapDef().getWalls();
    m_doors = gameWrapper.getMapDef().getDoors();
		m_wallID = getMaxID(m_walls);
		start();
	}  

  private void setupGrid(MyPoint mapDim, double edgeSize) {
      
      m_viewMode = "all";
		  m_mapDim = mapDim;
		  double minEdgeSize = m_configParams.getDoubleParam("minEdgeSize");
      m_edgeSize = edgeSize; // m_configParams.getDoubleParam("edgeSize");
		  if (m_edgeSize < minEdgeSize) { // enforce minimum cell size
			   m_edgeSize = minEdgeSize;
		  }
		  m_configParams.addDoubleParam("edgeSize", m_edgeSize);
		  m_yLen = m_edgeSize*Math.sqrt(3.0);
		  m_cells = new ArrayList<Cell>();
		  m_xoff = 3.0*m_edgeSize/2.0;		
		  m_yoff = m_yLen/2.0;
      m_cellsPerRow = (int)(mapDim.getX() / (3*m_edgeSize));
      m_configParams.addIntParam("cellsPerRow", m_cellsPerRow);
      m_numRows = (int)(m_mapDim.getY() / m_yoff);
      m_numCells = m_numRows*m_cellsPerRow;
      m_waterID = 0;
      m_wallID = 0;
      System.out.println("Cells per row = " + m_cellsPerRow + " edge size = " + m_edgeSize);
	}

  private void initParams() {
    m_configParams.addDoubleParam("edgeSize", 30.0);
    m_configParams.addDoubleParam("minEdgeSize", 5.0);
    m_configParams.addDoubleParam("wallThickness", 5.0);
    m_configParams.addDoubleParam("gridThickness", 1.0);
    m_configParams.addDoubleParam("placeThickness", 3.0);
    m_configParams.addDoubleParam("selectThickness", 3.0);
  }

	public void disableClick() {
		m_clickListenerEnabled = false;
	}

	public void enableClick() {
		m_clickListenerEnabled = true;
	}

  public void disableMotion() {
    m_motionListenerEnabled = false;
  }

  public void enableMotion() {
    m_motionListenerEnabled = true;
    updateUI();
  }

  public void setViewMode(String mode) {
    m_viewMode = mode;
  }
  public void setEditMode(String mode) {
    m_editMode = mode;
  }

  public void setEditAction(String action) {
    m_editAction = action;
    System.out.println("edit action = " + m_editAction);
  }

  public void setWallParams(double height, boolean hasRoof) {
    m_currentWallHeight = height;
    m_currentWallHasRoof = hasRoof;
  }

  public void setDoorParams(boolean isLocked) {
    m_currentDoorIsLocked = isLocked;
  }

  public void setDoorParams(String keyName) {
    m_currentDoorKeyName = keyName;
  }

  public void setDefaultColor(Color defaultColor) {
    m_defaultCellColor = defaultColor;
  }
  public void setDefaultTexture(Texture defaultTexture) {
    m_defaultCellTexture = defaultTexture;
  }

  public void start() {
      if (m_walls == null) {
        m_walls = new ArrayList<Wall>();
      }
      if (m_doors == null) {
        m_doors = new ArrayList<Door>();
      }
      m_editMode = "cellColor";
    	m_clickListenerEnabled = true;
      m_motionListenerEnabled = true;    	
    	addMouseListener(new MouseAdapter() { 
        public void mousePressed(MouseEvent me) {
//           System.out.println("edit mode is " + m_editMode);
//              System.out.println("mouse click at "+me.getX()+" "+me.getY());
          Wall wall = null;
          m_selectedCell = findCell(me.getX(), me.getY());
          System.out.println("Selected cell " + m_selectedCell.getID());         
          if (m_clickListenerEnabled == true) {            
            if (m_editMode.equals("cellColor")) {
               
              if (m_selectedCell != null) {
                m_selectedCell.setColor(MapEditor.colorSelector.getColor(), MapEditor.colorSelector.getName());                        	
 	  	          FillCell fillCell = new FillCell(m_selectedCell);
              	Container contentPane = m_frame.getContentPane();
        				contentPane.add(fillCell);
              }
              else {
                System.out.println("No cell was selected at "+me.getX()+","+me.getY());
              }
            }
            else if (m_editMode.equals("cellTexture")) {
                Texture t = MapEditor.textureSelector.getSelectedTexture();
//                System.out.println("selected texture is " + t.getName());
                m_selectedCell.setTexture(t);
                if (t.getName().contains("water")) {
                  addWater(m_selectedCell);
                }
            }
            else if (m_editMode.equals("highlight")) {
              if (m_selectedCell != null) {
                HighlightCell highlightCell = new HighlightCell(m_selectedCell, (float)m_configParams.getDoubleParam("selectThickness"));
                m_frame.getContentPane().add(highlightCell);
              }
            }
            else if (m_editMode.equals("wall")) {
              m_selectedLine = findLine(me.getX(), me.getY());
              if (m_selectedLine != null && m_editAction != null) {                
                m_selectedLine.setColor(MapEditor.colorSelector.getColor());            
                wall = findWall(m_selectedLine);
                System.out.println(" edit action = " + m_editAction);
                if (wall == null && m_editAction.equals("add")) { 
                  String id = getNextWallid();
                  wall = new Wall(id, m_selectedLine.getOuterCellDirection(), m_selectedLine.getEndp1(), 
                                    m_selectedLine.getEndp2(), m_selectedLine.getCell(),
                                    m_currentWallHeight, m_currentWallHasRoof, m_selectedLine.getColor());
                  wall.setOuterCell(m_selectedLine.getOuterCell());
                  addWall(wall);
                  m_selectedLine.addWall(wall, m_gameDef);
                }
                else if (wall != null && m_editAction.equals("remove")) {                  
                  String doorID = wall.getDoorID();
                  if (doorID != null) {
                    Door door = findDoor(doorID);
                    if (door != null) {
                      removeDoor(door);
                    }
                  }
                  removeWall(wall);
                  m_selectedLine.removeWall(wall);
                }
              }              
              else {
                System.out.println("No line was selected at "+me.getX()+","+me.getY());
              }
            } 
            else if (m_editMode.equals("door")) {
                m_selectedLine = findLine(me.getX(), me.getY());

                if (m_selectedLine != null) {
                  m_selectedDoor = null;
                  wall = findWall(m_selectedLine);
                  String doorID = null;
                  if (wall != null) {                    
                    doorID = wall.getDoorID();
                    System.out.println("selected wall has door with id " + doorID);
                  }
                  if (doorID == null) {                
                    DoorDef doorDef = getDoorDef(m_selectedLine);
                    if (doorDef != null && m_editAction.equals("add")) {
                      doorID = getNextDoorID();
                      Door door = new Door(doorID, doorDef, m_currentDoorIsLocked);
                      door.setKeyName(m_currentDoorKeyName);
                      addDoor(door);
                      wall.setDoorID(door.getID());
                    }
                  }
                  else if (m_editAction.equals("remove")) {
                      m_selectedDoor = findDoor(doorID);                      
                      removeDoor(m_selectedDoor);
                      wall.removeDoorID();                    
                  }
                  else if (m_editAction.equals("modify")) {
                      m_selectedDoor = findDoor(doorID);
                      m_selectedDoor.toggle();
                      m_selectedDoor.setIsLocked(m_currentDoorIsLocked);
                      m_selectedDoor.setKeyName(m_currentDoorKeyName);
                  }
                }
                else {
                    System.out.println("ERROR: cannot place door");
                  
                }                
            }
            else {
              System.out.println("Unknown edit mode" + m_editMode);
            }
      			updateUI();
      			m_frame.setVisible(true); // FillCell changes don't appear unless you do this
      		}
       }              	 
    });

    addMouseMotionListener(new CustomMouseMotionListener());
              
  }

    class CustomMouseMotionListener implements MouseMotionListener {
    	
      public void mouseDragged(MouseEvent e) {
        if (m_motionListenerEnabled == true) {
          if (m_editMode.equals("cellColor")) {
            m_selectedCell = findCell(e.getX(), e.getY()); 
            if (m_selectedCell != null) {
              m_selectedCell.setColor(MapEditor.colorSelector.getColor(), MapEditor.colorSelector.getName());
            }
          }
        }
      }

      public void mouseMoved(MouseEvent e) {
 //        	MapEditor.statusLabel.setText("Mouse Moved: ("+e.getX()+", "+e.getY() +")");
          if (m_motionListenerEnabled == true) {
            Container contentPane = m_frame.getContentPane(); 
            if (m_editMode.equals("cellColor") || m_editMode.equals("highlight")) {         	  		       
              if (mouseOverCell != null) {
			 	        FillCell fillCell = new FillCell(mouseOverCell);
				        contentPane.add(fillCell);
			        }          
              mouseOverCell = findCell(e.getX(), e.getY());
                     		
              if (mouseOverCell != null) {                
                 Place place = mouseOverCell.getPlace();
                 HighlightCell highCell = null;
                 if (place == null) {
 		               highCell = new HighlightCell(mouseOverCell, 
                                        (float)m_configParams.getDoubleParam("selectThickness"));
                 }
                 else {
                   highCell = new HighlightCell(mouseOverCell, 
                                        (float)m_configParams.getDoubleParam("selectThickness"),
                                        place.getMouseOverInfo());

                 }
                contentPane.add(highCell);
              }      				
            }          
      		  updateUI();
      		  m_frame.setVisible(true);
          }
        }    
      }

  private DoorDef getDoorDef(MyLine line) {
    Cell innerCell = null;
    Cell outerCell = null;
    MyPoint innerPos = null;   
    MyPoint origin = line.getEndp1();
    MyPoint other = line.getEndp2();
    double mx = (other.getX() - origin.getX())/2.0;
    double my = (other.getY() - origin.getY())/2.0;
    MyPoint wallPos = new MyPoint((int)(mx + origin.getX() + 0.5), (int)(my + origin.getY() + 0.5));
    System.out.println("origin: "+origin.getX()+","+origin.getY());
    System.out.println("other: "+other.getX()+","+other.getY());
    System.out.println("wall pos: "+wallPos.getX()+","+wallPos.getY());
    double m2 = mx*mx + my*my;
    double px, py;
    if (other.getY() == origin.getY()) {
      px = 0.0;
      py = mx;
    }
    else {
      double mxy = mx/my;
      double p2 = m2 / (1 + mxy*mxy);
      px = Math.sqrt(p2);      
      py = -1.0 * mx * px / my;
    }
    int x = (int)(wallPos.getX() + px + 0.5);
    int y = (int)(wallPos.getY() + py + 0.5);
    int xo = (int)(wallPos.getX() - px + 0.5);
    int yo = (int)(wallPos.getY() - py + 0.5);
    System.out.println("Got " +x+","+y+" " +xo+","+yo);
    if (line.getCell().getHexagon().contains(x,y)) {
      innerCell = line.getCell();
      outerCell = findCell(xo,yo);
      if (outerCell == null) {
        System.out.println("ERROR: invalid door location - must lead to a cell on the map");
        return null;
      }
      innerPos = new MyPoint(x,y);
    }
    else {
      outerCell = line.getCell();
      innerCell = findCell(x,y);
      if (innerCell == null) {
        System.out.println("ERROR: internal error - could not find a cell on the map!"); // should not happen if math is correct
        return null;
      }
      innerPos = new MyPoint(xo,yo);
    }
    Wall wall = findWall(line); 
    if (wall == null) {
      System.out.println("ERROR: no wall exists at selected position - a door requires a wall");
      return null;
    }
    MyLine doorVector = new MyLine(wallPos, innerPos, innerCell);
    return new DoorDef(innerCell, outerCell, wall, doorVector);
  }

	public Cell findCell(int x, int y) {		
        for (Cell cell : m_cells) {
        	if (cell.getHexagon().contains(x, y)) {
        		return cell;
        	}
					
		}
    return null;
	}

  public Cell findCell(int id) {
    if ( id >= 0 && id<m_numCells) {
      for (Cell cell : m_cells) {
        if (id == Integer.parseInt(cell.getID())) {
          return cell;
        }
      }
    }
    return null;
  }

  public Wall findWall(MyLine line) {
    for (Wall wall : m_walls) {
      if (wall.contains(line)) {
        return wall;
      }
    }
    return null;

  }

  private Wall findWall(String id) {
    for (Wall wall : m_walls) {
      if (wall.getID().equals(id)) {
        return wall;
      }
    }
    return null;
  }

  private Door findDoor(String doorID) {
    for (Door door : m_doors) {
      if (door.getID().equals(doorID)) {
        return door;
      }
    }
    return null;
  }

  private double distance(MyLine l, MyPoint p) {
    MyPoint p1 = l.getEndp1();
    MyPoint p2 = l.getEndp2();
    double dx = p2.getX() - p1.getX();
    double dy = p2.getY() - p1.getY();    
    double dist = dy*p.getX() - dx*p.getY() + p2.getX()*p1.getY() - p2.getY()*p1.getX();
    dist = (dist<0) ? -dist : dist;
    dist = dist / Math.sqrt(dx*dx + dy*dy);
    return dist;
  }

  private int[] getAdjacentCells(Cell cell) {
    int innerCell = Integer.parseInt(cell.getID());
    int row = innerCell/m_cellsPerRow;
    int outerCell[] = new int[6];
    outerCell[0] = innerCell - 2*m_cellsPerRow;
    outerCell[1] = innerCell - m_cellsPerRow;
    outerCell[2] = innerCell + m_cellsPerRow;
    outerCell[3] = innerCell + 2*m_cellsPerRow;
    outerCell[4] = innerCell + m_cellsPerRow - 1;
    outerCell[5] = innerCell - m_cellsPerRow - 1;
    if ( (row % 2) == 1 ) {
       outerCell[1] += 1;
       outerCell[2] += 1;
       outerCell[4] += 1;
       outerCell[5] += 1;
    }
    return outerCell;
  }
  /*
      find the cell boundary segment closest to point x,y
   */
  public MyLine findLine(int x, int y) {
    Cell cell = findCell(x,y);
    if (cell == null) {
      return null;
    }
    MyPoint p0 = new MyPoint(x,y);
    MyPoint[] vertex = new MyPoint[6];
    int xs = cell.getPosition().getX();
    int ys = cell.getPosition().getY();
    int x0 = xs + (int)(m_edgeSize/2.0 + 0.5);
    int x1 = xs + (int)(3.0*m_edgeSize/2.0 + 0.5);
    int x2 = xs + (int)(2.0*m_edgeSize + 0.5);
    int y2 = ys + (int)(m_yLen/2.0 + 0.5);
    int y3 = ys + (int)(m_yLen+0.5);

    vertex[0] = new MyPoint(x0, ys);
    vertex[1] = new MyPoint(x1, ys);
    vertex[2] = new MyPoint(x2, y2);
    vertex[3] = new MyPoint(x1, y3);
    vertex[4] = new MyPoint(x0, y3);
    vertex[5] = new MyPoint(xs, y2);

    int[] outerCell = getAdjacentCells(cell);

    double minDist = 10000000; // FIXME
    MyLine minLine = null;
    int mini = 0;
    int i;
    int outerID = 0;
    for ( i=0 ; i<6 ; i++ ) {
      int next = (i==5) ? 0 : i+1;
      MyLine line = new MyLine(vertex[i], vertex[next], cell);
      double dist = distance(line, p0);
//      System.out.println("vertex "+i+" "+line.getEndp1().getX()+","+line.getEndp1().getY()+" "+line.getEndp2().getX()+","+line.getEndp2().getY()+" dist = "+dist);
      if (dist < minDist) {
        minDist = dist;
        minLine = line;
        outerID = outerCell[i];
        mini = i;
      }
    }
    System.out.println("mini segment is " + mini);
    System.out.println("inner cell ID is " + cell.getID());
    System.out.println("outer cell ID is " + outerID);
    if (minLine != null) {      
      Cell oCell = findCell(outerID);
      minLine.setOuterCell(oCell);
      minLine.setOuterCellDirection(mini);
    }
//    System.out.println("edge "+mini+" is closest");
    return minLine;
  }

  public BufferedImage getMapImage() {
    int xb = (int)(m_edgeSize/2.0 + 0.5);
    int yb = (int)(m_yLen/2.0 + 0.5);
    int trimWidth = m_mapDim.getX() - 2*xb;
    int trimHeight = m_mapDim.getY() - 2*yb;
    BufferedImage image = new BufferedImage(m_mapDim.getX() , m_mapDim.getY(), BufferedImage.TYPE_INT_RGB);    
    renderMap(image.getGraphics());
    BufferedImage imageTrimmed = new BufferedImage(trimWidth ,trimHeight, BufferedImage.TYPE_INT_RGB);
    imageTrimmed = image.getSubimage( xb, yb, trimWidth, trimHeight);
    return imageTrimmed;
  }

  public GameDef getGameDef() {
    return m_gameDef;
  }

	public java.util.List<Cell> getCells() {
		return  m_cells;
	}

  public ConfigParams getConfigParams() {
    return m_configParams;
  }

  public MyLine getSelectedLine() {
    return m_selectedLine;
  }

  public Cell getSelectedCell() {
    return m_selectedCell;
  }

  public Door getSelectedDoor() {
    return m_selectedDoor;
  }

  public String getNextWaterid() {
    m_waterID++;
    return Integer.toString(m_waterID);
  }

  public String getNextWallid() {    
    m_wallID++;
    return Integer.toString(m_wallID);
  }

  // FIXME: create a base class to do this for other objects like doors, water. etc...
  private int getMaxID(java.util.List<Wall> walls) {
    int max = 0;
    if (walls != null) {      
      for (Wall wall : walls) {
        int id = Integer.parseInt(wall.getID());
        if (id > max) {
          max = id;
        }
      }
    }
    return max;
  }

  public String getNextDoorID() {
    if (m_doors == null) {
      return "0";
    }
    else {
      return Integer.toString(m_doors.size());
    }
  }

  public void addWall(Wall wall) {
    if (m_walls == null) {
      m_walls = new ArrayList<Wall>();
    }
    m_walls.add(wall);
  }

  public void removeWall(Wall wall) {
    if (m_walls != null) {
      m_walls.remove(wall);
    }
  }

  public void addDoor(Door door) {
    if (m_doors == null) {
      m_doors = new ArrayList<Door>();
    }
    m_doors.add(door);
  }

  public void removeDoor(Door door) {
    if (m_doors != null) {
      m_doors.remove(door);
    }
  }

  private void addWater(Cell cell) {
//    System.out.println("Add water for cell " + cell.getID());
    int[] outerCell = getAdjacentCells(cell);
    for ( int i=0 ; i<6 ; i++) {
//      System.out.println("look for adjacent cell " + outerCell[i]);
      Cell oCell = findCell(outerCell[i]);
      if (oCell != null) {
        Place place = oCell.getPlace();
//        System.out.println("found adjacent cell " + oCell.getID());
        if (oCell.getTexture() == null) {
//          System.out.println("Adjacent cell does not have a texture");          
        }
        else {
//          System.out.println("Adjacent cell has texture " + oCell.getTexture().getName()); 
        }

        if (place == null && (oCell.getTexture() == null || !oCell.getTexture().getName().contains("water"))) {
//          System.out.println("Adding new place in cell " + oCell.getID() + " " + oCell);
          place = new Place(oCell.getID(), "place" + oCell.getID());
//          System.out.println("New place created " + place);
          oCell.setPlace(place);
          m_gameDef.addPlace(place);
          place.setCell(oCell);
        }
        if (place != null) {
          int reverseDirection = (i < 3) ? i+3 : i-3;
          place.addWater(new Water(getNextWaterid(), reverseDirection));
        } 
      }
    }
  }

	public GameWrapper getGameWrapper() {
	  MapDef mapDef = new MapDef(m_mapDim, m_configParams);      
    for (Cell cell : m_cells) {
      CellDef cellDef = null;
      if (cell.getTexture() != null) {
        cellDef = new CellDef(cell.getID(), cell.getPosition(), cell.getTexture());
      }
      else {
        cellDef = new CellDef(cell.getID(), cell.getPosition(), cell.getColor());
      }
      mapDef.addCell(cellDef);      
    }
    mapDef.setWalls(m_walls);
    mapDef.setDoors(m_doors);
    mapDef.setDefaultColor(m_defaultCellColor);
    mapDef.setDefaultTexture(m_defaultCellTexture);
    GameWrapper gameWrapper = new GameWrapper(m_gameDef, mapDef);
		return gameWrapper;
	}

  private int[] getHex_X(int xs) {
    int[] xArray = new int[6];
    xArray[0] = xs + (int)(m_edgeSize/2.0 + 0.5);
    xArray[1] = xs + (int)(3.0*m_edgeSize/2.0 + 0.5);
    xArray[2] = xs + (int)(2.0*m_edgeSize + 0.5);
    xArray[3] = xArray[1];
    xArray[4] = xArray[0];
    xArray[5] = xs;
    return xArray;
  }

  private int[] getHex_Y(int ys) {
    int[] yArray = new int[6];
    yArray[0] = ys;
    yArray[1] = ys;
    yArray[2] = ys + (int)(m_yLen/2.0 + 0.5);
    yArray[3] = ys + (int)(m_yLen+0.5);
    yArray[4] = yArray[3];
    yArray[5] = yArray[2];
    return yArray;
  }

	private Polygon getHexagon(MyPoint pos) {
		int xs = pos.getX();
		int ys = pos.getY();
    Polygon p = new Polygon( getHex_X(xs), getHex_Y(ys), 6);
    return p;
  }

  public CrossPattern getCrossPattern(MyPoint pos) {
    return new CrossPattern( getHex_X(pos.getX()), getHex_Y(pos.getY()), 3);
  }

	private Color getBorderColor(Color color) {
    Color borderColor;
    if (color == Color.black || color == Color.darkGray) {
          borderColor = Color.lightGray;
      }
      else {
          borderColor = Color.darkGray;
      }
      return borderColor;
  }
  private void renderMap(Graphics g) {
    Graphics2D g2 = (Graphics2D) g;   
    for (Cell cell : m_cells) {      
      Color cellColor = cell.getColor();
      Texture cellTexture = cell.getTexture();
                       
      if (cellColor == null) {
        if (m_defaultCellTexture == null) {
          if (m_defaultCellColor == null) {
              cellColor = Color.white;
          }
          else {
              cellColor = m_defaultCellColor;
          }
        }
        else if (cellTexture == null) {
            cellTexture = m_defaultCellTexture;
        }
      }
      if (cellTexture != null) {
          g2.setClip(cell.getHexagon());
          TexturePaint tp = new TexturePaint(cellTexture.getImage(), cell.getHexagon().getBounds());
          g2.setPaint(tp);
          g2.fill(cell.getHexagon().getBounds());
          g2.setClip(null);
      } 
      else {    
         g2.setColor(cellColor);
         g2.fillPolygon(cell.getHexagon());
      }           
      g2.setColor(getBorderColor(cellColor));
      g2.setStroke(new BasicStroke((float)m_configParams.getDoubleParam("gridThickness")));
       
      if (m_viewMode.equals("all")) { 
        g2.drawPolygon(cell.getHexagon());
        if (cell.getPlace() != null && cell.getPlace().getCrossPattern() != null) {     
          g2.setStroke(new BasicStroke((float)m_configParams.getDoubleParam("gridThickness")));          
          cell.getPlace().getCrossPattern().draw(g2);
        }
      }
    }
    if (m_walls != null) {
      g2.setStroke(new BasicStroke((float)m_configParams.getDoubleParam("wallThickness")));
      for (Wall wall : m_walls) {
        g2.setColor(wall.getColor());      
        g2.drawLine(wall.getEndp1().getX(), wall.getEndp1().getY(), wall.getEndp2().getX(), wall.getEndp2().getY());
/*        
        g2.setColor(Color.red);
        Cell ic =  wall.getCell();   
        getCrossPattern(ic.getPosition()).draw(g2);
        g2.setColor(Color.cyan);
        Cell oc = wall.getOuterCell();
        if (oc != null) {   
          getCrossPattern(oc.getPosition()).draw(g2);
        }
 */       
      }
    }
  }

	public void paintComponent(Graphics g) {
		super.paintComponent(g);	
    Graphics2D g2 = (Graphics2D) g;
    renderMap(g);

    if (m_viewMode.equals("game")) {
      return;
    }  
    /*
        all graphics displayed after this point do not appear on a game map
        and are displayed when viewMode == "all" to facilitate editing the map
     */
    g2.setStroke(new BasicStroke((float)m_configParams.getDoubleParam("selectThickness")));
    if (m_selectedLine != null) {
      g2.setColor(m_selectedLine.getColor());
      g2.drawLine(m_selectedLine.getEndp1().getX(), m_selectedLine.getEndp1().getY(), m_selectedLine.getEndp2().getX(), m_selectedLine.getEndp2().getY());
    }
    if (m_selectedCell != null) {
      g2.setColor(getBorderColor(m_selectedCell.getColor()));
      g2.drawPolygon(m_selectedCell.getHexagon());
      if (m_selectedCell.getPlace() != null && m_selectedCell.getPlace().getCrossPattern() != null) {
        m_selectedCell.getPlace().getCrossPattern().draw(g2);
      }
    }
    
    if (m_doors != null) {
      for (Door door : m_doors) {
        Wall wall = findWall(door.getWallid());
        if (wall != null) {
          Color doorColor = wall.getColor();
          g2.setColor(doorColor);
          MyPoint wp = door.getWallPos();
          MyPoint ip = door.getInnerPos();
          g2.drawLine(wp.getX(), wp.getY(), ip.getX(), ip.getY());
        }
      }
    }
	}
}
