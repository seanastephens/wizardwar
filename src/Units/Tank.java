package Units;

import Units.Unit.AnimationData;

public class Tank extends Unit {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 6124601834650354504L;

	public Tank() {
		super();
		this.name = "Ogre";
		this.combat = 5;
		this.defense = 5;
		this.movePoints = 2;
		this.health = 100;
		this.initialHealth = 100;
		this.speed = 2;
		this.sprite_sheet = "OgreCropped.png";
		this.range = 1;
		this.mana = 0;
		this.standingAnimationData = new AnimationData(2, 0, 20, 60, 50);
		this.walkingAnimationData = new AnimationData(3, 58, 14, 60, 56);
		this.attackingAnimationData = new AnimationData(3, 300, 0, 90, 70);
	}

}
