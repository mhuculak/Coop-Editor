package coop.map;

public class Door {

	private String id;
	private String className;
	private MyPoint wallPos;           // point on the wall
	private MyPoint innerPos;           // point inside the door
	private String innerCellid;        // inside the house
	private String outerCellid;        // outside the house
	private String wallid;             // door must be on a wall, also each wall can have 0-1 doors	
	private boolean isLocked;
	private String keyName;            // name of key needed to unlock
	
	public Door(String id, DoorDef doorDef, boolean isLocked) {
		this.id = id;
		this.wallPos = doorDef.getDoorVector().getEndp1();
		this.innerPos = doorDef.getDoorVector().getEndp2();;
		this.innerCellid = doorDef.getInnerCell().getID();
		this.outerCellid = doorDef.getOuterCell().getID();
		this.wallid = doorDef.getWall().getID();
		this.isLocked = isLocked;
		className = "coop.map.Door";
	}

	public Door(String line) {
		String[] c = line.split(":");
		id = c[0].replaceAll("\\D+","");
		wallPos = new MyPoint(c[1]);
		innerPos = new MyPoint(c[2]);
		innerCellid = c[3];
		outerCellid = c[4];
		wallid = c[5];		
		isLocked = Boolean.parseBoolean(c[6]);
		if (c.length == 8) {
			keyName = c[7];
		}
		className = "coop.map.Door";
	}

	public String getID() {
		return id;
	}

	public String getClassName() {
		return className;
	}

	public MyPoint getWallPos() {
		return wallPos;
	}

	public MyPoint getInnerPos() {
		return innerPos;
	}

	public String getWallid() {
		return wallid;
	}

	public boolean isLocked() {
		return isLocked;
	}

	public void setIsLocked(boolean isLocked) {
		this.isLocked = isLocked;
	}

	public String getKeyName() {
		return keyName;
	}

	public void setKeyName(String kname) {
		keyName = kname;
	}

	public void toggle() { // toggles the direction the door faces
		int dx = innerPos.getX() - wallPos.getX();
		int dy = innerPos.getY() - wallPos.getY();
		innerPos = new MyPoint(wallPos.getX() - dx, wallPos.getY() - dy);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder(100);
		sb.append("door"+id+":"+wallPos.toString()+":"+innerPos.toString());
		sb.append(":"+innerCellid+":"+outerCellid+":"+wallid);
		sb.append(":"+Boolean.toString(isLocked));
		if (keyName != null) {
			sb.append(":"+keyName);
		}
		return sb.toString();
	}

}