package view;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import map.Tile;
import model.GameListener;
import model.WizardWar;

public class MainPanel extends JPanel implements GameListener {

	/* TILE_SIZE MUST BE A MULTIPLE OF TEN */
	private static int tileSize = 6;
	public static final int TILE_SIZE = 10 * tileSize;

	private Tile[][] tiles;
	private WizardWar game;
	private WizardGame parentPanel;
	private int xScroll = 0;
	private int yScroll = 0;

	private int xScrollMax;
	private int yScrollMax;
	private int xScrollMin;
	private int yScrollMin;

	private AnimationManager animationManager;
	
	private UseMenu menu;

	public MainPanel(WizardWar game, WizardGame mainPanel) {
		
		this.tiles = game.getMap().getTiles();
		this.animationManager = new AnimationManager(game, WizardGame.getPlayerID());
		animationManager.addListener(this);
		this.game = game;
		this.parentPanel = mainPanel;
		xScrollMax = 0;
		yScrollMax = 0;
		xScrollMin = -1 * (tiles[0].length * TILE_SIZE - WizardGame.MAIN_WIDTH);
		yScrollMin = -1 * (tiles.length * TILE_SIZE - WizardGame.MAIN_HEIGHT);

		addMouseListener(new ClickListener());
		addMouseMotionListener(new ClickListener());
		menu = new UseMenu(this.game);
		repaint();
	}

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;

		g2.clearRect(0, 0, getSize().width, getSize().height);
		animationManager.draw(g2, new Point(xScroll, yScroll));
	}

	public void applyScroll(Point change) {
		xScroll += change.x;
		yScroll += change.y;
		checkScrollBounds();
		repaint();
	}

	private void checkScrollBounds() {
		xScroll = Math.min(xScroll, xScrollMax);
		yScroll = Math.min(yScroll, yScrollMax);
		xScroll = Math.max(xScroll, xScrollMin);
		yScroll = Math.max(yScroll, yScrollMin);
	}

	public Point tilePointFromPixelPoint(Point p) {
		int x = (p.x - xScroll) / TILE_SIZE;
		int y = (p.y - yScroll) / TILE_SIZE;
		// This is reversed because of some map array crap.
		return new Point(y, x);
	}

	private class ClickListener extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
			if(SwingUtilities.isRightMouseButton(e)) {
				Point tile = tilePointFromPixelPoint(e.getPoint());
				//System.out.println(game.isYourUnit(tile, WizardGame.getPlayerID()));
				if(game.getMap().getTile(tile).getUnit()!= null && game.isYourUnit(tile, WizardGame.getPlayerID())) {
					menu.clearMenu();
					menu.setUnit(game.getMap().getTile(tile).getUnit());
					menu.setUpMenu();
					menu.show(e.getComponent(), e.getX(), e.getY());
				}			 
			}
			else { 
				Point tile = tilePointFromPixelPoint(e.getPoint());
				game.click(tile, WizardGame.getPlayerID());
				animationManager.click(tile, e);
			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			// When we have a mouse listener in a child component, it blocks
			// mouse messages to the parent. This is a work around to pass
			// mouseEvents back up the chain.
			parentPanel.mouseMoved(e);
			Point tile = tilePointFromPixelPoint(e.getPoint());
			animationManager.hoverOver(tile);
		}
	}

	@Override
	public void gameChanged() {
		repaint();
	}

}
