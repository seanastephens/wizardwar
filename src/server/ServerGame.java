package server;

import java.awt.Point;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import map.TileMap;
import messaging.DisconnectMessage;
import messaging.EndTurnMessage;
import messaging.MakeGameHandShake;
import model.Player;
import model.WizardWar;
import Units.Tank;
import Units.Unit;

public class ServerGame implements Runnable {

	private Map<String, ObjectInputStream> inputs;
	private Map<String, ObjectOutputStream> outputs;
	private WizardWar game;
	private Set<String> teams = new TreeSet<String>();

	private boolean running = true;

	public ServerGame(Map<String, ObjectInputStream> inputs,
			Map<String, ObjectOutputStream> outputs, String mapName, long id) {
		this.inputs = inputs;
		this.outputs = outputs;
		Logger.log(this, "Starting a new game " + id + " on " + mapName);
		game = new WizardWar(mapName, id);
		for (Unit u : game.getAllUnits()) {
			teams.add(u.getTeam());
		}
	}

	int turn = 0;

	public boolean hasRoomForAnotherPlayer() {
		return game.willAcceptAnotherPlayer();
	}

	public int getMaxNumberOfPlayers() {
		return game.getMap().getMaxNumberOfPlayers();
	}

	public int getNumberOfPlayers() {
		return game.getNumberOfPlayers();
	}

	public void addPlayer(String playerID, List<Unit> customUnits) {
		if (game.willAcceptAnotherPlayer()) {
			String team = teams.iterator().next();
			teams.remove(team);
			Logger.log(
					this,
					"Adding player " + playerID + " as player "
							+ game.getNumberOfPlayers() + " on team " + team);
			game.addPlayer(playerID, team);
			if (customUnits != null) {
				List<Unit> prevUnits = game.getPlayerWithName(playerID)
						.getUnits();
				for (int i = 0; i < prevUnits.size(); i++) {
					Unit old = prevUnits.get(i);
					Unit newU = customUnits.get(i);
					newU.setTeam(old.getTeam());
					newU.setLocation(old.getLocation());
					game.getMap().getTile(old.getLocation()).setUnit(newU);
					game.getAllUnits().remove(old);
					game.getAllUnits().add(newU);
				}
				game.getPlayerWithName(playerID).getUnits().clear();
				game.getPlayerWithName(playerID).getUnits().addAll(customUnits);
			}
		} else {
			Logger.log(this, "No more players allowed.");
		}
	}

	@Override
	public void run() {
		try {
			for (Player p : game.getPlayers()) {
				ObjectOutputStream o = outputs.get(p.getName());
				o.writeObject(game);
				Logger.log(this, "Sending Map from game " + game.getID()
						+ " to Player " + p.getName());
			}
		} catch (IOException e) {
			Logger.log(this, "Could not pass map to players");
			e.printStackTrace();
		}

		for (Player p : game.getPlayers()) {
			new Thread(new ServerGameListener(this, inputs.get(p.getName())))
					.start();
		}
	}

	public void disconnect(String playerID) {
		running = false;

		Logger.log(this, "Game end initiated by " + playerID);
		for (Player p : game.getPlayers()) {
			try {
				outputs.get(p.getName()).writeObject(
						new DisconnectMessage(game.getID(), "SERVER"));
				inputs.remove(p.getName()).close();
				outputs.remove(p.getName()).close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public WizardWar getGame() {
		return game;
	}

	public void distributeCommands(EndTurnMessage ets) {
		for (Player p : game.getPlayers()) {
			try {
				Logger.log(this,
						"Sending command-queue " + ets + " to " + p.getName());
				outputs.get(p.getName()).writeObject(ets);
			} catch (IOException e) {
				Logger.log(this, "Could not write command " + ets
						+ " to Player " + p.getName());
				e.printStackTrace();
			}
		}
	}

	public void sendChatMessage(String message) {
		for (Player p : game.getPlayers()) {
			try {
				Logger.log(this, "Sending message to " + p.getName());
				outputs.get(p.getName()).writeObject(message);
			} catch (IOException e) {
				Logger.log(this,
						"Could not write message to Player " + p.getName());
				e.printStackTrace();
			}
		}
	}

	public String getMapName() {
		return game.getMap().getName();
	}

	public long getGameID() {
		return game.getID();
	}

	public void updateGame(WizardWar game2) {
		game = game2;
	}
}
