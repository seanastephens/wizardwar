package messaging;

import java.io.Serializable;
import java.util.List;

import server.WizardServer;
import Units.Unit;

public class MakeGameHandShake implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4345137619714616883L;
	private String mapName;
	private boolean hosting;
	private String playerID;
	private List<Unit> customUnits;

	public MakeGameHandShake(String playerID, String mapName, boolean hosting, List<Unit> customUnits) {
		this.playerID = playerID;
		this.mapName = mapName;
		this.hosting = hosting;
		this.customUnits = customUnits;
	}

	public void execute(WizardServer target) {
		if(hosting){
			target.addToLobbyAsHost(playerID, mapName, customUnits);
		} else {
			target.addToLobbyAsGuest(playerID, mapName, customUnits);
		}
	}

	@Override
	public String toString() {
		return "[MES] Player " + playerID + " asked for a game on map " + mapName;
	}

	public String getPlayerID() {
		return playerID;
	}
	
	public List<Unit> getCustomUnitList() {
		return customUnits;
	}
}
