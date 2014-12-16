package Items;

import map.TileMap;
import Units.Unit;

public class Trap extends Item {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6964189078782200964L;

	public Trap() {
		super();
		this.sprite_sheet = "BladeTrap1.png";
		this.name = "Trap";
	}

	public void use(Unit user, TileMap map) {
		user.setHealth(0);
	}
}


