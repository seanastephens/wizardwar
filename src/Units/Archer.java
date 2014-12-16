package Units;

public class Archer extends Unit {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6860647918163026090L;

	public Archer() {
		super();
		this.name = "Archer";
		this.combat = 2;
		this.defense = 3;
		this.movePoints = 2;
		this.health = 40;
		this.initialHealth = 40;
		this.speed = 3;
		this.sprite_sheet = "FrioArcherCropped.png";
		this.range = 5;
		this.mana = 0;
		this.standingAnimationData = new AnimationData(4, 0, 0, 50, 50);
		this.walkingAnimationData = new AnimationData(4, 200, 0, 50, 50);
		this.attackingAnimationData = new AnimationData(5, 400, 0, 50, 50);
	}
}
