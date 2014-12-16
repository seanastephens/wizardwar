package Help;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MenuBar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import Units.Archer;
import Units.Scout;
import Units.Tank;

public class Help extends JFrame {
	
	private JFrame aFrame;
	private JPanel aPanel;
	private JPanel currentPanel;
	
	public static void main(String[] args) {
		new Help();
	}

	public Help() {
		this.setSize(new Dimension(510, 710));
		this.setLayout(null);
		aFrame = this;
		currentPanel = new JPanel();
		JMenuItem units = new JMenuItem("Units");
		JMenuItem potions = new JMenuItem("Potions and Items");
		JMenuItem movement = new JMenuItem("Movement and Attack");
		JMenuItem objective = new JMenuItem("Game Objectives");
		JMenuItem helpOptions = new JMenu("Help Options");
		
		units.addActionListener(new UnitsListener());
		potions.addActionListener(new ItemsListener());
		objective.addActionListener(new ObjectiveListener());
		movement.addActionListener(new MovementListener());

		helpOptions.add(objective);
		helpOptions.add(units);
		helpOptions.add(potions);
		helpOptions.add(movement);

		aPanel = new HomePanel();
		aPanel.setSize(500, 700);
		aPanel.setLocation(0, 0);
		aPanel.setBackground(Color.BLACK);
		aPanel.setLayout(null);
		

		JTextPane aText = new JTextPane();
		aText.setEditable(false);
		aText.setBackground(Color.BLACK);
		aText.setForeground(Color.WHITE);
		aText.setFont(new Font("Arial Black", Font.BOLD, 28));
		aText.setText("Welcome to the help menu for Wizard Wars. Please click on the help options menu to get help on how to play the game.");
        aText.setSize(490, 200);
        aText.setLocation(0, 250);
        aPanel.add(aText);
        
        
        add(aPanel);
           
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		menuBar.add(helpOptions);
		
		setVisible(true);
	}
	
	private class UnitsListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			aFrame.remove(aPanel);
			try {
				currentPanel = new InfoPanel("Units");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			aFrame.add(currentPanel);
			revalidate();
			repaint();
			
		}
	}
	
	private class ItemsListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			aFrame.remove(currentPanel);
			try {
				currentPanel = new InfoPanel("Items");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			aFrame.add(currentPanel);
			revalidate();
			repaint();
			
		}
	}
	
	private class ObjectiveListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			aFrame.remove(currentPanel);
			try {
				currentPanel = new InfoPanel("Game Objective");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			aFrame.add(currentPanel);
			revalidate();
			repaint();
			
		}
	}
	
	private class MovementListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			aFrame.remove(currentPanel);
			try {
				currentPanel = new InfoPanel("Movement");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			aFrame.add(currentPanel);
			revalidate();
			repaint();
			
		}
	}
	
	
}

	
	




