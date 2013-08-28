package core;

/**
 * The MapParser class is used for reading from text files that have
 * map information stored in them. It stores this information in a
 * double array.
 * 
 * @author Josh Branchaud, Dan Wiechert
 * @version 1.1
 * @since 1.0
 */

// Imported Classes
import java.io.*;
import java.util.ArrayList;

public class MapParser {
	// Tile size for the game
	private static final int TILE_SIZE = 50;
	
	// Constructor(s)
	/**
	 * The main constructor of the MapParser class.
	 */
	public MapParser() {
		// Do nothing
	} // End MapParser()
	// End Constructor(s)
	
	/**
	 * This method is used to parse a CircleBear map (.cbm) to load into the
	 * CBR game world.
	 * 
	 * @param filename A .cbm file to be parsed by the CBR system.
	 * @return An arraylist of the arrows in the game.
	 */
	public ArrayList<Arrow> parseInArrows(String filename) {
		BufferedReader br = null;
		ArrayList<Arrow> arrows = new ArrayList<Arrow>();
		
		// Trying to read in the map file
		try {
			File mapFile = new File(filename);
			br = new BufferedReader(new FileReader(mapFile));
			
			try {
				if(br != null) {
					String line = br.readLine();
					String[] currentArrow;
					int x, y;
					
					while (line != null) {
						// Parsing the current line
						currentArrow = line.split("::");
						x = Integer.parseInt(currentArrow[0]);
						y = Integer.parseInt(currentArrow[1]);
						
						arrows.add(new Arrow(new Position((x * TILE_SIZE) - TILE_SIZE, y), x));
						
						// Reading the next line
						line = br.readLine();
					} // End while
					
					// Closing the buffered reader
					br.close();
				} // End if
			} // End try
			catch (IOException e) {
				System.err.println("IOException: " + e.getLocalizedMessage());
				System.exit(-1);
			} // End catch
		} // End try
		catch(FileNotFoundException e) {
			System.err.println("FileNotFoundException: " + e.getMessage());
			System.exit(-1);
		} // End catch
				
		return arrows;
	} // End parseInArrows()
} // End MapParser class
