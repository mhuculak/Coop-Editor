package coop.mapeditor;

import coop.map.Texture;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.*;
import javax.imageio.ImageIO;

import java.util.Map;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.Iterator;
import java.io.*;

class DefaultTextures {
    private java.util.Map<String, Texture> textures;

    public DefaultTextures() {
  		textures = new HashMap<String, Texture>();
  		
  		doAdd("grass", "images/grass.png");
  		doAdd("road", "images/road.png");
      doAdd("rocks1", "images/rocks1.png");
      doAdd("rocks2", "images/rocks2.png");
      doAdd("rocks3", "images/rocks3.png");     
  		doAdd("trees1", "images/trees1.png");
      doAdd("trees2", "images/trees2.png");
      doAdd("trees3", "images/trees3.png");
      doAdd("trees4", "images/trees4.png");
      doAdd("stone1", "images/stone1.jpg");
      doAdd("stone2", "images/stone2.jpg");
      doAdd("stone3", "images/stone3.png");
  		doAdd("sand", "images/sand.png");
      doAdd("sand2", "images/sand2.png");
      doAdd("sand3", "images/sand3.png");
      doAdd("dirt" ,"images/dirt.png");
      doAdd("dirt2" ,"images/dirt2.png");
      doAdd("water", "images/water.png");
      doAdd("water2", "images/water2.jpg");
      doAdd("wood1", "images/wood1.jpg");
      doAdd("wood2", "images/wood2.jpg");
      doAdd("wood3", "images/wood3.jpg");
      doAdd("terrain1", "images/terrain1.jpg");
      doAdd("brick1", "images/brick1.jpg");
  	}

    private void doAdd(String name, String path) {
  		try {
  	    	textures.put(name, new Texture( name, ImageIO.read(new File(path))));
  	    }
  	    catch (IOException ex) {
  			ex.printStackTrace();
  		}
    }

    java.util.Map<String, Texture> getTextures() {
  		return textures;
    }
}

public class TextureSelector extends JPanel {
	private Texture selectedTexture;
    private String selectedName;
    private JFrame frame;
    private java.util.Map<String, Texture> textures;

    public TextureSelector() {
    	frame = new JFrame("Texture Selector");
    	frame.setSize(400, 400);
    	frame.add(this);

//    	DefaultTextures defaultTextures = new DefaultTextures();
//    	textures = defaultTextures.getTextures();
      loadTextures();

    	int numTextures = textures.size();
      	double s = Math.sqrt(numTextures);
      	int gridRows = (int)s;
      	int gridCols = gridRows;
      	while (gridRows*gridCols < numTextures) {
        	gridRows++;
      	}
    	GridLayout layout = new GridLayout(gridRows,gridCols);
    	setLayout(layout);	
    }

    public String getName() {
    	return selectedName;
    }

    public Texture getSelectedTexture() {
    	return selectedTexture;
    }

    public Texture getTexture(String name) {
    	return textures.get(name);
    }

    public void setVisible(boolean isVisible) {
        frame.setVisible(isVisible);
    }

    private void loadTextures() {
      try {
        File dir = new File("images");
        File[] dirListing = dir.listFiles();
        if (dirListing != null) {
            textures = new HashMap<String, Texture>();
            for (File imgf : dirListing) {
                String[] comp = imgf.getName().split("\\.");
                String basename = comp[0];
                if (basename != null & imgf != null) {
//                  System.out.println("Loading texture " + basename + " path = " + imgf.getPath());
                  BufferedImage img = ImageIO.read(imgf);
                  Texture tex = new Texture(basename, img);
                  textures.put(basename, tex);
//                  textures.put(basename, new Texture( basename, ImageIO.read(img)));
                }
            }
        }
      }
      catch (Exception e) {
            e.printStackTrace();            
      }      

    }

    public boolean addTexture(Texture texture) {
        if (textures.get(texture.getName()) == null) {
          textures.put(texture.getName(), texture);
          String path = "images/" + texture.getName() + ".png";
          try {
            File out = new File(path);
            ImageIO.write(texture.getImage(), "PNG", out);
            return true;
          }
          catch(Exception e) {
            e.printStackTrace();
            return false;
          }          
        }
        else {
          System.out.println("Texture " + texture.getName() + " already exists");
          return false;
        }
    }

    private void addButton(String name, Texture texture) {
      	JButton btn = new JButton(new ImageIcon(texture.getImage()));       
      	btn.addActionListener(new TextureClickListener());
      	btn.setActionCommand(name);
      	this.add(btn);
    }

    public void start() {
    	for (java.util.Map.Entry<String, Texture> entry : textures.entrySet()) {
    		String name = entry.getKey();
    		Texture t = entry.getValue();
//    		System.out.println("Adding " + name + " button");
			  addButton(name, t);
		  }
    }

    private class TextureClickListener implements ActionListener{
      public void actionPerformed(ActionEvent e) {
         String name = e.getActionCommand();  
         System.out.println("Selected texture " + name);
         selectedTexture = textures.get(name);
         selectedName = name;
      }		
   }

}