package core;

public class Arrow extends Item {
	// The position of this arrow
	private Position posn;
	
	/*
	 * The direction of the arrow:
	 * 1 = Left
	 * 2 = Down
	 * 3 = Up
	 * 4 = Right
	 */
	private int direction;
	
	// The amount the arrow moves up per render
	private final int MOVE_SIZE = 2;
	
	// Constructor(s)
	/**
	 * This is an overloaded constructor that sets
	 * position and direction.
	 * 
	 * @param initPosn The initial position of the arrow.
	 * @param dir The direction of the arrow.
	 */
	public Arrow(Position initPosn, int dir) {
		this.posn = initPosn;
		this.direction = dir;
	} // End Arrow(Position)
	// End Constructor(s)
	
	/**
	 * Moves the arrow up by MOVE_SIZE;
	 */
	public void moveUp() {
		int newY = this.posn.getYCoord() - MOVE_SIZE;
		this.posn.setYCoord(newY);
	} // End moveUp()
	
	@Override
	public Position getPosition() {
		return this.posn;
	} // End getPosition()

	@Override
	public void setPosition(Position newPosn) {
		this.posn = newPosn;		
	} // End setPosition()
	
	/**
	 * Gets the direction of the arrow.
	 * 
	 * @return An int representing the arrow's direction.
	 */
	public int getDirection() {
		return this.direction;
	} // End getDirection()
	
	/**
	 * Sets the direction of the arrow.
	 * 
	 * @param dir Arrow's direction.
	 */
	public void setDirection(int dir) {
		this.direction = dir;
	} // End setDirection
	
	/**
	 * This method scores the points for the CBR game.
	 * 
	 * @param posn A position to check for points.
	 * @return An int of the points awarded.
	 */
	public int scorePoints(Position posn) {
		int points = 0;
		int myY = this.getPosition().getYCoord();
		int otherY = posn.getYCoord();
		int diff = Math.abs(myY - otherY);
		
		if (diff <= 5)
			points = 100;
		else if (diff <= 15)
			points = 75;
		else if (diff <= 30)
			points = 50;
		else if (diff <= 50)
			points = 25;
		
		return points;
	} // End scorePoints()
} // End Arrow class
