package mapeditor;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import model.WizardWar;
import view.AnimationManager;
import view.MainPanel;

public class ThumbNailGenerator {

	private BufferedImage image;

	public ThumbNailGenerator(String mapName) {
		WizardWar wizardWar = new WizardWar(mapName, 0l);
		wizardWar.getMap().reloadImages();
		AnimationManager animationManager = new AnimationManager(wizardWar, "NOPE");
		int mapPixelWidth = wizardWar.getMap().getNumCols() * MainPanel.TILE_SIZE;
		int mapPixelHeight = wizardWar.getMap().getNumRows() * MainPanel.TILE_SIZE;
		BufferedImage bImage = new BufferedImage(mapPixelWidth, mapPixelHeight,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = bImage.createGraphics();
		animationManager.draw(g2, new Point(0, 0));
		g2.dispose();
		image = bImage;
	}

	public BufferedImage getScaledImage(int width, int height) {
		if (width < 1 || height < 1) {
			throw new IllegalArgumentException("You gave bad scaling dimensions: " + width + ", "
					+ height);
		}
		int yOffset = 0;
		int xOffset = 0;
		if (image.getWidth() > image.getHeight()) {
			double percentSmaller = (1. * image.getWidth() - 1. * image.getHeight())
					/ (1. * image.getWidth());
			yOffset = (int) ((percentSmaller * width) / 2);
		} else if (image.getWidth() < image.getHeight()) {
			double percentSmaller = (1. * image.getHeight() - 1. * image.getWidth())
					/ (1. * image.getHeight());
			xOffset = (int) ((percentSmaller * height) / 2);
		}
		BufferedImage bImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = bImage.createGraphics();
		g2.drawImage(image.getScaledInstance(width - 2 * xOffset, height - 2 * yOffset, 0),
				xOffset, yOffset, null);
		g2.dispose();

		return bImage;
	}

	public static void main(String[] args) {
		String mapName = "Canyon Catastrophe";

		ThumbNailGenerator thumbGen = new ThumbNailGenerator(mapName);
		BufferedImage thumbNail = thumbGen.getScaledImage(500, 500);
		try {
			ImageIO.write(thumbNail, "png", new File("thumbNails/" + mapName + ".png"));
		} catch (IOException e) {
			System.err.println("Could not write image " + mapName);
			e.printStackTrace();
		}
		System.out.println("DONE!");
		System.exit(0);
	}
}
