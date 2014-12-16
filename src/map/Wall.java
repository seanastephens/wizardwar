package map;

public class Wall extends Terrain {
	public Wall() {
		super();
		this.COST = Double.POSITIVE_INFINITY;
		this.DEFENSE = -1;
		this.VISIBILITY = 2;
		this.name = "Wall";
		this.sprite_sheet = "wall.jpg";
	}
}
