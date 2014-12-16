package server;

import java.io.IOException;
import java.io.ObjectInputStream;

import messaging.DisconnectMessage;
import view.ChatPanel;

public class ServerLobbyListener implements Runnable {

	private ObjectInputStream input;
	private String playerID;
	private boolean isAlive = true;

	public ServerLobbyListener(ObjectInputStream input, String playerID) {
		this.input = input;
		this.playerID = playerID;
	}

	public void run() {
		while (isAlive) {
			try {
				Object o = input.readObject();
				Logger.log(this, "Received a " + o.getClass());
				if (o instanceof String) {
					String message = (String) o;
					if (message.equals(ChatPanel.DEATH)) {
						isAlive = false;
					} else if (message.equals(ChatPanel.GAME_UPDATE)) {
						WizardServer.getLobby().sendAvailableUpdateTo(playerID);
					} else {
						WizardServer.getLobby().sendMessageToLobbyChat(message,
								playerID);
					}
				}
			} catch (ClassNotFoundException cnfe) {
				Logger.log(this, "Problem: " + cnfe.getMessage());
			} catch (IOException e) {
				Logger.log(this, "Connection problem: " + e.getMessage());
			}
		}
		WizardServer.getLobby().removePlayerFromChatLobby(playerID);
	}
}
