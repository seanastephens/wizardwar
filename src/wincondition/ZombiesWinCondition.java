package wincondition;

import model.Player;
import model.WizardWar;

public class ZombiesWinCondition extends WinCondition {

	@Override
	public boolean didWin(WizardWar game, Player p) {
		if (game.getRound() == 40)
			return true;
		else
			return false;
	}

	@Override
	public boolean didLose(WizardWar game, Player p) {
		if (p.getUnits().size() == 0) {
			return true;
		}

		return false;
	}
	
	public int unitsPerRound() {
		return 4;
	}
	
	public String getDescription() {
		return "Zombies - you know what to do. They are coming.";
	}
	public String toString() { return "Zombies"; }
}
