package Units;

import Units.Unit.AnimationData;

public class Wizard extends Unit {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3482532538763636649L;

	public Wizard() {
		super();
		this.name = "Wizard";
		this.combat = 2;
		this.defense = 3;
		this.movePoints = 3;
		this.health = 60;
		this.initialHealth = 60;
		this.speed = 3;
		this.sprite_sheet = "FrioWizardCropped.png";
		this.mana = 5;
		this.range = 3;
		this.standingAnimationData = new AnimationData(3, 0, 20, 50, 50);
		this.walkingAnimationData = new AnimationData(4, 150, 20, 50, 50);
		this.attackingAnimationData = new AnimationData(5, 300, 0, 70, 70);
	}
}
