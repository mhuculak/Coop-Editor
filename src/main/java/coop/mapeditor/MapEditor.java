package coop.mapeditor;

import coop.map.*;
import coop.io.*;
import coop.player.*;
import coop.item.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.lang.reflect.*;

public class MapEditor {
	private JFrame mainFrame;	      
	private JFrame colorFrame;
	private JFrame mapFrame;
	public static JLabel statusLabel;	
	

   	private MyPoint mapDim = null;
   	private GameMap map = null;
   	public static ColorSelector colorSelector = null;
   	private String mapFileName = null;
   	private String mapName = null;   

   	private Cell selectedCell = null;
    private String colorFile = null;
    private ItemWrapper itemWrapper = null;

    private Item newItem;

    private final double minStrength = 0.1;
    private final double maxStrength = 1.0;
    

 	  public MapEditor() {
		   prepareGUI();				
	  }

	  public static void main(String[] args){
      MapEditor mapEditor = new MapEditor();  
      mapEditor.start();    
   	} 

   	private void start() {
      itemWrapper = new ItemWrapper();      
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
        	Log("Save to file " + filename.getText() + " in " + dir.getText());
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
      map.getGameDef().setName(mapName);
      GameWrapper gameWrapper = map.getGameWrapper();
   		Log("Saving to file " + mapFileName);   		
   		MapFile.writeMapFile(mapFileName, gameWrapper);
   		Log("Saving game info to " + gameWrapper.getGameDef().getName());
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
   		Log("Opening to file " + mapFileName);
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
        Log("ERROR: No map available");
      }
      else {
        map.setEditMode("cell");
        map.enableClick();
        map.enableMotion();
      }
   	}

    private void editDoors() {
      if (map == null) {
        Log("ERROR: No map available");
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

    private void editItem() {

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
              Log("ERROR: No color selected");
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
             		Log("validation failed for x");
             		return;
             	}
             	if (yText.getText()==null || !yText.getText().matches("\\d+")) {return;}
             	Log("Create new map " + mapFileName + " size " + xText.getText() + " x " + yText.getText());
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

   	private void gamePlace() { 
   	  JFrame dialogFrame = new JFrame("Description");  		
   		dialogFrame.setLayout(new GridLayout(2, 1));
   		dialogFrame.setSize(400, 400);
   		JLabel instructionLabel = new JLabel("Select a cell and then add your description text.", JLabel.CENTER);
      final JTextField nameText = new JTextField(10);
      final JTextArea descText = new JTextArea(5, 20);
      Cell sourceCell = map.getSelectedCell();
      final Place sourcePlace = (sourceCell==null) ? null : sourceCell.getPlace();      
      
      if (sourcePlace != null) {
          instructionLabel.setText("You can update the text of the selected lable and/or select another cell to move it");
          nameText.setText(sourcePlace.getPlaceName());
          if (sourcePlace.getDesc() != null) {
              descText.setText(sourcePlace.getDesc());
          }          
      }   			   		   	
   		
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
      map.enableClick();
      map.enableMotion();
      

   		doneButton.addActionListener(new ActionListener() {
         	public void actionPerformed(ActionEvent e) {
         		Log("Adding description for selected cell");
            Cell selectedCell = map.getSelectedCell();
            if (selectedCell == null) {
              Log("No cell selected");
            }
            else {
         		  Place place = selectedCell.getPlace();
         		  if (place == null) { // create a new Place in a free cell
         			    place = new Place(selectedCell.getID(), nameText.getText());         			
         			    ValueProp xpos = new ValueProp("xpos");
         			    xpos.setValue(Integer.toString(selectedCell.getPosition().getX()));
         			    place.addProp(xpos);
         			    ValueProp ypos = new ValueProp("ypos");
         			    ypos.setValue(Integer.toString(selectedCell.getPosition().getY()));
         			    place.addProp(ypos);
                  place.setCrossPattern(map.getCrossPattern(selectedCell.getPosition()));
                  ValueProp descProp = new ValueProp("desc");
                  descProp.setValue(descText.getText());
                  place.addProp(descProp);
                  selectedCell.setPlace(place);
                  map.getGameDef().addPlace(place);
                  if (sourcePlace != null) {
                      place.setPlayers(sourcePlace.getPlayers()); // copy any players
                      sourceCell.setPlace(null);
                  }
         		  }
              else if (place == sourcePlace) { // update existing place
                  place.setPlaceName(nameText.getText());
                  place.setDesc(descText.getText());
              }
              else {
                Log("ERROR: a place can only be moved to a free cell");
              }            
         		 
         	  }
         		dialogFrame.dispatchEvent(new WindowEvent(dialogFrame, WindowEvent.WINDOW_CLOSING));
            map.setEditMode("cell");
         	}
      	});
   	} 

    private void editGamePlayer(Cell sourceCell, java.util.List<Player> players) {

      Player sourcePlayer = (players==null || players.size() == 0) ? null : players.remove(0);  
      JFrame dialogFrame = new JFrame("Player");
      dialogFrame.setLayout(new GridLayout(7, 1));
      dialogFrame.setSize(400, 600);
      JLabel instructionLabel = new JLabel("Enter the new player info", JLabel.CENTER);                   
      final JTextField nameText = new JTextField(10);      
      final JTextArea descText = new JTextArea(5, 20);
      final JTextField heightText = new JTextField(10);      
      final JTextField weightText = new JTextField(10);     
      final JTextField strengthText = new JTextField(10);      

      if (sourcePlayer == null) {
          nameText.setText("name here");
          heightText.setText("1.8");
          weightText.setText("80");
          strengthText.setText("0.7");
      }
      else {
          instructionLabel.setText("<html>You have three options here:<br>1. Edit info for player " + sourcePlayer.getName() + 
                    " and press Submit<br>" + 
                    "2. select another cell and then press Move<br>" +
                    "3. press New to add a new player to this location."); 
          nameText.setText(sourcePlayer.getName());
          heightText.setText(Double.toString(sourcePlayer.getHeight()));
          weightText.setText(Double.toString(sourcePlayer.getWeight()));
          strengthText.setText(Double.toString(sourcePlayer.getStrength()));
          descText.setText(sourcePlayer.getDesc());
      }
      Button submitButton = new Button("Submit");
      Button moveButton = new Button("Move");
      Button newButton = new Button("New");
      JPanel nameControlPanel = new JPanel(new FlowLayout());
      JPanel heightControlPanel = new JPanel(new FlowLayout());
      JPanel weightControlPanel = new JPanel(new FlowLayout());
      JPanel strengthControlPanel = new JPanel(new FlowLayout());
      JPanel descControlPanel = new JPanel(new FlowLayout());
      JPanel buttonControlPanel = new JPanel(new FlowLayout());
      JLabel nameLabel = new JLabel("Name:", JLabel.RIGHT);
      JLabel heightLabel = new JLabel("Height (m):", JLabel.RIGHT);
      JLabel weightLabel = new JLabel("Weight (kg):", JLabel.RIGHT);
      JLabel strengthLabel = new JLabel("Strength: (0-1.0)", JLabel.RIGHT);
      JLabel descLabel = new JLabel("Description:", JLabel.RIGHT);
           
      nameControlPanel.add(nameLabel);
      nameControlPanel.add(nameText);
      heightControlPanel.add(heightLabel);
      heightControlPanel.add(heightText);
      weightControlPanel.add(weightLabel);
      weightControlPanel.add(weightText);
      strengthControlPanel.add(strengthLabel);
      strengthControlPanel.add(strengthText);
      descControlPanel.add(descLabel);
      descControlPanel.add(descText);
      buttonControlPanel.add(submitButton);

      if (sourcePlayer != null) {
          buttonControlPanel.add(moveButton);
          buttonControlPanel.add(newButton);          
      }

      dialogFrame.add(instructionLabel); 
      dialogFrame.add(nameControlPanel);
      dialogFrame.add(heightControlPanel);
      dialogFrame.add(weightControlPanel);
      dialogFrame.add(strengthControlPanel);
      dialogFrame.add(descControlPanel);
      dialogFrame.add(buttonControlPanel);      
 
      dialogFrame.setVisible(true); 
      map.setEditMode("highlight");
      map.enableClick();
      map.enableMotion(); 

      newButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
              editGamePlayer(sourceCell, null);  // null means add new player
              dialogFrame.dispatchEvent(new WindowEvent(dialogFrame, WindowEvent.WINDOW_CLOSING));
          }              
      });   
      
      moveButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
              Cell selectedCell = map.getSelectedCell();
              Place sourcePlace = sourceCell.getPlace();              
              if (selectedCell == null) {
                  Log("No cell selected");
              }
              else {
                  Place place = selectedCell.getPlace();
                  if (place == null) {
                      Log("No Place found - use Game Desc to create it here");
                  }
                  else if (place != sourcePlace) {
                      sourcePlayer.setPlace(place);
                      place.addPlayer(sourcePlayer);
                      sourceCell.getPlace().removePlayer(sourcePlayer);
                  }
                  else {
                      Log("A different place was not selected...");
                  }
              }
              dialogFrame.dispatchEvent(new WindowEvent(dialogFrame, WindowEvent.WINDOW_CLOSING));
          }              
      }); 

      submitButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            Log("Adding player to selected cell");
            Cell selectedCell = map.getSelectedCell();
            if (selectedCell == null) {
              Log("No cell selected");
            }
            else {
              Place place = selectedCell.getPlace();              
              if (place == null) {
                Log("No Place found - use Game Desc to create it here");
              }
              else {
                if (sourcePlayer == null) {                    
                    String id = map.getGameDef().getNextPlayerID();
                    Player player = new Player( id, nameText.getText(), descText.getText(), place,
                          Double.parseDouble(heightText.getText()), 
                          Double.parseDouble(weightText.getText()), 
                          strengthApplyLimits(Double.parseDouble(strengthText.getText())));
                    map.getGameDef().addPlayer(player);
                    int s = (place.getPlayers() == null) ? 0 : place.getPlayers().size();
                    Log("Adding " + nameText.getText() + " to place with num players = " + s);
                    place.addPlayer(player);
                    Log("num players = " + place.getPlayers().size());

                }
                else if (place == sourcePlayer.getPlace()) {
                    sourcePlayer.setName(nameText.getText());
                    sourcePlayer.setDesc(descText.getText());
                    sourcePlayer.setHeight(Double.parseDouble(heightText.getText()));
                    sourcePlayer.setWeight(Double.parseDouble(weightText.getText()));
                    sourcePlayer.setStrength(strengthApplyLimits(Double.parseDouble(strengthText.getText())));
                    editGamePlayer(sourceCell, players);
                }
              }                         
            }
            dialogFrame.dispatchEvent(new WindowEvent(dialogFrame, WindowEvent.WINDOW_CLOSING));
            map.setEditMode("cell");
          }
        });    
    }

    private double strengthApplyLimits(double strength) {
        if (strength > maxStrength ) {
            strength = maxStrength ;                  
        }
        else if (strength < minStrength) {
            strength = minStrength;
        }
        return strength;
    }

   	private void gamePlayer() {
        Cell sourceCell = map.getSelectedCell();
        Place sourcePlace = (sourceCell == null) ? null : sourceCell.getPlace();
        java.util.List<Player> sourcePlayers = (sourcePlace == null) ? null : sourcePlace.getPlayers();
        java.util.List<Player> srcCopy = new ArrayList<Player>();
        if (sourcePlayers != null) {
            for ( Player p : sourcePlayers) {
                srcCopy.add(p);
            }
        }
             
        editGamePlayer(sourceCell, srcCopy);
    }

    private void editGameItems(Cell sourceCell, java.util.List<Item> sourceItems) { 
        JFrame dialogFrame = new JFrame("Item");
        dialogFrame.setLayout(new GridLayout(sourceItems.size()+2, 1));         
        dialogFrame.setSize(400, 500);       
        JLabel instructionLabel = new JLabel("<html>Jeremy, you have four options here:<br><br>1.Select an item and press <b>Modify</b><br>" +                  
                    "2. Select an item <b>and</b> select another cell and then press <b>Move</b><br>" +
                    "3. Press <b>New</b> to add a new item to this location.<br>" +
                    "4. Press Delete to remove the selected item<br>", JLabel.CENTER);
        java.util.Map<JCheckBox, Item> checkBoxMap = new HashMap<JCheckBox, Item>(); 
        dialogFrame.add(instructionLabel);     
        for (Item item : sourceItems) {            
            JCheckBox checkBox = new JCheckBox(item.getName() + " " + item.getType());
            checkBoxMap.put(checkBox, item);            
            JPanel panel = new JPanel();
            panel.setLayout(new FlowLayout());
            panel.add(checkBox);
            dialogFrame.add(panel);
        }
        Button modifyButton = new Button("Modify");
        Button moveButton = new Button("Move");
        Button deleteButton = new Button("Delete");
        Button newButton = new Button("New");
        JPanel buttonControlPanel = new JPanel(new FlowLayout());
        buttonControlPanel.add(modifyButton);
        buttonControlPanel.add(moveButton);
        buttonControlPanel.add(deleteButton);
        buttonControlPanel.add(newButton);
        dialogFrame.add(buttonControlPanel);
        dialogFrame.setVisible(true); 
        map.setEditMode("highlight");
        map.enableClick();
        map.enableMotion();

        modifyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Item item = getSelectedItem(checkBoxMap);
                if (item != null) {
                    modifyGameItem(item);
                }
                dialogFrame.dispatchEvent(new WindowEvent(dialogFrame, WindowEvent.WINDOW_CLOSING));
            }              
        });

        newButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addGameItem();  // null means add new player
                dialogFrame.dispatchEvent(new WindowEvent(dialogFrame, WindowEvent.WINDOW_CLOSING));
            }              
        });

        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Item item = getSelectedItem(checkBoxMap);
                if (item != null) {
                    Place place = item.getPlace();
                    place.removeItem(item);
                }
                dialogFrame.dispatchEvent(new WindowEvent(dialogFrame, WindowEvent.WINDOW_CLOSING));
            }              
        });

        moveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Item sourceItem = getSelectedItem(checkBoxMap);
                if (sourceItem == null) {
                    Log("No item selected");
                }
                else {
                    Cell selectedCell = map.getSelectedCell();
                    Place sourcePlace = sourceCell.getPlace();              
                    if (selectedCell == null) {
                        Log("No cell selected");
                    }
                    else {
                        Place place = selectedCell.getPlace();
                        if (place == null) {
                            Log("No Place found - use Game Desc to create it here");
                        }
                        else if (place != sourcePlace) {
                            sourceItem.setPlace(place);
                            place.addItem(sourceItem);
                            sourceCell.getPlace().removeItem(sourceItem);
                        }
                        else {
                            Log("A different place was not selected...");
                        }
                    }
                }
                dialogFrame.dispatchEvent(new WindowEvent(dialogFrame, WindowEvent.WINDOW_CLOSING));
            }              
        });   
    }

    private Item getSelectedItem(java.util.Map<JCheckBox, Item> checkBoxMap) {
        Iterator entries = checkBoxMap.entrySet().iterator();
        Item selectedItem = null;
        while ( entries.hasNext()) {
            java.util.Map.Entry entry = (java.util.Map.Entry)entries.next();
            JCheckBox checkBox = (JCheckBox)entry.getKey();                    
            if (checkBox.isSelected()) {
                selectedItem = (Item)entry.getValue();
            }
        }
        return selectedItem;
    }

    private void modifyGameItem(Item item) {
        JFrame dialogFrame = new JFrame("Edit "+item.getType());
        dialogFrame.setLayout(new GridLayout(5, 1));
        dialogFrame.setSize(400, 400); 
        JLabel instructionLabel = new JLabel("Edit the item", JLabel.CENTER);
        JLabel nameLabel = new JLabel("Item name:");
        JLabel weightLabel = new JLabel("Item weight (kg):");
        final JTextField nameText = new JTextField(10);
        nameText.setText(item.getName());
        final JTextField dataText = new JTextField(10);
        final JTextField weightText = new JTextField(10);
        weightText.setText(Double.toString(item.getWeight()));

        JLabel dataLabel = new JLabel(item.getType()+" data:");
        JPanel nameControlPanel = new JPanel();
        nameControlPanel.setLayout(new FlowLayout());
        JPanel dataControlPanel = new JPanel();
        JPanel weightControlPanel = new JPanel();
        nameControlPanel.add(nameLabel);
        nameControlPanel.add(nameText);
        dialogFrame.add(instructionLabel);
        dialogFrame.add(nameControlPanel);
        weightControlPanel.add(weightLabel);
        weightControlPanel.add(weightText);
        dialogFrame.add(weightControlPanel);

        if (item instanceof HasText) {
            HasText textItem = (HasText)item;
            dataText.setText(textItem.getText());
            dataControlPanel.setLayout(new FlowLayout());
            dataControlPanel.add(dataLabel);
            dataControlPanel.add(dataText);
            dialogFrame.add(dataControlPanel);
        }
        else if (item instanceof HasValue) {
            HasValue valueItem = (HasValue)item;
            dataText.setText(Double.toString(valueItem.getValue()));
            dataControlPanel.setLayout(new FlowLayout());
            dataControlPanel.add(dataLabel);
            dataControlPanel.add(dataText);
            dialogFrame.add(dataControlPanel);
        }
        Button submitButton = new Button("Submit");
        JPanel buttonControlPanel = new JPanel();
        buttonControlPanel.setLayout(new FlowLayout());
        buttonControlPanel.add(submitButton);
        dialogFrame.add(buttonControlPanel);
        dialogFrame.setVisible(true);

        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                item.setName(nameText.getText());
                item.setWeight(Double.parseDouble(weightText.getText()));
                if (item instanceof HasText) {
                    HasText textItem = (HasText)item;
                    textItem.setText(dataText.getText());
                }
                else if (item instanceof HasValue) {
                    HasValue valueItem = (HasValue)item;
                    valueItem.setValue(Double.parseDouble(dataText.getText()));
                }
                dialogFrame.dispatchEvent(new WindowEvent(dialogFrame, WindowEvent.WINDOW_CLOSING));
            }              
        });
    }

	  private void addGameItem() {
        java.util.Map<String, Class<?>> itemMap = itemWrapper.getItems();
        JFrame dialogFrame = new JFrame("Item");
        dialogFrame.setLayout(new GridLayout(4, 1));         
        dialogFrame.setSize(400, 500);
        JLabel instructionLabel = new JLabel("Select the name and type of item to add", JLabel.CENTER);        
        JLabel nameLablel = new JLabel("Item name:");        
        JLabel typeLabel = new JLabel("Item type:");
        final JTextField nameText = new JTextField(10);        
        final DefaultListModel itemListModel = new DefaultListModel();        
        for ( String itemType : itemMap.keySet()) {
            itemListModel.addElement(itemType);
        }
        final JList itemList = new JList(itemListModel);
        itemList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        itemList.setSelectedIndex(0);
        itemList.setVisibleRowCount(6);
        JScrollPane itemListScrollPane = new JScrollPane(itemList);
        Button submitButton = new Button("Submit");
        JPanel nameControlPanel = new JPanel();        
        JPanel typeControlPanel = new JPanel();
        JPanel buttonControlPanel = new JPanel();
        nameControlPanel.setLayout(new FlowLayout());
        typeControlPanel.setLayout(new FlowLayout());
        buttonControlPanel.setLayout(new FlowLayout());
             
        nameControlPanel.add(nameLablel);
        nameControlPanel.add(nameText);
        typeControlPanel.add(typeLabel);
        typeControlPanel.add(itemListScrollPane);
        buttonControlPanel.add(submitButton);
        dialogFrame.add(instructionLabel);
        dialogFrame.add(nameControlPanel);
        dialogFrame.add(typeControlPanel);
        dialogFrame.add(buttonControlPanel);
        dialogFrame.setVisible(true);
        newItem = null;       
                         
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // FIXME: a class can implement more than one interface!
                if (newItem != null && newItem instanceof HasText) {
                    HasText textItem = (HasText)newItem;
                    textItem.setText(nameText.getText());
                    dialogFrame.dispatchEvent(new WindowEvent(dialogFrame, WindowEvent.WINDOW_CLOSING));
                }
                else if (newItem != null && newItem instanceof HasValue) {
                    HasValue valueItem = (HasValue)newItem;
                    valueItem.setValue(Double.parseDouble(nameText.getText()));
                    dialogFrame.dispatchEvent(new WindowEvent(dialogFrame, WindowEvent.WINDOW_CLOSING));
                }                  
                else if (itemList.getSelectedIndex() != -1) {                    
                    Cell selectedCell = map.getSelectedCell();
                    if (selectedCell == null) {
                        Log("No cell selected");                        
                    }
                    else {
                        Place place = selectedCell.getPlace();
                        if (place == null) {
                            Log("No Place found - use Game Desc to create it here");
                        }
                        else {
                            Class c = itemMap.get(itemList.getSelectedValue());
                            Object obj = null;
                            try {
                                obj = c.newInstance();
                            }
                            catch (InstantiationException | IllegalAccessException ex) {
                                ex.printStackTrace();
                            }                       
                            newItem = (Item)obj;
                            String id = map.getGameDef().getNextItemID();
                            newItem.setName(nameText.getText());
                            newItem.setID(id);
                            newItem.setPlace(place);
                            map.getGameDef().addItem(newItem);
                            place.addItem(newItem);
                        }
                    }
                    if (newItem != null && newItem instanceof HasText) {
                        instructionLabel.setText("Enter the text for the " + newItem.getType());
                        nameLablel.setText(newItem.getType()+" text:");
                        nameText.setText("");
                        typeControlPanel.setVisible(false);                        
                    }
                    else if (newItem != null && newItem instanceof HasValue) {
                        instructionLabel.setText("Enter the value for the " + newItem.getType());
                        nameLablel.setText(newItem.getType()+" value:");
                        nameText.setText("");
                        typeControlPanel.setVisible(false);  
                    }
                    else {                                     
                      dialogFrame.dispatchEvent(new WindowEvent(dialogFrame, WindowEvent.WINDOW_CLOSING));
                    }
                }
            }              
        });  
   	}

    private void gameItem() {
        Cell sourceCell = map.getSelectedCell();
        Place sourcePlace = (sourceCell == null) ? null : sourceCell.getPlace();
        java.util.List<Item> sourceItems = (sourcePlace == null) ? null : sourcePlace.getItems(); 
        if (sourceItems == null || sourceItems.size() == 0) {
            addGameItem();
        }
        else {                    
            editGameItems(sourceCell, sourceItems);
        }

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
        JMenuItem createItemMenuItem = new JMenuItem("Item");
        createItemMenuItem.setActionCommand("Item");

        editMenu.add(cellMenuItem);
        editMenu.add(colorsMenuItem);
        editMenu.add(defaultColorMenuItem);
        editMenu.add(wallMenuItem);
        editMenu.add(doorMenuItem);
        editMenu.add(createItemMenuItem);

        JMenuItem modeMenuItem = new JMenuItem("Mode");
        modeMenuItem.setActionCommand("Mode");
        viewMenu.add(modeMenuItem);

        JMenuItem placeMenuItem = new JMenuItem("Place");
      	placeMenuItem.setActionCommand("Place");
      	JMenuItem playerMenuItem = new JMenuItem("Player");
      	playerMenuItem.setActionCommand("Player");
      	JMenuItem itemMenuItem = new JMenuItem("Item");
      	itemMenuItem.setActionCommand("Item");      	

      	gameMenu.add(placeMenuItem);
      	gameMenu.add(playerMenuItem);
      	gameMenu.add(itemMenuItem);      	

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

      	placeMenuItem.addActionListener(gameMenuListener);
      	playerMenuItem.addActionListener(gameMenuListener);
      	itemMenuItem.addActionListener(gameMenuListener);      	

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
          statusLabel.setText("");
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
          else if (cmd.equals("Item")) {
            editItem();
          }
         	
      }    
   }

   class ViewMenuListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
          statusLabel.setText("");        
          String cmd = e.getActionCommand();
          Log("view menu got command: " + cmd);
          if (cmd.equals("Mode")){
            setViewMode();   
          }
        }
    }

   class GameMenuListener implements ActionListener {
      	public void actionPerformed(ActionEvent e) {
          statusLabel.setText("");
      		String cmd = e.getActionCommand();
//         	statusLabel.setText( cmd + " JMenuItem clicked.");
         	if (cmd.equals("Place")){
         		gamePlace();   // asave in a new file
         	}
         	else if (cmd.equals("Player")) {
         		gamePlayer();
         	}
         	else if (cmd.equals("Item")) {
         		gameItem();
         	}         	         	
      }    
   }

   private void Log(String msg) {
      statusLabel.setText(msg);
      System.out.println(msg);
   }

}