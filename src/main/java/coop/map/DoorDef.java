package coop.map;

public class DoorDef {
	private Cell innerCell;
	private Cell outerCell;
	private Wall wall;
	private MyLine doorVector;

	public DoorDef(Cell innerCell, Cell outerCell, Wall wall, MyLine doorVector) {
		this.innerCell = innerCell;
		this.outerCell = outerCell;
		this.wall = wall;		
		this.doorVector = doorVector;
	}

	public MyLine getDoorVector() {
		return doorVector;
	} 

	public Cell getInnerCell() {
		return innerCell;
	}

	public Cell getOuterCell() {
		return outerCell;
	}

	public Wall getWall() {
		return wall;
	}
}