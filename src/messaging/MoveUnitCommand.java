package messaging;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import model.WizardWar;
import server.Logger;
import view.MainPanel;
import Units.Unit;
import Units.UnitMode;

public class MoveUnitCommand extends Command {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2281899776352479764L;

	private String unitID;

	private Point to;
	private Point from;
	private Unit actual;
	private Timer t;
	private WizardWar theGame;
	private Command theCommand;
	private int i;
	private Unit theUnit;

	public MoveUnitCommand(Unit u, Point to, Point from) {
		this.unitID = u.getClass().getSimpleName() + ":" + u.getUID();
		this.to = to;
		this.from = from;
		theCommand = this;
		theUnit = u;
	}

	@Override
	public Unit getUnit() {
		return theUnit;
	}

	@Override
	public void execute(WizardWar target) {
		this.actual = target.getUnit(unitID);
		if (actual == null) {
			return;
		}
		
		
		actual.loadImage();
		this.theGame = target;
		target.setActiveUnit(actual);
		i = 0;

		t = new Timer(30, new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				i++;
				actual.loadImage();
				try {
					if (to.x == from.x) {
						if (to.y > from.y) {
							actual.updateAnimation(UnitMode.WalkingRight);
							actual.adjust(MainPanel.TILE_SIZE / 10, 0);
						}

						else if (to.y < from.y) {
							actual.updateAnimation(UnitMode.WalkingLeft);
							actual.adjust(-1 * (MainPanel.TILE_SIZE / 10), 0);
						}
					}

					else if (to.y == from.y) {
						if (to.x > from.x) {
							actual.updateAnimation(UnitMode.WalkingUp);
							actual.adjust(0, MainPanel.TILE_SIZE / 10);
						}

						else if (to.x < from.x) {
							actual.updateAnimation(UnitMode.WalkingDown);
							actual.adjust(0, -1 * (MainPanel.TILE_SIZE / 10));
						}
					}

					if (i == 10) {
						if (to.y > from.y) {
							actual.updateAnimation(UnitMode.StandingRight);
						} else if (to.y < from.y) {
							actual.updateAnimation(UnitMode.StandingLeft);
						} else if (to.x > from.x) {
							actual.updateAnimation(UnitMode.StandingLeft);
						} else if (to.x < from.x) {
							actual.updateAnimation(UnitMode.StandingRight);
						}
						t.stop();
						actual.resetAdjustment();
						theGame.moveUnit(actual, to, theCommand);
						actual.setHasMoved(false);
					}
				} catch (Exception ex) {
					// I don;t know how to fix this.
					Logger.log(this, actual.getClass().toString()
							+ ex.getMessage());
					((Timer)e.getSource()).stop();
				}
			}
		});
		t.start();
	}

	public Point getSourcePoint() {
		return from;
	}

	public Point getDestinationPoint() {
		return to;
	}

	@Override
	public String toString() {
		return "[COM] " + unitID + " is moving to Tile " + to.toString();
	}

	@Override
	public Unit getActingUnit(WizardWar game) {
		return game.getUnit(unitID);
	}

	@Override
	public boolean equals(Object ob) {
		if (ob instanceof MoveUnitCommand) {
			boolean unitEq = ((MoveUnitCommand) ob).unitID.equals(unitID);
			boolean toEq = ((MoveUnitCommand) ob).to.equals(to);
			boolean fromEq = ((MoveUnitCommand) ob).from.equals(from);
			if (unitEq && toEq && fromEq) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int getSafeCompletionTimeInMS() {
		return 350;
	}
}
