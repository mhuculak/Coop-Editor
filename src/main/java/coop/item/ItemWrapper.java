package coop.item;

import java.util.*;

public class ItemWrapper {
/*	
	private ArrayList<Class<?>> actionItems;           // items used to perform an action
    private ArrayList<Class<?>> containerItems;        // items that can hold other items
    private ArrayList<Class<?>> binderItems;           // items that can be used connect other items
    private ArrayList<Class<?>> obstacleItems;         // items that can be used as obstacles
    private ArrayList<Class<?>> changeItems;           // items that can be modified
*/    
    private Map<String, Class<?>> itemMap;              // all items;    
    private List<String> actions;                 // cannonical list of all possible actions

    public ItemWrapper() {
    	initItems();
    	initActions();
    }

    public Map<String, Class<?>> getItems() {
    	return itemMap;
    }

	private void initItems() {
/*		
        actionItems.add(Key.class);
        actionItems.add(Pen.class);
        actionItems.add(Paper.class);
        changeItems.add(Paper.class);
        binderItems.add(Rope.class);
        obstacleItems.add(Boulder.class);
*/        
        itemMap = new HashMap<String, Class<?>>();
        itemMap.put("key", Key.class);
        itemMap.put("pen", Pen.class);
        itemMap.put("paper", Paper.class);
        itemMap.put("note", Note.class);
        itemMap.put("rope", Rope.class);
        itemMap.put("boulder", Boulder.class);        
    }

    private void initActions() {
    	actions = new ArrayList<String>();
    	actions.add("move");
    	actions.add("talk");
    	actions.add("phone");
    	actions.add("write");
    	actions.add("lock");
    	actions.add("unlock");
    	actions.add("open");
    	actions.add("close");
    	actions.add("pick");
    	actions.add("drop");
    	actions.add("lift");
 	  	actions.add("throw");
 	  	actions.add("buy");
 	  	actions.add("bribe");
 	  	actions.add("give");
 	  	actions.add("tie");
 	  	actions.add("cut");
 	  	actions.add("dig");
 	  	actions.add("pour");
 	  	actions.add("cook");
 	  	actions.add("climb");
 	  	actions.add("boost"); 	  	
     }

      public boolean isAction(String action) {
     	for ( String a : actions) {
     		if (action.equals(a)) {
     			return true;
     		}
     	}
     	return false;
     }
/*
     public ArrayList<Class<?>> getActionItems() {
     	return actionItems;
     }

     public ArrayList<Class<?>> getContainerItems() {
     	return containerItems;
     }

     public ArrayList<Class<?>> getBinderItems() {
     	return binderItems;
     }

	public ArrayList<Class<?>> getObstacleItems() {
     	return obstacleItems;
     }

    public ArrayList<Class<?>> getChangeItems() {
    	return changeItems;
    }
*/
    

}