package wincondition;

import java.io.Serializable;

import model.Player;
import model.WizardWar;

public abstract class WinCondition implements Serializable {
	protected WizardWar theGame;

	public abstract boolean didWin(WizardWar game, Player p);

	public static WinCondition getWinConditionOfType(String type) {
		switch (type) {
		case "CTF":
			return new CTFWinCondition();
		case "DeathMatch":
			return new DeathmatchWinCondition();
		case "Zombies":
			return new ZombiesWinCondition();
		default:
			throw new IllegalArgumentException("Invalid game type String: "
					+ type);
		}
	}

	 public abstract boolean didLose(WizardWar game, Player client);
		
	 public abstract String getDescription();
}


