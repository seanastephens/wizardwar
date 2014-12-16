package server;

import java.awt.Point;
import java.io.ObjectInputStream;

import Units.Tank;
import Units.Unit;
import map.TileMap;
import messaging.Command;
import messaging.EndTurnMessage;
import messaging.Message;
import model.Player;
import model.WizardWar;

public class ServerGameListener implements Runnable{
	
	private ServerGame game;
	private ObjectInputStream input;
	private int unitID;
	private String opposingTeam;
	private int spawnNumber;
	
	public ServerGameListener(ServerGame s, ObjectInputStream o){
		game = s;
		input = o;
		spawnNumber = 1;
		unitID = 1000000000;
		opposingTeam = game.getGame().getAllUnits().get(0).getTeam();
	}
	
	public void spawnUnits() {
		WizardWar aGame = game.getGame();
		TileMap map = aGame.getMap();
		Unit aTank;
		for (int row = 0; row < spawnNumber; row++) {
			for (int col = 0; col < spawnNumber + 1; col++) {
				if (map.getTile(new Point(row, col)).getUnit() == null) {
					aTank = new Tank();
						aTank.loadImage();
					aTank.setUID(unitID++);
					aTank.setTeam(opposingTeam);
					map.getTile(new Point(row, col)).setUnit(aTank);
					aTank.setLocation(new Point(row, col));
					for (Player p : aGame.getListOfPlayers()) {
						if (opposingTeam
								.equals(p.getTeam()))
							p.addUnit(aTank);
					}
					
					aGame.addUnit(aTank);
					System.out.println("Added " + row + " " + col + " "
							+ aTank.getUID());

				}
			}
		}
       
		aGame.notifyListeners();

	}
	
	public void run() {
		while (true) {
			try {
				Object ob = input.readObject();
				if (ob instanceof Command) {
					Command com = (Command) ob;
					Logger.log(this, com.toString());
					com.execute(game.getGame());
				} else if (ob instanceof Message) {
					if (ob instanceof EndTurnMessage && game.getGame().getGameType().equals("Zombies")) {
						spawnUnits();
					}
					Message msg = (Message) ob;
					Logger.log(this, msg.toString());
					msg.execute(game);
				}
			} catch (Exception e) {
				Logger.log(this, "In Client Handler:");
				e.printStackTrace();
				break;
			}
		}
		Logger.log(this, "Game " + game.getGame().getID() + " ending.");
	}
}
