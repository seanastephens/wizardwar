package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import model.WizardWar;
import server.Logger;
import Units.Unit;

public class UnitSelectionWindow extends JFrame {

	private boolean finished = false;
	private String team;
	private String mapName;
	private List<Unit> defaultUnits = new ArrayList<Unit>();

	private boolean isSinglePlayerGame;
	private boolean isHost;
	private ButtonListener buttonListener;

	public UnitSelectionWindow(String mapName, boolean isSinglePlayerGame,
			boolean isHost) {
		this.mapName = mapName;
		this.isSinglePlayerGame = isSinglePlayerGame;
		this.isHost = isHost;
		loadUnits();

		setSize(600, 600);
		buttonListener = new ButtonListener();
		setLayout(null);
		
		addUnits();

		setVisible(true);
	}

	private void loadUnits() {
		WizardWar ww = new WizardWar(mapName, -1L);
		List<String> teams = new LinkedList<String>();

		if (isSinglePlayerGame || isHost) {
			// First p
			Logger.log(this, "SelectorWindow thinks this is a singleplayer game.");
			team = "A";
		} else {
			// Second p
			team = "B";
		}
		Logger.log(this, "SelecterWindow thinks we are team " + team);

		for (Unit u : ww.getAllUnits()) {
			if (u.getTeam().equals(team)) {
				defaultUnits.add(u);
			}
		}
	}

	private List<UnitButton> buttonList = new ArrayList<UnitButton>();

	private void addUnits() {
		JPanel wrapper = new JPanel();
		JPanel titlePanel = new JPanel();
		titlePanel.setPreferredSize(new Dimension(600, 100));
		JLabel title = new JLabel("<html>  Construct your team!<br>Choose your units</html>");
		title.setForeground(Color.YELLOW);
		title.setFont(new Font("Rapscallion", Font.PLAIN, 36));
		titlePanel.add(title);
		titlePanel.setBackground(Color.GRAY);
		wrapper.add(titlePanel);
		wrapper.setLocation(0,0);
		wrapper.setBackground(Color.GRAY);
		wrapper.setSize(new Dimension(600, 600));
		wrapper.setLayout(new FlowLayout());
		for (Unit u : defaultUnits) {
			u.loadImage();
			UnitButton button = new UnitButton(u);
			button.setPreferredSize(new Dimension(90, 90));
			button.setBackground(Color.BLACK);
			button.setIcon(new ImageIcon(u.getImage()));
			button.addActionListener(buttonListener);
			buttonList.add(button);
			button.setToolTipText(u.getCharacterName() + " - a " + u.getName());
			wrapper.add(button);
		}
		JPanel buttonWrapper = new JPanel();
		buttonWrapper.setPreferredSize(new Dimension(600, 100));
		buttonWrapper.setBackground(Color.GRAY);
		JButton doneButton = new JButton("Start the Battle!");
		doneButton.addActionListener(new DoneListener());
		doneButton.setFont(new Font("Rapscallion", Font.PLAIN, 36));
		buttonWrapper.add(doneButton);
		JButton quitButton = new JButton("Quit");
		quitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				System.exit(1);
			}
		});
		buttonWrapper.add(quitButton);
		wrapper.add(buttonWrapper);
		add(wrapper);
	}

	public boolean isFinished() {
		return finished;
	}

	public List<Unit> getFinalUnitList() {
		List<Unit> finalUnitList = new ArrayList<Unit>();
		for (UnitButton b : buttonList) {
			finalUnitList.add(b.unit);
		}
		return finalUnitList;
	}

	private class UnitButton extends JButton {
		public Unit unit;

		public UnitButton(Unit u) {
			unit = u;
		}
	}

	private class ButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent ae) {
			UnitButton button = (UnitButton) ae.getSource();
			String[] options = { "Archer", "Artillery", "Scout", "Swordsman",
					"Ogre", "Wizard" };
			int choice = JOptionPane.showOptionDialog(UnitSelectionWindow.this,
					"Replace " + button.unit.getCharacterName() +" with a:", "", JOptionPane.DEFAULT_OPTION,
					JOptionPane.PLAIN_MESSAGE, null, options, 0);
			if (choice == JOptionPane.CANCEL_OPTION
					|| choice == JOptionPane.CLOSED_OPTION) {
				return;
			}
			button.unit = Unit.getNewUnitOfType(options[choice]);
			button.unit.imageReset();
			button.unit.loadImage();
			button.setIcon(new ImageIcon(button.unit.getImage()));
		}
	}

	private class DoneListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			finished = true;
			setVisible(false);
			dispose();
		}
	}
}
