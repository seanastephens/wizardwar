package server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import messaging.ChatLobbyMessage;
import messaging.MakeGameHandShake;
import view.WizardGame;
import Units.Unit;

public class WizardServer implements Runnable {

	private static final int MP_PORT = WizardGame.MP_PORT;
	private int port;

	private ServerSocket socket;
	private static Lobby lobby;

	private Map<String, ObjectInputStream> inputs;
	private Map<String, ObjectOutputStream> outputs;

	public static void main(String[] args) {
		new Thread(new WizardServer(MP_PORT)).start();
	}

	public void run() {
		try {
			socket = new ServerSocket(port);
			inputs = new ConcurrentHashMap<String, ObjectInputStream>();
			outputs = new ConcurrentHashMap<String, ObjectOutputStream>();
			Logger.log(this, "Server started on port " + port);

			lobby = new Lobby(inputs, outputs);

			while (true) {
				try {
					Logger.log(this, "Waiting for connection...");
					Socket s = socket.accept();

					ObjectOutputStream output = new ObjectOutputStream(
							s.getOutputStream());
					ObjectInputStream input = new ObjectInputStream(
							s.getInputStream());

					Object obj = input.readObject();
					if (obj instanceof MakeGameHandShake) {
						MakeGameHandShake newPlayerMessage = ((MakeGameHandShake) obj);
						inputs.put(newPlayerMessage.getPlayerID(), input);
						Logger.log(this,
								"Adding " + newPlayerMessage.getPlayerID()
										+ " to inputs.");
						outputs.put(newPlayerMessage.getPlayerID(), output);
						Logger.log(this,
								"Adding " + newPlayerMessage.getPlayerID()
										+ " to outputs.");
						Logger.log(this, newPlayerMessage.toString());
						newPlayerMessage.execute(WizardServer.this);
					} else if (obj instanceof ChatLobbyMessage) {
						ChatLobbyMessage chatMessage = (ChatLobbyMessage) obj;
						inputs.put(chatMessage.getPlayerID(), input);
						outputs.put(chatMessage.getPlayerID(), output);
						Logger.log(this, "Adding " + chatMessage.getPlayerID()
								+ " to chat lobby.");
						lobby.addPlayerToChatLobby(chatMessage.getPlayerID());
					} else {
						Logger.log(this,
								"Received an invalid object: " + obj.getClass());
					}
				} catch (Exception e) {
					Logger.log(this, "In Client Accepter:");
					e.printStackTrace();
					break;
				}
			}
		} catch (Exception e) {
			Logger.log(this, "Error creating server:");
			e.printStackTrace();
		}
	}

	public void addToLobbyAsHost(String playerID, String mapName, List<Unit> customUnits) {
		lobby.addNewHost(playerID, mapName,customUnits);
	}
	
	public void addToLobbyAsGuest(String playerID, String mapName, List<Unit> customUnits) {
		lobby.addPlayerToExistingGame(playerID,mapName,customUnits);
	}

	public static Lobby getLobby() {
		return lobby;
	}

	public WizardServer(int port) {
		this.port = port;
	}
}
