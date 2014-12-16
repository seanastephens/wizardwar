package AI;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import messaging.AttackCommand;
import messaging.Command;
import messaging.EndTurnMessage;
import messaging.MoveUnitCommand;
import model.Player;
import model.WizardWar;
import pathfinding.Path;
import pathfinding.PathFinder;
import Units.Unit;

public class DeathmatchStrategy extends AIDecisionMaker {

	private List<Point> occupied = new LinkedList<Point>();

	public DeathmatchStrategy(WizardWar game, String name) {
		super(game, name);
	}

	@Override
	public EndTurnMessage getQueueOfActions() {
		List<Command> commandList = new LinkedList<Command>();
		Player self = getSelf();
		List<Unit> ourUnits = self.getUnits();
		Collections.shuffle(ourUnits);
		for (Unit u : ourUnits) {
			commandList.addAll(getUnitStrategy(u));
		}
		game.clearRegisteredDestinations();
		EndTurnMessage ets = new EndTurnMessage(game.getID(), name, commandList);
		return ets;

	}

	public List<Command> getUnitStrategy(Unit u) {
		// If there are no enemies, send a no-op move command.
		List<Unit> hostiles = getListOfHostileUnits();
		List<Command> theCommands = new ArrayList<Command>();
		if (hostiles.size() == 0) {
			theCommands.add(new MoveUnitCommand(u, u.getLocation(), u
					.getLocation()));
			return theCommands;
		}

		// If we can attack someone, do it.
		List<Unit> targets = getListOfEnemiesWeCanAttack(u);
		if (targets.size() > 0) {
			theCommands.add(new AttackCommand(u, targets.get(0)));
			return theCommands;
		}

		// Otherwise, find the nearest enemy and move toward them.
		Point nearestEnemy = getPointOfNearestEnemyTo(u);
		Point move = getBestMoveToward(nearestEnemy, u);
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

		Point prev = u.getLocation();
		u.setLocation(move);
		targets = getListOfEnemiesWeCanAttack(u);
		if (targets.size() > 0) {
			theCommands.add(new AttackCommand(u, targets.get(0)));
			return theCommands;
		}
		u.setLocation(prev);

		return theCommands;

	}
}
