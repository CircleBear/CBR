package core;

/**
 * The purpose of this abstract class is to represent the general
 * structure of an item for CBR which can be extended by
 * specific items themselves (like arrow).
 * 
 * @author Josh Branchaud, Dan Wiechert
 * @version 1.0
 * @since 1.0
 */
public abstract class Item {
	/**
	 * getPosition is a method that retrieves the private variable
	 * that represents this item's position.
	 * @return Position
	 */
	public abstract Position getPosition();
	
	/**
	 * setPosition is a method that sets the value of this item's
	 * Position to the given Position value.
	 * @param newPosn
	 */
	public abstract void setPosition(Position newPosn);
} // End Item class
