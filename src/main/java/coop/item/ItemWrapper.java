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
        itemMap = new HashMap<String, Class<?>>();
        itemMap.put("key", Key.class);
        itemMap.put("pen", Pen.class);
        itemMap.put("paper", Paper.class);
        itemMap.put("note", Note.class);
        itemMap.put("rope", Rope.class);
        itemMap.put("obstacle", Obstacle.class);
        itemMap.put("gold", Gold.class);
        itemMap.put("phone", Phone.class);
        itemMap.put("shovel", Shovel.class);
        itemMap.put("knife", Knife.class);
        itemMap.put("bucket", Bucket.class); 
        itemMap.put("bag", Bag.class);          
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
        actions.add("fill");
        actions.add("put");
        actions.add("remove");
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
}