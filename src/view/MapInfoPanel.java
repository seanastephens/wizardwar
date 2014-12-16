package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import Help.Help;
import map.TileMap;
import mapeditor.MapEditor;

public class MapInfoPanel extends JPanel implements ListSelectionListener,
		ChangeListener {

	private Map<String, String> mapInfos = new HashMap<String, String>();
	private JTextPane textArea;
	private MapEditor theEdit;

	public void init() {
		assert (getWidth() > 0);
		assert (getHeight() > 0);

		setLayout(null);
		textArea = new JTextPane();
		textArea.setSize(getWidth(), getHeight() - 60);
		textArea.setLocation(0, 0);
		textArea.setEditable(false);
		add(textArea);

	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		String mapName = ((JList<String>) e.getSource()).getSelectedValue();
		if (mapName != null) {
			loadDescription(mapName);
		}
	}

	private void loadDescription(String mapName) {

		if (!mapInfos.containsKey(mapName)) {
			mapInfos.put(mapName, getDescription(mapName));
		}

		String selectedDescription = mapInfos.get(mapName);

		if (selectedDescription != null) {
			textArea.setText(selectedDescription);
		}
	}

	private String getDescription(String mapName) {
		TileMap t = new TileMap(mapName);
		return mapName + " :\n" + "Players : " + t.getMaxNumberOfPlayers()
				+ "\nSize : " + t.getNumRows() + " x " + t.getNumCols() + "\n"
				+ t.getGameTypeDescription();
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		JTabbedPane pane = (JTabbedPane) e.getSource();
		JList<String> list = (JList<String>) pane.getSelectedComponent();
		String mapName = list.getSelectedValue();
		if (mapName != null) {
			loadDescription(mapName);
		}
	}

}