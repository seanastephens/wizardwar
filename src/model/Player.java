package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import Units.Unit;

public class Player implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5579252067431201185L;
	private List<Unit> units;
	private boolean isTurn;
	private String theName;
	private String team;
	private int score;

	public Player(String playerName, String team) {
		this.units = new ArrayList<Unit>();
		isTurn = false;
		theName = playerName;
		score = 0;
		this.team = team;
	}

	public boolean isTurn() {
		return isTurn;
	}

	public String getName() {
		return theName;
	}

	public void addUnit(Unit u) {
		units.add(u);
	}

	public void removeUnit(Unit u) {
		units.remove(u);
	}

	public List<Unit> getUnits() {
		return units;
	}
	
	public void adjustScore(int adjust) {
		score += adjust;
	}

	public int getScore() {
		return score;
	}

	public int getItemAmount() {
		int result = 0;
		for (Unit u : units) {
			result += u.getAmountItems();
		}
		return result;
	}

	public void setTurn(boolean b) {
		isTurn = b;
	}

	public String getTeam() {
		return team;
	}
}
