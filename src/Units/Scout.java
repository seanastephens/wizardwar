package Units;

import Units.Unit.AnimationData;

public class Scout extends Unit {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4444199432824939220L;

	public Scout() {
		super();
		this.name = "Scout";
		this.combat = 1;
		this.defense = 1;
		this.movePoints = 5;
		this.health = 20;
		this.initialHealth = 20;
		this.speed = 5;
		this.range = 1;
		this.mana = 0;
		this.sprite_sheet = "FrioThiefCropped.png";
		this.standingAnimationData = new AnimationData(4, 0, 25, 50, 45);
		this.walkingAnimationData = new AnimationData(5, 200, 25, 65, 45);
		this.attackingAnimationData = new AnimationData(4, 525, 25, 70, 45);
		this.flip = true;
	}

}
