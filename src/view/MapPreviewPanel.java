package view;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class MapPreviewPanel extends JPanel implements ChangeListener,
		ListSelectionListener {

	private Map<String, Image> thumbNails = new HashMap<String, Image>();
	private Image selectedImage;

	public void init() {
		assert (getWidth() > 0);
		assert (getHeight() > 0);

		for (File f : new File("thumbNails/").listFiles()) {
			try {
				Image image = ImageIO.read(f);
				image = image.getScaledInstance(getWidth(), getHeight(), 0);
				thumbNails.put(f.getName(), image);
			} catch (IOException e) {
				System.err.println();
				e.printStackTrace();
			}
		}
	}

	public void paintComponent(Graphics g) {
		if (selectedImage != null) {
			g.fillRect(0, 0, getWidth(), getHeight());
			g.drawImage(selectedImage, 0, 0, null);
		}
	}

	private void loadThumbNail(String mapName) {
		selectedImage = thumbNails.get(mapName + ".png");
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		JTabbedPane pane = (JTabbedPane) e.getSource();
		JList<String> list = (JList<String>) pane.getSelectedComponent();
		String mapName = list.getSelectedValue();
		if (mapName != null) {
			loadThumbNail(mapName);
		}
		repaint();
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		String selection = ((JList<String>) e.getSource()).getSelectedValue();
		if (selection != null) {
			loadThumbNail(selection);
			repaint();
		}
	}
}