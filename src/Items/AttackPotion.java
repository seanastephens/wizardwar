package Items;

import map.TileMap;
import Units.Unit;

public class AttackPotion extends Item {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2983135145678114848L;

	public AttackPotion() {
		super();
		this.Increase = 1;
		this.sprite_sheet = "AttackPotion.png";
		this.name = "Attack Potion";
	}

	public void use(Unit user, TileMap map) {
		if (user.getItemsList().contains(this) && user.canUseItem(this)) {
			user.setCombat(Increase + user.getCombat());
			user.removeItem(this);
		}
	}

}
