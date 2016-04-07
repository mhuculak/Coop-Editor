package coop.mapeditor;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Map;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.Iterator;
import java.io.*;

class DefaultColors {
  private java.util.Map<String, Color> colors;

  public DefaultColors() {
    colors = new HashMap<String, Color>();
    colors.put("black", Color.black);
    colors.put("lightGray", Color.lightGray);
    colors.put("blue",Color.blue);
    colors.put("magenta",Color.magenta);
    colors.put("cyan",Color.cyan);
    colors.put("orange",Color.orange);
    colors.put("darkGray",Color.darkGray);
    colors.put("pink",Color.pink);
    colors.put("gray",Color.gray);
    colors.put("red",Color.red);
    colors.put("green",Color.green);
    colors.put("white",Color.white);
    colors.put("yellow",Color.yellow);
  }

  public java.util.Map<String, Color>  getColors() {
    return colors;
  } 
}

public class ColorSelector extends JPanel {

    private Color selectedColor;
    private String selectedName;
    private JFrame frame;
    private java.util.Map<String, Color> colors;
    private final String defaultFileName = "colors.txt";
    private String fileName;

	public ColorSelector(JFrame frame, String fname) {
		  this.frame = frame;
      fileName = fname;
		  colors = readColorFile();
      if (colors == null ) {
        DefaultColors defaultColors = new DefaultColors();
        colors = defaultColors.getColors();
      }
      int numColors = colors.size();
      double s = Math.sqrt(numColors);
      int gridRows = (int)s;
      int gridCols = gridRows;
      while (gridRows*gridCols < numColors) {
        gridRows++;
      }
    	GridLayout layout = new GridLayout(gridRows,gridCols);
    	setLayout(layout);
    }
    
    public String getName() {
    	return selectedName;
    }
    public Color getColor() {
    	return selectedColor;
    }

    public void setVisible(boolean isVisible) {
        frame.setVisible(isVisible);
    }

    public void addColor(String name, Color color) {
      colors.put(name, color);
      addButton(name, color);
    }

    private void addButton(String name, Color c) {
      JButton btn = new JButton(name);
      btn.setBackground(c);
      btn.setForeground(c);     
      btn.addActionListener(new ColorClickListener());
      btn.setActionCommand(name);
      this.add(btn);
    }

    public void start() {
    	for (java.util.Map.Entry<String, Color> entry : colors.entrySet()) {
    		String name = entry.getKey();
    		Color c = entry.getValue();
//    		System.out.println("Adding " + name + " button");
			  addButton(name, c);
		  }
    }

    private class ColorClickListener implements ActionListener{
      public void actionPerformed(ActionEvent e) {
         String name = e.getActionCommand();  
         System.out.println("Selected color " + name);
         selectedColor = colors.get(name);
         selectedName = name;
      }		
   }

   private java.util.Map<String, Color> readColorFile() {
      if (fileName == null) {
        fileName = defaultFileName;
      }
      try {
        FileInputStream fis = new FileInputStream(fileName);  
        BufferedReader br = new BufferedReader(new InputStreamReader(fis)); 
        java.util.Map<String, Color> cmap = new HashMap<String, Color>();
        String line = null;
        while ((line = br.readLine()) != null) {
          String[] name_color = line.split(":");
          String n = name_color[0];
          String[] c = name_color[1].split(",");
          Color col = new Color(Integer.parseInt(c[0]), Integer.parseInt(c[1]), Integer.parseInt(c[2])); 
          cmap.put( n, col);
        }
        return cmap;
      }
      catch (IOException e) {
        e.printStackTrace();
      } 
      return null;
   }

   public void saveAs(String fname) {
      fileName = fname;
      save();
   }

   public void save() {   
      if (fileName == null) {
        fileName = defaultFileName;
      }
      try {
        File fout = new File(fileName);
        FileOutputStream fos = new FileOutputStream(fout);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        Iterator entries = colors.entrySet().iterator();
        while ( entries.hasNext()) {
          Entry entry = (Entry) entries.next();
          Color c = (Color)entry.getValue();
          bw.write(entry.getKey()+":"+c.getRed() + "," + c.getGreen() + "," + c.getBlue());
          bw.newLine();
        }
        bw.close();     
      }
      catch (IOException e) {
        e.printStackTrace();
      }      
   }
	
}
