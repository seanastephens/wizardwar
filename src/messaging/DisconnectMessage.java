package messaging;

import server.ServerGame;

public class DisconnectMessage extends Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1169049258370075258L;

	public DisconnectMessage(long gameID, String playerID) {
		super(gameID, playerID);
	}

	public void execute(ServerGame target) {
		target.disconnect(getPlayerID());
	}

	@Override
	public String toString() {
		return "[MES] Player " + getPlayerID() + " in game " + getGameID() + " disconnected.";
	}
}
