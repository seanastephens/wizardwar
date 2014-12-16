package messaging;

import java.io.Serializable;

import server.ServerGame;

public abstract class Message implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2318609963741107545L;
	private long gameID;
	private String playerID;

	public Message(long gameID, String playerID) {
		this.gameID = gameID;
		this.playerID = playerID;
	}

	public abstract void execute(ServerGame target);

	public long getGameID() {
		return gameID;
	}

	public String getPlayerID() {
		return playerID;
	}

	public abstract String toString();

}
