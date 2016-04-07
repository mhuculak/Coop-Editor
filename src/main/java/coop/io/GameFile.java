package coop.io;

import coop.map.Place;
import coop.map.GameDef;
import coop.map.MapDef;
import coop.map.Wall;
import coop.map.Water;
import coop.map.Door;

import coop.player.Player;
import coop.item.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.xml.sax.*;
import org.w3c.dom.*;
import java.io.*;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class GameFile {

    static Map<String, String> springCrap;

    private static void initSpringCrap() {
        springCrap = new HashMap<String, String>();
        springCrap.put("xmlns", "http://www.springframework.org/schema/beans");
        springCrap.put("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        springCrap.put("xsi:schemaLocation", "http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.0.xsd");
    }

    private static void addDoubleProperty(Document dom, Element bean, String name, double value) {
        Element prop = dom.createElement("property");
        bean.appendChild(prop);
        Attr attr = dom.createAttribute("name");
        attr.setValue(name);
        prop.setAttributeNode(attr);
        Element innerBean = dom.createElement("bean");
        prop.appendChild(innerBean);
        attr = dom.createAttribute("class");
        attr.setValue("java.lang.Double");
        innerBean.setAttributeNode(attr);
        Element arg = dom.createElement("constructor-arg");
        innerBean.appendChild(arg);
        attr = dom.createAttribute("value");
        attr.setValue(Double.toString(value));
        arg.setAttributeNode(attr);
    }

    private static void addBooleanProperty(Document dom, Element bean, String name, boolean value) {
        Element prop = dom.createElement("property");
        bean.appendChild(prop);
        Attr attr = dom.createAttribute("name");
        attr.setValue(name);
        prop.setAttributeNode(attr);
        Element innerBean = dom.createElement("bean");
        prop.appendChild(innerBean);
        attr = dom.createAttribute("class");
        attr.setValue("java.lang.Boolean");
        innerBean.setAttributeNode(attr);
        Element arg = dom.createElement("constructor-arg");
        innerBean.appendChild(arg);
        attr = dom.createAttribute("value");
        attr.setValue(Boolean.toString(value));
        arg.setAttributeNode(attr);
    }

    private static void addIntegerProperty(Document dom, Element bean, String name, int value) {
        Element prop = dom.createElement("property");
        bean.appendChild(prop);
        Attr attr = dom.createAttribute("name");
        attr.setValue(name);
        prop.setAttributeNode(attr);
        Element innerBean = dom.createElement("bean");
        prop.appendChild(innerBean);
        attr = dom.createAttribute("class");
        attr.setValue("java.lang.Integer");
        innerBean.setAttributeNode(attr);
        Element arg = dom.createElement("constructor-arg");
        innerBean.appendChild(arg);
        attr = dom.createAttribute("value");
        attr.setValue(Integer.toString(value));
        arg.setAttributeNode(attr);
    }


    private static void addStringProperty(Document dom, Element bean, String name, String value) {
        Element prop = dom.createElement("property");
        bean.appendChild(prop);
        Attr attr = dom.createAttribute("name");
        attr.setValue(name);
        prop.setAttributeNode(attr);
        attr = dom.createAttribute("value");
        attr.setValue(value);
        prop.setAttributeNode(attr);
    }

    private static void addReferenceProperty(Document dom, Element bean, String name, String reference) {
        Element prop = dom.createElement("property");
        bean.appendChild(prop);
        Attr attr = dom.createAttribute("name");
        attr.setValue(name);
        prop.setAttributeNode(attr);
        Element ref = dom.createElement("ref");
        prop.appendChild(ref);
        attr = dom.createAttribute("local");
        attr.setValue(reference);
        ref.setAttributeNode(attr);
    }    

    private static Element addBean(Document dom, Element rootEle, String id, String cname) {
        Element bean = dom.createElement("bean");               
        rootEle.appendChild(bean);
        Attr attr = dom.createAttribute("id");
        attr.setValue(id);
        bean.setAttributeNode(attr);
        attr = dom.createAttribute("class");
        attr.setValue(cname);
        bean.setAttributeNode(attr);
        return bean;
    }

    private static void addConstructor(Document dom, Element bean, String name, String value) {
        Element prop = dom.createElement("constructor-arg");
        bean.appendChild(prop);
        Attr attr = dom.createAttribute("name");
        attr.setValue(name);
        prop.setAttributeNode(attr);
        attr = dom.createAttribute("value");
        attr.setValue(value);
        prop.setAttributeNode(attr);
    }

    public static void saveToXML(GameDef gameDef, MapDef mapDef) {
//    	String fname = gameDef.getName() + ".xml";
        String fname = "export/Beans.xml";
    	Document dom;
    	Element e = null;
    	Attr attr = null;
    	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        initSpringCrap();
        double edgeSize = mapDef.getConfigParams().getDoubleParam("edgeSize");
        int cellsPerRow = mapDef.getConfigParams().getIntParam("cellsPerRow");       
    	
    	try {
        	// use factory to get an instance of document builder
        	DocumentBuilder db = dbf.newDocumentBuilder();
        	// create instance of DOM
        	dom = db.newDocument();

        	// create the root element
        	Element rootEle = dom.createElement("beans");

            for (Map.Entry<String, String> entry : springCrap.entrySet()) {
                attr = dom.createAttribute(entry.getKey());
                attr.setValue(entry.getValue());
                rootEle.setAttributeNode(attr);
            }

       	 	// create data elements and place them under root

            for (Item item : gameDef.getItems()) {
                System.out.println("Adding item " + item.getName() + " id = " + item.getID() + " type = " + item.getType() + " weight = " + item.getWeight());
                Element bean = addBean(dom, rootEle, "item"+item.getID(), item.getClass().getName());                               
                addConstructor(dom, bean, "name", item.getName());                
                addStringProperty(dom, bean, "type", item.getType());                 
                addDoubleProperty(dom, bean, "weight", item.getWeight());                
                if (item instanceof HasText) {
                    HasText titem = (HasText)item;
                    addStringProperty(dom, bean, "text", titem.getText());                   
                }
                if (item instanceof HasValue) {
                    HasValue vitem = (HasValue)item;
                    addDoubleProperty(dom, bean, "value", vitem.getValue());                    
                }
            }

            for (Door door : mapDef.getDoors()) {
                System.out.println("Adding door " + door.getID());
                Element bean = addBean(dom, rootEle, "door"+door.getID(), door.getClassName());
                addConstructor(dom, bean, "name", door.getID());  
                addBooleanProperty(dom, bean, "locked", door.isLocked());

                if (door.getKeyName() != null && !door.getKeyName().equals("")) {
                    Key key = (Key)gameDef.findItem(door.getKeyName(), Key.class);
                    if (key == null) {
                        System.out.println("ERROR: unable to resolve reference to key " + door.getKeyName() + " for door " + door.getID());
                    }
                    else {
                        addReferenceProperty(dom, bean, "key", "item"+key.getID());
                    }
//                    addStringProperty(dom, bean, "key", door.getKeyName());                    
                   
                }
            }

            Element gameDoors = addBean(dom, rootEle, "doors", "coop.map.GameDoors");
            Element doors = dom.createElement("property");
            gameDoors.appendChild(doors);
            attr = dom.createAttribute("name");
            attr.setValue("doors");
            doors.setAttributeNode(attr);
            Element doorList = dom.createElement("list");
            doors.appendChild(doorList);
            for (Door door : mapDef.getDoors()) {
                Element pref = dom.createElement("ref");
                attr = dom.createAttribute("local");
                attr.setValue("door"+door.getID());
                pref.setAttributeNode(attr);                        
                doorList.appendChild(pref);
            }

            for (Wall wall : mapDef.getWalls()) {
                System.out.println("Adding wall " + wall.getID());
                Element bean = addBean(dom, rootEle, "wall"+wall.getID(), wall.getClassName());
                addConstructor(dom, bean, "name", wall.getID());
                addDoubleProperty(dom, bean, "height", wall.getHeight());
                addBooleanProperty(dom, bean, "hasRoof", wall.hasRoof());
                if (wall.getDoorID() != null) {
                    addReferenceProperty(dom, bean, "door", "door"+wall.getDoorID());                    
                }
            }

            Element gameWalls = addBean(dom, rootEle, "walls", "coop.map.GameWalls");
            Element walls = dom.createElement("property");
            gameWalls.appendChild(walls);
            attr = dom.createAttribute("name");
            attr.setValue("walls");
            walls.setAttributeNode(attr);
            Element wallList = dom.createElement("list");
            walls.appendChild(wallList);
            for (Wall wall : mapDef.getWalls()) {
                Element pref = dom.createElement("ref");
                attr = dom.createAttribute("local");
                attr.setValue("wall"+wall.getID());
                pref.setAttributeNode(attr);                        
                wallList.appendChild(pref);
            }

            Element gameItems = addBean(dom, rootEle, "items", "coop.item.GameItems");
            Element items = dom.createElement("property");
            gameItems.appendChild(items);
            attr = dom.createAttribute("name");
            attr.setValue("items");
            items.setAttributeNode(attr);
            Element itemList = dom.createElement("list");
            items.appendChild(itemList);
            for (Item item : gameDef.getItems()) {
                Element pref = dom.createElement("ref");
                attr = dom.createAttribute("local");
                attr.setValue("item"+item.getID());
                pref.setAttributeNode(attr);                        
                itemList.appendChild(pref);
            }

            for (Place place : gameDef.getPlaces()) {
                Element bean = addBean(dom, rootEle, "position"+place.getID(), "coop.map.Position");
                addDoubleProperty(dom, bean, "x", place.getCell().getPosition().getX());
                addDoubleProperty(dom, bean, "y", place.getCell().getPosition().getY());
            }

            for (Player player : gameDef.getPlayers()) {
                Element bean = addBean(dom, rootEle, "playerPosition"+player.getID(), "coop.map.Position");
                addDoubleProperty(dom, bean, "x", player.getPlace().getCell().getCenter().getX());
                addDoubleProperty(dom, bean, "y", player.getPlace().getCell().getCenter().getY());
            }

            for (Player player : gameDef.getPlayers()) {
                System.out.println("Adding player " + player.getName() + " id = " + player.getID());
                Element bean = addBean(dom, rootEle, "player"+player.getID(), player.getClassName());
                addConstructor(dom, bean, "name", player.getName());                
                addStringProperty(dom, bean, "desc", player.getDesc());                                 
                addDoubleProperty(dom, bean, "height", player.getHeight());
                addDoubleProperty(dom, bean, "weight", player.getWeight()); 
                addDoubleProperty(dom, bean, "strength", player.getStrength());
//              addReferenceProperty(dom, bean, "place", "place"+player.getPlace().getID());  // this creates a recursive infinite loop in the spring bean loader so dont do it!
                addReferenceProperty(dom, bean, "position", "playerPosition"+player.getID());
            }

            Element gamePlayers = addBean(dom, rootEle, "players", "coop.player.GamePlayers");
            Element available = dom.createElement("property");
            gamePlayers.appendChild(available);
            attr = dom.createAttribute("name");
            attr.setValue("available");
            available.setAttributeNode(attr);
            Element availPlayerList = dom.createElement("list");
            available.appendChild(availPlayerList);
            for (Player player : gameDef.getPlayers()) {
                Element pref = dom.createElement("ref");
                attr = dom.createAttribute("local");
                attr.setValue("player"+player.getID());
                pref.setAttributeNode(attr);                        
                availPlayerList.appendChild(pref);
            }

       	 	for (Place place : gameDef.getPlaces()) {
               
                if (place.getCell().getTexture() != null && place.getCell().getTexture().getName().contains("water")) {
                    System.out.println("Ingonoring " + place.getPlaceName() + " because it is under water");
                }
                else {
                    System.out.println("Adding place " + place.getPlaceName());
            		Element bean = addBean(dom, rootEle, "place"+place.getID(), place.getClassName());
                    addConstructor(dom, bean, "name", place.getPlaceName());         		
                    addStringProperty(dom, bean, "desc", place.getDesc());
                    addIntegerProperty(dom, bean, "cellID", Integer.parseInt(place.getCell().getID())); 
                    addDoubleProperty(dom, bean, "edgeSize", edgeSize);  
                    addIntegerProperty(dom, bean, "cellsPerRow", cellsPerRow);              
                    addReferenceProperty(dom, bean, "position", "position"+place.getID());   
                
                    if (place.getItems() != null && place.getItems().size() > 0) {

                        Element pitems = dom.createElement("property");
                        bean.appendChild(pitems);
                        attr = dom.createAttribute("name");
                        attr.setValue("items") ;
                        pitems.setAttributeNode(attr);             
                        Element pitemList = dom.createElement("list");
                        pitems.appendChild(pitemList);

                        for (Item item : place.getItems()) {
                            Element vref = dom.createElement("ref");
                            attr = dom.createAttribute("local");
                            attr.setValue("item"+item.getID());
                            vref.setAttributeNode(attr);                       
                            pitemList.appendChild(vref);
                        }
                    }

                    if (place.getPlayers() != null && place.getPlayers().size() > 0) {

                        Element players = dom.createElement("property");
                        bean.appendChild(players);
                        attr = dom.createAttribute("name");
                        attr.setValue("players");
                        players.setAttributeNode(attr);                
                        Element playerList = dom.createElement("list");
                        players.appendChild(playerList);

                        for (Player player : place.getPlayers()) {
                            Element pref = dom.createElement("ref");
                            attr = dom.createAttribute("local");
                            attr.setValue("player"+player.getID());
                            pref.setAttributeNode(attr);                        
                            playerList.appendChild(pref);
                        }
                    }

                    if (place.getWalls() != null && place.getWalls().size() > 0) {

                        Element pwalls = dom.createElement("property");
                        bean.appendChild(pwalls);
                        attr = dom.createAttribute("name");
                        attr.setValue("wallMap");
                        pwalls.setAttributeNode(attr);                             
                        Element wallMap = dom.createElement("map");
                        pwalls.appendChild(wallMap);                   

                        for (Wall wall : place.getWalls()) {
                            Element entry = dom.createElement("entry");
                            attr = dom.createAttribute("key");
                            attr.setValue("wall"+wall.getID());
                            entry.setAttributeNode(attr);
                            attr = dom.createAttribute("value");                        
                            attr.setValue(Integer.toString(wall.getDirection()));
                            entry.setAttributeNode(attr);
                            wallMap.appendChild(entry);                                               
                        }
                    }

                    if (place.getWater() != null && place.getWater().size() > 0) {

                        System.out.println("Exporting water boundaries for " + place.getPlaceName());

                        Element pwater = dom.createElement("property");
                        bean.appendChild(pwater);
                        attr = dom.createAttribute("name");
                        attr.setValue("waterMap");
                        pwater.setAttributeNode(attr);                             
                        Element waterMap = dom.createElement("map");
                        pwater.appendChild(waterMap);                   

                        for (Water water : place.getWater()) {
                            Element entry = dom.createElement("entry");
                            attr = dom.createAttribute("key");
                            attr.setValue("water"+water.getID());
                            entry.setAttributeNode(attr);
                            attr = dom.createAttribute("value");                        
                            attr.setValue(Integer.toString(water.getDirection()));
                            entry.setAttributeNode(attr);
                            waterMap.appendChild(entry);                                               
                        }
                    }

                }
        	}

            Element gamePlaces = addBean(dom, rootEle, "places", "coop.map.GamePlaces");
            Element places = dom.createElement("property");
            gamePlaces.appendChild(places);
            attr = dom.createAttribute("name");
            attr.setValue("places");
            places.setAttributeNode(attr);
            Element placeList = dom.createElement("list");
            places.appendChild(placeList);
            for (Place place : gameDef.getPlaces()) {

                if (place.getCell().getTexture() == null || !place.getCell().getTexture().getName().contains("water")) {
                    Element pref = dom.createElement("ref");
                    attr = dom.createAttribute("local");
                    attr.setValue("place"+place.getID());
                    pref.setAttributeNode(attr);                        
                    placeList.appendChild(pref);
                }
            }
            
        	dom.appendChild(rootEle);

        	try {
            	Transformer tr = TransformerFactory.newInstance().newTransformer();
            	tr.setOutputProperty(OutputKeys.INDENT, "yes");
            	tr.setOutputProperty(OutputKeys.METHOD, "xml");
            	tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");            	
            	tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            	// send DOM to file
            	tr.transform(new DOMSource(dom), 
                                 new StreamResult(new FileOutputStream(fname)));

        	} catch (TransformerException te) {
            	System.out.println(te.getMessage());
        	} catch (IOException ioe) {
            	System.out.println(ioe.getMessage());
        	}
    	} catch (ParserConfigurationException pce) {
        	System.out.println("UsersXML: Error trying to instantiate DocumentBuilder " + pce);
	    }
	}

}