package Help;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import Units.Archer;
import Units.Artillery;
import Units.Scout;
import Units.Swordsman;
import Units.Tank;
import Units.Wizard;

public class InfoPanel extends JPanel {
	private Image scout;
	private Image tank;
	private Image archer;
	private Image swordsman;
	private Image wizard;
	private Image artillery;

	public InfoPanel(String type) throws FileNotFoundException {

		this.setSize(500, 700);
		this.setLocation(0, 0);
		this.setBackground(Color.BLACK);
		this.setLayout(null);

		scout = new Scout().getImage();
		tank = new Tank().getImage();
		archer = new Archer().getImage();
		swordsman = new Swordsman().getImage();
		wizard = new Wizard().getImage();
		artillery = new Artillery().getImage();

		
		JTextPane aPane = new JTextPane();
		aPane.setForeground(Color.WHITE);
		aPane.setFont(new Font("Arial Black", Font.BOLD, 16));
		File inputFile;
		switch (type) {
		case "Game Objective":
			inputFile = new File("GameObjective.txt");
			break;
		case "Units":
			inputFile = new File("Units.txt");
			break;
		case "Items":
			inputFile = new File("GameObjective.txt");
			break;
		case "Movement":
			inputFile = new File("Movement.txt");
			break;
		default: 
			inputFile = new File("Invalid");
			System.out.println("Invalid info text file");
			

		}
		
		Scanner fileScan = new Scanner(inputFile);
		String temp = "";
		while (fileScan.hasNext()) {
			temp += fileScan.next() + " ";
		}

		aPane.setText(temp);
		aPane.setSize(500, 700);
		aPane.setLocation(0, 0);
		aPane.setBackground(Color.BLACK);
		add(aPane);
	}
}
