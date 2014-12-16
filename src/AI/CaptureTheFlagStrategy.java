package AI;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import pathfinding.Path;
import pathfinding.PathFinder;
import messaging.AttackCommand;
import messaging.Command;
import messaging.EndTurnMessage;
import messaging.MoveUnitCommand;
import model.Player;
import model.WizardWar;
import Items.Item;
import Units.Unit;

public class CaptureTheFlagStrategy extends AIDecisionMaker {
	private Player self;
	private static final int amount_Of_Redirect = 4;
	private WizardWar game;
	private String name;

	public CaptureTheFlagStrategy(WizardWar game, String name) {
		super(game, name);
		this.game = game;
		this.name = name;
	}

	@Override
	public EndTurnMessage getQueueOfActions() {
		List<Command> commandList = new LinkedList<Command>();
		self = getSelf();
		List<Unit> ourUnits = self.getUnits();
		Collections.shuffle(ourUnits);
		for (Unit u : ourUnits) {
			commandList.addAll(getUnitStrategy(u));
		}

		EndTurnMessage ets = new EndTurnMessage(game.getID(), name, commandList);
		return ets;

	}

	private List<Command> getUnitStrategy(Unit u) {
		List<Command> theCommands = new ArrayList<Command>();

		// If captured flag move to homebase
		if (u.capturedFlag()) {
			System.out.println(u.getName() + " " + u.getUID() + " condition 1");
			Point move = getBestMoveToward(game.getLocationOfHomeBase(), u);
			return moveToGoal(move, u, theCommands);
		}

		// If flag is up for grabs go get
		if (!FlagCaptured()) {
			System.out.println(u.getName() + " " + u.getUID() + " condition 2");
			Point move2 = getBestMoveToward(game.getLocationOfFlag(), u);
			return moveToGoal(move2, u, theCommands);
		}

		// If we can attack someone, do it.
		List<Unit> targets = getListOfEnemiesWeCanAttack(u);
		if (targets.size() > 0) {
			System.out.println(u.getName() + " " + u.getUID() + " condition 3");
			theCommands.add(new AttackCommand(u, targets.get(0)));
			return theCommands;
		}

		// Else move toward enemy flag carrier if there is one
		else if (getFlagCarrier() != null) {
			System.out.println(u.getName() + " " + u.getUID() + " condition 4");
			Point move = getBestMoveToward(getFlagCarrier(), u);
			return moveToGoal(move, u, theCommands);
		}

		else {
			// else deathmatch default
			System.out.println(u.getName() + " " + u.getUID() + " condition 5");
			return new DeathmatchStrategy(game,name).getUnitStrategy(u);
		}

	}

	public Point getFlagCarrier() {
		for (Unit hostile : getListOfHostileUnits()) {
			if (hostile.capturedFlag()) {
				return hostile.getLocation();
			}
		}

		return null;
	}

	private boolean FlagCaptured() {
		for (Unit u : game.getAllUnits()) {
			for (Item i : u.getItemsList()) {
				if (i.getName().equals("Flag")) {
					return true;
				}
			}
		}

		return false;
	}

	private List<Command> moveToGoal(Point move, Unit u,
			List<Command> theCommands) {
		if (move == null) {
			theCommands.add(new MoveUnitCommand(u, u.getLocation(), u
					.getLocation()));
			return theCommands;
		}

		// The place we move to is now occupied.
		PathFinder aPath = new PathFinder(game.getMap(), u);
		Path thePath = aPath.getPath(move);
		List<Point> moves = thePath.getPath();
		for (int i = 0; i < moves.size() - 1; i++) {
			theCommands.add(new MoveUnitCommand(u, moves.get(i + 1), moves
					.get(i)));
		}

		game.registerDestination(move);
		return theCommands;
	}
}

/*
 * public MoveUnitCommand moveTowardsFlag(Unit u) { if (!flagCaptured()) {
 * getBestMoveToward(game.get, u)
 * 
 * } }
 * 
 * // future CTF implementation public MoveUnitCommand moveTowardsEnemyBase(Unit
 * u) {
 * 
 * 
 * }
 * 
 * public Command MoveToPotion(Unit u) { // // // if(!flagCaptured()){ // if
 * ((u.getHealth() < u.getInitialHealth() / 2 && /*a health potion is // within
 * range u.getNearbyItems.equals(HealthPotion)
 */
// return new moveUnitCommand(game.getID(), WizardGame.TEMP_NAME, u,
// /*u.getNearbyItems.location() */)
// // repeat for defense potion
// //repeat for attack potion
// }

/*
 * return new MoveUnitCommand(u, new Point(4 + 5, 4 + 5), new Point()); }
 * 
 * 
 * public Command ScoutStrategy() { Unit u = null; for (Player p :
 * game.getPlayers()) { if (p.getName().equals(name)) { for (int i = 0; i <
 * p.getUnits().size(); i++) { if
 * (p.getUnits().get(i).getName().equals("Scout")) u = p.getUnits().get(i); } }
 * }
 * 
 * if ((u.getHealth() < u.getInitialHealth() / 2) // if he needs a potion // in
 * range, go get it || (u.getDefense() < u.getInitialDefense() / 2) ||
 * (u.getCombat() < u.getInitialCombat() / 2) || (u.getMana() <
 * u.getInitialMana() / 2)) return MoveToPotion(u);
 * 
 * else { return moveTowardsEnemyBase(u); }
 * 
 * // if(flag has not been captured) // if health below half and health potion
 * within move_points range, move // towards it // "  " same for defense, etc,
 * other potions if needed // else move max move_points toward enemy base // if
 * at end of move there is an enemy within range, attack // if no enemy within
 * range, end turn
 * 
 * // List<Unit> enemies = getListOfEnemiesWeCanAttack(u); // if (enemies.size()
 * > 0) { // for (Unit enemy : enemies) { // if (enemy.getDefense() <=
 * u.getCombat()) { // return new AttackCommand(game.getID(), name, u, enemy);
 * // } else { // u.blockAttack(); // } // } // } }
 */

