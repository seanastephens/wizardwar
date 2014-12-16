package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.Timer;

import map.Tile;
import map.TileMap;
import messaging.AttackCommand;
import messaging.Command;
import messaging.MoveUnitCommand;
import model.GameListener;
import model.Player;
import model.WizardWar;
import pathfinding.PathFinder;
import Items.Item;
import Units.InvisibleUnit;
import Units.Scout;
import Units.Unit;
import Units.UnitMode;

public class AnimationManager implements Serializable {

	private float[] FRIENDLY_COLOR_FILTER = { 1.0f, 1.5f, 1.0f, 1.0f };
	private float[] ENEMY_COLOR_FILTER = { 2.5f, 1.0f, 1.0f, 1.0f };
	private Color PENDING_MOVE_COLOR = new Color(1.0f, 0.5f, 0.0f, 0.5f);
	private Color PENDING_MOVE_ARROW_COLOR = new Color(1.0f, 0.5f, 0.0f, 1.0f);
	private Color PENDING_ATTACK_COLOR = new Color(1.0f, 0.0f, 0.0f, 0.5f);
	private float[] ATTACK_FILTER = { 1.0f, 0.5f, 0.0f, 1.0f };

	private int TILE_SIZE = MainPanel.TILE_SIZE;
	private int PENDING_MOVE_BOX_WIDTH = 8;
	private Tile lastHoveredTile;
	private WizardWar game;
	private TileMap map;
	private String playerID;
	private List<GameListener> listeners = new ArrayList<GameListener>();
	private Tile selectedTile;
	private InfoPopup popup;
	private List<Point> pathTiles = new ArrayList<Point>();

	public AnimationManager(WizardWar game, String playerID) {
		this.game = game;
		this.playerID = playerID;
		this.map = game.getMap();
		Unit temp = new Scout();
		temp.setLocation(new Point(0, 0));
		this.popup = new InfoPopup(temp);
		this.selectedTile = new Tile();
		new Timer(200, new ActionListener() {
			Random r = new Random();

			public void actionPerformed(ActionEvent arg0) {
				Unit activeUnit = AnimationManager.this.game.getActiveUnit();
				List<Unit> units = AnimationManager.this.game.getAllUnits();
				for (Unit u : units) {
					if (!u.equals(activeUnit) && r.nextInt(6) == 0) {
						UnitMode mode = u.getLastAnimationMode();
						if (mode == UnitMode.StandingRight) {
							u.updateAnimation(UnitMode.StandingRight);
						} else if (mode == UnitMode.StandingLeft) {
							u.updateAnimation(UnitMode.StandingLeft);
						}
					}
				}
			}
		}).start();
	}

	public void draw(Graphics2D g2, Point scroll) {
		for (int row = 0; row < map.getNumRows(); row++) {
			for (int col = 0; col < map.getNumCols(); col++) {
				Point tilePoint = new Point(col * TILE_SIZE, row * TILE_SIZE);
				tilePoint.translate(scroll.x, scroll.y);
				map.getTile(new Point(row, col)).draw(g2, tilePoint);
			}
		}

		List<Command> reversedCommandList = game.getMovesToMake();
		for (int i = 0; i < reversedCommandList.size(); i++) {
			Command c = reversedCommandList.get(i);
			if (c instanceof MoveUnitCommand) {
				MoveUnitCommand move = ((MoveUnitCommand) c);
				Point start = move.getSourcePoint();
				Point end = move.getDestinationPoint();

				/* Below here lies madness... */
				Point pixelStart = new Point(start.x * TILE_SIZE, start.y
						* TILE_SIZE);
				Point pixelEnd = new Point(end.x * TILE_SIZE, end.y * TILE_SIZE);
				pixelStart.translate(scroll.y, scroll.x);
				pixelEnd.translate(scroll.y, scroll.x);
				int startX = Math.min(pixelStart.y, pixelEnd.y);
				int startY = Math.min(pixelStart.x, pixelEnd.x);

				g2.setColor(PENDING_MOVE_COLOR);

				if (Math.abs(pixelStart.y - pixelEnd.y) == 0) {
					drawVerticalBox(g2, startX + TILE_SIZE / 2, startY
							+ TILE_SIZE / 2);
				} else {
					drawHorizontalBox(g2, startX + TILE_SIZE / 2, startY
							+ TILE_SIZE / 2);
				}

				Direction direction = getDirection(start, end);

				boolean isLastCommand = i == reversedCommandList.size() - 1;
				if (isLastCommand) {
					drawArrow(g2, startX + TILE_SIZE / 2, startY + TILE_SIZE
							/ 2, direction);
				} else {
					Unit thisUnit = c.getActingUnit(game);
					Unit nextUnit = reversedCommandList.get(i + 1)
							.getActingUnit(game);
					boolean isLastCommandForThisUnit = (thisUnit != nextUnit)
							|| !(reversedCommandList.get(i + 1) instanceof MoveUnitCommand);
					if (isLastCommandForThisUnit) {
						drawArrow(g2, startX + TILE_SIZE / 2, startY
								+ TILE_SIZE / 2, direction);
					}
				}
			} else if (c instanceof AttackCommand) {
				AttackCommand attack = (AttackCommand) c;
				Point start = attack.getSourcePoint();
				Point end = attack.getDestinationPoint();

				/* Below here lies madness... */
				Point pixelStart = new Point(start.x * TILE_SIZE, start.y
						* TILE_SIZE);
				Point pixelEnd = new Point(end.x * TILE_SIZE, end.y * TILE_SIZE);
				pixelStart.translate(scroll.y, scroll.x);
				pixelEnd.translate(scroll.y, scroll.x);
				g2.setColor(PENDING_ATTACK_COLOR);
				g2.setStroke(new BasicStroke(4.0f, BasicStroke.CAP_ROUND,
						BasicStroke.JOIN_BEVEL));
				g2.drawLine(pixelStart.y + TILE_SIZE / 2, pixelStart.x
						+ TILE_SIZE / 2, pixelEnd.y + TILE_SIZE / 2, pixelEnd.x
						+ TILE_SIZE / 2);

			}
		}

		Unit active = game.getActiveUnit();
		if (active != null) {
			int col = active.getLocation().y;
			int row = active.getLocation().x;
			Point tilePoint = new Point(col * TILE_SIZE, row * TILE_SIZE);
			tilePoint.translate(scroll.x, scroll.y);
			active.draw(g2, tilePoint);
		}
		Point popupTile = popup.getLocation();
		Point popupPixelPoint = new Point(popupTile.y * TILE_SIZE, popupTile.x
				* TILE_SIZE);
		popupPixelPoint.translate(scroll.x, scroll.y);
		popup.draw(g2, popupPixelPoint);
	}

	private enum Direction {
		UP, DOWN, LEFT, RIGHT
	}

	private Direction getDirection(Point start, Point end) {
		if (start.x > end.x) {
			return Direction.UP;
		} else if (start.x < end.x) {
			return Direction.DOWN;
		} else if (start.y > end.y) {
			return Direction.LEFT;
		} else {
			return Direction.RIGHT;
		}
	}

	private void drawHorizontalBox(Graphics2D g2, int startX, int startY) {
		g2.fillRect(startX - PENDING_MOVE_BOX_WIDTH, startY
				- PENDING_MOVE_BOX_WIDTH, TILE_SIZE + 2
				* PENDING_MOVE_BOX_WIDTH, 2 * PENDING_MOVE_BOX_WIDTH);
	}

	private void drawVerticalBox(Graphics2D g2, int startX, int startY) {
		g2.fillRect(startX - PENDING_MOVE_BOX_WIDTH, startY
				- PENDING_MOVE_BOX_WIDTH, 2 * PENDING_MOVE_BOX_WIDTH, TILE_SIZE
				+ 2 * PENDING_MOVE_BOX_WIDTH);
	}

	private void drawArrow(Graphics2D g2, int startX, int startY, Direction dir) {
		if (dir == Direction.RIGHT) {
			startX += TILE_SIZE;
		} else if (dir == Direction.DOWN) {
			startY += TILE_SIZE;
		}

		int[] xPoints = new int[3];
		int[] yPoints = new int[3];

		switch (dir) {
		case UP:
			xPoints[0] = startX - 2 * PENDING_MOVE_BOX_WIDTH;
			xPoints[1] = startX + 2 * PENDING_MOVE_BOX_WIDTH;
			xPoints[2] = startX;
			yPoints[0] = startY + PENDING_MOVE_BOX_WIDTH;
			yPoints[1] = startY + PENDING_MOVE_BOX_WIDTH;
			yPoints[2] = startY - 2 * PENDING_MOVE_BOX_WIDTH;
			break;
		case DOWN:
			xPoints[0] = startX - 2 * PENDING_MOVE_BOX_WIDTH;
			xPoints[1] = startX + 2 * PENDING_MOVE_BOX_WIDTH;
			xPoints[2] = startX;
			yPoints[0] = startY - PENDING_MOVE_BOX_WIDTH;
			yPoints[1] = startY - PENDING_MOVE_BOX_WIDTH;
			yPoints[2] = startY + 2 * PENDING_MOVE_BOX_WIDTH;
			break;
		case LEFT:
			xPoints[0] = startX + PENDING_MOVE_BOX_WIDTH;
			xPoints[1] = startX + PENDING_MOVE_BOX_WIDTH;
			xPoints[2] = startX - 2 * PENDING_MOVE_BOX_WIDTH;
			yPoints[0] = startY - 2 * PENDING_MOVE_BOX_WIDTH;
			yPoints[1] = startY + 2 * PENDING_MOVE_BOX_WIDTH;
			yPoints[2] = startY;
			break;
		case RIGHT:
			xPoints[0] = startX - PENDING_MOVE_BOX_WIDTH;
			xPoints[1] = startX - PENDING_MOVE_BOX_WIDTH;
			xPoints[2] = startX + 2 * PENDING_MOVE_BOX_WIDTH;
			yPoints[0] = startY - 2 * PENDING_MOVE_BOX_WIDTH;
			yPoints[1] = startY + 2 * PENDING_MOVE_BOX_WIDTH;
			yPoints[2] = startY;
			break;
		}
		Shape arrow = new Polygon(xPoints, yPoints, 3);
		Color save = g2.getColor();
		g2.setColor(PENDING_MOVE_ARROW_COLOR);
		g2.fill(arrow);
		g2.setColor(save);
	}

	public void click(Point tile, MouseEvent e) {
		String ourTeam = "";
		for (Player p : game.getPlayers()) {
			if (p.getName().equals(playerID)) {
				ourTeam = p.getTeam();
			}
		}

		selectedTile.setSelected(false);
		selectedTile = map.getTile(tile);
		clearPathTiles();

		if (selectedTile.getUnit() == null
				|| selectedTile.getUnit() instanceof InvisibleUnit) {
			return;
		}

		Unit unitOnSelectedTile = selectedTile.getUnit();
		if (game.getPlayerWithName(playerID).getUnits()
				.contains(unitOnSelectedTile)
				&& game.getCurrentTurn().getTeam().equals(ourTeam)) {
			selectedTile.setSelected(true);
			markPathTiles(unitOnSelectedTile);
		}
		popup.setUnit(unitOnSelectedTile);
		popup.fadeIn();
	}

	public void hoverOver(Point tile) {
		if (!map.getTile(tile).equals(lastHoveredTile)) {
			popup.fadeOut();
		}
		lastHoveredTile = map.getTile(tile);
		notifyListeners();
	}

	private void markPathTiles(Unit u) {
		PathFinder pathFinder = new PathFinder(map, u);
		List<Point> possibleMoves = pathFinder.getPossibleDestinations();
		possibleMoves.add(u.getLocation()); // highlight our tile
		for (Point p : possibleMoves) {
			float[] teamFilter = getTeamFilter(u);
			map.getTile(p).setImageFilter(teamFilter);
			pathTiles.add(p);
		}

		for (Point p : possibleMoves) {
			attackTiles.addAll(pathFinder.getInRangeTiles(p, u));
		}

		for (Point p : attackTiles) {
			if (!pathTiles.contains(p)
					&& (map.getTile(p).getMoveCost() != Double.POSITIVE_INFINITY || map
							.getTile(p).getUnit() != null)) {
				map.getTile(p).setImageFilter(ATTACK_FILTER);
			}
		}

	}

	private List<Point> attackTiles = new ArrayList<Point>();

	private float[] getTeamFilter(Unit u) {
		if (game.getPlayerWithName(playerID).getUnits().contains(u)) {
			return FRIENDLY_COLOR_FILTER;
		} else {
			return ENEMY_COLOR_FILTER;
		}
	}

	private void clearPathTiles() {
		for (Point p : pathTiles) {
			map.getTile(p).setImageBrightnessScaling(1.0f);
		}
		pathTiles.clear();
		for (Point p : attackTiles) {
			map.getTile(p).setImageBrightnessScaling(1.0f);
		}
		attackTiles.clear();
	}

	public void addListener(GameListener g) {
		listeners.add(g);
	}

	public void notifyListeners() {
		for (GameListener g : listeners) {
			g.gameChanged();
		}
	}

	private class InfoPopup implements ActionListener {

		private final int POPUP_WIDTH = 100;
		private final int POPUP_HEIGHT = 100;
		private final int BORDER_WIDTH = 10;
		private final int BAR_HEIGHT = 15;

		/* Text offset from left side of bar. */
		private final int HEALTH_TEXT_H_OFFSET = 20;
		/* Test offset from bottom of bar. */
		private final int HEALTH_TEXT_V_OFFSET = 4;

		private final int FADE_TIME_IN_MS = 300;
		private final float MAXIMUM_ALPHA = 1.0f;
		private final float IMAGE_SIZE_AS_FRACTION_OF_PANEL = 0.25f;
		private final int IMAGE_SIZE = (int) (IMAGE_SIZE_AS_FRACTION_OF_PANEL * POPUP_WIDTH);
		private final float BACKGROUND_ALPHA_FRACTION = 0.5f;
		private final int FIDE_TIC_LENGTH = FADE_TIME_IN_MS / 10;
		private final float ALPHA_CHANGE_RATE = 0.1f;

		private Font barTextFont = new Font("Helvetica", Font.PLAIN, 10);
		private Color transparentBlack = new Color(0.0f, 0.0f, 0.0f, 0.0f);
		private Color transparentRed = new Color(1.0f, 0.0f, 0.0f, 0.0f);
		private Color transparentGreen = new Color(0.0f, 1.0f, 0.0f, 0.0f);
		private Color transparentWhite = new Color(1.0f, 1.0f, 1.0f, 0.0f);

		private Unit unit;
		private Point tileCoords;
		private Timer alphaTimer = new Timer(FIDE_TIC_LENGTH, this);
		private float alpha;
		private boolean shouldBeVisible = false;

		public InfoPopup(Unit unit) {
			setUnit(unit);
			alphaTimer.start();
		}

		public void setUnit(Unit u) {
			this.unit = u;
			tileCoords = new Point(unit.getLocation().x - 1,
					unit.getLocation().y + 1);
		}

		public void draw(Graphics2D g2, Point p) {
			Color saveColor = g2.getColor();

			g2.setColor(transparentBlack);
			g2.fillRect(p.x, p.y, POPUP_WIDTH, POPUP_HEIGHT);

			double percentHealth = (unit.getHealth() * 1.)
					/ (1. * unit.getInitialHealth());
			int greenWidth = (int) (percentHealth * (POPUP_WIDTH - 2 * BORDER_WIDTH));
			int redWidth = (POPUP_WIDTH - 2 * BORDER_WIDTH) - greenWidth;
			g2.setColor(transparentRed);
			g2.fillRect(p.x + BORDER_WIDTH + greenWidth, p.y + BORDER_WIDTH,
					redWidth, BAR_HEIGHT);
			g2.setColor(transparentGreen);
			g2.fillRect(p.x + BORDER_WIDTH, p.y + BORDER_WIDTH, greenWidth,
					BAR_HEIGHT);

			g2.setFont(barTextFont);
			g2.setColor(transparentBlack);
			g2.drawString("HEALTH", p.x + BORDER_WIDTH + HEALTH_TEXT_H_OFFSET,
					p.y + BORDER_WIDTH + BAR_HEIGHT - HEALTH_TEXT_V_OFFSET);

			g2.setColor(transparentWhite);
			g2.drawString("ATK: " + unit.getCombat(), p.x + BORDER_WIDTH, p.y
					+ 2 * BORDER_WIDTH + 2 * BAR_HEIGHT);

			g2.drawString("DEF: " + unit.getDefense(), p.x + BORDER_WIDTH, p.y
					+ 3 * BORDER_WIDTH + 3 * BAR_HEIGHT);

			int startingY = POPUP_HEIGHT - BORDER_WIDTH;
			for (Item itemToDraw : unit.getItemsList()) {
				BufferedImage bimage = getScaledImageWithAlpha(itemToDraw);

				int xCoord = p.x + POPUP_WIDTH - IMAGE_SIZE - BORDER_WIDTH;
				int yCoord = p.y + startingY - IMAGE_SIZE;
				g2.drawImage(bimage, xCoord, yCoord, null);
				startingY -= IMAGE_SIZE;
			}

			g2.setColor(saveColor);
		}

		protected BufferedImage getScaledImageWithAlpha(Item itemToDraw) {
			float[] scaleValues = { alpha, alpha, alpha, alpha };
			float[] offsetValues = { 0.0f, 0.0f, 0.0f, 0.0f };

			Image item = itemToDraw.getImage();
			Image scaledItem = item
					.getScaledInstance(IMAGE_SIZE, IMAGE_SIZE, 0);
			RescaleOp r = new RescaleOp(alpha, 0.0f, null);
			r = new RescaleOp(scaleValues, offsetValues, null);
			BufferedImage bimage = new BufferedImage(scaledItem.getWidth(null),
					scaledItem.getHeight(null), BufferedImage.TYPE_INT_ARGB);
			Graphics2D bGr = bimage.createGraphics();
			bGr.drawImage(scaledItem, 0, 0, null);
			bGr.dispose();
			r.filter(bimage, bimage);
			return bimage;
		}

		public Point getLocation() {
			return tileCoords;
		}

		public void fadeOut() {
			shouldBeVisible = false;
		}

		public void fadeIn() {
			shouldBeVisible = true;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			adjustAlpha();
			transparentBlack = new Color(0.0f, 0.0f, 0.0f, alpha
					* BACKGROUND_ALPHA_FRACTION);
			transparentRed = new Color(1.0f, 0.0f, 0.0f, alpha);
			transparentGreen = new Color(0.0f, 1.0f, 0.0f, alpha);
			transparentWhite = new Color(1.0f, 1.0f, 1.0f, alpha);
			notifyListeners();
		}

		private void adjustAlpha() {
			if (shouldBeVisible) {
				alpha += ALPHA_CHANGE_RATE;
				alpha = Math.min(alpha, MAXIMUM_ALPHA);
			} else {
				alpha -= ALPHA_CHANGE_RATE;
				alpha = Math.max(alpha, 0);
			}
		}
	}
}
