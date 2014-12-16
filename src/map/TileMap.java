package map;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import view.MainPanel;
import wincondition.WinCondition;
import Items.Item;
import Units.Unit;
import server.Logger;

public class TileMap implements Serializable {

	private final static String MAP_CONFIG_PATH = "map_files/";
	private final static String UNIT_CONFIG_PATH = "unit_files/";
	private final static String ITEM_CONFIG_PATH = "item_files/";

	private Tile[][] tiles;
	private int width;
	private int height;
	private int maxNumberOfPlayers;
	private String name;

	private WinCondition winCondition;

	public TileMap(String fileName) {
		loadTiles(fileName);
		loadUnits(fileName);
		loadItems(fileName);
		this.name = fileName;
	}

	public void loadTiles(String mapName) {
		Scanner mapFileInput;
		try {
			mapFileInput = new Scanner(new File(MAP_CONFIG_PATH + mapName));
			mapFileInput.useDelimiter(",|\n|\r\n");
			parseMapDimensions(mapFileInput.nextLine() + "="
					+ mapFileInput.nextLine());
			parseGameType(mapFileInput.nextLine());
			initializeTileArray();

			for (int row = 0; row < height; row++) {
				for (int col = 0; col < width; col++) {

					String type = mapFileInput.next();
					Tile currentTile = tiles[row][col];
					currentTile.setTerrain(Terrain.getNewTerrainOfType(type));
				}
			}
		} catch (FileNotFoundException e) {
			Logger.log(this, "Could not load map config file for '" + mapName
					+ "'");
		}
	}

	private void parseMapDimensions(String dimensionLine) {
		String[] fields = dimensionLine.split("=");
		assert (fields.length == 4);

		height = Integer.valueOf(fields[1]);
		width = Integer.valueOf(fields[3]);
	}

	private void parseGameType(String gameTypeLine) {
		String[] fields = gameTypeLine.split("=");
		assert (fields.length == 2);

		String gameType = fields[1];
		winCondition = WinCondition.getWinConditionOfType(gameType);
	}

	private void initializeTileArray() {
		tiles = new Tile[height][width];
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				tiles[row][col] = new Tile();
			}
		}
	}

	private void loadUnits(String fileName) {
		final int UNIT_TEAM = 0;
		final int UNIT_TYPE = 1;
		final int UNIT_XSTART = 2;
		final int UNIT_YSTART = 3;

		Scanner unitInput = null;
		try {
			unitInput = new Scanner(new File(UNIT_CONFIG_PATH + fileName));
		} catch (IOException e) {
			Logger.log(this, "Could not load unit config file for '" + fileName
					+ "'");
			return;
		}

		String maxNumberOfPlayersString = unitInput.nextLine();
		String[] maxNumberOfPlayersFields = maxNumberOfPlayersString.split("=");
		maxNumberOfPlayers = Integer.valueOf(maxNumberOfPlayersFields[1]);

		while (unitInput.hasNextLine()) {
			String line = unitInput.nextLine();
			String[] fields = line.split(",");
			Unit u = Unit.getNewUnitOfType(fields[UNIT_TYPE]);
			u.setTeam(fields[UNIT_TEAM]);
			int x = Integer.parseInt(fields[UNIT_XSTART]);
			int y = Integer.parseInt(fields[UNIT_YSTART]);
			u.setLocation(new Point(x, y));
			tiles[x][y].setUnit(u);
			if (fields.length > 4) {
				for (int i = 4; i < fields.length; i++) {
					u.pickedUpItem(Item.getNewItemOfType(fields[i]));
				}
			}
		}

		unitInput.close();
	}

	private void loadItems(String fileName) {
		final int ITEM_TYPE = 0;
		final int ITEM_XSTART = 1;
		final int ITEM_YSTART = 2;

		Scanner itemInput = null;
		try {
			itemInput = new Scanner(new File(ITEM_CONFIG_PATH + fileName));
		} catch (IOException e) {
			Logger.log(this, "Could not load item config file for '" + fileName
					+ "'");
			return;
		}

		while (itemInput.hasNextLine()) {
			String line = itemInput.nextLine();
			String[] fields = line.split(",");
			Item i = Item.getNewItemOfType(fields[ITEM_TYPE]);
			int x = Integer.parseInt(fields[ITEM_XSTART]);
			int y = Integer.parseInt(fields[ITEM_YSTART]);
			i.setLocation(new Point(x, y));
			if (getTile(i.getLocation()).getUnit() != null) {
				getTile(i.getLocation()).getUnit().pickedUpItem(i);
			} else {
				getTile(i.getLocation()).setItem(i);
			}
		}

		itemInput.close();
	}

	public int getMaxNumberOfPlayers() {
		return maxNumberOfPlayers;
	}

	public Tile[][] getTiles() {
		return tiles;
	}

	public boolean isPointInBounds(Point p) {
		return (p.x >= 0 && p.y >= 0 && p.x < tiles.length && p.y < tiles[0].length);
	}

	public Tile getTile(Point t) {
		if (t.x >= 0 && t.y >= 0 && t.x < tiles.length && t.y < tiles[0].length) {
			return tiles[t.x][t.y];
		}
		return null;
	}

	public void reloadImages() {
		for (Tile[] row : tiles) {
			for (Tile tile : row) {
				tile.getTerrain().loadImage();
				if (tile.getUnit() != null) {
					tile.getUnit().imageReset();
					tile.getUnit().loadImage();
				}
				if (tile.getItem() != null) {
					tile.getItem().loadImage();
				}
			}
		}
		applyTerrainTransitions();
	}

	public void addUnit(Unit u, Point tile) {
		tiles[tile.x][tile.y].setUnit(u);
	}

	public int getNumRows() {
		return height;
	}

	public int getNumCols() {
		return width;
	}

	public List<Point> getNeighbors(Point p) {
		List<Point> neighbors = new ArrayList<Point>();

		if (p.x > 0) {
			neighbors.add(new Point(p.x - 1, p.y)); // Top
		}
		if (p.y > 0) {
			neighbors.add(new Point(p.x, p.y - 1)); // Bottom
		}
		if (p.x < height - 1) {
			neighbors.add(new Point(p.x + 1, p.y)); // Left
		}

		if (p.y < width - 1) {
			neighbors.add(new Point(p.x, p.y + 1)); // Left
		}

		return neighbors;
	}

	public String getSurroundingMountainTiles(int row, int col) {
		String terrainTransition = "";
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				if (i == 0 && j == 0) {
					continue;
				}
				if (tiles[(i + (row + height)) % height][(j + (col + width))
						% width].getTerrain().name.equals("Mountain"))
					terrainTransition += 1;
				else
					terrainTransition += 0;
			}
		}

		return terrainTransition;
	}
	
	public String getSurroundingHillTiles(int row, int col) {
		String terrainTransition = "";
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				if (i == 0 && j == 0) {
					continue;
				}
				if (tiles[(i + (row + height)) % height][(j + (col + width))
						% width].getTerrain().name.equals("Hill"))
					terrainTransition += 1;
				else
					terrainTransition += 0;
			}
		}

		return terrainTransition;
	}

	public String getSurroundingPathTiles(int row, int col) {
		String paths = "";
		if (row - 1 > 0 && tiles[row - 1][col].getTerrain().name.equals("Road"))
			paths += 1;
		else
			paths += 0;
		if (col - 1 > 0 && tiles[row][col - 1].getTerrain().name.equals("Road"))
			paths += 1;
		else
			paths += 0;
		if (col + 1 < width && tiles[row][col + 1].getTerrain().name.equals("Road"))
			paths += 1;
		else
			paths += 0;
		if (row + 1 < height && tiles[row + 1][col].getTerrain().name.equals("Road"))
			paths += 1;
		else
			paths += 0;
		return paths;
	}
	
	public String getNonWaterTiles(int row, int col) {
		String paths = "";
		if (row - 1 > 0 && !tiles[row - 1][col].getTerrain().name.equals("Water"))
			paths += 1;
		else
			paths += 0;
		if (col - 1 > 0 && !tiles[row][col - 1].getTerrain().name.equals("Water"))
			paths += 1;
		else
			paths += 0;
		if (col + 1 < width && !tiles[row][col + 1].getTerrain().name.equals("Water"))
			paths += 1;
		else
			paths += 0;
		if (row + 1 < height && !tiles[row + 1][col].getTerrain().name.equals("Water"))
			paths += 1;
		else
			paths += 0;
		return paths;
	}

	public void applyTerrainTransitions() {
		ArrayList<BufferedImage> mountainTransitionsList = Grassland
				.loadTransitionImage("MountainTransitions");
		ArrayList<BufferedImage> deepGrassTransitionList = Grassland.loadTransitionImage("DeepGrassTransitions");

		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				
				if(tiles[row][col].getTerrain().name.equals("Road")){
					BufferedImage transition = Road.loadTransitionImage(getSurroundingPathTiles(row,col));
					tiles[row][col].getTerrain().setModifiedImage(
							transition);
				}
				if(tiles[row][col].getTerrain().name.equals("Water")){
					BufferedImage transition = Water.loadTransitionImage(getNonWaterTiles(row,col));
					tiles[row][col].getTerrain().setModifiedImage(
							transition);
				}
				
				if (!tiles[row][col].getTerrain().name.equals("Hill") && !tiles[row][col].getTerrain().name.equals("Mountain")) {
					String neighbors = getSurroundingHillTiles(row, col);

					if (neighbors != "00000000") { // if there are non-grass
													// surrounding tiles
						BufferedImage transitionImage = tiles[row][col]
								.getTerrain().makeTransitionImage(neighbors,
										deepGrassTransitionList);
						BufferedImage defaultImage = tiles[row][col]
								.getTerrain().getImage();
						BufferedImage finalImage = new BufferedImage(
								MainPanel.TILE_SIZE, MainPanel.TILE_SIZE,
								BufferedImage.TYPE_INT_ARGB);
						Graphics2D imageGraphicsContext = finalImage
								.createGraphics();
						imageGraphicsContext
								.drawImage(defaultImage, 0, 0, null);
						imageGraphicsContext.drawImage(transitionImage, 0, 0,
								null);
						tiles[row][col].getTerrain().setModifiedImage(
								finalImage);
						imageGraphicsContext.dispose();
					}
				}
				
				if (!tiles[row][col].getTerrain().name.equals("Mountain")) {
					String neighbors = getSurroundingMountainTiles(row, col);

					if (neighbors != "00000000") { // if there are non-grass
													// surrounding tiles
						BufferedImage transitionImage = tiles[row][col]
								.getTerrain().makeTransitionImage(neighbors,
										mountainTransitionsList);
						BufferedImage defaultImage = tiles[row][col]
								.getTerrain().getImage();
						BufferedImage finalImage = new BufferedImage(
								MainPanel.TILE_SIZE, MainPanel.TILE_SIZE,
								BufferedImage.TYPE_INT_ARGB);
						Graphics2D imageGraphicsContext = finalImage
								.createGraphics();
						imageGraphicsContext
								.drawImage(defaultImage, 0, 0, null);
						imageGraphicsContext.drawImage(transitionImage, 0, 0,
								null);
						tiles[row][col].getTerrain().setModifiedImage(
								finalImage);
						imageGraphicsContext.dispose();
					}
				}
			}
		}

	}

	public List<Tile> getListOfTiles() {
		List<Tile> tileList = new ArrayList<Tile>();
		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[0].length; j++) {
				tileList.add(tiles[i][j]);
			}
		}
		return tileList;
	}

	public WinCondition getWinCondition() {
		assert (winCondition != null);
		return winCondition;
	}

	public String getName() {
		return name;
	}

	public String getGameTypeDescription() {
		return winCondition.getDescription();
	}
}
