package view;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import messaging.DisconnectMessage;
import messaging.MakeGameHandShake;
import model.WizardWar;
import server.Logger;
import server.WizardServer;
import songplayer.SongPlayer;
import AI.AIStrategy;
import Units.Unit;

public class WizardGame extends JFrame {

	private static String TEMP_NAME = "You! ("
			+ Long.toString(System.currentTimeMillis()).substring(11)
			+ new Random().nextInt(100) + ")";

	public static String getPlayerID() {
		return TEMP_NAME;
	}
	
	public static void setPlayerID(String name) {
		TEMP_NAME = name;
	}

	public static final int SP_PORT = 9002;
	public static final int MP_PORT = 9001;
	private static final Dimension WINDOWED_DEFAULT_SIZE = new Dimension(900,
			720);

	public static int WINDOW_WIDTH;
	public static int WINDOW_HEIGHT;
	public static int MAIN_HEIGHT;
	public static int MAIN_WIDTH;
	public static int MENU_HEIGHT;
	public static int MENU_WIDTH;

	public static final boolean FULL_SCREEN = false;

	private Point mousePoint = new Point(400, 400);
	private Socket s = null;
	private ObjectOutputStream output = null;
	private ObjectInputStream input = null;
	private WizardWar game = null;
	private int port;
	public static String host = "localhost";

	private MainPanel main;
	private MenuPanel menu;
	private boolean startNewServer;
	private String mapChoice;
	private SongPlayer songPlayer;
	private boolean wantsToHost;
	private boolean isSinglePlayerGame = false;
	private List<Unit> finalUnits;

	public static void main(String[] args) {
		new WizardGame(true);
	}

	public boolean isSinglePlayerMode() {
		return isSinglePlayerGame;
	}

	public WizardGame(boolean b) {
		startNewServer = b;

		loadFont();

		initForFullOrWindowed();

		songPlayer = new SongPlayer();
		songPlayer.startLobbyMusic();

		int mode = getModeChoice();
		configureForMode(mode);
		getPlayerUnitCustomization(mapChoice);

		songPlayer.kill();

		ProgressManager pm = null;

		try {
			s = new Socket(host, port);
			Logger.log(this, "Connected");
			output = new ObjectOutputStream(s.getOutputStream());
			input = new ObjectInputStream(s.getInputStream());
			Logger.log(this, "Player prepped");

			MakeGameHandShake msg = null;
			if (mode == LobbySelectorWindow.SP_OPTION) {

				msg = new MakeGameHandShake(TEMP_NAME, mapChoice, true,
						finalUnits);
				output.writeObject(msg);
				AIStrategy ai = new AIStrategy(host, port, mapChoice);
				new Thread(ai).start();
			} else {
				msg = new MakeGameHandShake(TEMP_NAME, mapChoice, wantsToHost,
						finalUnits);
				output.writeObject(msg);
			}

			pm = new ProgressManager(output);
			Object o = input.readObject();
			if (o instanceof DisconnectMessage) {
				JOptionPane
						.showMessageDialog(
								this,
								"The host terminated the match. Please restart WizardWars and find a new match.");
				System.exit(1);
			}
			game = (WizardWar) o;
			game.getMap().reloadImages();
		} catch (EOFException eof) {
			JOptionPane.showMessageDialog(this,
					"No one else is online. Please try again later.");
			System.exit(1);

		} catch (IOException e) {
			System.out.println("connection error:");
			e.printStackTrace();
			System.exit(1);
		} catch (ClassNotFoundException e) {
			System.out.println("Uhoh");
			e.printStackTrace();
			System.exit(1);
		}

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addWindowListener(new ExitListener());
		// Also need to wrap the events that we get from here...
		addMouseMotionListener(new MouseAdapter() {
			public void mouseMoved(MouseEvent e) {
				WizardGame.this.mouseMoved(e);
			}
		});

		songPlayer.startGameMusic();

		setLayout(null);
		MAIN_HEIGHT = (3 * WINDOW_HEIGHT) / 4;
		MENU_HEIGHT = WINDOW_HEIGHT / 4;
		MAIN_WIDTH = WINDOW_WIDTH;
		MENU_WIDTH = WINDOW_WIDTH;

		main = new MainPanel(game, this);
		menu = new MenuPanel(game, input, output, this);

		game.addListener(main);
		game.addListener(menu);

		add(main);
		add(menu);

		main.setLocation(0, 0);
		menu.setLocation(0, MAIN_HEIGHT);
		main.setSize(MAIN_WIDTH, MAIN_HEIGHT);
		menu.setSize(MENU_WIDTH, MENU_HEIGHT);

		new Thread(new ScrollActor(this, main)).start();
		IOListener ioListener = new IOListener(this, input, game, false);
		ioListener.addChatListener(menu);
		new Thread(ioListener).start();
		pm.kill();
		pm.setVisible(false);
		pm.dispose();
		setVisible(true);

	}

	private void initForFullOrWindowed() {
		if (FULL_SCREEN) {
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			WINDOW_WIDTH = (int) screenSize.getWidth();
			WINDOW_HEIGHT = (int) screenSize.getHeight();
			setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
			setResizable(false);
			setUndecorated(true);
		} else {
			setSize(WINDOWED_DEFAULT_SIZE);
			WINDOW_WIDTH = WINDOWED_DEFAULT_SIZE.width;
			WINDOW_HEIGHT = WINDOWED_DEFAULT_SIZE.height;
		}
	}

	private int getModeChoice() {
		LobbySelectorWindow w = new LobbySelectorWindow();
		while (!w.hasChoice()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				System.out.println("oh shi-");
			}
		}
		int choice = w.getModeChoice();
		w.dispose();

		if (choice == LobbySelectorWindow.CANCEL_OPTION) {
			Logger.log(this, "Player stopped the game.");
			System.exit(0);
		}
		mapChoice = w.getMapChoice();
		wantsToHost = w.wantsToHost();
		return choice;
	}

	private void configureForMode(int choice) {
		if (choice == LobbySelectorWindow.SP_OPTION) {
			Logger.log(this, "Player started a single player game.");
			if (startNewServer) {
				new Thread(new WizardServer(SP_PORT)).start();
			}
			port = SP_PORT;
			host = "localhost";
			isSinglePlayerGame = true;
		} else {
			System.out.println("Player started a multi player game.");
			port = MP_PORT;
		}
	}

	private void getPlayerUnitCustomization(String mapName) {
		UnitSelectionWindow w = new UnitSelectionWindow(mapName,
				isSinglePlayerGame, wantsToHost);
		while (!w.isFinished()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				System.out.println("oh shi-");
			}
		}
		finalUnits = w.getFinalUnitList();
	}

	// Workaround for mouseMotion blocking problem.
	public Point getMousePoint() {
		return mousePoint;
	}

	public void mouseMoved(MouseEvent e) {
		mousePoint = e.getPoint();
	}

	private class ExitListener extends WindowAdapter {

		@Override
		public void windowClosing(WindowEvent arg0) {
			try {
				output.writeObject(new DisconnectMessage(game.getID(),
						TEMP_NAME));
			} catch (IOException e) {
				System.out.println("Failed to disconnect.");
				e.printStackTrace();
			}
		}
	}

	private void loadFont() {

		File rapscallionFont = new File("RAPSCALL.TTF");

		try {
			Font rapscallion = Font.createFont(Font.TRUETYPE_FONT, rapscallionFont);
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(rapscallion);
		} catch (IOException e) {
			System.out.println("Failed to load font");
		} catch (FontFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}
}
