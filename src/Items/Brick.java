package Items;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import map.TileMap;
import map.Wall;
import Units.Unit;

public class Brick extends Item{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1830727193772552403L;

	public Brick() {
		super();
		this.sprite_sheet = "Brick.png";
		this.name = "Brick";
	}

	public void use(Unit user, TileMap map) {
		if (user.getItemsList().contains(this) && user.canUseItem(this)) {
			map.getTile(user.getLocation()).setTerrain(new Wall());
			user.removeItem(this);
		}
	}
}