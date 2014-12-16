package map;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import javax.imageio.ImageIO;

import view.MainPanel;

public abstract class Terrain implements Serializable {

	private static String BASE_PATH = "images/MapImages/";

	protected double COST;
	protected int DEFENSE;
	protected int VISIBILITY;
	protected static transient Map<String, BufferedImage> images = new HashMap<String, BufferedImage>();
	protected String sprite_sheet;
	protected String name;
	protected BufferedImage finalImage;

	public double getMoveCost() {
		return COST;
	}

	public int getDefenseBonus() {
		return DEFENSE;
	}

	public BufferedImage getImage() {
		if (finalImage == null)
			return images.get(name);
		else
			return finalImage;
	}

	public void setImage(BufferedImage im) {
		images.put(name, im);
	}

	public void setModifiedImage(BufferedImage image) {
		finalImage = image;
	}

	public String getName() {
		return name;
	}

	public BufferedImage makeTransitionImage(String surroundingTiles,
			ArrayList<BufferedImage> transitionList) {
		BufferedImage transitionImage = new BufferedImage(MainPanel.TILE_SIZE,
				MainPanel.TILE_SIZE, BufferedImage.TYPE_INT_ARGB);
		Graphics2D imageGraphicsContext = transitionImage.createGraphics();

		for (int i = 0; i < 8; i++) {
			if (surroundingTiles.charAt(i) == '1')
				imageGraphicsContext.drawImage(transitionList.get(i), 0, 0,
						null);
		}
		imageGraphicsContext.dispose();
		return transitionImage;
	}

	public void loadImage() {
		// Don't reload this stuff... it takes a long time!
		if (images.containsKey(name)) {
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
			images.put(name, image);
		} catch (IOException e) {
			System.err.println("Could not load image for" + this.name);
		}
	}

	public static Terrain getNewTerrainOfType(String type) {
		switch (type) {
		case "Grassland":
			return new Grassland();
		case "Hill":
			return new Hill();
		case "Base":
			return new Homebase();
		case "Mountain":
			return new Mountain();
		case "Road":
			return new Road();
		case "Wall":
			return new Wall();
		case "Water":
			return new Water();
		default:
			throw new IllegalArgumentException("Gave an invalid Terrain type.");
		}
	}
}
