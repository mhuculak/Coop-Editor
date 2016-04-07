package coop.mapeditor;

import coop.map.MyPoint;

import java.awt.*;
import javax.swing.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import java.awt.geom.Ellipse2D;

import java.io.*;

public class TheMist  {

	MyPoint mistDim;
	BufferedImage rawMist;	
	BufferedImage transparentMist;

	public TheMist(MyPoint dim) {
		mistDim = dim;
		try {
			rawMist = ImageIO.read(new File("images/mist.jpg"));
		}
		catch (IOException ex) {
  			ex.printStackTrace();
  		}
  		transparentMist  = new BufferedImage(mistDim.getX() , mistDim.getY(), BufferedImage.TYPE_INT_ARGB);    
    	createMist(transparentMist.getGraphics()); 		
	}
	private void createMist(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
		TexturePaint tp = new TexturePaint(rawMist, new Rectangle(mistDim.getX() , mistDim.getY()));
		g2.setPaint(tp);
		float alpha;
		int n = 50;
		AlphaComposite alcom;
		double width = mistDim.getX()/(2*n);
		double height = mistDim.getY()/(2*n);
		int w = (int)(width +0.5);
       	int h = (int)(width + 0.5);
		double midx = mistDim.getX()/2;
		double midy = mistDim.getY()/2;
		for ( int i=0 ; i<n ; i++) {
/*			
			alpha = (float)(i)/(float)(n);
*/
			alpha = (float)(1.0 - Math.exp(-3.0*i/n)); 			
			alcom = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
        	g2.setComposite(alcom); 
        	int xpos = (int)(midx + width*i +0.5);
        	int xneg = (int)(midx - width*i +0.5);
        	int ypos = (int)(midy + height*i + 0.5);
        	int yneg = (int)(midy - height*i + 0.5);        	

        	g2.setPaint(tp);
        	g2.fillRect(xneg, 0, w , mistDim.getY());
        	g2.fillRect(xpos, 0, w , mistDim.getY());
        	g2.fillRect(0, yneg,  mistDim.getX(), h);
        	g2.fillRect(0, ypos,  mistDim.getX(), h);
		}
		g2.fillRect(0, 0, w , mistDim.getY());
		g2.fillRect(0, 0,  mistDim.getX(), h);
	}

/*
	private void createMist(Graphics g) {
		int clearRadius = 100;
		int midx = mistDim.getX()/2;
		int midy =  mistDim.getY()/2;
		Graphics2D g2 = (Graphics2D) g.create();
		TexturePaint tp = new TexturePaint(rawMist, new Rectangle(mistDim.getX() , mistDim.getY()));
		g2.setPaint(tp);
		float alpha;
		AlphaComposite alcom;
		if (mistDim.getX() > clearRadius && mistDim.getY() >  clearRadius) {
			alpha = 1.0f;
			alcom = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
        	g2.setComposite(alcom);            
        	g2.fillRect(0, 0, mistDim.getX() , mistDim.getY());
        	for ( int i=0 ; i<=10 ; i++) {
	        	alpha = 1.0f - i*0.1f;
    	    	alcom = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
        		g2.setComposite(alcom); 
        		int width = clearRadius + (int)((mistDim.getX()-clearRadius)*(1.0 - i/10.0));
        		int height = clearRadius + (int)((mistDim.getY()-clearRadius)*(1.0 - i/10.0));
        		Ellipse2D.Double circle = new Ellipse2D.Double(midx, midy, width, height);
        		g2.fill(circle);
        	}
        }
        else { // image too small for mist
        	alpha = 0.0f;
			alcom = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
        	g2.setComposite(alcom);            
        	g2.fillRect(0, 0, mistDim.getX() , mistDim.getY());
        }
	}
*/
	public BufferedImage getMist() {
		return transparentMist;
	}

	public boolean save(String path) {
		try {
            File out = new File(path);
            ImageIO.write(transparentMist, "PNG", out);
            return true;
          }
          catch(Exception e) {
            e.printStackTrace();
            return false;
          }        
	}
}