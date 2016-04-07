package coop.map;

import java.awt.*;

public class MyLine {
  private MyPoint endpoint1;
  private MyPoint endpoint2;
  private Cell innerCell;
  private Cell outerCell;
  private int outerCelldirection; 
  private Color color;

  public MyLine(MyPoint e1, MyPoint e2, Cell c) {
    endpoint1 = e1;
    endpoint2 = e2;
    innerCell = c;    
  }

  public MyLine(MyPoint e1, MyPoint e2) {
    endpoint1 = e1;
    endpoint2 = e2;
  }

  public void setColor(Color color) {
    this.color = color;
  }

  public MyPoint getEndp1() {
    return endpoint1;
  }

  public MyPoint getEndp2() {
    return endpoint2;
  }

  public Cell getCell() {
    return innerCell;
  }

  public Cell getOuterCell() {
    return outerCell;
  }

  public void setOuterCell(Cell c) {
    outerCell = c;
  }

  public int getOuterCellDirection() {
    return outerCelldirection;
  }

  public void setOuterCellDirection(int dir) {
    outerCelldirection = dir;
  }

  public Color getColor() {
    return color;
  }

  public void addWall(Wall wall, GameDef gameDef) {
    if (innerCell != null) {
      Place innerPlace = innerCell.getPlace();                  
      if (innerPlace == null) {
        innerPlace = new Place(innerCell.getID(), "place" + innerCell.getID());
        innerCell.setPlace(innerPlace);
        gameDef.addPlace(innerPlace);
        innerPlace.setCell(innerCell);                   
      }                  
      innerPlace.addWall(wall);
    }
    if (outerCell != null) {
      Place outerPlace = outerCell.getPlace();
      if (outerPlace == null) {
        outerPlace = new Place(outerCell .getID(), "place" + outerCell .getID());
        outerCell.setPlace(outerPlace);
        gameDef.addPlace(outerPlace);
        outerPlace.setCell(outerCell); 
      }
      outerPlace.addWall(wall);
    }
  }

  public void removeWall(Wall wall) {
    if (innerCell != null && innerCell.getPlace() != null) {
      innerCell.getPlace().removeWall(wall);
    }
    if (outerCell != null && outerCell.getPlace() != null) {
      outerCell.getPlace().removeWall(wall);
    }
  }
}