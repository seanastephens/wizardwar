package Help;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

import Items.AttackPotion;
import Items.DefensePotion;
import Items.EnergyPotion;
import Items.HealthPotion;
import Items.ManaPotion;
import Items.Trap;
import Units.Archer;
import Units.Artillery;
import Units.Scout;
import Units.Swordsman;
import Units.Tank;
import Units.Wizard;

public class HomePanel extends JPanel {
	private Image scout;
	private Image tank;
	private Image archer;
	private Image swordsman;
	private Image wizard;
	private Image artillery;
	private Image attackPotion;
	private Image defensePotion;
	private Image healthPotion;
	private Image energyPotion;
	private Image manaPotion;
	private Image trap;

	public HomePanel() {
		Scout aScout = new Scout();
		aScout.loadImage();
		scout = aScout.getImage();

		Tank aTank = new Tank();
		aTank.loadImage();
		tank = aTank.getImage();

		Archer aArcher = new Archer();
		aArcher.loadImage();
		archer = aArcher.getImage();

		Swordsman aSword = new Swordsman();
		aSword.loadImage();
		swordsman = aSword.getImage();

		Wizard aWizard = new Wizard();
		aWizard.loadImage();
		wizard = aWizard.getImage();

		Artillery theArtillery = new Artillery();
		theArtillery.loadImage();
		artillery = theArtillery.getImage();

		AttackPotion attack = new AttackPotion();
		attack.loadImage();
		attackPotion = attack.getImage();

		DefensePotion defense = new DefensePotion();
		defense.loadImage();
		defensePotion = defense.getImage();

		HealthPotion health = new HealthPotion();
		health.loadImage();
		healthPotion = health.getImage();

		EnergyPotion energy = new EnergyPotion();
		energy.loadImage();
		energyPotion = energy.getImage();

		ManaPotion mana = new ManaPotion();
		mana.loadImage();
		manaPotion = health.getImage();

		Trap aTrap = new Trap();
		aTrap.loadImage();
		trap = aTrap.getImage();
	}

	@Override
	public void paintComponent(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, 500, 700);
		g.drawImage(scout, 0, 0, null);
		g.drawImage(tank, 82, 0, null);
		g.drawImage(archer, 164, 0, null);
		g.drawImage(swordsman, 246, 0, null);
		g.drawImage(wizard, 328, 0, null);
		g.drawImage(artillery, 410, 0, null);

		for (int i = 1; i < 3; i++) {
			g.drawImage(attackPotion, 0, 20 + 70 * i, null);
			g.drawImage(defensePotion, 82, 20 + 70 * i, null);
			g.drawImage(energyPotion, 164, 20 + 70 * i, null);
			g.drawImage(healthPotion, 246, 20 + 70 * i, null);
			g.drawImage(manaPotion, 328, 20 + 70 * i, null);
			g.drawImage(trap, 410, 20 + 70 * i, null);
		}

	}
}
