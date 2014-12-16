package view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import messaging.ChatLobbyMessage;
import server.Logger;

public class ChatPanel extends JPanel {

	public static String DEATH = "!@#$%^&*()a;shda;lkmxclwqhelka";
	public static String GAME_UPDATE = "smdo23yrf9cp8smd30984f6cx45@#$%^&DCV2joeids";

	private JTextPane textArea;
	private JTextField enterArea;
	private ObjectOutputStream output;
	private ClientLobbyListener listener;

	public ChatPanel() throws IOException {
		Socket s = new Socket(WizardGame.host, WizardGame.MP_PORT);
		ObjectInputStream input = new ObjectInputStream(s.getInputStream());
		output = new ObjectOutputStream(s.getOutputStream());
		output.writeObject(new ChatLobbyMessage(WizardGame.getPlayerID()));
		listener = new ClientLobbyListener(input, this);
		new Thread(listener).start();
	}

	public void init() {
		textArea = new JTextPane();
		textArea.setSize(getWidth(), getHeight() - 40);
		textArea.setLocation(0, 0);
		textArea.setForeground(Color.BLACK);

		enterArea = new JTextField();
		enterArea.setSize(getWidth(), 40);
		enterArea.setLocation(0, getHeight() - 40);
		enterArea.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (enterArea.getText().length() > 0) {
					try {
						output.writeObject(enterArea.getText());
					} catch (IOException ioe) {
						Logger.log(this, "IOException: " + ioe.getMessage());
					}
					enterArea.setText("");
				}
			}
		});

		setLayout(null);
		add(textArea);
		add(enterArea);
		repaint();
	}
	
	public void getAvailableGameUpdate(){
		try {
			output.writeObject(GAME_UPDATE);
		} catch (IOException e1) {
			Logger.log(this, "IO problem in ChatPanel initial game request: "
					+ e1.getMessage());
		}
	}

	public void addText(String message) {
		textArea.setText(textArea.getText() + message + "\n");
	}

	public void kill() {
		try {
			output.writeObject(DEATH);
		} catch (IOException e) {
			Logger.log(this, "Could not kill the client side lobby IO");
		}
		listener.kill();
	}

	private List<AVGListener> listeners = new ArrayList<AVGListener>();

	public void addAvailableGameListener(AVGListener a) {
		listeners.add(a);
	}

	public void notifyAVGListeners(String gameList) {
		for (AVGListener avg : listeners) {
			avg.updateGameList(gameList);
		}
	}

}
