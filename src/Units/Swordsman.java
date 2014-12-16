package Units;

import Units.Unit.AnimationData;

public class Swordsman extends Unit {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7193759313635852827L;

	public Swordsman() {
		super();
		this.name = "Swordsman";
		this.combat = 3;
		this.defense = 4;
		this.movePoints = 4;
		this.health = 80;
		this.initialHealth = 80;
		this.speed = 3;
		this.range = 1;
		this.mana = 0;
		this.sprite_sheet = "FrioSwordsmanCropped.png";
		this.standingAnimationData = new AnimationData(4, 0, 20, 50, 50);
		this.walkingAnimationData = new AnimationData(4, 200, 20, 50, 50);
		this.attackingAnimationData = new AnimationData(5, 400, 0, 75, 70);
	}
}
