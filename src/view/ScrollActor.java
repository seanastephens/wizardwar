package view;

import java.awt.Point;

public class ScrollActor implements Runnable {

	private final int SCROLL_UPDATE_RATE_IN_MS = 25;
	private final int SCROLL_SPEED = 15;
	private final int JFRAME_TOP_OFFSET = 25;
	private final int SCROLL_MARGIN = 25;
	private int BOTTOM_SCROLL_MARGIN;

	private WizardGame game;
	private MainPanel panel;

	public ScrollActor(WizardGame game, MainPanel panel) {
		this.game = game;
		this.panel = panel;
		if (WizardGame.FULL_SCREEN) {
			BOTTOM_SCROLL_MARGIN = 3 * SCROLL_MARGIN;
		} else {
			BOTTOM_SCROLL_MARGIN = SCROLL_MARGIN;
		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(SCROLL_UPDATE_RATE_IN_MS);
			} catch (InterruptedException e) {
				System.out.println("WTF");
			}
			Point mouse = game.getMousePoint();
			int x = 0;
			int y = 0;
			if (mouse.x < SCROLL_MARGIN) {
				x += SCROLL_SPEED;
			}
			if (mouse.x > panel.getSize().width - SCROLL_MARGIN) {
				x -= SCROLL_SPEED;
			}
			if (mouse.y < SCROLL_MARGIN + JFRAME_TOP_OFFSET) {
				y += SCROLL_SPEED;
			}
			if (mouse.y > WizardGame.WINDOW_HEIGHT - BOTTOM_SCROLL_MARGIN) {
				y -= SCROLL_SPEED;
			}
			if (x != 0 || y != 0) {
				panel.applyScroll(new Point(x, y));
			}
		}
	}
}
