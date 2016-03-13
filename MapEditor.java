import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

class MapEditor {
	private JFrame mainFrame;	      
	private JFrame colorFrame;
	private JFrame mapFrame;
	public static JLabel statusLabel;	
	
//   	private static double gridFactor = 20; 
   	private MyPoint mapDim = null;
   	private GameMap map = null;
   	public static ColorSelector colorSelector = null;
   	private String mapFileName = null;
   	private String mapName = null;   

   	private Cell selectedCell = null;
    private String colorFile = null;
    

	public MapEditor() {
//		mapDim = new MyPoint(1000, 1000);
		prepareGUI();		
		
	}

	public static void main(String[] args){
      MapEditor mapEditor = new MapEditor();  
      mapEditor.start();    
   	} 

   	private void start() {
   		setupMenus(); 
   		colorSelector.start();         		
      mainFrame.setVisible(true); 
   	}

   	private String checkExtension(String path) {
   		String extension = null;
		int i = path.lastIndexOf('.');
		if (i > 0) {
    		extension = path.substring(i+1);
		}
		if (extension == null) {
			path = path + ".map";
		}
		return path;
   	}

   	private String chooseFile() {
   		JTextField filename = new JTextField(); 
   		JTextField dir = new JTextField(); 
        JFileChooser c = new JFileChooser();
      
        int rVal = c.showOpenDialog(mainFrame);
        if (rVal == JFileChooser.APPROVE_OPTION) {
          	filename.setText(c.getSelectedFile().getName());
        	dir.setText(c.getCurrentDirectory().toString());
        	System.out.println("Save to file " + filename.getText() + " in " + dir.getText());
      	}
      	else if (rVal == JFileChooser.CANCEL_OPTION) {
        	filename.setText("You pressed cancel");
        	dir.setText("");
      	}
      	mapName = filename.getText();
      	String path = dir.getText() + "/" + filename.getText();      	
		return checkExtension(path);
   	}

   	private void save(boolean newName) {   		
         		
   		if (newName == true || mapFileName == null) {
   			mapFileName = chooseFile();
   			mapFrame.setTitle(mapName);
   		}
      GameWrapper gameWrapper = map.getGameWrapper(mapName);
   		System.out.println("Saving to file " + mapFileName);   		
   		MapFile.writeMapFile(mapFileName, gameWrapper);
   		System.out.println("Saving game info to " + gameWrapper.getGameDef().getName());
   		GameFile.saveToXML(gameWrapper.getGameDef());
      colorSelector.save();
   	}

   	private void open() {
   		cleanup(true);   		
   		mapFileName = chooseFile();
   		if (mapName == null) { 
   			mapFrame = new JFrame("No Name Map");  // shouldn't happen
   		}
   		else {
   			mapFrame = new JFrame(mapName);
   		}		
   		System.out.println("Opening to file " + mapFileName);
   		GameWrapper gameWrapper = GameWrapper.readGameWrapperFile(mapFileName);   		  		
   		mapDim = gameWrapper.getMapDef().getDim(); 		
   		mapFrame.setSize(mapDim.getX(), mapDim.getY());
   		map = new GameMap(gameWrapper, mapFrame);

   		Container contentPane = mapFrame.getContentPane();
        contentPane.add(map);
        mapFrame.setVisible(true);
        
   	}

	  private void editCells() {	
		  colorFrame.setVisible(true);
      if (map == null) {
        System.out.println("ERROR: No map available");
      }
      else {
        map.setEditMode("cell");
        map.enableClick();
        map.enableMotion();
      }
   	}

    private void editDoors() {
      if (map == null) {
        System.out.println("ERROR: No map available");
        return;
      }
      map.setEditMode("door");
      map.disableMotion();
      JFrame dialogFrame = new JFrame("Edits Walls"); 
      dialogFrame.setLayout(new GridLayout(4, 1));
      dialogFrame.setSize(400, 400);
      JCheckBox lockedCheck = new JCheckBox("door is locked");
      Button addButton = new Button("Add");
      Button modifyButton = new Button("Modify");
      Button removeButton = new Button("Remove");
      JPanel controlPanel1 = new JPanel(new FlowLayout());
      JPanel controlPanel2 = new JPanel(new FlowLayout());
      controlPanel1.add(lockedCheck);
      controlPanel2.add(addButton);
      controlPanel2.add(modifyButton);
      controlPanel2.add(removeButton);
      dialogFrame.add(controlPanel1);
      dialogFrame.add(controlPanel2);
      dialogFrame.setVisible(true); 

      addButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            map.setEditAction("add");             
            map.setDoorParams(lockedCheck.isSelected());
            dialogFrame.dispatchEvent(new WindowEvent(dialogFrame, WindowEvent.WINDOW_CLOSING));
          }
      });

      modifyButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {           
            map.setEditAction("modify");
            map.setDoorParams(lockedCheck.isSelected());   
            dialogFrame.dispatchEvent(new WindowEvent(dialogFrame, WindowEvent.WINDOW_CLOSING));                      
          }
      }); 

      removeButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {           
            map.setEditAction("remove");   
            dialogFrame.dispatchEvent(new WindowEvent(dialogFrame, WindowEvent.WINDOW_CLOSING));                      
          }
      });          
    }

    private void editWalls() {
      colorFrame.setVisible(true);
      JFrame dialogFrame = new JFrame("Edits Walls");     
      dialogFrame.setLayout(new GridLayout(4, 1));
      dialogFrame.setSize(400, 400);
      
      JPanel controlPanel1 = new JPanel(new FlowLayout());
      JPanel controlPanel2 = new JPanel(new FlowLayout());
      JPanel controlPanel3 = new JPanel(new FlowLayout());
      JLabel instructionLabel = new JLabel("Select color and place walls on the map");
      JLabel heightLabel = new JLabel("Wall height (0-10): ");
      TextField heightText = new TextField(6);
      heightText.setText("5.0");
      JCheckBox roofCheck = new JCheckBox("wall has roof");
      
      controlPanel1.add(heightLabel);
      controlPanel1.add(heightText);      
      controlPanel2.add(roofCheck);
      
      dialogFrame.add(controlPanel1);
      dialogFrame.add(controlPanel2);
      dialogFrame.add(instructionLabel);           
      dialogFrame.add(controlPanel3);
      dialogFrame.setVisible(true); 
       
      mapFrame.setVisible(true);
      map.setEditMode("wall");
      map.enableClick();
      map.disableMotion();
      Button addButton = new Button("Add");
      Button removeButton = new Button("Remove");
      controlPanel3.add(addButton);
      controlPanel3.add(removeButton);  

      addButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            map.setEditAction("add");  
            map.setWallParams(Double.parseDouble(heightText.getText()), roofCheck.isSelected());
            dialogFrame.dispatchEvent(new WindowEvent(dialogFrame, WindowEvent.WINDOW_CLOSING));         
          }
      });

      removeButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {

            map.setEditAction("remove");              
            dialogFrame.dispatchEvent(new WindowEvent(dialogFrame, WindowEvent.WINDOW_CLOSING));         
          }
      });

    }

    private void editColors() {
      JFrame dialogFrame = new JFrame("Edit Colors");     
      dialogFrame.setLayout(new GridLayout(2, 1));
      dialogFrame.setSize(400, 400);
      JPanel controlPanel1 = new JPanel(new FlowLayout());
      JPanel controlPanel2 = new JPanel(new FlowLayout());
      JLabel nameLabel = new JLabel("Color Name: ", JLabel.RIGHT);
      final TextField nameText = new TextField(10);
      JCheckBox defaultCheck = new JCheckBox("Set as default");
      Button submitButton = new Button("Submit");
      controlPanel1.add(nameLabel);
      controlPanel1.add(nameText);
      controlPanel2.add(defaultCheck);
      controlPanel2.add(submitButton);
      dialogFrame.add(controlPanel1);
      dialogFrame.add(controlPanel2);
      dialogFrame.setVisible(true); 

      Color newColor = JColorChooser.showDialog(mainFrame,
               "Choose new color", Color.white);

      submitButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            colorSelector.addColor(nameText.getText(), newColor);  
            if (defaultCheck.isSelected()) {
              map.setDefaultColor(newColor);  
            }
                      
            dialogFrame.dispatchEvent(new WindowEvent(dialogFrame, WindowEvent.WINDOW_CLOSING));         
          }
      });

    }

    private void pickDefaultColor() {
      colorFrame.setVisible(true);
      JFrame dialogFrame = new JFrame("Default Color");
      dialogFrame.setLayout(new GridLayout(2, 1));
      JLabel instructionLabel = new JLabel("Select the default color from the color selector window");
      Button doneButton = new Button("Done");
      JPanel controlPanel = new JPanel(new FlowLayout());
      controlPanel.add(doneButton);
      dialogFrame.add(instructionLabel);
      dialogFrame.add(controlPanel);
      dialogFrame.setSize(400, 400);
      dialogFrame.setVisible(true);

      doneButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Color defaultColor = colorSelector.getColor();
            if (defaultColor == null) {
              System.out.println("ERROR: No color selected");
            }
            else {
              map.setDefaultColor(defaultColor);
          }       
            dialogFrame.dispatchEvent(new WindowEvent(dialogFrame, WindowEvent.WINDOW_CLOSING));         
          }
      });
     
    }

   	private void newMap() {
   		cleanup(true);
   		JFrame dialogFrame = new JFrame("New Map");  		
   		dialogFrame.setLayout(new GridLayout(3, 1));
   		dialogFrame.setSize(400, 400);
   		
   		JPanel controlPanel1 = new JPanel(new FlowLayout());
   		JPanel controlPanel2 = new JPanel(new FlowLayout());
   		JPanel controlPanel3 = new JPanel(new FlowLayout());
   		dialogFrame.add(controlPanel1);
   		dialogFrame.add(controlPanel2);
   		dialogFrame.add(controlPanel3);
      dialogFrame.setVisible(true); 

   		JLabel nameLabel = new JLabel("Map Name: ", JLabel.RIGHT);
   		JLabel xLabel= new JLabel("X size: ", JLabel.RIGHT);
      JLabel yLabel = new JLabel("Y size: ", JLabel.RIGHT);

      	final TextField nameText = new TextField(20);
      	nameText.setText("testmap");
        final TextField xText = new TextField(6);
        xText.setText("1000"); 
        final TextField yText = new TextField(6);
        yText.setText("1000"); 

        Button doneButton = new Button("Done");        

        doneButton.addActionListener(new ActionListener() {
         	public void actionPerformed(ActionEvent e) {     
            	mapFileName = nameText.getText();
            	mapName = nameText.getText();
             	      			
             	if (mapFileName == null) {return;}
             	if (xText.getText()==null || !xText.getText().matches("\\d+")) {
             		System.out.println("validation failed for x");
             		return;
             	}
             	if (yText.getText()==null || !yText.getText().matches("\\d+")) {return;}
             	System.out.println("Create new map " + mapFileName + " size " + xText.getText() + " x " + yText.getText());
             	mapFrame = new JFrame(mapFileName);
             	mapFileName = checkExtension(mapFileName);
             	mapDim = new MyPoint(xText.getText(), yText.getText());
                mapFrame.setSize(mapDim.getX(), mapDim.getY());
                map = new GameMap(mapFrame, mapDim);
                Container contentPane = mapFrame.getContentPane();
      			contentPane.add(map);
      			mapFrame.setVisible(true);
      			dialogFrame.dispatchEvent(new WindowEvent(dialogFrame, WindowEvent.WINDOW_CLOSING));
         	}
      	});

        

      	controlPanel1.add(nameLabel);
      	controlPanel1.add(nameText);
      	controlPanel2.add(xLabel);       
      	controlPanel2.add(xText);
      	controlPanel2.add(yLabel);       
      	controlPanel2.add(yText);
      	controlPanel3.add(doneButton);   
      	dialogFrame.setVisible(true);   

   	}

    private void setViewMode() {
      JFrame dialogFrame = new JFrame("View Mode");
      dialogFrame.setLayout(new GridLayout(3, 1));
      dialogFrame.setSize(400, 400);
      JCheckBox allCheck = new JCheckBox("View all");
      JCheckBox gameCheck = new JCheckBox("View game");
      JPanel controlPanel1 = new JPanel(new FlowLayout());
      controlPanel1.add(allCheck);
      JPanel controlPanel2 = new JPanel(new FlowLayout());
      controlPanel2.add(gameCheck);
      JLabel instructionLabel = new JLabel("stuff...");
      dialogFrame.add(instructionLabel);
      dialogFrame.add(controlPanel1);
      dialogFrame.add(controlPanel2);
      dialogFrame.setVisible(true); 
      allCheck.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent e) {         
            map.setViewMode("all");
            dialogFrame.dispatchEvent(new WindowEvent(dialogFrame, WindowEvent.WINDOW_CLOSING));
         }           
      });
      gameCheck.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent e) {         
            map.setViewMode("game");
            dialogFrame.dispatchEvent(new WindowEvent(dialogFrame, WindowEvent.WINDOW_CLOSING));
         }           
      });
    }

   	private void gameDesc() { 
   	  JFrame dialogFrame = new JFrame("Description");  		
   		dialogFrame.setLayout(new GridLayout(2, 1));
   		dialogFrame.setSize(400, 400);

   		JLabel instructionLabel = new JLabel("Select a cell and then add your description text.", JLabel.CENTER);   			   		
   	
   		final JTextField nameText = new JTextField(10);
   		final JTextArea descText = new JTextArea(5, 20);
   		Button doneButton = new Button("Done");
 		  JPanel controlPanel = new JPanel(new FlowLayout());
      JLabel nameLabel = new JLabel("Cell name:", JLabel.RIGHT);
      dialogFrame.add(instructionLabel);
      
      controlPanel.add(nameLabel);
      controlPanel.add(nameText);
      controlPanel.add(descText);
      controlPanel.add(doneButton);
      dialogFrame.add(controlPanel);
      dialogFrame.setVisible(true); 

      map.setEditMode("highlight");

   		doneButton.addActionListener(new ActionListener() {
         	public void actionPerformed(ActionEvent e) {
         		System.out.println("Adding description for selected cell");
            Cell selectedCell = map.getSelectedCell();
            if (selectedCell == null) {
              System.out.println("No cell selected");
            }
            else {
         		  Place place = selectedCell.getPlace();
         		  if (place == null) {
         			  place = new Place(selectedCell.getID(), nameText.getText());         			
         			  ValueProp xpos = new ValueProp("xpos");
         			  xpos.setValue(Integer.toString(selectedCell.getPosition().getX()));
         			  place.addProp(xpos);
         			  ValueProp ypos = new ValueProp("ypos");
         			  ypos.setValue(Integer.toString(selectedCell.getPosition().getY()));
         			  place.addProp(ypos);
         		  }            
         		  ValueProp descProp = new ValueProp("desc");
         		  descProp.setValue(descText.getText());
         		  place.addProp(descProp);
         		  selectedCell.setPlace(place);
         	  }
         		dialogFrame.dispatchEvent(new WindowEvent(dialogFrame, WindowEvent.WINDOW_CLOSING));
            map.setEditMode("cell");
         	}
      	});
   		

   	} 

   	private void gameAction() {

   	}

	private void gameItem() {
   		
   	}

   	private void gameProps() {
   		
   	}

   	private void cleanup(boolean dosave) {
   		if (mapFrame != null) {
   			mapFrame.dispatchEvent(new WindowEvent(mapFrame, WindowEvent.WINDOW_CLOSING));
   		}
   		if (map != null && dosave == true) {
   			save(false);
   		}
   	}

   	private void setupMenus() {
   		JMenuBar menuBar = new JMenuBar();
   		JMenu fileMenu = new JMenu("File");
   		JMenu editMenu = new JMenu("Edit");
      JMenu viewMenu = new JMenu("View");
   		JMenu gameMenu = new JMenu("Game");		

   		JMenuItem newMenuItem = new JMenuItem("New");
      	newMenuItem.setMnemonic(KeyEvent.VK_N);
      	newMenuItem.setActionCommand("New");
      	JMenuItem openMenuItem = new JMenuItem("Open");
      	openMenuItem.setActionCommand("Open");
		    JMenuItem saveMenuItem = new JMenuItem("Save");
      	saveMenuItem.setActionCommand("Save");
      	JMenuItem saveAsMenuItem = new JMenuItem("SaveAs");
      	saveAsMenuItem.setActionCommand("SaveAs");
        JMenuItem closeMenuItem = new JMenuItem("Close");
      	closeMenuItem.setActionCommand("Close");	
      	JMenuItem exitMenuItem = new JMenuItem("Exit");
      	exitMenuItem.setActionCommand("Exit");

      	fileMenu.add(newMenuItem);
        fileMenu.add(openMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.add(saveAsMenuItem);
        fileMenu.add(closeMenuItem);       
        fileMenu.add(exitMenuItem); 

      	JMenuItem cellMenuItem = new JMenuItem("Cells");
      	cellMenuItem.setActionCommand("Cells");
      	JMenuItem colorsMenuItem = new JMenuItem("Colors");
        colorsMenuItem.setActionCommand("Colors");
        JMenuItem defaultColorMenuItem = new JMenuItem("Default Color");
        defaultColorMenuItem.setActionCommand("Default Color");
        JMenuItem wallMenuItem = new JMenuItem("Walls");
        wallMenuItem.setActionCommand("Walls");
        JMenuItem doorMenuItem = new JMenuItem("Doors");
        doorMenuItem.setActionCommand("Doors");

        editMenu.add(cellMenuItem);
        editMenu.add(colorsMenuItem);
        editMenu.add(defaultColorMenuItem);
        editMenu.add(wallMenuItem);
        editMenu.add(doorMenuItem);

        JMenuItem modeMenuItem = new JMenuItem("Mode");
        modeMenuItem.setActionCommand("Mode");
        viewMenu.add(modeMenuItem);

        JMenuItem descMenuItem = new JMenuItem("Desc");
      	descMenuItem.setActionCommand("Desc");
      	JMenuItem actionMenuItem = new JMenuItem("Action");
      	actionMenuItem.setActionCommand("Action");
      	JMenuItem itemMenuItem = new JMenuItem("Item");
      	itemMenuItem.setActionCommand("Item");
      	JMenuItem propsMenuItem = new JMenuItem("Props");
      	propsMenuItem.setActionCommand("Props");

      	gameMenu.add(descMenuItem);
      	gameMenu.add(actionMenuItem);
      	gameMenu.add(itemMenuItem);
      	gameMenu.add(propsMenuItem);

        FileMenuListener fileMenuListener = new FileMenuListener();
        EditMenuListener editMenuListener = new EditMenuListener();
        ViewMenuListener viewMenuListener = new ViewMenuListener();
        GameMenuListener gameMenuListener = new GameMenuListener();

        newMenuItem.addActionListener(fileMenuListener);
      	openMenuItem.addActionListener(fileMenuListener);
      	saveMenuItem.addActionListener(fileMenuListener);
      	saveAsMenuItem.addActionListener(fileMenuListener);
      	closeMenuItem.addActionListener(fileMenuListener);
      	exitMenuItem.addActionListener(fileMenuListener);

      	cellMenuItem.addActionListener(editMenuListener);
      	colorsMenuItem.addActionListener(editMenuListener);
      	defaultColorMenuItem.addActionListener(editMenuListener);
      	wallMenuItem.addActionListener(editMenuListener);
        doorMenuItem.addActionListener(editMenuListener);

        modeMenuItem.addActionListener(viewMenuListener);

      	descMenuItem.addActionListener(gameMenuListener);
      	actionMenuItem.addActionListener(gameMenuListener);
      	itemMenuItem.addActionListener(gameMenuListener);
      	propsMenuItem.addActionListener(gameMenuListener);

      	menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);
        menuBar.add(gameMenu);
        mainFrame.setJMenuBar(menuBar);

   	}

	private void prepareGUI() {
      mainFrame = new JFrame("Map Editor");
      mainFrame.setSize(400, 400);
      mainFrame.setVisible(true);
      statusLabel = new JLabel("", JLabel.CENTER);
      mainFrame.add(statusLabel);      

      colorFrame = new JFrame("Color Selection");
      colorFrame.setSize(400, 400);
      
      colorSelector = new ColorSelector(colorFrame, colorFile);
      colorFrame.add(colorSelector);
      colorFrame.setVisible(false);
   }

    class FileMenuListener implements ActionListener {
      	public void actionPerformed(ActionEvent e) {
      		String cmd = e.getActionCommand();
//         	statusLabel.setText( cmd + " JMenuItem clicked.");
         	if (cmd.equals("SaveAs")){
         		save(true);   // asave in a new file
         	}
         	else if (cmd.equals("Save")) {
         		save(false);  // save in exisiting file
         	}
         	else if (cmd.equals("Open")) {
         		open();
         	}
         	else if (cmd.equals("New")) {
         		newMap();
         	}
         	else if (cmd.equals("Close")) {
         		cleanup(false);  // dont save changes
         	}
         	else if (cmd.equals("Exit")) {
         		cleanup(true);   // save changes
         		System.exit(0);
         	}         	
      }    
   }

   class EditMenuListener implements ActionListener {
      	public void actionPerformed(ActionEvent e) {
      		String cmd = e.getActionCommand();
//         	statusLabel.setText( cmd + " JMenuItem clicked.");
         	if (cmd.equals("Cells")){
         		editCells();  
         	}
          else if (cmd.equals("Walls")) {
            editWalls();
          }
          else if (cmd.equals("Colors")) {
            editColors();
          }
          else if (cmd.equals("Default Color")) {
            pickDefaultColor();
          }
          else if (cmd.equals("Doors")) {
            editDoors();
          }
         	
      }    
   }

   class ViewMenuListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {         
          String cmd = e.getActionCommand();
          System.out.println("view menu got command: " + cmd);
          if (cmd.equals("Mode")){
            setViewMode();   
          }
        }
    }

   class GameMenuListener implements ActionListener {
      	public void actionPerformed(ActionEvent e) {
      		String cmd = e.getActionCommand();
//         	statusLabel.setText( cmd + " JMenuItem clicked.");
         	if (cmd.equals("Desc")){
         		gameDesc();   // asave in a new file
         	}
         	else if (cmd.equals("Action")) {
         		gameAction();
         	}
         	else if (cmd.equals("Item")) {
         		gameItem();
         	}
         	else if (cmd.equals("Props")) {
         		gameProps();
         	}
         	
      }    
   }

}