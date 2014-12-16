package map;

public class Hill extends Terrain {
	public Hill() {
		super();
		this.COST = 2;
		this.DEFENSE = 2;
		this.VISIBILITY = 1;
		this.name = "Hill";
		this.sprite_sheet = "hill.png";
	}
}
