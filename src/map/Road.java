package map;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.imageio.ImageIO;

import view.MainPanel;

public class Road extends Terrain {
	private static ArrayList<BufferedImage> pathImages;
	
	public Road() {
		super();
		this.COST = .5;
		this.DEFENSE = 0;
		this.VISIBILITY = 3;
		this.name = "Road";
		this.sprite_sheet = "road.jpg";
	}

	public static BufferedImage loadTransitionImage(String neighbors){
		pathImages = new ArrayList<BufferedImage>();
		
			try {
				File imageFile = new File("images/MapImages/PathTransitions/" + neighbors + ".png");
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
						.println("Could not load transition images for path");
			}
		return null;
		
	}
}
