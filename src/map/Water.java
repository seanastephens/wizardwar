package map;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import view.MainPanel;

public class Water extends Terrain{
	
	private static ArrayList<BufferedImage> waterImages;
	public Water(){
		super();
		this.COST = Double.POSITIVE_INFINITY;
		this.DEFENSE = -1;
		this.VISIBILITY = 0;
		this.name = "Water";
		this.sprite_sheet = "water.png";
	}

	public static BufferedImage loadTransitionImage(String neighbors) {
		waterImages = new ArrayList<BufferedImage>();
		
		try {
			File imageFile = new File("images/MapImages/WaterTransitions/" + neighbors + ".png");
			Image initialImage = ImageIO.read(imageFile);
			Image scaledImage = initialImage.getScaledInstance(
					MainPanel.TILE_SIZE, MainPanel.TILE_SIZE, 0);

			BufferedImage image = new BufferedImage(
					scaledImage.getWidth(null),
					scaledImage.getHeight(null),
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D imageGraphicsContext = image.createGraphics();
			imageGraphicsContext.drawImage(scaledImage, 0, 0, null);
			imageGraphicsContext.dispose();
			return image;
		} catch (IOException e) {
			System.err
					.println("Could not load transition images for water");
		}
	return null;
	}

}
