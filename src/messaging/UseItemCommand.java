package messaging;

import java.awt.Point;

import model.WizardWar;
import Items.Item;
import Units.Unit;

public class UseItemCommand extends Command {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7644458368440327914L;
	private String unitID;
	private String itemID;
	private Unit theUnit;

	public UseItemCommand(Unit user, Item item) {
		this.unitID = user.getClass().getSimpleName() + ":" + user.getUID();
		this.itemID = item.getClass().getSimpleName() + ":" + item.getUID();
		theUnit = user;
	}

	public void execute(WizardWar target) {
		Unit actualUnit = target.getUnit(unitID);
		Item actualItem = target.getItem(itemID);
		actualUnit.useItem(actualItem, target);
	}
	
	@Override
	public Unit getUnit() {
		return theUnit;
	}

	@Override
	public String toString() {
		return "[COM] " + unitID + " is using " + itemID;
	}

	@Override
	public Unit getActingUnit(WizardWar game) {
		return game.getUnit(unitID);
	}
	
	public Point getSourcePoint() {
		return new Point(-1,-1);
	}
	
	public Point getDestinationPoint() {
		return new Point(-1,-1);
	}

	@Override
	public int getSafeCompletionTimeInMS() {
		return 100;
	}
}
