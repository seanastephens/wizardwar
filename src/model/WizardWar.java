package model;

import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import map.Tile;
import map.TileMap;
import messaging.AttackCommand;
import messaging.Command;
import messaging.MoveUnitCommand;
import messaging.UseItemCommand;
import pathfinding.Path;
import pathfinding.PathFinder;
import wincondition.CTFWinCondition;
import wincondition.WinCondition;
import wincondition.ZombiesWinCondition;
import Items.Item;
import Items.Trap;
import Units.InvisibleUnit;
import Units.Unit;

public class WizardWar implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8307139394589113588L;
	private TileMap map;
	private boolean isOver;
	private List<Player> players = new ArrayList<Player>();
	private List<Unit> units = new ArrayList<Unit>();
	private List<Item> items = new ArrayList<Item>();
	private long id;
	private Tile selectedTile;
	private List<GameListener> listeners = new ArrayList<GameListener>();
	private Unit currentUnitSelected;
	private List<Command> movesToMake = new ArrayList<Command>();
	private String attackString = "";
	private String moveString = "";
	private WinCondition winCondition;
	private int round;
	private boolean flag;

	public WizardWar(String mapName, long id) {
		map = new TileMap(mapName);
		round = 1;
		flag = false;
		isOver = false;
		this.id = id;
		for (Tile t : map.getListOfTiles()) {
			if (t.getItem() != null) {
				items.add(t.getItem());
			}
			if (t.getUnit() != null) {
				units.add(t.getUnit());
			}
		}
		winCondition = map.getWinCondition();
	}

	public TileMap getMap() {
		return map;
	}

	public List<Unit> getAllUnits() {
		return units;
	}

	public List<Item> getAllItems() {
		return items;
	}

	public long getID() {
		return id;
	}

	public int getNumberOfPlayers() {
		return players.size();
	}

	public Player getPlayerWithName(String name) {
		for (Player p : players) {
			if (p.getName().equals(name)) {
				return p;
			}
		}
		throw new NoSuchElementException("That player does not exist!");
	}

	public void moveUnit(Unit u, Point to, Command c) {

		if (u.getLocation().equals(to)) {
			notifyListeners();
			return;
		}

		assert (map.getTile(to).getUnit() == null || map.getTile(to).getUnit() instanceof InvisibleUnit);

		map.getTile(u.getLocation()).removeUnit();
		map.getTile(to).setUnit(u);
		u.setLocation(to);

		if (map.getTile(to).getItem() != null && u.getAmountItems() < 2) {
			if (map.getTile(to).getItem() instanceof Trap) {
				System.err.println("TRAP");
				map.getTile(to).getItem().use(u, map);
			} else
				u.pickedUpItem(map.getTile(to).getItem());
			for (Player p : getPlayers()) {
				if (p.getUnits().contains(u)) {
					p.adjustScore(20);
				}
			}

			map.getTile(to).removeItem();
		}
		cleanUp();
		notifyListeners();
	}

	public Unit getUnit(String playerUnitID) {
		for (Unit u : units) {
			String id = u.getClass().getSimpleName() + ":" + u.getUID();
			if (id.equals(playerUnitID)) {
				return u;
			}
		}

		return null;
	}

	public Item getItem(String playerItemID) {
		for (Item i : items) {
			String id = i.getClass().getSimpleName() + ":" + i.getUID();
			if (id.equals(playerItemID)) {
				return i;
			}
		}
		throw new NoSuchElementException("That item does not exist!");
	}

	public void addPlayer(String playerName, String team) {

		if (!willAcceptAnotherPlayer()) {
			throw new IllegalStateException("Don't add too many players...");
		}

		Player newPlayer = new Player(playerName, team);
		players.add(newPlayer);
		for (Unit u : units) {
			if (u.getTeam().equals(newPlayer.getTeam())) {
				newPlayer.addUnit(u);
			}
		}
		/*
		 * MORE HAX: initializes the first player to go first... works because
		 * we only add players before the game starts.
		 */
		players.get(0).setTurn(true);
	}

	public Player getCurrentTurn() {
		for (Player p : players) {
			if (p.isTurn()) {
				return p;
			}
		}

		throw new IllegalStateException(
				"No one is assigned to be the current player!");
	}

	public void advancePlayerTurn() {
		int lastPlayer = 0;
		for (int i = 0; i < players.size(); i++) {
			Player cur = players.get(i);
			if (cur.isTurn()) {
				cur.setTurn(false);
				lastPlayer = i;
			}
		}

		players.get((lastPlayer + 1) % players.size()).setTurn(true);
		movesToMake.clear();
		notifyListeners();

	}

	public void addListener(GameListener g) {
		listeners.add(g);
	}

	public List<Player> getPlayers() {
		return players;
	}

	public boolean didTeamWin(String team) {
		for (Player p : players) {
			if (!p.getTeam().equals(team)) {
				continue;
			}
			if (!winCondition.didWin(this, p)) {
				return false;
			}
		}

		return true;
	}

	public boolean didPlayerWin(String player) {
		return winCondition.didWin(this, getPlayerWithName(player));
	}

	public int getTeamUnitCount(String team) {
		int count = 0;
		for (Player p : players) {
			if (p.getTeam().equals(team)) {
				count += p.getUnits().size();
			}
		}
		return count;
	}

	public boolean willAcceptAnotherPlayer() {
		return players.size() < map.getMaxNumberOfPlayers();
	}

	private List<Unit> toKill;

	public boolean cleanUp() {
		toKill = new ArrayList<Unit>();
		for (Unit u : units) {
			if (u.getHealth() <= 0) {
				toKill.add(u);
			}
		}

		for (Unit u : toKill) {
			for (Player p : getPlayers()) {
				p.getUnits().remove(u);
			}

			for (Item i : u.getItemsList()) {
				if (i.getName().equals("Flag"))
					map.getTile(u.getLocation()).setItem(i);
			}
			units.remove(u);
			map.getTile(u.getLocation()).setUnit(null);
		}

		return toKill.size() > 0;
	}

	public String getGameType() {
		if (this.winCondition instanceof CTFWinCondition) {
			return "CTF";
		}

		else if (this.winCondition instanceof ZombiesWinCondition) {
			return "Zombies";
		}

		return "DeathMatch";
	}

	public void click(Point tile, String playerID) {

		String ourTeam = "";
		for (Player p : getPlayers()) {
			if (p.getName().equals(playerID)) {
				ourTeam = p.getTeam();
			}
		}

		if (getCurrentTurn().getTeam().equals(ourTeam) && isOver() == false) {

			selectedTile = map.getTile(tile);

			if (ourTeam.length() == 0) {
				throw new IllegalStateException();
			}

			if (selectedTile.getUnit() != null
					&& currentUnitSelected == null
					&& selectedTile.getUnit().getTeam()
							.equals(getPlayerWithName(playerID).getTeam())) {
				currentUnitSelected = selectedTile.getUnit();
			} else if (selectedTile.getUnit() == null
					&& currentUnitSelected != null) {
				if (currentUnitSelected.getTeam().equals(ourTeam)
						&& getCurrentTurn().getTeam().equals(ourTeam)
						&& new PathFinder(map, currentUnitSelected)
								.getPossibleDestinations().contains(tile)) {

					Command attkToRemove = null;
					List<Command> movesToRemove = new ArrayList<Command>();
					if (currentUnitSelected.hasMoved() == true) {
						for (Command c : movesToMake) {
							if (c instanceof MoveUnitCommand) {
								if (currentUnitSelected.getUID() == c.getUnit()
										.getUID()) {
									movesToRemove.add(c);
								}
							} else if (c instanceof AttackCommand
									&& currentUnitSelected.getUID() == c
											.getUnit().getUID()) {
								attkToRemove = c;
							}
						}
					}
					if (attkToRemove != null) {
						movesToMake.remove(attkToRemove);
					}
					for (Command c : movesToRemove) {
						movesToMake.remove(c);
					}

					// Remove the invisible unit at the destination point
					if (!movesToRemove.isEmpty()) {
						map.getTile(
								movesToRemove.get(movesToRemove.size() - 1)
										.getDestinationPoint()).removeUnit();
					}

					PathFinder aPath = new PathFinder(map, currentUnitSelected);
					Path thePath = aPath.getPath(tile);
					registerDestination(tile);
					List<Point> moves = thePath.getPath();
					for (int i = 0; i < moves.size() - 1; i++) {
						movesToMake.add(new MoveUnitCommand(
								currentUnitSelected, moves.get(i + 1), moves
										.get(i)));
					}

					currentUnitSelected.setHasMoved(true);
				}

				currentUnitSelected = null;
			}

			else if (getCurrentTurn().getTeam().equals(ourTeam)
					&& selectedTile.getUnit() != null
					&& !(selectedTile.getUnit() instanceof InvisibleUnit)
					&& currentUnitSelected != null) {
				Unit defender = selectedTile.getUnit();
				Unit attacker = currentUnitSelected;
				if (!attacker.getTeam().equals(defender.getTeam())
						&& attacker.getTeam().equals(ourTeam)) {
					ArrayList<Command> movesToRemove = new ArrayList<Command>();
					if (attacker.hasAttacked() == true) {
						for (Command c : movesToMake) {
							if (c instanceof AttackCommand) {
								if (attacker.getUID() == c.getUnit().getUID()) {
									movesToRemove.add(c);
								}
							}
						}
					}
					for (Command c : movesToRemove) {
						movesToMake.remove(c);
					}
					List<Point> attackTiles = new ArrayList<Point>();
					PathFinder pathFinder = new PathFinder(map, attacker);
					List<Point> possibleMoves = pathFinder
							.getPossibleDestinations();
					possibleMoves.add(attacker.getLocation());
					for (Point p : possibleMoves) {
						attackTiles.addAll(pathFinder.getInRangeTiles(p,
								attacker));

					}

					Point dest = null;
					for (Command c : movesToMake) {
						if (c instanceof MoveUnitCommand
								&& c.getUnit().getUID() == attacker.getUID()) {
							dest = c.getDestinationPoint();
						}
					}

					if (attackTiles.contains(defender.getLocation())) {

						if (attacker.hasMoved()
								&& !pathFinder.getInRangeTiles(dest, attacker)
										.contains(defender.getLocation())) {
							List<Command> toRemoveAgain = new LinkedList<Command>();
							for (Command c : movesToMake) {
								if (c.getUnit().getUID() == attacker.getUID()) {
									toRemoveAgain.add(c);
								}
							}
							for (Command c : toRemoveAgain) {
								movesToMake.remove(c);
							}
							if (!toRemoveAgain.isEmpty()) {
								map.getTile(
										toRemoveAgain.get(
												toRemoveAgain.size() - 1)
												.getDestinationPoint())
										.removeUnit();
							}

							PathFinder aPath = new PathFinder(map,
									currentUnitSelected);
							Point move = aPath
									.getBestMoveToward(defender.getLocation(),
											currentUnitSelected);
							Path movePath = aPath.getPath(move);
							List<Point> moves = movePath.getPath();
							for (int i = 0; i < moves.size() - 1; i++) {
								movesToMake.add(new MoveUnitCommand(
										currentUnitSelected, moves.get(i + 1),
										moves.get(i)));
							}
							registerDestination(moves.get(moves.size() - 1));

							currentUnitSelected.setHasMoved(true);

						} else if (!attacker.hasMoved()) {
							List<Command> toRemoveAgain = new LinkedList<Command>();
							for (Command c : movesToMake) {
								if (c.getUnit().getUID() == attacker.getUID()) {
									toRemoveAgain.add(c);
								}
							}
							for (Command c : toRemoveAgain) {
								movesToMake.remove(c);
							}
							if (!toRemoveAgain.isEmpty()) {
								map.getTile(
										toRemoveAgain.get(
												toRemoveAgain.size() - 1)
												.getDestinationPoint())
										.removeUnit();
							}
							if (!pathFinder.getInRangeTiles(
									attacker.getLocation(), attacker).contains(
									defender.getLocation())) {

								PathFinder aPath = new PathFinder(map,
										currentUnitSelected);
								Point move = aPath.getBestMoveToward(
										defender.getLocation(),
										currentUnitSelected);
								Path movePath = aPath.getPath(move);
								List<Point> moves = movePath.getPath();
								for (int i = 0; i < moves.size() - 1; i++) {
									movesToMake.add(new MoveUnitCommand(
											currentUnitSelected, moves
													.get(i + 1), moves.get(i)));
								}
								registerDestination(moves.get(moves.size() - 1));

								currentUnitSelected.setHasMoved(true);
							}
						}
						movesToMake.add(new AttackCommand(attacker, defender));
						currentUnitSelected.setHasAttacked(true);
					}
					currentUnitSelected = null;
				}

				if (attacker.getTeam().equals(defender.getTeam())) {
					currentUnitSelected = defender;
				}
			}

		}

		notifyListeners();
	}

	/**
	 * Generates and adds a new UseItemCommand to the list of commands to
	 * execute
	 * 
	 * @param unit
	 *            The unit the given item will be used on
	 * @param item
	 *            The item to be used
	 */
	public void UseItem(Unit unit, Item item) {
		this.movesToMake.add(new UseItemCommand(unit, item));
	}

	public String attackString() {
		return attackString;
	}

	public String moveString() {
		return moveString;
	}

	public void notifyListeners() {
		for (GameListener g : listeners) {
			g.gameChanged();
		}
	}

	public Unit getSelectedUnit() {
		return currentUnitSelected;
	}

	public List<Command> getMovesToMake() {
		List<Command> temp = new LinkedList<Command>(movesToMake);
		return temp;
	}

	public List<Player> getListOfPlayers() {
		return players;
	}

	public boolean didWin(Player p) {
		return winCondition.didWin(this, p);
	}

	public List<Point> getListOfOccupiedPoints() {
		List<Point> occupiedTiles = new ArrayList<Point>();
		for (Unit u : units) {
			occupiedTiles.add(u.getLocation());
		}
		return occupiedTiles;
	}

	private List<Point> occupied = new LinkedList<>();

	public void registerDestination(Point p) {
		map.getTile(p).setUnit(new InvisibleUnit());
		occupied.add(p);
	}

	public void clearRegisteredDestinations() {
		for (Point p : occupied) {
			map.getTile(p).removeUnit();
		}
		occupied.clear();
	}

	private Unit activeUnit;

	public void setActiveUnit(Unit u) {
		activeUnit = u;
	}

	public Unit getActiveUnit() {
		return activeUnit;
	}

	public Point getLocationOfFlag() {
		for (Item anItem : getAllItems()) {
			if (anItem.getName().equals("Flag")) {
				return anItem.getLocation();
			}
		}
		return null;
	}

	public Point getLocationOfHomeBase() {
		Tile[][] myTiles = getMap().getTiles();
		for (int i = 0; i < myTiles.length; i++) {
			for (int j = 0; j < myTiles[0].length; j++) {
				if (myTiles[i][j].getTerrain().getName().equals("Homebase")) {
					return new Point(i, j);
				}
			}
		}
		return null;
	}

	public void clearAttackCommands() {
		List<Command> toRemove = new ArrayList<Command>();
		for (Command c : movesToMake) {
			if (c instanceof AttackCommand) {
				toRemove.add(c);
			}
		}
		for (Command c : toRemove) {
			movesToMake.remove(c);
		}
	}

	public boolean isYourUnit(Point pt, String playerID) {
		String ourTeam = "";
		for (Player p : getPlayers()) {
			if (p.getName().equals(playerID)) {
				ourTeam = p.getTeam();
			}
		}
		Tile tile = this.getMap().getTile(pt);
		Unit u = tile.getUnit();
		if (u == null) {
			return false;
		}
		return u.getTeam().equals(getPlayerWithName(playerID).getTeam());
	}

	public int getRound() {
		return round;
	}

	public void increaseRound(int adjust) {
		round += adjust;
	}

	public boolean didLose(Player client) {
		return winCondition.didLose(this, client);
	}


	public boolean isOver() {
		// TODO Auto-generated method stub
		return isOver;
	}

	public void setIsOver(boolean flag) {
		isOver = flag;
	}

	public void addUnit(Unit aUnit) {
		units.add(aUnit);
	}
}
