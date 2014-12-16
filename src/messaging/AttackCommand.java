package messaging;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.Timer;

import model.Player;
import model.WizardWar;
import Units.Unit;
import Units.UnitMode;

public class AttackCommand extends Command {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3953467474085426420L;
	private String attacker;
	private String defender;
	private Point from;
	private Point to;
	private Unit theUnit;

	public AttackCommand(Unit attacker, Unit defender) {
		this.attacker = attacker.getClass().getSimpleName() + ":"
				+ attacker.getUID();
		this.defender = defender.getClass().getSimpleName() + ":"
				+ defender.getUID();
		this.from = attacker.getLocation();
		this.to = defender.getLocation();
		theUnit = attacker;
	}

	Timer timer;
	int i = 0;
	Unit atkUnit;
	Unit dfdUnit;
	WizardWar target;
	Player attackPlayer;
	Player defensePlayer;

	@Override
	public Unit getUnit() {
		return theUnit;
	}

	@Override
	public void execute(WizardWar aTarget) {
		this.target = aTarget;
		atkUnit = target.getUnit(attacker);
		dfdUnit = target.getUnit(defender);
		if (atkUnit == null || dfdUnit == null) {
			return;
		}

		if (dfdUnit.getHealth() > 0) {

			for (Player p : target.getPlayers()) {
				if (p.getUnits().contains(atkUnit)) {
					attackPlayer = p;
				} else if (p.getUnits().contains(dfdUnit)) {
					defensePlayer = p;
				}
			}

			new Timer(25, new ActionListener() {

				private float[] purple = { 4, 1, 1, 1 };
				int i = 0;
				Point uLoc = dfdUnit.getLocation();

				@Override
				public void actionPerformed(ActionEvent arg0) {
					if (i++ == 10) {
						((Timer) arg0.getSource()).stop();
					} else if (i % 2 == 1) {
						target.getMap().getTile(uLoc)
								.setImageFilter(purple);
					} else {
						target.getMap().getTile(uLoc)
								.setImageBrightnessScaling(1.0f);
					}
					target.notifyListeners();

				}
			}).start();

			timer = new Timer(30, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (i++ == 10) {
						atkUnit.attack(dfdUnit);
						atkUnit.setHasAttacked(false);
						if (dfdUnit.getHealth() <= 0) {
							attackPlayer.adjustScore(100);
							defensePlayer.adjustScore(-100);
						}
						AttackCommand.this.target.cleanUp();
						atkUnit.updateAnimation(UnitMode.StandingRight);
						timer.stop();
						return;
					}
					atkUnit.updateAnimation(UnitMode.AttackingRight);
				}
			});
			timer.start();

		}

	}

	@Override
	public String toString() {
		return "[COM] " + attacker + " is attacking " + defender;
	}

	@Override
	public Unit getActingUnit(WizardWar game) {
		return game.getUnit(attacker);
	}

	@Override
	public int getSafeCompletionTimeInMS() {
		return 400;
	}

	public Point getSourcePoint() {
		return from;
	}

	public Point getDestinationPoint() {
		return to;
	}

}
