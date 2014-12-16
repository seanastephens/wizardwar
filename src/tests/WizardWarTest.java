package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.awt.Point;
import java.util.List;

import map.Tile;
import map.TileMap;
import model.GameListener;
import model.Player;
import model.WizardWar;

import org.junit.Test;

import Items.Trap;
import Units.Scout;
import Units.Unit;
import Units.Wizard;

public class WizardWarTest {

	/**
	 * Checks that WizardWar correctly loads units onto the map. This is sort of
	 * a TileMap test, but because the two are so coupled (bad!), it is worth
	 * testing here.
	 */
	@Test
	public void testUnitLoading() {
		WizardWar w = new WizardWar("test_map", 0l);

		Tile[][] tiles = w.getMap().getTiles();
		w.addPlayer("p1 - should be a", "A");
		w.addPlayer("p2 - should be b", "B");

		assertTrue(tiles[1][1].getUnit() instanceof Scout);
		assertTrue(tiles[2][2].getUnit() instanceof Wizard);

		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[0].length; j++) {
				if ((i != 1 || j != 1) && (i != 2 || j != 2)) {
					assertTrue(tiles[i][j].getUnit() == null);
				}
			}
		}
	}

	@Test
	public void testItemOnUnitLoading() {
		WizardWar w = new WizardWar("test_map", 0l);

		Tile[][] tiles = w.getMap().getTiles();
		w.addPlayer("p1 - should be a", "A");
		w.addPlayer("p2 - should be b", "B");

		Unit unit = tiles[1][1].getUnit();
		assertTrue(unit.getItemsList().size() == 1);
		assertTrue(unit.getItemsList().get(0) instanceof Trap);
	}

	/**
	 * This tests that units get added to the correct player lists for a simple
	 * game scenario.
	 */
	@Test
	public void testPlayerUnitLists() {
		WizardWar w = new WizardWar("test_map", 0l);

		w.addPlayer("p1 -- a", "A");
		w.addPlayer("p2 -- b", "B");

		Player a = null;
		Player b = null;
		for (Player p : w.getPlayers()) {
			if (p.getName().equals("p1 -- a")) {
				a = p;
			} else if (p.getName().equals("p2 -- b")) {
				b = p;
			}
		}

		assertEquals(1, a.getUnits().size());
		assertEquals(1, b.getUnits().size());

		assertTrue(a.getUnits().get(0) instanceof Scout);
		assertTrue(b.getUnits().get(0) instanceof Wizard);
	}

	/**
	 * Ensures the game will not accept too many players.
	 */
	@Test(expected = IllegalStateException.class)
	public void testCantAddTooManyPlayers() {
		WizardWar w = new WizardWar("test_map", 0l);

		assertTrue(w.willAcceptAnotherPlayer());
		w.addPlayer("p1 -- a", "A");
		assertTrue(w.willAcceptAnotherPlayer());
		w.addPlayer("p2 -- b", "B");
		assertFalse(w.willAcceptAnotherPlayer());
		w.addPlayer("Can't add me!", "B");
	}

	/**
	 * Check that moving a unit does everything that we want it to.
	 */
	@Test
	public void testMoveUnit() {
		WizardWar w = new WizardWar("test_map", 0l);
		Point origin = new Point(0, 0);
		w.addPlayer("a", "A");
		w.addPlayer("b", "B");

		Unit u = w.getMap().getTiles()[1][1].getUnit();
		assertNotNull(u); // sanity check
		w.moveUnit(u, origin, null);

		assertEquals(origin, u.getLocation());
		assertEquals(u, w.getMap().getTiles()[0][0].getUnit());
		assertNull(w.getMap().getTiles()[1][1].getUnit());
	}

	/**
	 * Check that moving a unit triggers a map update
	 */
	@Test
	public void testMoveUnitNotifies() {
		WizardWar w = new WizardWar("test_map", 0l);
		Point origin = new Point(0, 0);
		w.addPlayer("a", "A");
		w.addPlayer("b", "B");
		MockListener g = new MockListener();
		w.addListener(g);

		Unit u = w.getMap().getTiles()[1][1].getUnit();
		assertNotNull(u); // sanity check
		w.moveUnit(u, origin, null);

		assertTrue(g.called);
	}

	/**
	 * Test of some basic getters for peace of mind.
	 */
	@Test
	public void testGetters() {
		WizardWar w = new WizardWar("test_map", 0l);
		TileMap m = new TileMap("test_map");
		w.addPlayer("a", "A");
		w.addPlayer("b", "B");

		assertEquals(m.getMaxNumberOfPlayers(), w.getMap().getMaxNumberOfPlayers());
		assertEquals(m.getTiles().length, w.getMap().getTiles().length);
		assertEquals(m.getTiles()[0].length, w.getMap().getTiles()[0].length);

		assertEquals(0l, w.getID());
		assertEquals(2, w.getNumberOfPlayers());
	}

	/**
	 * Tests count functions. yep.
	 */
	@Test
	public void testCompHumanCounts() {
		WizardWar w = new WizardWar("test_map", 0l);
		w.addPlayer("__AI__", "A");
		w.addPlayer("__HU__", "B");

		assertEquals(1, w.getTeamUnitCount("A"));
		assertEquals(1, w.getTeamUnitCount("B"));
	}

	/**
	 * Tests that the board will remove the dead crap.
	 */
	@Test
	public void testCleanUp() {
		WizardWar w = new WizardWar("test_map", 0l);
		w.addPlayer("__AI__", "A");
		w.addPlayer("__HU__", "B");

		for (Tile[] row : w.getMap().getTiles()) {
			for (Tile t : row) {
				if (t.getUnit() != null) {
					t.getUnit().damageDone(1000000);
				}
			}
		}

		MockListener g = new MockListener();
		w.addListener(g);
		w.cleanUp();

		for (Tile[] row : w.getMap().getTiles()) {
			for (Tile t : row) {
				assertNull(t.getUnit());
			}
		}

		for (Player p : w.getPlayers()) {
			assertEquals(0, p.getUnits().size());
		}

		assertTrue(g.called);
	}

	@Test
	public void testAddUnitPutsInAllLists() {
		WizardWar w = new WizardWar("test_map", 0l);
		w.addPlayer("__AI__", "A");
		w.addPlayer("__HU__", "B");

		assertEquals(2, w.getAllUnits().size());
		assertEquals(2, w.getPlayers().size());
		assertEquals(1, w.getPlayers().get(0).getUnits().size());
		assertEquals(1, w.getPlayers().get(1).getUnits().size());
	}

	@Test
	public void testCleanUpTakesFromAllLists() {
		WizardWar w = new WizardWar("test_map", 0l);
		w.addPlayer("__AI__", "A");
		w.addPlayer("__HU__", "B");

		for (Unit u : w.getAllUnits()) {
			u.damageDone(9001);
		}

		w.cleanUp();

		assertEquals(0, w.getAllUnits().size());
		assertEquals(2, w.getPlayers().size());
		assertEquals(0, w.getPlayers().get(0).getUnits().size());
		assertEquals(0, w.getPlayers().get(1).getUnits().size());
	}

	@Test
	public void testAddItemPutsInAllLists() {
		WizardWar w = new WizardWar("test_map", 0l);
		w.addPlayer("__AI__", "A");
		w.addPlayer("__HU__", "B");

		assertEquals(1, w.getAllItems().size());
		assertTrue(w.getMap().getTile(new Point(0, 1)).getItem() instanceof Trap);
	}

	@Test
	public void testCurrentTurnAdvances() {
		WizardWar w = new WizardWar("test_map", 0l);
		w.addPlayer("__AI__", "A");
		w.addPlayer("__HU__", "B");

		Player turn = w.getCurrentTurn();
		assertNotNull(turn);
		w.advancePlayerTurn();
		assertFalse(turn.equals(w.getCurrentTurn()));
	}

	@Test
	public void testOccupiedTiles() {
		WizardWar w = new WizardWar("test_map", 0l);
		w.addPlayer("__AI__", "A");
		w.addPlayer("__HU__", "B");

		List<Point> occupied = w.getListOfOccupiedPoints();
		assertEquals(2, occupied.size());
		assertTrue(occupied.contains(new Point(1, 1)));
		assertTrue(occupied.contains(new Point(2, 2)));

		// Kill the units
		for (Point p : occupied) {
			w.getMap().getTile(p).getUnit().damageDone(1000);
		}
		w.cleanUp();
		occupied = w.getListOfOccupiedPoints();

		assertEquals(0, occupied.size());
	}

	@Test
	public void testGetPlayerList() {
		WizardWar w = new WizardWar("test_map", 0l);
		w.addPlayer("__AI__", "A");
		w.addPlayer("__HU__", "B");

		Player ai = w.getPlayerWithName("__AI__");
		Player hu = w.getPlayerWithName("__HU__");

		List<Player> players = w.getListOfPlayers();
		assertEquals(2, players.size());
		assertTrue(players.contains(ai));
		assertTrue(players.contains(hu));
	}
	
	@Test
	public void testWinConditionsValidAtStart(){
		WizardWar w = new WizardWar("test_map", 0l);
		w.addPlayer("__AI__", "A");
		w.addPlayer("__HU__", "B");
		
		assertFalse(w.didPlayerWin("__AI__"));
		assertFalse(w.didPlayerWin("__HU__"));
	}	
	
	@Test
	public void testWinConditionsValidAtEnd(){
		WizardWar w = new WizardWar("test_map", 0l);
		w.addPlayer("__AI__", "A");
		w.addPlayer("__HU__", "B");
		
		for(Unit u : w.getAllUnits()){
			u.damageDone(1000);
		}
		w.cleanUp();
		
		assertTrue(w.didPlayerWin("__AI__"));
		assertTrue(w.didPlayerWin("__HU__"));
	}

	private class MockListener implements GameListener {
		public boolean called = false;

		public void gameChanged() {
			called = true;
		}
	}
}
