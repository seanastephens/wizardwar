package map;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.Serializable;

import view.MainPanel;
import Items.Item;
import Units.Unit;

public class Tile implements Serializable {
	private Terrain type;
	private Unit unit_on_tile;
	private Item item_on_tile;
	private boolean selected = false;
	private float[] imageFilter = { 1.0f, 1.0f, 1.0f, 1.0f };
	private float[] imageOffset = { 1.0f, 1.0f, 1.0f, 1.0f };

	public Terrain getTerrain() {
		return this.type;
	}

	public Unit getUnit() {
		return this.unit_on_tile;
	}

	public Item getItem() {
		return this.item_on_tile;
	}

	public void setTerrain(Terrain t) {
		this.type = t;
	}

	public void setUnit(Unit u) {
		this.unit_on_tile = u;
	}

	public void setItem(Item i) {
		this.item_on_tile = i;
	}

	public void removeUnit() {
		this.unit_on_tile = null;
	}

	public void removeItem() {
		this.item_on_tile = null;
	}

	public Image getImage() {
		return type.getImage();
	}

	public void setSelected(boolean sel) {
		selected = sel;
	}

	public void setImageBrightnessScaling(float brightness) {
		float[] brightnessWithoutAlpha = { brightness, brightness, brightness, 1.0f };
		setImageFilter(brightnessWithoutAlpha);
	}

	public void setImageFilter(float[] multipliers) {
		imageFilter = multipliers.clone();
	}

	public double getMoveCost() {
		if (unit_on_tile != null) {
			return Double.POSITIVE_INFINITY;
		}
		return type.getMoveCost();
	}

	public void draw(Graphics2D g2, Point p) {

		if (usingDefaultFilter()) {
			g2.drawImage(type.getImage(), p.x, p.y, null);
		} else {
			BufferedImage bImage = type.getImage();

			// We still have to copy or else we over write the stored image in
			// the terrain.
			BufferedImage copyOfTerrainImage = new BufferedImage(bImage.getWidth(),
					bImage.getHeight(), BufferedImage.TYPE_INT_ARGB);

			Graphics2D copyGraphics = copyOfTerrainImage.createGraphics();
			copyGraphics.drawImage(bImage, 0, 0, null);
			copyGraphics.dispose();

			RescaleOp r = new RescaleOp(imageFilter, imageOffset, null);
			r.filter(copyOfTerrainImage, copyOfTerrainImage);

			g2.drawImage(copyOfTerrainImage, p.x, p.y, null);
		}
		/* Overlay the unit if we have one. */
		if (item_on_tile != null) {
			g2.drawImage(item_on_tile.getImage(), p.x, p.y, null);
		}

		if (unit_on_tile != null) {
			unit_on_tile.draw(g2,p);

			Color save = g2.getColor();
			Font saveFont = g2.getFont();

			g2.setColor(Color.RED);
			g2.setFont(new Font("Courier", Font.PLAIN, 16));
			g2.drawString(unit_on_tile.getTeam(), p.x, p.y + MainPanel.TILE_SIZE);

			g2.setColor(save);
			g2.setFont(saveFont);
		}

		/* Add a rectangle if we are selected. */
		if (selected) {
			Color saveColor = g2.getColor();
			g2.setColor(Color.BLUE);
			g2.drawRect(p.x, p.y, MainPanel.TILE_SIZE - 1, MainPanel.TILE_SIZE - 1);
			g2.drawRect(p.x + 1, p.y + 1, MainPanel.TILE_SIZE - 3, MainPanel.TILE_SIZE - 3);
			g2.setColor(saveColor);
		}
	}

	private boolean usingDefaultFilter() {
		boolean defaultFilter = true;
		defaultFilter &= imageFilter[0] == 1.0f;
		defaultFilter &= imageFilter[1] == 1.0f;
		defaultFilter &= imageFilter[2] == 1.0f;
		defaultFilter &= imageFilter[3] == 1.0f;
		return defaultFilter;
	}
}
