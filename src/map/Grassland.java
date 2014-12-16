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

public class Grassland extends Terrain {

	private static ArrayList<BufferedImage> mainTransitions;

	public Grassland() {
		super();
		this.COST = 1;
		this.DEFENSE = 1;
		this.VISIBILITY = 2;
		this.name = "Grassland";
		this.sprite_sheet = "grassland.jpg";
	}

	public static ArrayList<BufferedImage> loadTransitionImage(String folder) {
		mainTransitions = new ArrayList<BufferedImage>();

		File transitions = new File("images/MapImages/" + folder);
		File[] listTransitions = transitions.listFiles();
		List<File> files = new ArrayList<File>();
		for(File f : listTransitions){
			files.add(f);
		}
		Collections.sort(files, new Comparator<File>(){
			public int compare(File a, File b){
				return a.getPath().compareTo(b.getPath());
			}
		});
		
		for (int i = 0; i < listTransitions.length; i++) {
			String fullPath = files.get(i).getPath();
			try {
				File imageFile = new File(fullPath);
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
				mainTransitions.add(image);
			} catch (IOException e) {
				System.err
						.println("Could not load transition images for" + folder);
			}
		}
		return mainTransitions;
	}
}
