package wincondition;

import model.Player;
import model.WizardWar;
import view.WizardGame;

public class DeathmatchWinCondition extends WinCondition {

	@Override
	public boolean didWin(WizardWar game, Player p) {
		for (Player c : game.getListOfPlayers()) {
			if (!c.getName().equals(p.getName())) {
				if (c.getUnits().size() == 0) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean didLose(WizardWar game, Player p) {
		if (p.getUnits().size() == 0) {
			return true;
		}

		return false;
	}
	
	public String getDescription() {
		return "Deathmatch - Eliminate all enemy units!";
	}
	
	public String toString() { return "Deathmatch"; }
}
