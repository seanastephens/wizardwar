package tests;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import map.Grassland;
import map.Hill;
import map.Homebase;
import map.Mountain;
import map.Road;
import map.Terrain;
import map.Wall;

import org.junit.Before;
import org.junit.Test;

import Items.AttackPotion;
import Items.DefensePotion;
import Items.EnergyPotion;
import Items.Flag;
import Items.HealthPotion;
import Items.Item;
import Items.ManaPotion;
import Items.Trap;
import Units.Archer;
import Units.Artillery;
import Units.Scout;
import Units.Swordsman;
import Units.Tank;
import Units.Unit;
import Units.UnitMode;
import Units.Wizard;

/**
 * Checks that we have valid image paths
 */
public class ImageLoaderTest {

	int NUM_ANIMATIONS = 1000;

	List<Unit> units = new ArrayList<Unit>();
	List<Item> items = new ArrayList<Item>();
	List<Terrain> terrains = new ArrayList<Terrain>();

	@Before
	public void setupUnitLists() {
		units.add(new Archer());
		units.add(new Artillery());
		units.add(new Scout());
		units.add(new Swordsman());
		units.add(new Tank());
		units.add(new Wizard());

		items.add(new AttackPotion());
		items.add(new DefensePotion());
		items.add(new EnergyPotion());
		items.add(new Flag());
		items.add(new HealthPotion());
		items.add(new ManaPotion());
		items.add(new Trap());

		terrains.add(new Grassland());
		terrains.add(new Hill());
		terrains.add(new Homebase());
		terrains.add(new Mountain());
		terrains.add(new Road());
		terrains.add(new Wall());

		for (Unit u : units) {
			u.loadImage();
			assertNotNull(u.getClass().getSimpleName(), u.getImage());
		}

		for (Terrain t : terrains) {
			t.loadImage();
			assertNotNull(t.getClass().getSimpleName(), t.getImage());
		}

		for (Item i : items) {
			i.loadImage();
			assertNotNull(i.getClass().getSimpleName(), i.getImage());
		}
	}

	private void testUnitAnimation(Unit u, UnitMode mode) {
		for (int i = 0; i < NUM_ANIMATIONS; i++) {
			u.updateAnimation(mode);
			assertNotNull(u.getClass().getSimpleName() + " : " + mode,
					u.getImage());
		}
	}

	@Test
	public void testUnitImageAnimations() {
		for (Unit u : units) {
			for (UnitMode mode : UnitMode.values()) {
				testUnitAnimation(u, mode);
			}
		}
	}
}
