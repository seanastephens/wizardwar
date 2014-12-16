package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Point;
import java.util.List;

import map.Grassland;
import map.Hill;
import map.Homebase;
import map.Mountain;
import map.Road;
import map.Tile;
import map.TileMap;
import map.Wall;

import org.junit.Test;

public class TileMapTest {

	@Test
	public void testTileMapParsesCorrectSizeMap() {
		TileMap t = new TileMap("test_map");

		Tile[][] tiles = t.getTiles();

		assertEquals(4, tiles.length);
		assertEquals(4, tiles[0].length);
	}

	@Test
	public void testTileMapParsesCorrectTiles() {
		TileMap t = new TileMap("test_map");

		Tile[][] tiles = t.getTiles();

		assertTrue(tiles[0][0].getTerrain() instanceof Grassland);
		assertTrue(tiles[0][1].getTerrain() instanceof Grassland);
		assertTrue(tiles[0][2].getTerrain() instanceof Grassland);
		assertTrue(tiles[0][3].getTerrain() instanceof Mountain);
		assertTrue(tiles[1][0].getTerrain() instanceof Wall);
		assertTrue(tiles[1][1].getTerrain() instanceof Wall);
		assertTrue(tiles[1][2].getTerrain() instanceof Wall);
		assertTrue(tiles[1][3].getTerrain() instanceof Hill);
		assertTrue(tiles[2][0].getTerrain() instanceof Grassland);
		assertTrue(tiles[2][1].getTerrain() instanceof Grassland);
		assertTrue(tiles[2][2].getTerrain() instanceof Grassland);
		assertTrue(tiles[2][3].getTerrain() instanceof Mountain);
		assertTrue(tiles[3][0].getTerrain() instanceof Road);
		assertTrue(tiles[3][1].getTerrain() instanceof Road);
		assertTrue(tiles[3][2].getTerrain() instanceof Road);
		assertTrue(tiles[3][3].getTerrain() instanceof Homebase);
	}

	@Test
	public void testFindNeightbors() {
		TileMap t = new TileMap("test_map");

		// Top
		Point top = new Point(0, 1);
		List<Point> neighbors_top = t.getNeighbors(top);
		assertTrue(neighbors_top.contains(new Point(0, 0)));
		assertTrue(neighbors_top.contains(new Point(1, 1)));
		assertTrue(neighbors_top.contains(new Point(0, 2)));

		// Bottom
		Point bottom = new Point(t.getNumRows() - 1, 1);
		List<Point> neighbors_bottom = t.getNeighbors(bottom);
		assertTrue(neighbors_bottom.contains(new Point(t.getNumRows() - 1, 0)));
		assertTrue(neighbors_bottom.contains(new Point(t.getNumRows() - 1, 2)));
		assertTrue(neighbors_bottom.contains(new Point(t.getNumRows() - 2, 1)));

		// Left
		Point left = new Point(1, 0);
		List<Point> neighbors_left = t.getNeighbors(left);
		assertTrue(neighbors_left.contains(new Point(0, 0)));
		assertTrue(neighbors_left.contains(new Point(2, 0)));
		assertTrue(neighbors_left.contains(new Point(1, 1)));

		// --Right
		Point right = new Point(1, t.getNumRows() - 1);
		List<Point> neighbors_right = t.getNeighbors(right);
		assertTrue(neighbors_right.contains(new Point(0, t.getNumRows() - 1)));
		assertTrue(neighbors_right.contains(new Point(2, t.getNumRows() - 1)));
		assertTrue(neighbors_right.contains(new Point(1, t.getNumRows() - 2)));

		// --Top left Corner
		Point top_left = new Point(0, 0);
		List<Point> neighbors_top_left = t.getNeighbors(top_left);
		assertTrue(neighbors_top_left.contains(new Point(0, 1)));
		assertTrue(neighbors_top_left.contains(new Point(1, 0)));

		// --Top right Corner
		Point top_right = new Point(0, t.getNumCols() - 1);
		List<Point> neighbors_top_right = t.getNeighbors(top_right);
		assertTrue(neighbors_top_right.contains(new Point(0, t.getNumCols() - 2)));
		assertTrue(neighbors_top_right.contains(new Point(1, t.getNumCols() - 1)));

		// --Bottom left Corner
		Point bottom_left = new Point(t.getNumRows() - 1, 0);
		List<Point> neighbors_bottom_left = t.getNeighbors(bottom_left);
		assertTrue(neighbors_bottom_left.contains(new Point(t.getNumRows() - 1, 1)));
		assertTrue(neighbors_bottom_left.contains(new Point(t.getNumRows() - 2, 0)));

		// --Bottom right corner
		Point bottom_right = new Point(t.getNumRows() - 1, t.getNumCols() - 1);
		List<Point> neighbors_bottom_right = t.getNeighbors(bottom_right);
		assertTrue(neighbors_bottom_right
				.contains(new Point(t.getNumRows() - 1, t.getNumCols() - 2)));
		assertTrue(neighbors_bottom_right
				.contains(new Point(t.getNumRows() - 2, t.getNumCols() - 1)));
	}

}
