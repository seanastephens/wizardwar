package messaging;

import java.awt.Point;
import java.io.Serializable;

import model.WizardWar;
import Units.Unit;

public abstract class Command implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3605012042969805626L;

	public abstract void execute(WizardWar game);

	public abstract String toString();

	public abstract int getSafeCompletionTimeInMS();

	public abstract Unit getActingUnit(WizardWar game);
	
	public abstract Point getSourcePoint();

	public abstract Point getDestinationPoint();
	
	public abstract Unit getUnit();

}
