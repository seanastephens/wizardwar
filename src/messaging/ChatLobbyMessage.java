package messaging;

import java.io.Serializable;

public class ChatLobbyMessage implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5391424397114145991L;
	private String playerID;
	
	public ChatLobbyMessage(String playerID){
		this.playerID = playerID;
	}
	
	public String getPlayerID(){
		return playerID;
	}

}
