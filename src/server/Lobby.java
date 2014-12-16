package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import view.ChatPanel;
import Units.Unit;

public class Lobby {

	private List<ServerGame> pendingGames = new ArrayList<ServerGame>();
	private List<String> playersInLobby = new ArrayList<String>();
	private Map<String, ObjectInputStream> inputs;
	private Map<String, ObjectOutputStream> outputs;

	private static long nextID = 0;

	public Lobby(Map<String, ObjectInputStream> inputs,
			Map<String, ObjectOutputStream> outputs) {
		this.inputs = inputs;
		this.outputs = outputs;
	}

	public void addNewHost(String playerID, String mapName, List<Unit> customUnits) {
		Logger.log(this, "Starting a new game on " + mapName);
		ServerGame newGame = new ServerGame(inputs, outputs, mapName, nextID++);
		Logger.log(this,
				"Adding " + playerID + " to game " + newGame.getGameID());
		newGame.addPlayer(playerID,customUnits);
		pendingGames.add(newGame);
		startAndRemoveFullGames();
	}
	
	public void addPlayerToExistingGame(String playerID, String mapName, List<Unit> customUnits){
		for(ServerGame game : pendingGames){
			if(game.getMapName().equals(mapName) && game.hasRoomForAnotherPlayer()){
				game.addPlayer(playerID, customUnits);
			}
		}
		startAndRemoveFullGames();
	}

	private void startAndRemoveFullGames() {
		List<ServerGame> fullGames = new ArrayList<ServerGame>();
		for (ServerGame game : pendingGames) {
			if (!game.hasRoomForAnotherPlayer()) {
				new Thread(game).start();
				fullGames.add(game);
			}
			String playerCountLog = "Game " + game.getGameID() + " has "
					+ game.getNumberOfPlayers() + "/"
					+ game.getNumberOfPlayers() + " players";
			Logger.log(this, playerCountLog);
		}
		for (ServerGame game : fullGames) {
			pendingGames.remove(game);
		}
		sendAvailableGameUpdate(getAvailableGameString());
	}

	public void addPlayerToChatLobby(String playerID) {
		sendMessageToLobbyChat("connected", playerID);
		playersInLobby.add(playerID);
		new Thread(new ServerLobbyListener(inputs.get(playerID), playerID))
				.start();
		Logger.log(this, "Added " + playerID + " to lobby dist. list");
	}

	public void removePlayerFromChatLobby(String playerID) {
		playersInLobby.remove(playerID);
		Logger.log(this, "Removed " + playerID + " to lobby dist. list");
		sendMessageToLobbyChat("disconnected", playerID);
	}

	public void sendMessageToLobbyChat(String message, String playerID) {
		String augmentedMessage;
		if (message.equals("connected") || message.equals("disconnected")) {
			augmentedMessage = "[" + playerID + " " + message + "]";
		} else {
			augmentedMessage = "[" + playerID + "] " + message;
		}

		for (String name : playersInLobby) {
			try {
				Logger.log(this, "Writing to player " + name);
				outputs.get(name).writeObject(augmentedMessage);
			} catch (IOException e) {
				Logger.log(this, "Could not write to chat-lobby player " + name);
			}
		}
	}

	private String getAvailableGameString() {
		String ret = "";
		for (ServerGame game : pendingGames) {
			ret += game.getMapName() + " (" + game.getNumberOfPlayers() + "/"
					+ game.getMaxNumberOfPlayers() + ")";
			ret += ":";
		}
		return ret;
	}

	public void sendAvailableGameUpdate(String update) {
		for (String name : playersInLobby) {
			try {
				Logger.log(this, "Writing to player " + name);
				outputs.get(name).writeObject(ChatPanel.GAME_UPDATE + update);
			} catch (IOException e) {
				Logger.log(this, "Could not write to chat-lobby player " + name);
			}
		}
	}
	
	public void sendAvailableUpdateTo(String playerID){
		try {
			Logger.log(this, "Writing single-update to player " + playerID);
			outputs.get(playerID).writeObject(ChatPanel.GAME_UPDATE + getAvailableGameString());
		} catch (IOException e) {
			Logger.log(this, "Could not write single-update to chat-lobby player " + playerID);
		}
	}
}
