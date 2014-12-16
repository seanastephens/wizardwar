package mapeditor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import map.Tile;
/**
 * Generates the map,item, and unit files from a 2D Array of Tiles
 * @author N R Callahan
 * @version 1.0 25APR2014
 */
public class MapGenerator {
	private int NUMBER_OF_PLAYERS;
	private String GAME_TYPE;
	private Tile[][] map;
	private String mapString;
	private String itemString;
	private String unitString;
	
	public MapGenerator(Tile[][] map, int NUMBER_OF_PLAYERS, String GAME_TYPE) {
		this.NUMBER_OF_PLAYERS = NUMBER_OF_PLAYERS;
		this.GAME_TYPE = GAME_TYPE;
		System.err.println(GAME_TYPE);
		this.map = map;
	}
/**
 * Generates the information necessary to print to the file.  Should be called before saveMap.	
 */
	public void generate() {
		mapString = generateMapString();
		itemString = generateItemString();
		unitString = generateUnitString();
	}
/**
 * Save the map information to the title specified.  Run generate before saving
 * @param title The tile of the map file
 */
	public void saveMap(String title) {
		try {
			title += ".user";
			PrintWriter FMOUT = new PrintWriter("map_files/"+title);
			PrintWriter FIOUT = new PrintWriter("item_files/"+title);
			PrintWriter FUOUT = new PrintWriter("unit_files/"+title);
			
			FMOUT.write(mapString);
			FIOUT.write(itemString);
			FUOUT.write(unitString);
			FMOUT.close(); FIOUT.close(); FUOUT.close();
		} catch(IOException ioe) { ioe.printStackTrace(); }
        ThumbNailGenerator tng = new ThumbNailGenerator(title);
        BufferedImage thumbNail = tng.getScaledImage(500, 500);
        try {
            ImageIO.write(thumbNail, "png", new File("thumbNails/" + title + ".png"));
        } catch (IOException e) {
            System.err.println("Could not write image " + title);
            e.printStackTrace();
        }
	}
	private String generateItemString() {
		String output = "";
		for(int i = 0; i < map.length; i++) {
			for(int j = 0; j < map[0].length; j++) {
				if (map[i][j].getItem() != null) {
					output += map[i][j].getItem().getClass().getSimpleName() + ",";
					output += map[i][j].getItem().getLocation().x + ",";
					output += map[i][j].getItem().getLocation().y + "\n";
				}
			}
		}
		return output;
	}
	private String generateMapString() {
		String output = "";
		output += "NUMBER_OF_ROWS="+map.length+"\n";
		output += "NUMBER_OF_COLS="+map[0].length+"\n";
		/* TODO FIXME XXX PLOX */
		output += "GAME_TYPE=" + GAME_TYPE + "\n";
		/* K THX BAI */
		for(int i = 0; i < map.length; i++) {
			for(int j = 0; j < map[0].length; j++) {
				if(j == map[0].length-1) {
					if(map[i][j].getTerrain().getName().equals("Homebase")) { output += "Base"; }
					else output += map[i][j].getTerrain().getName();	
				}
				else { 
					if(map[i][j].getTerrain().getName().equals("Homebase")) { output += "Base,"; }
					else output += map[i][j].getTerrain().getName()+","; 
				}
			}
			output+= "\n";
		}
		
		return output;
	}
	private String generateUnitString() {
		String output = "";
		output += "NUMBER_OF_PLAYERS="+NUMBER_OF_PLAYERS+"\n";
		for(int i = 0; i < map.length; i++) {
			for(int j = 0; j < map[0].length; j++) {
				if (map[i][j].getUnit() != null) {
					output += map[i][j].getUnit().getTeam() + ",";
					output += map[i][j].getUnit().getClass().getSimpleName() + ",";
					output += i + ",";
					output += j + "\n";
				}
			}
		}	
		return output;		
	}
}
