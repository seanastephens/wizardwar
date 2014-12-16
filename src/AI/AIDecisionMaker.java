package AI;

import java.awt.Point;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;

import messaging.EndTurnMessage;
import model.Player;
import model.WizardWar;
import pathfinding.PathFinder;
import Units.Unit;

public abstract class AIDecisionMaker {

	protected WizardWar game;
	protected String name;
	

	public AIDecisionMaker(WizardWar game, String name) {
		this.game = game;
		this.name = name;
	}

	public abstract EndTurnMessage getQueueOfActions();

	/**
	 * @param u
	 *            - Unit to search around.
	 * @return List of Unit enemies that are within attack range for Unit u.
	 */
	public List<Unit> getListOfEnemiesWeCanAttack(Unit u) {
		List<Unit> hostiles = getListOfHostileUnits();
		List<Unit> hostilesInRange = new LinkedList<Unit>();
		PathFinder p = new PathFinder(game.getMap(), u);
		for (Unit hostile : hostiles) {
			Point ourLocation = u.getLocation();
			Point hostileLocation = hostile.getLocation();
			if (p.getInRangeTiles(ourLocation, u).contains(hostileLocation)) {
				hostilesInRange.add(hostile);
			}
		}
		return hostilesInRange;
	}

	public int getManhattanDistance(Point a, Point b) {
		return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);

	}

	/**
	 * Returns the tile-Point location of the nearest enemy Unit.
	 * 
	 * @param u
	 *            - Unit to search around.
	 * @return closest location, or null if no enemy units.
	 */
	public Point getPointOfNearestEnemyTo(final Unit u) {
		Unit closestUnit = Collections.min(getListOfHostileUnits(),
				new Comparator<Unit>() {
					public int compare(Unit a, Unit b) {
						double distToA = u.getLocation().distanceSq(
								a.getLocation());
						double distToB = u.getLocation().distanceSq(
								b.getLocation());
						return (int) (distToA - distToB);
					}
				});
		return closestUnit.getLocation();
	}

	/**
	 * NOTE: this method uses the team of the player with which this
	 * AIDecisionMake was initialized.
	 * 
	 * @return List of Unit with different team than player.
	 */
	protected List<Unit> getListOfHostileUnits() {
		List<Unit> hostiles = new LinkedList<Unit>();
		String ourTeam = getSelf().getTeam();

		for (Unit u : game.getAllUnits()) {
			if (!u.getTeam().equals(ourTeam)) {
				hostiles.add(u);
			}
		}
		return hostiles;
	}

	/**
	 * TODO: remove/refactor?
	 * 
	 * @return Player associated with the playerID that this AIDecisionMaker was
	 *         initialized with.
	 */
	protected Player getSelf() {
		Player p = game.getPlayerWithName(name);
		if (p == null) {
			throw new IllegalStateException("You don't exist o0");
		}
		return p;
	}

	/**
	 * @param goal
	 *            - Point to move toward
	 * @param u
	 *            - Unit to move
	 * @return a move that will not conflict with other moves made by this
	 *         AIDecisionMaker, in the direction of goal.
	 */
	protected Point getBestMoveToward(final Point goal, Unit u) {
		PathFinder pathFinder = new PathFinder(game.getMap(), u);
		List<Point> validMoves = pathFinder.getPossibleDestinations();
		if (validMoves.size() == 0) {
			return null;
		}
		return Collections.min(validMoves, new Comparator<Point>() {
			public int compare(Point a, Point b) {
				return (int) (goal.distanceSq(a) - goal.distanceSq(b));
			}
		});
	}

	public static AIDecisionMaker getAIOfType(String type, WizardWar game,
			String playerID) {
		switch (type) {
		case "CTF":
			return new CaptureTheFlagStrategy(game, playerID);
		case "DeathMatch":
			return new DeathmatchStrategy(game, playerID);
		case "Zombies":
			return new DeathmatchStrategy(game, playerID);
		default:
			throw new IllegalArgumentException("Invalid game type : " + type);
		}
	}
}
