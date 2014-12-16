package wincondition;

import java.awt.Point;

import Items.Item;
import Units.Unit;
import model.Player;
import model.WizardWar;

public class CTFWinCondition extends WinCondition {

	@Override
	public boolean didWin(WizardWar game, Player p) {
		for (Player current : game.getListOfPlayers()) {
			if (current.getTeam().equals(p.getTeam())) {
				for (Unit u : current.getUnits()) {
					if (u.capturedFlag()) {
						if (u.getLocation().x == game.getLocationOfHomeBase().x) {
							if (u.getLocation().y == game
									.getLocationOfHomeBase().y) {
								return true;
							}
						}

					}
				}
			}
		}

		return false;
	}

	@Override
	public boolean didLose(WizardWar game, Player p) {
		for (Player current : game.getListOfPlayers()) {
			if (!current.getTeam().equals(p.getTeam())) {
				for (Unit u : current.getUnits()) {
					if (u.capturedFlag()) {
						if (u.getLocation().x == game.getLocationOfHomeBase().x) {
							if (u.getLocation().y == game
									.getLocationOfHomeBase().y) {
								return true;
							}
						}

					}
				}
			}

		}

		return false;
	}

	public String getDescription() {
		return "Capture the Flag - Grab the flag and take it to the base!";
	}
	public String toString() { return "CTF"; }
	
}
