package Items;

import map.TileMap;
import Units.Unit;

public class DefensePotion extends Item {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3993287230848156388L;

	public DefensePotion() {
		super();
		this.Increase = 1;
		this.sprite_sheet = "DefensePotion.png";
		this.name = "Defense Potion";
	}

	public void use(Unit user, TileMap map) {
		if (user.getItemsList().contains(this) && user.canUseItem(this)) {
			user.setDefense(Increase + user.getDefense());
			user.removeItem(this);
		}
	}

}
