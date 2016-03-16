package coop.io;

import coop.map.Place;
import coop.map.GameDef;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.xml.sax.*;
import org.w3c.dom.*;
import java.io.*;
import java.util.Map;
import java.util.Iterator;
import java.util.Map.Entry;

public class GameFile {
	public static GameDef readXML(String fname) {
        GameDef gameDef = new GameDef();
        gameDef.setName(fname);
        Document dom;
        // Make an  instance of the DocumentBuilderFactory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            // use the factory to take an instance of the document builder
            DocumentBuilder db = dbf.newDocumentBuilder();
            // parse using the builder to get the DOM mapping of the    
            // XML file
            dom = db.parse(fname);
            dom.getDocumentElement().normalize();
            NodeList nList = dom.getElementsByTagName("bean");
            for (int i = 0; i < nList.getLength(); i++) {
            	Node bNode = nList.item(i);
            	if (bNode.getNodeType() == Node.ELEMENT_NODE) {
            		Element beanElement = (Element)bNode;
            		String id = beanElement.getAttribute("id");
            		String className = beanElement.getAttribute("class");
            		Place place = new Place(id);
            		NodeList properties = beanElement.getElementsByTagName("property");
            		for (int j=0 ; j<properties.getLength() ; j++) {
            			Node pNode = properties.item(j);            			
            			if (pNode.getNodeType() == pNode.ELEMENT_NODE) {
            				Element pElement = (Element)pNode;
            				String name = pElement.getAttribute("name");
            				if (name.equals("items")) {
            					NodeList itemlist = pElement.getElementsByTagName("value");
            					ItemsProp items = new ItemsProp("items");
            					for ( int k=0 ; k<itemlist.getLength() ; k++) {
            						Node vNode = itemlist.item(k);
            						if (vNode.getNodeType() == vNode.ELEMENT_NODE) {
            							Element vElement = (Element)vNode;
            							items.addItem(vElement.getTextContent());
            						}
            					}

            				}
            				else if (name.equals("actions")) {
            					NodeList entrylist = pElement.getElementsByTagName("entry");
            					ActionsProp actions = new ActionsProp("actions");
            					for ( int k=0 ; k<entrylist.getLength() ; k++) {
            						Node eNode = entrylist.item(k);
            						if (eNode.getNodeType() == eNode.ELEMENT_NODE) {
            							Element eElement = (Element)eNode;
            							actions.addAction(eElement.getAttribute("key"), eElement.getAttribute("value"));
            						}
            					}

            				}
            				else {
            					String value = pElement.getAttribute("value");
            					if (value != null) {
	            					ValueProp prop = new ValueProp(name);
	            					prop.setValue(value);
	            					place.addProp(prop);
	            				}

            				}

            			}
            		}
                    gameDef.addPlace(place);

            	}
            }
                     
            return gameDef;

        } catch (ParserConfigurationException pce) {
            System.out.println(pce.getMessage());
        } catch (SAXException se) {
            System.out.println(se.getMessage());
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        }

        return null;
    }

    public static void saveToXML(GameDef gameDef) {
    	String fname = gameDef.getName() + ".xml";
    	Document dom;
    	Element e = null;
    	Attr attr = null;
    	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    	
    	try {
        	// use factory to get an instance of document builder
        	DocumentBuilder db = dbf.newDocumentBuilder();
        	// create instance of DOM
        	dom = db.newDocument();

        	// create the root element
        	Element rootEle = dom.createElement("beans");

       	 	// create data elements and place them under root

       	 	for (Place place : gameDef.getPlaces()) {
                System.out.println("Adding place " + place.getPlaceName());
        		Element bean = dom.createElement("bean");        		
        		rootEle.appendChild(bean);

        		attr = dom.createAttribute("id");
        		attr.setValue(place.getPlaceName());
        		bean.setAttributeNode(attr);
        		attr = dom.createAttribute("class");
        		attr.setValue(place.getClassName());
        		bean.setAttributeNode(attr);

        		Element arg = dom.createElement("constructor-arg");
        		bean.appendChild(arg);
        		attr = dom.createAttribute("name");
        		attr.setValue("name");
        		arg.setAttributeNode(attr);
        		attr = dom.createAttribute("value");
        		attr.setValue(place.getPlaceName());
        		arg.setAttributeNode(attr);

        		for (Prop prop : place.getProps()) {

        			Element property = dom.createElement("property");
        			bean.appendChild(property);
        			if (prop instanceof ValueProp) {
        				ValueProp vprop = (ValueProp)prop;
        				attr = dom.createAttribute("name");
        				attr.setValue(prop.getName());
        				property.setAttributeNode(attr);
        				attr = dom.createAttribute("value");
        				attr.setValue(vprop.getValue());
        				property.setAttributeNode(attr);
        			}
        			else if (prop instanceof ItemsProp) {
        				ItemsProp items = (ItemsProp)prop;
        				attr = dom.createAttribute("name");
        				attr.setValue("items");
        				Element list = dom.createElement("list");
        				property.appendChild(list);
        				for (String item: items.getItems()) {
        					Element ivalue = dom.createElement("value");
        					ivalue.appendChild(dom.createTextNode(item));
        					list.appendChild(ivalue);
        				}
        			}
        			else if (prop instanceof ActionsProp) {
        				ActionsProp actions = (ActionsProp)prop;
        				attr = dom.createAttribute("name");
        				attr.setValue("actions");
        				Element pmap = dom.createElement("map");
        				property.appendChild(pmap);
        				Map<String,String> aMap = actions.getActions();
        				Iterator entries = aMap.entrySet().iterator();
        				while ( entries.hasNext()) {
        					Entry entry = (Entry) entries.next();
        					Element aentry = dom.createElement("entry");
        					attr = dom.createAttribute("key");
        					attr.setValue(entry.getKey().toString());
        					aentry.setAttributeNode(attr);
        					attr = dom.createAttribute("value");
        					attr.setValue(entry.getValue().toString());
        					aentry.setAttributeNode(attr);
        					pmap.appendChild(aentry);
        				}
        			}
        			else {
        				System.out.println("Unknown property " + prop.getName());
        			}
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