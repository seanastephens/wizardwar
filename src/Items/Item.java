package Items;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import map.TileMap;
import view.MainPanel;
import Units.Unit;

public abstract class Item implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4122514816417742230L;

	protected static final String BASE_PATH = "images/ItemImages/";

	protected String sprite_sheet;
	protected String name;
	protected static transient Map<String, BufferedImage> images = new HashMap<>();
	protected int Increase;
	protected Point location;

	private int IID;
	private static int nextIID = 0;

	public Item() {
		IID = nextIID++;
	}

	public abstract void use(Unit user, TileMap map);

	public void loadImage() {
		if(images.containsKey(getClass().getSimpleName())){
			return;
		}
		try {
			String fullPath = BASE_PATH + this.sprite_sheet;
			File imageFile = new File(fullPath);
			Image initialImage = ImageIO.read(imageFile);
			Image scaledImage = initialImage.getScaledInstance(
					MainPanel.TILE_SIZE, MainPanel.TILE_SIZE, 0);

			BufferedImage image = new BufferedImage(scaledImage.getWidth(null),
					scaledImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
			Graphics2D imageGraphicsContext = image.createGraphics();
			imageGraphicsContext.drawImage(scaledImage, 0, 0, null);
			imageGraphicsContext.dispose();
			images.put(getClass().getSimpleName(), image);

		} catch (IOException e) {
			System.err.println("Could not load image for" + this.name);
		}
	}

	public BufferedImage getImage() {
		return images.get(getClass().getSimpleName());
	}

	public void setImage(BufferedImage img) {
		images.put(getClass().getSimpleName(), img);
	}

	public int getUID() {
		return IID;
	}

	public String getName() {
		return name;
	}

	public void setLocation(Point p) {
		location = p;
	}

	public Point getLocation() {
		return location;
	}

	public static Item getNewItemOfType(String type) {
		switch (type) {
		case "AttackPotion":
			return new AttackPotion();
		case "DefensePotion":
			return new DefensePotion();
		case "EnergyPotion":
			return new EnergyPotion();
		case "Flag":
			return new Flag();
		case "HealthPotion":
			return new HealthPotion();
		case "ManaPotion":
			return new ManaPotion();
		case "Trap":
			return new Trap();
		case "Brick":
			return new Brick();
		default:
			throw new IllegalArgumentException("Not a valid item class name");
		}
	}
}
