package Items;

import map.TileMap;
import Units.Unit;

public class EnergyPotion extends Item {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6700617308663915255L;

	public EnergyPotion() {
		super();
		this.Increase = 1;
		this.sprite_sheet = "EnergyPotion.png";
		this.name = "Energy Potion";
	}

	public void use(Unit user, TileMap map) {
		if (user.getItemsList().contains(this) && user.canUseItem(this)) {
			user.setMovePoints(Increase + user.getMovePoints());
			user.removeItem(this);
		}
	}
}
