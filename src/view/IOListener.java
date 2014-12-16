package view;

import java.awt.Point;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.swing.JOptionPane;

import Units.Tank;
import Units.Unit;
import map.TileMap;
import messaging.DisconnectMessage;
import messaging.EndTurnMessage;
import model.Player;
import model.WizardWar;
import server.Logger;
import server.ServerGame;

public class IOListener implements Runnable {

	private ObjectInputStream input;
	private WizardWar game;
	private Object source;
	private boolean isAI;
	private MenuPanel chatListener;
	private int unitID;
	private String opposingTeam;
	private int spawnNumber;

	public IOListener(Object source, ObjectInputStream input, WizardWar game,
			boolean isAI) {
		this.source = source;
		this.input = input;
		this.game = game;
		this.isAI = isAI;
		spawnNumber = 1;
		unitID = 1000000000;
		
		opposingTeam = "B";
	}

	public void addChatListener(MenuPanel m) {
		chatListener = m;
	}

	public void spawnUnits() {
		TileMap map = game.getMap();
		Unit aTank;
		for (int row = 0; row < spawnNumber; row++) {
			for (int col = 0; col < spawnNumber + 1; col++) {
				if (map.getTile(new Point(row, col)).getUnit() == null) {
					aTank = new Tank();
					if (!isAI) {
						aTank.loadImage();
					}
					aTank.setUID(unitID++);
					aTank.setTeam("B");
					map.getTile(new Point(row, col)).setUnit(aTank);
					aTank.setLocation(new Point(row, col));
					for (Player p : game.getListOfPlayers()) {
						if (opposingTeam.equals(p.getTeam()))
							p.addUnit(aTank);
					}

					game.addUnit(aTank);
					System.out.println("Added " + row + " " + col + " "
							+ aTank.getUID());

				}
			}
		}
		
		spawnNumber = 1;

		game.notifyListeners();

	}

	@Override
	public void run() {
		while (true) {
			try {
				Object ob = input.readObject();
				Logger.log(this, "IO in " + source.getClass().getSimpleName()
						+ " received " + ob.getClass().getSimpleName());
				if (ob instanceof EndTurnMessage) {
					if (game.getGameType().equals("Zombies")) {
						spawnUnits();
					}
					if (isAI) {
						((EndTurnMessage) ob).executeOnAI(game);
					} else {
						((EndTurnMessage) ob).executeOnClient(game);
					}

				} else if (ob instanceof String) {
					if (source.getClass().getSimpleName().equals("WizardGame")) {
						chatListener.addMessageToChatLog((String) ob);
					}
				} else if (ob instanceof DisconnectMessage) {
					Logger.log(this, "IOListener for "
							+ source.getClass().getSimpleName()
							+ " disconnecting.");
					if (chatListener != null) {
						JOptionPane.showMessageDialog(chatListener.getParent(),
								"Your opponent disconnected!");
					}
					System.exit(1);
				}
			} catch (IOException e) {
				System.err.println("IOException in IOListener: ");
				e.printStackTrace();
				break;
			} catch (ClassNotFoundException e) {
				System.err.println("UHOH: ");
				e.printStackTrace();
				break;
			}
		}
	}
}
