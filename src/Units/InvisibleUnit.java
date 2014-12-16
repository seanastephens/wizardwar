package Units;

import java.awt.Graphics2D;
import java.awt.Point;

public class InvisibleUnit extends Unit {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3674665710245203850L;

	public InvisibleUnit() {
		this.team = "";
		this.name = "";
		this.combat = 0;
		this.defense = 0;
		this.movePoints = 0;
		this.health = 0;
		this.initialHealth = 0;
		this.speed = 0;
		this.sprite_sheet = "";
		this.range = 0;
		this.mana = 0;
	}
	
	@Override
	public void draw(Graphics2D g2, Point p) {
		return;
	}

}
