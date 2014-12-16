package messaging;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.Timer;

import model.WizardWar;
import server.Logger;
import server.ServerGame;

public class EndTurnMessage extends Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7939408306278778244L;
	private List<Command> commands;
	private WizardWar game;
	public EndTurnMessage(long gameID, String playerID, List<Command> commands) {
		super(gameID, playerID);
		this.commands = commands;

	}

	public void execute(ServerGame target) {
		game = target.getGame();

		for (Command c : commands) {
			c.execute(game);
			Logger.log(target, c.toString());
		}
		game.advancePlayerTurn();
		target.distributeCommands(this);
	}

	Timer t;

	public void executeOnClient( final WizardWar game) {
		executeActions(game);
	}

	public void executeOnAI(final WizardWar game) {
		executeActions(game);
		try {
			Thread.sleep(400);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void executeActions(final WizardWar game) {
		t = new Timer(325, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (commands.size() > 0) {
					commands.remove(0).execute(game);
				} else {
					t.stop();
					game.advancePlayerTurn();

				}
			}
		});
		t.start();
	}

	@Override
	public String toString() {
		return "[MES] " + getPlayerID() + " in game " + getGameID()
				+ " ended their turn.";
	}
}
