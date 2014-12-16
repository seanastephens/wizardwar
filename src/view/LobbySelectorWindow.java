package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.LineBorder;

import server.Logger;

public class LobbySelectorWindow extends JFrame {

	public static final int CANCEL_OPTION = -1;
	public static final int SP_OPTION = 0;
	public static final int MP_OPTION = 1;

	private final String BACKGROUND_IMAGE_FILE = "images/SplashScreen.png";
	private final Color BORDER_COLOR = Color.YELLOW;
	private final int PANEL_WIDTH = 600;
	private final int PANEL_HEIGHT = 600;
	private final int BORDER_WIDTH = 20;
	private final Dimension PANEL_SIZE = new Dimension(PANEL_WIDTH,
			PANEL_HEIGHT);
	private final int BUTTON_WIDTH = 150;
	private final int BUTTON_HEIGHT = 72;

	private boolean wantsToHost;
	private boolean hasReceivedAChoice;
	private int choice = 0;
	private String mapChoice = "default";
	private JButton singlePlayerButton;
	private JButton multiPlayerButton;
	private JButton cancelButton;
	private BackGroundPanel wrapper;

	public LobbySelectorWindow() {
		setSize(PANEL_SIZE);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int upperLeftCornerXCoord = screenSize.width / 2 - PANEL_WIDTH / 2;
		int upperLeftCornerYCoord = screenSize.height / 2 - PANEL_HEIGHT / 2;
		setLocation(upperLeftCornerXCoord, upperLeftCornerYCoord);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setUndecorated(true);
		setLayout(null);

		initializeChildren();
		buildMainMenu();

		setVisible(true);
	}

	public void initializeChildren() {
		wrapper = new BackGroundPanel();
		wrapper.setLocation(0, 0);
		wrapper.setSize(PANEL_SIZE);
		wrapper.setLayout(null);
		add(wrapper);

		singlePlayerButton = new JButton("Solo");
		singlePlayerButton.setFont(new Font("Rapscallion",Font.PLAIN,50));
		singlePlayerButton.setBackground(new Color(0xbf8006ff));
		singlePlayerButton.addActionListener(new SinglePlayerButtonListener());
		singlePlayerButton.setLocation(BORDER_WIDTH, PANEL_HEIGHT
				- BORDER_WIDTH - BUTTON_HEIGHT);
		singlePlayerButton.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);

		multiPlayerButton = new JButton("Multi");
		multiPlayerButton.setFont(new Font("Rapscallion",Font.PLAIN,50));
		multiPlayerButton.setBackground(new Color(0xbf8006));
		multiPlayerButton.addActionListener(new MultiPlayerButtonListener());
		multiPlayerButton.setLocation(BUTTON_WIDTH + 3 * BORDER_WIDTH,
				PANEL_HEIGHT - BORDER_WIDTH - BUTTON_HEIGHT);
		multiPlayerButton.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);

		cancelButton = new JButton("Exit");
		cancelButton.setFont(new Font("Rapscallion",Font.PLAIN,50));
		cancelButton.setBackground(new Color(0xbf8006));
		cancelButton.addActionListener(new CancelButtonListener());
		cancelButton.setLocation(2 * BUTTON_WIDTH + 5 * BORDER_WIDTH,
				PANEL_HEIGHT - BORDER_WIDTH - BUTTON_HEIGHT);
		cancelButton.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
//		Icon k = new ImageIcon("images/UnitImages/testicle.png");
//		cancelButton.setIcon(k);
	}

	public void buildMainMenu() {
		setSize(PANEL_WIDTH, PANEL_HEIGHT);
		wrapper.setSize(PANEL_WIDTH, PANEL_HEIGHT);
		hasReceivedAChoice = false;

		wrapper.add(singlePlayerButton);
		wrapper.add(multiPlayerButton);
		wrapper.add(cancelButton);
		wrapper.displayImage(true);

		wrapper.repaint();
	}

	public boolean hasChoice() {
		return hasReceivedAChoice;
	}

	public int getModeChoice() {
		return choice;
	}

	public String getMapChoice() {
		return mapChoice;
	}

	private class BackGroundPanel extends JPanel {

		private Image image;
		private boolean shouldDisplayImage = true;

		public BackGroundPanel() {
			try {
				Image rawImage = ImageIO.read(new File(BACKGROUND_IMAGE_FILE));
				image = rawImage
						.getScaledInstance(PANEL_WIDTH, PANEL_HEIGHT, 0);
			} catch (IOException e) {
				System.err
						.println("Could not load splash screen background image.");
			}
		}
		
		

		@Override
		public void paintComponent(Graphics g) {
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, getWidth(), getHeight());
			if (shouldDisplayImage) {
				g.drawImage(image, 0, 0, null);
			}
			g.setColor(BORDER_COLOR);
			g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
			paintChildren(g);
		}

		public void displayImage(boolean shouldDisplay) {
			shouldDisplayImage = shouldDisplay;
		}
	}

	private class SinglePlayerButtonListener implements ActionListener {

		private MapFileList mapFileList;
		private MapFileList saveFileList;
		private JTabbedPane tabPane;
		private MapPreviewPanel mapPreviewPanel;
		private MapInfoPanel mapInfoPanel;
		private JButton startButton;
		private JButton backButton;
		private JButton deleteButton;

		@Override
		public void actionPerformed(ActionEvent e) {
			choice = SP_OPTION;
			wrapper.removeAll();
			wrapper.displayImage(false);

			mapFileList = new NewGameFileList();
			saveFileList = new SaveFileList();

			tabPane = new JTabbedPane();
			tabPane.setLocation(BORDER_WIDTH, BORDER_WIDTH);
			tabPane.setSize(PANEL_WIDTH / 2 - 2 * BORDER_WIDTH, PANEL_HEIGHT
					- 6 * BORDER_WIDTH);
			tabPane.addTab("New Game", mapFileList);
			tabPane.addTab("Saved Games", saveFileList);
			wrapper.add(tabPane);

			mapPreviewPanel = new MapPreviewPanel();
			mapPreviewPanel.setLocation(PANEL_WIDTH / 2 + BORDER_WIDTH,
					BORDER_WIDTH);
			mapPreviewPanel.setSize(PANEL_WIDTH / 2 - 2 * BORDER_WIDTH,
					PANEL_HEIGHT / 2 - 2 * BORDER_WIDTH);
			mapPreviewPanel.init();
			wrapper.add(mapPreviewPanel);

			mapInfoPanel = new MapInfoPanel();
			mapInfoPanel.setLocation(PANEL_WIDTH / 2 + BORDER_WIDTH,
					PANEL_HEIGHT / 2 + BORDER_WIDTH);
			mapInfoPanel.setSize(PANEL_WIDTH / 2 - 2 * BORDER_WIDTH,
					PANEL_HEIGHT / 2 - 6 * BORDER_WIDTH);
			mapInfoPanel.init();
			wrapper.add(mapInfoPanel);

			saveFileList.addListSelectionListener(mapPreviewPanel);
			saveFileList.addListSelectionListener(mapInfoPanel);
			mapFileList.addListSelectionListener(mapPreviewPanel);
			mapFileList.addListSelectionListener(mapInfoPanel);
			tabPane.addChangeListener(mapPreviewPanel);
			tabPane.addChangeListener(mapInfoPanel);

			saveFileList.setSelectedIndex(0);
			mapFileList.setSelectedIndex(0);

			backButton = new JButton("Back");
			backButton.setFont(new Font("Rapscallion",Font.PLAIN,40));
			backButton.addActionListener(new BackButtonListener());
			backButton.setSize(PANEL_WIDTH / 4 - 2 * BORDER_WIDTH,
					3 * BORDER_WIDTH);
			backButton.setLocation(3 * (PANEL_WIDTH / 4) + BORDER_WIDTH,
					PANEL_HEIGHT - BORDER_WIDTH - backButton.getHeight());
			wrapper.add(backButton);

			startButton = new JButton("Start Game");
			startButton.setFont(new Font("Rapscallion",Font.PLAIN,40));
			startButton.addActionListener(new StartButtonListener());
			startButton.setSize(PANEL_WIDTH / 2 - 2 * BORDER_WIDTH,
					3 * BORDER_WIDTH);
			startButton.setLocation(BORDER_WIDTH, PANEL_HEIGHT - BORDER_WIDTH
					- startButton.getHeight());
			wrapper.add(startButton);

			deleteButton = new JButton("Delete");
			deleteButton.setFont(new Font("Rapscallion",Font.PLAIN,40));
			deleteButton.addActionListener(new DeleteButtonListener());
			deleteButton.setSize(PANEL_WIDTH / 4 - 2 * BORDER_WIDTH,
					3 * BORDER_WIDTH);
			deleteButton.setLocation(PANEL_WIDTH / 2 + BORDER_WIDTH,
					PANEL_HEIGHT - BORDER_WIDTH - startButton.getHeight());
			wrapper.add(deleteButton);

			wrapper.repaint();
		}

		private class StartButtonListener implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent e) {
				@SuppressWarnings("unchecked")
				JList<String> selected = (JList<String>) tabPane
						.getSelectedComponent();
				if (!selected.isSelectionEmpty()) {
					mapChoice = selected.getSelectedValue();
					hasReceivedAChoice = true;
					wantsToHost = false;
				}
			}
		}

		private class BackButtonListener implements ActionListener {

			@Override
			public void actionPerformed(ActionEvent e) {
				wrapper.removeAll();
				buildMainMenu();
			}
		}

		private class DeleteButtonListener implements ActionListener {

			@Override
			public void actionPerformed(ActionEvent e) {
				@SuppressWarnings("unchecked")
				JList<String> selectedComponent = (JList<String>) tabPane
						.getSelectedComponent();
				if (selectedComponent.isSelectionEmpty()) {
					return;
				}
				String selected = selectedComponent.getSelectedValue();
				
				if(!selected.contains(".sav")){
					return;
				}
				
				int decision = JOptionPane.showConfirmDialog(
						LobbySelectorWindow.this,
						"Are you sure you want to delete '" + selected + "'?");

				if (decision == JOptionPane.CANCEL_OPTION
						|| decision == JOptionPane.NO_OPTION) {
					return;
				}
				boolean deleted = true;
				deleted &= new File("map_files/" + selected).delete();
				Logger.log(this, "Deleted map_files/" + selected + ": "
						+ deleted);
				deleted &= new File("unit_files/" + selected).delete();
				Logger.log(this, "Deleted unit_files/" + selected + ": "
						+ deleted);
				deleted &= new File("item_files/" + selected).delete();
				Logger.log(this, "Deleted item_files/" + selected + ": "
						+ deleted);
				deleted &= new File("thumbNails/" + selected + ".png").delete();
				Logger.log(this, "Deleted thumbNails/" + selected + ": "
						+ deleted);
				if (!deleted) {
					JOptionPane.showMessageDialog(LobbySelectorWindow.this,
							"There was a problem deleting the file.");
				}
				tabPane.remove(1);
				saveFileList = new SaveFileList();
				tabPane.addTab("Saved Games", saveFileList);
			}
		}
	}

	private class MultiPlayerButtonListener implements ActionListener {
		private MapFileList mapFileList;
		private ChatPanel chatPanel;
		private JTabbedPane tabPane;
		private AvailableGameList availableGameList;
		private MapPreviewPanel mapPreviewPanel;
		private MapInfoPanel mapInfoPanel;
		private JButton startButton;
		private JButton backButton;

		@Override
		public void actionPerformed(ActionEvent e) {
			String name = JOptionPane.showInputDialog("Pick a user-name:");
			WizardGame.setPlayerID(name + new Random().nextInt(1000));
			try {
				chatPanel = new ChatPanel();
			} catch (IOException ioe) {
				JOptionPane.showMessageDialog(LobbySelectorWindow.this,
						"Could not connect to Multi-player server");
				ioe.printStackTrace();
				new BackButtonListener().actionPerformed(null);
				return;
			}

			chatPanel.setLocation(PANEL_WIDTH + BORDER_WIDTH, BORDER_WIDTH);
			chatPanel.setSize(PANEL_WIDTH / 2 - 2 * BORDER_WIDTH, PANEL_HEIGHT
					- 6 * BORDER_WIDTH);
			chatPanel.init();
			add(chatPanel);

			choice = MP_OPTION;
			wrapper.removeAll();
			wrapper.displayImage(false);

			LobbySelectorWindow.this.setSize(new Dimension(
					(wrapper.getWidth() * 3) / 2, wrapper.getHeight()));
			wrapper.setSize(new Dimension((wrapper.getWidth() * 3) / 2, wrapper
					.getHeight()));

			mapFileList = new StandardFileList();
			mapFileList.setSize(PANEL_WIDTH / 2 - 2 * BORDER_WIDTH,
					PANEL_HEIGHT - 6 * BORDER_WIDTH);

			availableGameList = new AvailableGameList();
			availableGameList.setSize(PANEL_WIDTH / 2 - 2 * BORDER_WIDTH,
					PANEL_HEIGHT - 6 * BORDER_WIDTH);

			tabPane = new JTabbedPane();
			tabPane.setLocation(BORDER_WIDTH, BORDER_WIDTH);
			tabPane.setSize(PANEL_WIDTH / 2 - 2 * BORDER_WIDTH, PANEL_HEIGHT
					- 6 * BORDER_WIDTH);
			tabPane.addTab("Host", mapFileList);
			tabPane.addTab("Join", availableGameList);
			wrapper.add(tabPane);

			chatPanel.addAvailableGameListener(availableGameList);
			chatPanel.getAvailableGameUpdate();

			mapPreviewPanel = new MapPreviewPanel();
			mapPreviewPanel.setLocation(PANEL_WIDTH / 2 + BORDER_WIDTH,
					BORDER_WIDTH);
			mapPreviewPanel.setSize(PANEL_WIDTH / 2 - 2 * BORDER_WIDTH,
					PANEL_HEIGHT / 2 - 2 * BORDER_WIDTH);
			mapPreviewPanel.init();
			wrapper.add(mapPreviewPanel);

			mapInfoPanel = new MapInfoPanel();
			mapInfoPanel.setLocation(PANEL_WIDTH / 2 + BORDER_WIDTH,
					PANEL_HEIGHT / 2 + BORDER_WIDTH);
			mapInfoPanel.setSize(PANEL_WIDTH / 2 - 2 * BORDER_WIDTH,
					PANEL_HEIGHT / 2 - 6 * BORDER_WIDTH);
			mapInfoPanel.init();
			wrapper.add(mapInfoPanel);

			mapFileList.addListSelectionListener(mapPreviewPanel);
			mapFileList.addListSelectionListener(mapInfoPanel);
			mapFileList.setSelectedIndex(0);

			backButton = new JButton("Back");
			backButton.addActionListener(new BackButtonListener());
			backButton.setSize(PANEL_WIDTH / 2 - 2 * BORDER_WIDTH,
					3 * BORDER_WIDTH);
			backButton.setLocation(PANEL_WIDTH / 2 + BORDER_WIDTH, PANEL_HEIGHT
					- BORDER_WIDTH - backButton.getHeight());
			wrapper.add(backButton);

			startButton = new JButton("Join Game");
			startButton.addActionListener(new StartButtonListener());
			startButton.setSize(PANEL_WIDTH / 2 - 2 * BORDER_WIDTH,
					3 * BORDER_WIDTH);
			startButton.setLocation(PANEL_WIDTH / 2 - startButton.getWidth()
					- BORDER_WIDTH,
					PANEL_HEIGHT - BORDER_WIDTH - startButton.getHeight());
			wrapper.add(startButton);

			wrapper.repaint();
		}

		private class StartButtonListener implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent e) {
				JList<String> selected = (JList<String>) tabPane
						.getSelectedComponent();
				if (!selected.isSelectionEmpty()) {
					chatPanel.kill();
					mapChoice = selected.getSelectedValue();
					if (mapChoice.contains("(")) {
						wantsToHost = false;
						// ( signifies we are joining a game from the hosted
						// game list
						mapChoice = mapChoice.substring(0,
								mapChoice.indexOf("(")).trim();
						Logger.log(this, "Selected : " + mapChoice);
					} else {
						wantsToHost = true;
					}
					hasReceivedAChoice = true;
				}
			}
		}

		private class BackButtonListener implements ActionListener {

			@Override
			public void actionPerformed(ActionEvent e) {
				wrapper.removeAll();
				if (chatPanel != null) {
					chatPanel.kill();
				}
				buildMainMenu();
			}
		}

		private class AvailableGameList extends JList<String> implements
				AVGListener {

			public AvailableGameList() {
				setBackground(Color.BLACK);
				setForeground(Color.WHITE);
				setFont(new Font("Helvetica", Font.PLAIN, 20));
				setBorder(new LineBorder(Color.WHITE));
			}

			@Override
			public void updateGameList(String gameList) {
				setListData(gameList.split(":"));
			}
		}
	}

	private class CancelButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			choice = CANCEL_OPTION;
			hasReceivedAChoice = true;
		}
	}

	public boolean wantsToHost() {
		return wantsToHost;
	}
}
