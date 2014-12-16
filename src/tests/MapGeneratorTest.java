package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Point;
import java.util.List;

import org.junit.Test;

import map.*;
import pathfinding.*;
import Units.*;
import mapeditor.*;

public class MapGeneratorTest {
	@Test
	public void generateMapTest() {
		TileMap map = new TileMap("default");
		Tile[][] map_tiles = map.getTiles();
		MapGenerator mg = new MapGenerator(map_tiles,2,"DeathMatch");
		mg.generate();
	}
	@Test
	public void saveFileTest() {
		TileMap map = new TileMap("default");
		Tile[][] map_tiles = map.getTiles();
		MapGenerator mg = new MapGenerator(map_tiles,2,"DeathMatch");
		mg.generate();
		mg.saveMap("test_default.txt");
	}
}