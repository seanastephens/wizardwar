package Items;

import map.TileMap;
import Units.Unit;

public class ManaPotion extends Item {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4910994869501244861L;

	public ManaPotion() {
		super();
		this.Increase = 1;
		this.sprite_sheet = "ManaPotion.png";
		this.name = "Mana Potion";

	}

	public void use(Unit user, TileMap map) {
		if (user.getItemsList().contains(this) && user.canUseItem(this)) {
			user.setMana(Increase + user.getMana());
			user.removeItem(this);
		}
	}
}
