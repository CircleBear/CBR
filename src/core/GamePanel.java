package core;

/**
 * This is the class that does all logic checking,
 * drawing of images, gets user input, and runs
 * the game. 
 * 
 * @author Dan Wiechert
 * @version 1.0
 * @since 1.0
 */

//Import statements
import javax.imageio.ImageIO;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class GamePanel extends JPanel implements Runnable, KeyListener {
	// Dimensions of game and tile sizes
	private static final int WIDTH = 200;
	private static final int HEIGHT = 600;
	private static final int TILE_SIZE = 50;
	
	// Defines ints for the directions
	private static final int LEFT = 1;
	private static final int DOWN = 2;
	private static final int UP = 3;
	private static final int RIGHT = 4;
	
	// Numbers used in FPS calculations
	private static final int NO_DELAYS_PER_YIELD = 16;
	private static int MAX_FRAME_SKIPS = 5;
	
	// The game that created this GamePanel
	private Game gameTop;
	
	// The current map (commented out)
	//private char[][] map;
	
	// ArrayLists of the arrows in the game
	private ArrayList<Arrow> arrows = new ArrayList<Arrow>();
	private ArrayList<Arrow> topArrows = new ArrayList<Arrow>();
	
	// Image files
	private Image dbImage = null;
	private BufferedImage topLeftArrow;
	private BufferedImage topDownArrow;
	private BufferedImage topUpArrow;
	private BufferedImage topRightArrow;
	private BufferedImage leftArrow;
	private BufferedImage downArrow;
	private BufferedImage upArrow;
	private BufferedImage rightArrow;
	private Graphics dbg = null;
	
	// New global variables (temp comment)
	private long period;
	private Thread animator;           // the thread that performs the animation
	private boolean running = false;   // used to stop the animation thread
	private boolean isPaused = false;
	private long gameStartTime;
	
	// Start Constructor(s)
	/**
	 * This is the main constructor of the GamePanel and initializes all variables.
	 * 
	 * @param game The game that created this GamePanel.
	 * @param boxes An ArrayList of the boxes on the map.
	 * @param period A long to help with FPS calculations.
	 */
	public GamePanel(Game game, /*char[][] map,*/ ArrayList<Arrow> arrows, long period) {
		this.gameTop = game;
		//this.map = map;
		this.arrows = arrows;
		this.period = period;
		
		createTopArrows();
		readInImages();
		
		setBackground(Color.white);
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		
		setFocusable(true);
	    requestFocus();    // the JPanel now has focus, so receives key events
		//readyForTermination(); Don't need this for now.
		
		addKeyListener(this);
	} // End GamePanel(Game, map)
	// End Constructor(s)
	
	// =================================================================================
	// Start section of running game
	// =================================================================================

	/**
	 * Wait for the JPanel to be added to the JFrame before starting.
	 */
	public void addNotify() { 
		super.addNotify();   // creates the peer
	    startGame();         // start the thread
	} // End addNotify()
	
	/**
	 * Initializes and starts the thread
	 */
	private void startGame() { 
		if (animator == null || !running) {
			animator = new Thread(this);
		    animator.start();
	    }
	} // End startGame()

	/**
	 * Called when the JFrame is actived / deiconified.
	 */
	public void resumeGame() {
		isPaused = false;  
	} // End resumeGame()

	/**
	 * Called when the JFram is deactivated / iconified.
	 */
	public void pauseGame() {
		isPaused = true;
	} // End pauseGame()

	/**
	 * Called when the JFrame is closing.
	 */
	public void stopGame() {
		running = false;
	} // End stopGame()
	  
	  /**
	   * TODO: Comment...
	   */
	public void run() {
		long beforeTime, afterTime, timeDiff, sleepTime;
		long overSleepTime = 0L;
		int noDelays = 0;
		long excess = 0L;
		  
		gameStartTime = System.nanoTime();
		beforeTime = gameStartTime;
		  
		running = true;
		  
		while(running) {
			gameUpdate();
			gameRender();
			paintScreen();
			  
			afterTime = System.nanoTime();
			timeDiff = afterTime - beforeTime;
			sleepTime = (period - timeDiff) - overSleepTime;
			  
			if(sleepTime > 0) {
				try {
					Thread.sleep(sleepTime/1000000L);
				}
				catch(InterruptedException e) {
					System.err.println(e.getMessage());
				}
				  
				overSleepTime = (System.nanoTime() - afterTime) - sleepTime;
			}
			else {
				excess -= sleepTime;
				overSleepTime = 0L;
				  
				if(++noDelays >= NO_DELAYS_PER_YIELD) {
					Thread.yield();
					noDelays = 0;
				}
			}
			  
			beforeTime = System.nanoTime();
			  
			int skips = 0;
			while((excess > period) && (skips < MAX_FRAME_SKIPS)) {
				excess -= period;
				gameUpdate();
				skips++;
			}
		} // End running 
	} // End run()
	
	// =================================================================================
	// End section for running game
	// =================================================================================
	
	// =================================================================================
	// Start section for working with images
	// =================================================================================
	  
	/**
	 * This method re-draws all the images once the
	 * logic is updated.
	 */
	private void gameRender() {
		if (dbImage == null) {
			dbImage = createImage(WIDTH, HEIGHT);
			
			if (dbImage == null) {
				System.out.println("dbImage is null.");
				return;
			}
			else
				dbg = dbImage.getGraphics();
		} // End if
		
		// clear the background
	    dbg.setColor(Color.BLUE);
	    dbg.fillRect (0, 0, WIDTH, HEIGHT);
	    
	    // Drawing in the floor, boxes, bombs, bomber, and explosions
	    this.fillInTopArrows(dbg);
	    this.fillInArrows(dbg);
	} // End gameRender()
	
	/**
	 * This method reads in all the images that will be used
	 * during the game.
	 */
	private void readInImages() {
		// TODO: Read in correct images
		
		// Load the explosion image(s)
		try {
		 	this.topLeftArrow = ImageIO.read(new File("src/Images/fire_left_1.png"));
			this.topDownArrow = ImageIO.read(new File("src/Images/fire_down_1.png"));
			this.topUpArrow = ImageIO.read(new File("src/Images/fire_up_1.png"));
			this.topRightArrow = ImageIO.read(new File("src/Images/fire_right_1.png"));
			this.leftArrow = ImageIO.read(new File("src/Images/fire_left_1.png"));
			this.downArrow = ImageIO.read(new File("src/Images/fire_down_1.png"));
			this.upArrow = ImageIO.read(new File("src/Images/fire_up_1.png"));
			this.rightArrow = ImageIO.read(new File("src/Images/fire_right_1.png"));
		}
		catch(IOException e) {
			System.err.println(e.getMessage());
		}
	} // End readInImages()
	
	private void createTopArrows() {
		topArrows.add(new Arrow(new Position((LEFT * TILE_SIZE) - TILE_SIZE, TILE_SIZE), LEFT));
		topArrows.add(new Arrow(new Position((DOWN * TILE_SIZE) - TILE_SIZE, TILE_SIZE), DOWN));
		topArrows.add(new Arrow(new Position((UP * TILE_SIZE) - TILE_SIZE, TILE_SIZE), UP));
		topArrows.add(new Arrow(new Position((RIGHT * TILE_SIZE) - TILE_SIZE, TILE_SIZE), RIGHT));
	} // End createTopArrows()
	
	/**
	 * This method draws the arrows graphics on the screen.
	 * 
	 * @param dbg A graphics screen to draw on.
	 */
	private void fillInArrows(Graphics dbg) {	
		// Draws the arrows
		for(int i = 0; i < this.arrows.size(); i++) {
			try {
				int x = arrows.get(i).getPosition().getXCoord();
				int y = arrows.get(i).getPosition().getYCoord();
				
				/*
				 * Drawing different arrow images based on
				 * what direction they are.
				 */
				if (arrows.get(i).getDirection() == LEFT)
					dbg.drawImage(this.leftArrow, x, y, this);
				else if (arrows.get(i).getDirection() == DOWN)
					dbg.drawImage(this.downArrow, x, y, this);
				else if (arrows.get(i).getDirection() == UP)
					dbg.drawImage(this.upArrow, x, y, this);
				else if (arrows.get(i).getDirection() == RIGHT)
					dbg.drawImage(this.rightArrow, x, y, this);
			}
			catch (NullPointerException e) {
				// Ignore exception because bomb is gone
			}
		} // End for
	} // End fillInArrows()
	
	/**
	 * This method draws the arrows graphics on the screen.
	 * 
	 * @param dbg A graphics screen to draw on.
	 */
	private void fillInTopArrows(Graphics dbg) {	
		// Draws the arrows
		for(int i = 0; i < this.topArrows.size(); i++) {
			try {
				int x = this.topArrows.get(i).getPosition().getXCoord();
				int y = this.topArrows.get(i).getPosition().getYCoord();
				
				/*
				 * Drawing different arrow images based on
				 * what direction they are.
				 */
				if (topArrows.get(i).getDirection() == LEFT)
					dbg.drawImage(this.topLeftArrow, x, y, this);
				else if (topArrows.get(i).getDirection() == DOWN)
					dbg.drawImage(this.topDownArrow, x, y, this);
				else if (topArrows.get(i).getDirection() == UP)
					dbg.drawImage(this.topUpArrow, x, y, this);
				else if (topArrows.get(i).getDirection() == RIGHT)
					dbg.drawImage(this.topRightArrow, x, y, this);
			}
			catch (NullPointerException e) {
				// Ignore exception because bomb is gone
			}
		} // End for
	} // End fillInTopArrows()
	
	/**
	 * Uses active rendering to put the buffered image on-screen.
	 */
	private void paintScreen() { 
		Graphics g;
      
	    try {
	    	g = this.getGraphics();
	      
	        if ((g != null) && (dbImage != null))
	        	g.drawImage(dbImage, 0, 0, null);
	      
	        g.dispose();
	    }
	    catch (Exception e) { 
	    	System.out.println("Graphics context error: " + e);  
	    }
	} // End paintScreen()
	
	// =================================================================================
	// End section for working with images
	// =================================================================================
	
	// =================================================================================
	// Start section for game logic
	// =================================================================================
	
	/**
	 * This updates the logic of the game.
	 */
	private void gameUpdate() {	
		/*
		 * Removing arrows that are past the top.
		 */
		for (int b = (arrows.size() - 1); b > -1; b--)
			if (arrows.get(b).getPosition().getYCoord() <= 0)
				arrows.remove(b);
		
		/*
		 * Moving the arrows up.
		 */
		for (int a = 0; a < this.arrows.size(); a++)
			this.arrows.get(a).moveUp();
	} // End gameUpdate()
	
	/**
	 * This method checks to see if a hit was made
	 * by using Position.scorePoints().
	 * 
	 * @param dir The direction to check hit.
	 */
	private void checkHit(int dir) {
		int points = 0;
		 
		/*
		 * Switch on the direction to check against other
		 * arrows to see if they score points. If an
		 * arrow scores points, they are displayed and
		 * the arrow is removed.
		 */
		switch (dir) {
			case 1: // Left
				for (int z = 0; z < arrows.size(); z++) {
					points = topArrows.get(dir - 1).scorePoints(arrows.get(z).getPosition());
					
					if (points != 0) {
						System.out.println("Left: " + points);
						arrows.remove(z);
					} // End if
				} // End for
				
				break;
			case 2: // Down
				for (int z = 0; z < arrows.size(); z++) {
					points = topArrows.get(dir - 1).scorePoints(arrows.get(z).getPosition());
					
					if (points != 0) {
						System.out.println("Down: " + points);
						arrows.remove(z);
					} // End if
				} // End for
				
				break;
			case 3: // Up
				for (int z = 0; z < arrows.size(); z++) {
					points = topArrows.get(dir - 1).scorePoints(arrows.get(z).getPosition());
					
					if (points != 0) {
						System.out.println("Up: " + points);
						arrows.remove(z);
					} // End if
				} // End for
				
				break;
			case 4: // Right
				for (int z = 0; z < arrows.size(); z++) {
					points = topArrows.get(dir - 1).scorePoints(arrows.get(z).getPosition());
					
					if (points != 0) {
						System.out.println("Right: " + points);
						arrows.remove(z);
					} // End if
				} // End for
				
				break;
			default:
				// Do nothing, won't occur
				break;
		} // End switch
	} // End checkHit()
	
	// =================================================================================
	// End section for game logic
	// =================================================================================
	
	// =================================================================================
	// Start section for keyboard input
	// =================================================================================
	
	/*
	 * (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	public void keyReleased(KeyEvent ke) {
		// Do nothing
	} // End keyReleased()
	
	/*
	 * (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	public void keyTyped(KeyEvent ke) {
		// Do nothing
	} // End keyTyped()
	
	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	public void keyPressed(KeyEvent ke) {		
		int keyCode = ke.getKeyCode(); // Getting key code pressed
		
		/*
		 * This is a switch on the keys we want to look out for
		 * that help with the movement of Bombers, pausing and 
		 * un-pausing the game, exiting the game, menus, and 
		 * dropping each Bomber's bombs.
		 */
		switch (keyCode) {
			case 10:	// ENTER
				// TODO: Implement num pad numbers for arrows
				// TODO: Check how close the hit was for points
				break;
			case 16:	// SHIFT
				break;
			case 27:	// ESCAPE
				running = false;
				System.exit(0);
				break;
			case 37:	// (â†�) LEFT Player 1
				checkHit(LEFT);
				break;
			case 38:	// (â†‘) UP Player 1
				checkHit(UP);
				break;
			case 39:	// (â†’) RIGHT Player 1
				checkHit(RIGHT);
				break;
			case 40:	// (â†“) DOWN Player 1
				checkHit(DOWN);
				break;
			case 65:	// (A) LEFT Player 2
				//if (positionMover(2, -TILE_SIZE, 0))
				//	player2.moveLeft();
				break;
			case 68:	// (D) RIGHT Player 2
				//if (positionMover(2, TILE_SIZE, 0))
				//	player2.moveRight();
				break;
			case 80:	// P
				// TODO: Use to pause and un-pause game
				break;
			case 83:	// (S) DOWN Player 2
				//if (positionMover(2, 0, TILE_SIZE))
				//	player2.moveDown();
				break;
			case 87:	// (W) UP Player 2
				//if (positionMover(2, 0, -TILE_SIZE))
				//	player2.moveUp();
				break;
			default:	// Any other key
				// Ignore other keystrokes
				break;
		} // End switch
	} // End keyPressed()
	
	// =================================================================================
	// End section for keyboard input
	// =================================================================================
} // End GamePanel