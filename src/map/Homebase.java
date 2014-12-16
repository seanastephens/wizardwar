package map;

public class Homebase extends Terrain {
	private String team;
	
	public Homebase() {
		super();
		this.COST = 1;
		this.DEFENSE = 3;
		this.VISIBILITY = 3;
		this.name = "Homebase";
		this.sprite_sheet = "homebase.jpg";
	}

}
