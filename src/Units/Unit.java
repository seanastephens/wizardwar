package Units;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;

import model.WizardWar;
import server.Logger;
import view.MainPanel;
import Items.Flag;
import Items.Item;

public abstract class Unit implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6140413768760499277L;

	private static String BASE_PATH = "images/UnitImages/";

	// Statistics for each unit
	protected int combat, speed, defense, health, range;
	protected double mana;
	protected boolean hasMoved;
	protected boolean hasAttacked;
	protected int initialHealth;
	protected int movePoints;
	protected List<Item> items;
	protected Point location;
	protected transient BufferedImage image;
	protected String sprite_sheet;
	protected String name, team;
	protected Point adjust;
	protected AnimationData walkingAnimationData;
	protected AnimationData standingAnimationData;
	protected AnimationData attackingAnimationData;
	protected boolean flip = false;
	private int count = 0;
	private boolean imagesAreloaded = false;
	private UnitMode lastMode = UnitMode.StandingRight;

	private static transient Map<String, List<BufferedImage>> walkingAnimationLeft = new HashMap<>();
	private static transient Map<String, List<BufferedImage>> standingAnimationLeft = new HashMap<>();
	private static transient Map<String, List<BufferedImage>> attackingAnimationLeft = new HashMap<>();
	private static transient Map<String, List<BufferedImage>> walkingAnimationRight = new HashMap<>();
	private static transient Map<String, List<BufferedImage>> standingAnimationRight = new HashMap<>();
	private static transient Map<String, List<BufferedImage>> attackingAnimationRight = new HashMap<>();

	private int UID;
	private static int nextUID = 0;

	// Sprite Sheet

	public Unit() {
		items = new ArrayList<Item>();
		UID = nextUID++;
		hasMoved = false;
		hasAttacked = false;
		adjust = new Point();

	}

	public void damageDone(int damage) {
		health -= damage;
	}

	public boolean attack(Unit otherUnit) { // returns true if attack was
		if(otherUnit != null) {
			double damage_modifier = 15;
			double attack_power = this.combat + this.speed + (1*this.mana);
			int damage = Math.abs((int)(damage_modifier*(attack_power - (otherUnit.defense + .5*otherUnit.speed))));
		
		//--Ogre goes beserker if less than 20% health
			if(this.name.equals("Ogre")) {
				if(this.health <= this.initialHealth*.20)
					damage = 2*damage;
			}
			if(this.name.equals("Wizards")) {
				attack_power = this.combat + this.speed + (1.25*this.mana);
				damage = Math.abs((int)((damage_modifier-5)*(attack_power - (otherUnit.defense + .5*otherUnit.speed))));
				if(this.mana > 0) 
					this.mana-= .5;
			}
			Logger.log(this, this.name + " did " + damage + " damage to  " + otherUnit.name);
			otherUnit.damageDone(damage);
		}

		return true;
		// rock beats scissors beats paper beats rock
	}

	public void move(int terrainCost) {

		movePoints -= terrainCost;
	}

	public void pickedUpItem(Item item) {
		items.add(item);
	}

	// ???? XXX
	public void blockAttack() {
		defense--;
	}

	public void setTeam(String team) {
		this.team = team;
	}

	public String getTeam() {
		return team;
	}

	public void setRange(int range) {
		this.range = range;
	}

	public int getRange() {
		return range;
	}

	public int getCombat() {
		return combat;
	}

	public int getMana() {
		return (int) mana;
	}

	public void setUID(int id) {
		this.UID = id;
	}

	public void setCombat(int combat) {
		this.combat = combat;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getDefense() {
		return defense;
	}

	public void setDefense(int defense) {
		this.defense = defense;
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public int getMovePoints() {
		return movePoints;
	}

	public void setMovePoints(int movePoints) {
		this.movePoints = movePoints;
	}

	public void setMana(double d) {
		this.mana = d;
	}

	public List<Item> getItemsList() {
		return items;
	}

	public int getAmountItems() {
		return items.size();
	}

	public void removeItem(Item aItem) {
		items.remove(aItem);
	}

	public boolean canUseItem(Item aItem) {
		return true;
	}

	public void useItem(Item toUse, WizardWar game) {
		toUse.use(this, game.getMap());
	}

	public int getUID() {
		return UID;
	}

	public void updateAnimation(UnitMode mode) {
		if (!imagesAreloaded) {
			return;
		}

		Map<String, List<BufferedImage>> imageMap = null;
		switch (mode) {
		case StandingLeft:
			imageMap = standingAnimationLeft;
			break;
		case StandingRight:
			imageMap = standingAnimationRight;
			break;
		case AttackingDown:
		case AttackingLeft:
			imageMap = attackingAnimationLeft;
			break;
		case AttackingRight:
		case AttackingUp:
			imageMap = attackingAnimationRight;
			break;
		case WalkingLeft:
		case WalkingDown:
			imageMap = walkingAnimationLeft;
			break;
		case WalkingRight:
		case WalkingUp:
			imageMap = walkingAnimationRight;
			break;
		default:
			throw new IllegalArgumentException(
					"An invalid UnitMode was passed ???");
		}
		List<BufferedImage> imageList = imageMap
				.get(getClass().getSimpleName());
		image = imageList.get(count % imageList.size());
		count++;
		lastMode = mode;
	}

	public UnitMode getLastAnimationMode() {
		return lastMode;
	}

	public void imageReset() {
		imagesAreloaded = false;
	}

	public void loadImage() {
		if (imagesAreloaded) {
			return;
		}

		String key = getClass().getSimpleName();
		walkingAnimationLeft.put(key, new ArrayList<BufferedImage>());
		walkingAnimationRight.put(key, new ArrayList<BufferedImage>());
		standingAnimationLeft.put(key, new ArrayList<BufferedImage>());
		standingAnimationRight.put(key, new ArrayList<BufferedImage>());
		attackingAnimationLeft.put(key, new ArrayList<BufferedImage>());
		attackingAnimationRight.put(key, new ArrayList<BufferedImage>());

		try {
			String fullPath = BASE_PATH + this.sprite_sheet;
			File spriteSheetFile = new File(fullPath);
			BufferedImage spriteSheet = ImageIO.read(spriteSheetFile);

			if (flip) {
				loadPartOfSpriteSheet(walkingAnimationData,
						walkingAnimationRight, spriteSheet);
				loadPartOfSpriteSheet(standingAnimationData,
						standingAnimationRight, spriteSheet);
				loadPartOfSpriteSheet(attackingAnimationData,
						attackingAnimationRight, spriteSheet);
			} else {
				loadPartOfSpriteSheet(walkingAnimationData,
						walkingAnimationLeft, spriteSheet);
				loadPartOfSpriteSheet(standingAnimationData,
						standingAnimationLeft, spriteSheet);
				loadPartOfSpriteSheet(attackingAnimationData,
						attackingAnimationLeft, spriteSheet);
			}

		} catch (IOException e) {
			System.err.println("Could not load image for" + this.name);
			e.printStackTrace();
		}

		mirrorImageLists();
		imagesAreloaded = true;

		updateAnimation(UnitMode.StandingRight);
	}

	private void loadPartOfSpriteSheet(AnimationData data,
			Map<String, List<BufferedImage>> imageMap, BufferedImage wholeImage) {
		int x = data.x;
		int y = data.y;
		int width = data.width;
		int height = data.height;
		int num = data.numberOfImages;

		for (int i = 0; i < num; i++) {
			Image initialImage = wholeImage.getSubimage(x + (width * i), y,
					width, height);
			Image scaledImage = initialImage.getScaledInstance(
					MainPanel.TILE_SIZE, MainPanel.TILE_SIZE, 0);
			image = new BufferedImage(scaledImage.getWidth(null),
					scaledImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
			Graphics2D imageGraphicsContext = image.createGraphics();
			imageGraphicsContext.drawImage(scaledImage, 0, 0, null);
			imageGraphicsContext.dispose();
			imageMap.get(getClass().getSimpleName()).add(image);
		}
	}

	private void mirrorImageLists() {
		String key = getClass().getSimpleName();
		if (walkingAnimationLeft.get(key).isEmpty()) {
			mirroredCopy(walkingAnimationRight.get(key),
					walkingAnimationLeft.get(key));
		} else {
			mirroredCopy(walkingAnimationLeft.get(key),
					walkingAnimationRight.get(key));
		}

		if (standingAnimationLeft.get(key).isEmpty()) {
			mirroredCopy(standingAnimationRight.get(key),
					standingAnimationLeft.get(key));
		} else {
			mirroredCopy(standingAnimationLeft.get(key),
					standingAnimationRight.get(key));
		}

		if (attackingAnimationLeft.get(key).isEmpty()) {
			mirroredCopy(attackingAnimationRight.get(key),
					attackingAnimationLeft.get(key));
		} else {
			mirroredCopy(attackingAnimationLeft.get(key),
					attackingAnimationRight.get(key));
		}
	}

	private void mirroredCopy(List<BufferedImage> source,
			List<BufferedImage> dest) {
		for (BufferedImage image : source) {
			dest.add(getLeftRightFlippedImage(image));
		}
	}

	private BufferedImage getLeftRightFlippedImage(BufferedImage image) {
		BufferedImage flippedImage = new BufferedImage(image.getWidth(),
				image.getHeight(), image.getType());
		for (int i = 0; i < image.getWidth(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {
				int flippedPixel = image.getRGB(i, j);
				flippedImage.setRGB(image.getWidth() - i - 1, j, flippedPixel);
			}
		}
		return flippedImage;
	}

	public void draw(Graphics2D g2, Point p) {
		g2.drawImage(image, p.x + adjust.x, p.y + adjust.y, null);

	}

	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage im) {
		image = im;
	}

	public String getName() {
		return name;
	}

	public void setLocation(Point p) {
		location = p;
	}

	public Point getLocation() {
		return location;
	}

	public int getInitialHealth() {
		return initialHealth;
	}

	public static Unit getNewUnitOfType(String type) {
		switch (type) {
		case "Archer":
			return new Archer();
		case "Artillery":
			return new Artillery();
		case "Scout":
			return new Scout();
		case "Swordsman":
			return new Swordsman();
		case "Ogre":
		case "Tank":
			return new Tank();
		case "Wizard":
			return new Wizard();
		default:
			System.err.print("Invalid unit: " + type);
			throw new IllegalArgumentException();
		}
	}

	public int getInitialDefense() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getInitialCombat() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getInitialMana() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean hasMoved() {
		return hasMoved;
	}

	public void setHasMoved(boolean moved) {
		hasMoved = moved;
	}

	public boolean hasAttacked() {
		return hasAttacked;
	}

	public void setHasAttacked(boolean attacked) {
		hasAttacked = attacked;
	}

	public void adjust(int x, int y) {
		adjust.translate(x, y);
	}

	public void resetAdjustment() {
		adjust = new Point(0, 0);
	}

	public boolean capturedFlag() {
		for (Item anItem : getItemsList()) {
			if (anItem instanceof Flag) {
				return true;
			}
		}
		return false;
	}

	protected class AnimationData implements Serializable {
		public final int numberOfImages;
		public final int x;
		public final int y;
		public final int width;
		public final int height;

		public AnimationData(int num, int x, int y, int width, int height) {
			this.numberOfImages = num;
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}
	}

	private String[] charNames = { "Vlad", "Peter", "Arthur", "Derp",
			"Charles", "Buster", "Lancelot", "Dumbledore", "Gandalf", "Zorlar",
			"Legolas", "50 cent", "Asirormryna", "Belon", "Claup", "Darkal",
			"Jecar", "Soqueem", "Leron", "Om'ere", "Mossamkin", "Oresskel",
			"Alegarmos", "Inaugh", "Imoon", "Warwar", "Undyor", "Siros",
			"Vidrait", "Lyeeld", "Lirakmos", "Enthsamald", "Lubur", "Chaen",
			"Echgash", "Enuen", "Skelop", "Engeg", "Er-or", "Deny", "Valulu",
			"Aeni", "Risst", "Omengcer", "Alekime", "Sayque", "Sayart",
			"Unttai", "Shy'raya", "Til", "Inaendenth", "Moyeris", "Jekindel",
			"Toceli", "Honosh", "Saytyer", "Kinwarryn", "Slogkight",
			"Ang'nysa", "Lyev", "Eenda", "Ingris", "Eesti", "Endan", "Keacny",
			"Emust", "Brean", "Moratis", "Rakat", "Teenight", "Sud", "Shyban",
			"Undray", "Ar'ad", "Shaech", "Cunadu", "Jenob", "Dynaldo",
			"Ineoch", "Imaiss", "Oory", "Aughden", "Ia'enthe", "Ghatas",
			"Einge", "Yrpere", "Zaissnal", "Garonworat", "Osrad", "Ghaightusk",
			"Aleingit", "Lerem", "Athpol", "Traper", "Inekimden", "Den'ale",
			"Cer'end", "Queth", "Dynaleing", "Ener", "Draurnu", "Soler",
			"Engrynald", "Umund", "Kinlyeough", "Dra'entha", "Serowor", "Uita",
			"Rakath", "Aleekin", "Aughesse", "Criepdar", "Lodim", "Dynmosough",
			"Verril", "Hinaleest", "Skeltan", "Cauldir", "Eurni", "Lorerkin",
			"Elmver", "Vesmor", "Rhoest", "Cirym", "Febum", "Miss", "Hattas",
			"Draw", "Eena", "Enryne", "Poldeltia", "Sean", "Austin", "Dejaco",
			"Callie" };
	private String charName = charNames[new Random().nextInt(charNames.length)];

	public String getCharacterName() {
		return charName;
	}
}
