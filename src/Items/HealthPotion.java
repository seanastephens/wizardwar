package Items;

import map.TileMap;
import Units.Unit;

public class HealthPotion extends Item {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6793899149619981696L;

	public HealthPotion() {
		super();
		this.Increase = 20;
		this.sprite_sheet = "HealthPotion.png";
		this.name = "Health Potion";
	}

	public void use(Unit user, TileMap map) {
		if (user.getItemsList().contains(this) && user.canUseItem(this)) {
			user.setHealth(Increase + user.getHealth());
			user.removeItem(this);
		}
	}
}
