package AI;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import messaging.EndTurnMessage;
import messaging.MakeGameHandShake;
import model.WizardWar;
import server.Logger;
import view.IOListener;

public class AIStrategy implements Runnable {

	static long nextID = 0;

	String name = "__AI__" + nextID++;
	ObjectOutputStream output;
	ObjectInputStream input;
	WizardWar game;
	Socket s;

	public AIStrategy(String host, int port, String mapName) {
		try {
			s = new Socket(host, port);
			Logger.log(this, name + " Connected");
			output = new ObjectOutputStream(s.getOutputStream());
			input = new ObjectInputStream(s.getInputStream());
			Logger.log(this, name + " prepped");

			output.writeObject(new MakeGameHandShake(name, mapName, false, null));

		} catch (IOException e) {
			System.out.println("In AIStrategy init:");
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			game = (WizardWar) input.readObject();
		} catch (IOException | ClassNotFoundException e) {
			System.out.println("AIStrategy connection error:");
			e.printStackTrace();
		}

		new Thread(new IOListener(this, input, game, true)).start();
		AIDecisionMaker AIDM = AIDecisionMaker.getAIOfType(game.getGameType(), game, name);

		while (true) {
			try {
				waitForOurTurn();

				Logger.log(this, "Thinking...");
				Thread.sleep(100);

				EndTurnMessage ets = AIDM.getQueueOfActions();
				output.writeObject(ets);

				Logger.log(this, "sent messages back");

				waitForOurTurnToEnd();

			} catch (IOException | InterruptedException e) {
				System.out.println("In AIStrategy: ");
				e.printStackTrace();
			}
		}
	}

	private void waitForOurTurn() throws InterruptedException {
		while (!game.getCurrentTurn().getName().equals(name)) {
			Thread.sleep(25);
		}
	}

	private void waitForOurTurnToEnd() throws InterruptedException {
		while (game.getCurrentTurn().getName().equals(name)) {
			Thread.sleep(25);
		}
	}
}
