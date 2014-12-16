package map;

public class Mountain extends Terrain {
	public Mountain() {
		super();
		this.COST = Double.POSITIVE_INFINITY;
		this.DEFENSE = -1;
		this.VISIBILITY = 0;
		this.name = "Mountain";
		this.sprite_sheet = "mountain.jpg";
	}
}
