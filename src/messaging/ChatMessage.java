package messaging;

import server.ServerGame;

public class ChatMessage extends Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4813037903005607179L;
	private String message;
	
	public ChatMessage(long gameID, String playerID, String message) {
		super(gameID, playerID);
		this.message = message;
	}
	
	@Override
	public void execute(ServerGame target) {
		String augmentedMessage = "[" + getPlayerID() + "] " + message + "\n"; 
		target.sendChatMessage(augmentedMessage);
	}

	@Override
	public String toString() {
		return "[MES] " + getPlayerID() + " in game " + getGameID() + " sent a chat message: " + message;
	}

}
