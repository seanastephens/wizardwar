package Items;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.awt.Image;

import javax.imageio.ImageIO;

import map.TileMap;
import view.MainPanel;
import Units.Unit;

public class Flag extends Item {

	private String teamOwner;
	private static List<BufferedImage> flagAnimation;
	private BufferedImage image;

	public Flag() {
		super();
		this.sprite_sheet = "Flag.png";
		this.name = "Flag";
	}

	public void setTeam(String team) {
		teamOwner = team;
	}

	public String getTeam() {
		return teamOwner;
	}

	@Override
	public void use(Unit user, TileMap map) {
		// TODO Auto-generated method stub

	}

//	@Override
//	public void loadImage() {
//		if (flagAnimation != null) {
//			return;
//		}
//		flagAnimation = new ArrayList<BufferedImage>();
//		int width = 0, height = 0, x = 0, y = 0;
//		try {
//			String fullPath = BASE_PATH + this.sprite_sheet;
//			File spriteSheetFile = new File(fullPath);
//			BufferedImage spriteSheet = ImageIO.read(spriteSheetFile);
//
//			for (int i = 0; i < 10; i++) {
//				width = 108;
//				height = 100;
//				Image initialImage = spriteSheet.getSubimage(x, y
//						+ (height * i), width, height);
//				Image scaledImage = initialImage.getScaledInstance(
//						MainPanel.TILE_SIZE, MainPanel.TILE_SIZE, 0);
//				image = new BufferedImage(scaledImage.getWidth(null),
//						scaledImage.getHeight(null),
//						BufferedImage.TYPE_INT_ARGB);
//				Graphics2D imageGraphicsContext = image.createGraphics();
//				imageGraphicsContext.dispose();
//				flagAnimation.add(image);
//			}
//		} catch (IOException e) {
//			System.err.println("Could not load flag image");
//			e.printStackTrace();
//		}
//	}

	private int count = 0;

	protected Point adjust;

	public void resetAdjustment() {
		adjust = new Point(0, 0);
	}

	public void adjust(int x, int y) {
		adjust.translate(x, y);
	}

	public void draw(Graphics2D g2, Point p) {
		g2.drawImage(image, p.x + adjust.x, p.y + adjust.y, null);

	}
}