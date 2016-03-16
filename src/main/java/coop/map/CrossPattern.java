package coop.map;

import java.awt.Graphics2D;
import java.awt.Graphics;
import java.util.*;

public class CrossPattern {
	private List<MyLine> lines;  
  
    public CrossPattern( int[] xArr, int[] yArr, int size) { 
    	lines = new ArrayList<MyLine>(size);
    	int s2 = size*2;
    	if (xArr.length != s2 || yArr.length != s2) {
      		System.out.println("ERROR: CellCrossPattern invalid args size = " + size + "x len = " + xArr.length + " y len = " +yArr.length);
     		return;
    	}
    	for (int i=0 ; i<size ; i++ ) {
      		lines.add(new MyLine( new MyPoint( xArr[i], yArr[i]), new MyPoint( xArr[size+i], yArr[size+i])));
   		}
    }

    public void draw(Graphics2D g2) {
    	for ( MyLine line : lines) {
      		g2.drawLine(line.getEndp1().getX(), line.getEndp1().getY(), line.getEndp2().getX(), line.getEndp2().getY());
    	}
    }  
}