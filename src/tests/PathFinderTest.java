package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Point;
import java.util.List;

import org.junit.Test;

import map.TileMap;
import pathfinding.*;
import Units.*;

public class PathFinderTest {

	/**
	 * This test confirms that the neighbor field of the Nodes in the PathFinder
	 * hashmap are being set correctly, at least in this edge case.
	 */
	@Test
	public void checkNeighborsForMidMapPoint() {
		TileMap map = new TileMap("default");
		Tank mover = new Tank();
		mover.setLocation(new Point(0, 0));
		PathFinder pf = new PathFinder(map, mover);

		List<Point> neighbors = pf.getHashMap().get(new Point(10, 10)).neighbors;
		assertTrue(neighbors.contains(new Point(10, 9)));
		assertTrue(neighbors.contains(new Point(9, 10)));
		assertTrue(neighbors.contains(new Point(11, 10)));
		assertTrue(neighbors.contains(new Point(10, 11)));
	}

	/**
	 * This test checks that the Node neighbor field in the PathFinder hashmap
	 * is being set correctly for a corner case.
	 */
	@Test
	public void checkNeighborsOnMapCorner() {
		TileMap map = new TileMap("default");
		Tank mover = new Tank();
		mover.setLocation(new Point(0, 0));
		PathFinder pf = new PathFinder(map, mover);

		List<Point> neighbors = pf.getHashMap().get(
				new Point(map.getNumRows() - 1, map.getNumCols() - 1)).neighbors;
		assertTrue(neighbors.contains(new Point(map.getNumRows() - 2, map.getNumCols() - 1)));
		assertTrue(neighbors.contains(new Point(map.getNumRows() - 1, map.getNumCols() - 2)));
	}

	/**
	 * This test checks that the Node neighbor field in the PathFinder hashmap
	 * is being set correctly for a different corner case.
	 */
	@Test
	public void checkNeighborsOnOtherMapCorner() {
		TileMap map = new TileMap("default");
		Tank mover = new Tank();
		mover.setLocation(new Point(0, 0));
		PathFinder pf = new PathFinder(map, mover);

		List<Point> neighbors = pf.getHashMap().get(new Point(0, map.getNumCols() - 1)).neighbors;
		assertTrue(neighbors.contains(new Point(0, map.getNumCols() - 2)));
		assertTrue(neighbors.contains(new Point(1, map.getNumCols() - 1)));
	}

	@Test
	public void checkPossibleDestinations() {
		TileMap map = new TileMap("test_map");
		Tank mover = new Tank();
		mover.setLocation(new Point(0, 0));
		PathFinder pf = new PathFinder(map, mover);
		List<Point> destinations = pf.getPossibleDestinations();
		assertEquals(2, destinations.size());
		assertTrue(destinations.contains(new Point(0, 1)));
		assertTrue(destinations.contains(new Point(0, 2)));
	}


//	@Test
//	public void checkPossibleDestinationsOnBlank() {
//		TileMap map = new TileMap("blank_map");
//		Tank mover = new Tank();
//		mover.setLocation(new Point(0, 0));
//		PathFinder pf = new PathFinder(map, mover);
//		List<Point> destinations = pf.getPossibleDestinations();
//		for (Point p : destinations) {
//			assertTrue(mover.getMovePoints() >= p.x + p.y);
//		}
//		assertEquals(5, destinations.size());
//	}
}
