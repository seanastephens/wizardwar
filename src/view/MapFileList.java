package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.border.LineBorder;

import mapeditor.MapEditor;

public class MapFileList extends JList<String> {

	private String MAP_FILE_DIRECTORY = "map_files";
	private Color BACK_GROUND_COLOR = new Color(0.0f, 0.0f, 0.0f, 1.0f);
	private Color TEXT_COLOR = new Color(1.0f, 1.0f, 1.0f, 1.0f);

	protected List<String> mapStringList;
	private JButton mapEdit;
	private boolean shouldFilterTestMaps = true;
	private MapEditor theEdit;
	private boolean createdMap;

	public MapFileList() {
		mapEdit = new JButton("Solo: create your own map");
		mapEdit.addActionListener(new mapEditListener());
		mapEdit.setSize(250, 50);
		mapEdit.setLocation(0, 400);
		add(mapEdit);
		createdMap = false;
		loadFileList();
		setUpGraphicalProperties();
	}

	public MapFileList(boolean b) {
		shouldFilterTestMaps = false;
		loadFileList();
		setUpGraphicalProperties();
	}

	private void loadFileList() {
		File mapFolder = new File(MAP_FILE_DIRECTORY);
		File[] mapFiles = mapFolder.listFiles();
		mapStringList = new ArrayList<String>();
		for (File f : mapFiles) {
			if (isValidName(f.getName())) {
				mapStringList.add(f.getName());
			}
		}
		this.setListData(mapStringList.toArray(new String[mapStringList.size()]));
	}

	private boolean isValidName(String name) {
		if (name.equals("README")) {
			return false;
		} else if (name.contains("test") && shouldFilterTestMaps) {
			return false;
		}
		return true;
	}

	private void setUpGraphicalProperties() {
		setBackground(BACK_GROUND_COLOR);
		setForeground(TEXT_COLOR);
		setFont(new Font("Helvetica", Font.PLAIN, 20));
		setBorder(new LineBorder(Color.WHITE));
	}

	private class mapEditListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (createdMap == true) {
				loadFileList();
				mapEdit.setText("Single Player: create your own map");
			}

			else {
				theEdit = new MapEditor();
				theEdit.setVisible(true);
				mapEdit.setText("Load created map");
				createdMap = true;
			}

		}

	}
}