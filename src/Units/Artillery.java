package Units;

import Units.Unit.AnimationData;

public class Artillery extends Unit {

	/**
	 * 
	 */
	private static final long serialVersionUID = -529289088408234571L;

	public Artillery() {
		this.combat = 4;
		this.defense = 1;
		this.movePoints = 1;
		this.health = 60;
		this.initialHealth = 60;
		this.speed = 1;
		this.range = 7;
		this.mana = 0;
		this.name = "Artillery";
		this.sprite_sheet = "Artillery.png";
		this.standingAnimationData = new AnimationData(1, 0, 0, 74, 74);
		this.walkingAnimationData = new AnimationData(3, 0, 0, 74, 74);
		this.attackingAnimationData = new AnimationData(3, 0, 0, 74, 74);
	}
}
