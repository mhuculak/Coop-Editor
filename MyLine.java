import java.awt.*;

public class MyLine {
  private MyPoint endpoint1;
  private MyPoint endpoint2;
  private Cell cell; 
  private Color color;

  public MyLine(MyPoint e1, MyPoint e2, Cell c) {
    endpoint1 = e1;
    endpoint2 = e2;
    cell = c;    
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
    return cell;
  }

  public Color getColor() {
    return color;
  }
}