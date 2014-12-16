package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import Help.Help;
import mapeditor.MapEditor;
import mapeditor.MapGenerator;
import messaging.ChatMessage;
import messaging.DisconnectMessage;
import messaging.EndTurnMessage;
import model.GameListener;
import model.Player;
import model.WizardWar;

public class MenuPanel extends JPanel implements GameListener {
	private JTextPane theText;
	private WizardWar theGame;
	private JFrame help;
	private ObjectOutputStream output;
	private WizardGame thePanel;
	private Image background;
	private MapEditor theEdit;
	JButton endTurn;
	JButton saveGame;
	JTextPane text;
	JTextField enter;
	JScrollPane textWrapper;

	public MenuPanel(WizardWar game, ObjectInputStream input,
			ObjectOutputStream output, WizardGame panel) {
		this.setLayout(null);
		theText = new JTextPane();
		theText.setEditable(false);
		theText.setPreferredSize(new Dimension(200, WizardGame.MENU_HEIGHT - 20));
		this.output = output;
		thePanel = panel;
		theGame = game;
		setUpMenus();
		updateText();

		try {
			background = ImageIO.read(new File("images/Menu.png"))
					.getScaledInstance((int) (panel.getWidth() * 1.2),
							(int) (panel.getWidth() * .9), 0);
		} catch (IOException e) {
			System.out.println("Ruoh! image fail");
		}

		// Will mess with dimensions of text once i can see a working GUI
		// again...

		theText.setBackground(Color.BLACK);
		theText.setForeground(Color.WHITE);
		JScrollPane aPane = new JScrollPane(theText);
		aPane.setLocation(155, 0);
		aPane.setSize(140, 200);
		add(aPane, new Dimension(155, 0));

		JPanel chatWrapper = new JPanel();
		text = new JTextPane();
		text.setPreferredSize(new Dimension(300, WizardGame.MENU_HEIGHT - 80));
		text.setEditable(false);
		text.setBackground(Color.BLACK);
		text.setForeground(Color.WHITE);
		textWrapper = new JScrollPane(text);
		enter = new JTextField();
		enter.setPreferredSize(new Dimension(300, 30));
		enter.addActionListener(new ChatListener());

		chatWrapper.setLayout(new BorderLayout());
		chatWrapper.add(textWrapper, BorderLayout.NORTH);
		chatWrapper.add(enter, BorderLayout.SOUTH);
		chatWrapper.setSize(300, WizardGame.MENU_HEIGHT - 50);
		chatWrapper.setLocation(310, 0);
		add(chatWrapper, new Dimension(310, 0));

		repaint();
	}

	@Override
	public void paintComponent(Graphics g) {
		g.setColor(Color.GRAY);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.drawImage(background, (int) (thePanel.getWidth() * -.1), -40, null);
	}

	private void setUpMenus() {
		JButton quitGame = new JButton("Quit Game");
		quitGame.addActionListener(new quitGameListener());
		quitGame.setSize(150, 25);
		quitGame.setLocation(0, 30);

		if (thePanel.isSinglePlayerMode()) {
			saveGame = new JButton("Save Game");
			saveGame.addActionListener(new saveGameListener());
			saveGame.setSize(150, 25);
			saveGame.setLocation(0, 55);
			add(saveGame, new Dimension(0, 55));
		}

		JButton helpButton = new JButton("Help");
		helpButton.addActionListener(new HelpListener());
		helpButton.setSize(150, 25);
		helpButton.setLocation(0, 80);

		endTurn = new JButton("It is not your turn");
		endTurn.addActionListener(new endTurnListener());
		endTurn.setSize(150, 30);
		endTurn.setLocation(0, 105);

		add(quitGame, new Dimension(0, 30));
		add(helpButton, new Dimension(0, 80));
		add(endTurn, new Dimension(0, 105));

	}

	public void updateText() {
		String temp = "";

		Player client = theGame.getPlayerWithName(WizardGame.getPlayerID());

		if (client.isTurn()) {
			endTurn.setText("End your turn");
			endTurn.setBackground(Color.GREEN);
		}

		else if (!client.isTurn()) {
			endTurn.setBackground(Color.RED);
			endTurn.setText("It is not your turn");
		}

		temp = "Units: " + String.format("%10d", client.getUnits().size());
		temp += "\nItems: " + String.format("%9d", client.getItemAmount());
		temp += "\nScore: " + String.format("%9d", client.getScore());
		
		if (theGame.getGameType().equals("Zombies")) {
			temp += "\nTurn: " + String.format("%11d", theGame.getRound());
		}

		if (theGame.didWin(client) && theGame.isOver() == false && client.isTurn()) {
			JOptionPane.showMessageDialog(thePanel, "You won the game ");
			theGame.setIsOver(true);
		}

		if (theGame.didLose(client) && theGame.isOver() == false && client.isTurn()) {
			JOptionPane.showMessageDialog(thePanel, "You lost the game ");
			theGame.setIsOver(true);
		}
		
		theText.setText(temp);
		repaint();

	}

	@Override
	public void gameChanged() {
		updateText();
		if (theGame.getCurrentTurn().getName().equals(WizardGame.getPlayerID())) {
			endTurn.enableInputMethods(true);
		} else {
			endTurn.enableInputMethods(false);
		}
	}

	private class endTurnListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			if (theGame.isOver() == false) {
				Player client = theGame.getPlayerWithName(WizardGame
						.getPlayerID());
				if (client.isTurn()) {
					theGame.increaseRound(1);
					EndTurnMessage ets = new EndTurnMessage(theGame.getID(),
							WizardGame.getPlayerID(), theGame.getMovesToMake());
					try {
						output.writeObject(ets);
						theGame.clearAttackCommands();
					} catch (IOException f) {
					}

				}
			}

		}
	}

	private class quitGameListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				output.writeObject(new DisconnectMessage(theGame.getID(),
						WizardGame.getPlayerID()));
			} catch (IOException f) {
				System.out.println("Failed to disconnect.");
				f.printStackTrace();
			}
			System.exit(0);
		}
	}

	private class saveGameListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			MapGenerator gennie = new MapGenerator(theGame.getMap().getTiles(),
					2, theGame.getGameType());
			gennie.generate();
			String save_name = "";
			while (save_name.equals("") || save_name.contains(".sav")) {
				save_name = JOptionPane.showInputDialog(
						"Please enter a save name :", "untitled");
			}
			// Sanitize that input
			save_name = save_name.replaceAll("[^A-Za-z0-9]", "");
			gennie.saveMap(save_name + ".sav");

		}
	}

	private class ChatListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String message = enter.getText();
			if (message.length() == 0) {
				return;
			}
			String name = WizardGame.getPlayerID();
			try {
				output.writeObject(new ChatMessage(theGame.getID(), name,
						message));
			} catch (IOException ioe) {
				server.Logger.log(this, "Could not write chat message: "
						+ message);
			}
			enter.setText("");
		}
	}

	private class HelpListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			help = new Help();
			help.setVisible(true);

		}

	}

	public void addMessageToChatLog(String message) {
		text.setText(text.getText() + message);
	}
}
