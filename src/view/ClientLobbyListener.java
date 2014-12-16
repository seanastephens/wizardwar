package view;

import java.io.IOException;
import java.io.ObjectInputStream;

import server.Logger;

public class ClientLobbyListener implements Runnable {

	private ObjectInputStream input;
	private ChatPanel chatPanel;
	private boolean isAlive = true;

	public ClientLobbyListener(ObjectInputStream i, ChatPanel c) {
		input = i;
		chatPanel = c;
	}

	public void run() {
		while (isAlive) {
			try {
				Object o = input.readObject();
				if (o instanceof String) {
					String message = (String) o;
					if (message.contains(ChatPanel.GAME_UPDATE)) {
						chatPanel.notifyAVGListeners(message
								.substring(ChatPanel.GAME_UPDATE.length()));
					} else {
						chatPanel.addText((String) o);
					}
				}
			} catch (ClassNotFoundException cnfe) {
				Logger.log(this, "Something bad was passed over the wire: "
						+ cnfe.getMessage());
			} catch (IOException e) {
				Logger.log(this, "IOException: " + e.getMessage());
			}
		}
	}

	public void kill() {
		isAlive = false;
	}

}
