package mapeditor;

import javax.swing.*;

import view.MainPanel;
import view.MapFileList;
import map.*;
import Items.*;
import Units.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;

public class MapEditor extends JFrame{
//--Generic Info
	private int debug = 0;
	private boolean checkNew = false;
	
//--Frame Info
	private static int WINDOW_HEIGHT = 750;
	private static int WINDOW_WIDTH = 1280;
	private JMenuBar menuBar;
	private JMenuItem units;
	private JMenuItem items;
	private JMenuItem grid;
	private JToolBar toolbar;

//--Explorer
	private JTabbedPane explorer;
	private int EXPLORER_WIDTH = 230;
	
//--Content
	private ContentPane content;
	private int CONTENT_WIDTH = 1040;
	private int CONTENT_HEIGHT = 670;
	
//--Map Information
	private Tile[][] map;
	private int DEFAULT_MAP_WIDTH = 20;
	private int DEFAULT_MAP_HEIGHT = 20;
	private int MAP_WIDTH;
	private int MAP_HEIGHT;
	private int TILE_WIDTH;
	private int TILE_HEIGHT;
	private boolean show_grid = true;
	private boolean show_items = true;
	private boolean show_units = true;
	private boolean map_loaded = false;
    private String map_name = "untitled";
	
//--Game Information
	private int NUMBER_OF_PLAYERS = 2;
	private List<String> TEAM_NAMES;
	private String GAME_TYPE = "";
	
//--Edit Information
	private boolean is_unit_selected = false;
	private boolean is_item_selected = false;
	private boolean is_terrain_selected = false;
	private Unit unit_selected;
	private Item item_selected;
	private Terrain terrain_selected;
	
//--Images
	private BufferedImage archer_image;
	private BufferedImage artillery_image;
	private BufferedImage scout_image;
	private BufferedImage swordsman_image;
	private BufferedImage tank_image;
	private BufferedImage wizard_image;
	private BufferedImage grassland_image;
	private BufferedImage homebase_image;
	private BufferedImage hill_image;
	private BufferedImage mountain_image;
	private BufferedImage road_image;
	private BufferedImage wall_image;
	private BufferedImage water_image;
	private BufferedImage attack_image;
	private BufferedImage defense_image;
	private BufferedImage energy_image;
	private BufferedImage flag_image;
	private BufferedImage health_image;
	private BufferedImage mana_image;
	private BufferedImage trap_image;
	private BufferedImage brick_image;
	
	
	public static void main(String[] args) {
		MapEditor editor = new MapEditor();
	}
	
	public MapEditor() {
		this.setLocation(200,200);
		this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		this.setResizable(false);
		TEAM_NAMES = new ArrayList<String>();
		setUpToolBar();
		loadDefaultImages();
		loadDefaultMap();
		loadContent();
		CONTENT_HEIGHT = content.getHeight();
		content.addMouseListener(new ContentClickListener());
		loadExplorer();
		this.addMouseListener(new ContentClickListener());
		this.setVisible(true);
		getGameInformation();
	}
	
//--GUI Methods
	private void loadContent() {
		content = new ContentPane();
		content.setLayout(null);
		content.setBackground(Color.BLACK);
		this.add(content);
	}
	private void loadExplorer() {
		explorer = new JTabbedPane();
		JPanel unit_panel = unitPanel();
		JPanel item_panel = itemPanel();
		JPanel terrain_panel = terrainPanel();
		explorer.addTab("Units",null,unit_panel,"Units that can be added to map");
		explorer.addTab("Items",null,item_panel,"Items that can be added to map");
		explorer.addTab("Terrain",null,terrain_panel,"Map Terrain Types");	
		explorer.setBounds(1050, 0, EXPLORER_WIDTH, 720);
		content.add(explorer);
	}
	private JPanel unitPanel() {
		JPanel unitPanel = new JPanel();
		unitPanel.setLayout(null);
		
		ImageClickListener icl = new ImageClickListener();
		
		JLabel archer = new JLabel("Archer");
		JLabel archer_image_label = new JLabel(new ImageIcon(archer_image));
		archer_image_label.setBounds(10,10,75,75);
		archer_image_label.setName("Archer");
		archer.setBounds(100,35,75,40);
		
		
		JLabel artillery = new JLabel("Artillery");
		JLabel artillery_image_label = new JLabel(new ImageIcon(artillery_image));
		artillery_image_label.setName("Artillery");
		artillery_image_label.setBounds(10,95,75,75);
		artillery.setBounds(100,120,75,25);
		
		JLabel scout = new JLabel("Scout");
		JLabel scout_image_label = new JLabel(new ImageIcon(scout_image));
		scout_image_label.setName("Scout");
		scout_image_label.setBounds(10,180,75,75);
		scout.setBounds(100,205,75,25);
		
		JLabel swordsman = new JLabel("Swordsman");
		JLabel swordsman_image_label = new JLabel(new ImageIcon(swordsman_image));
		swordsman_image_label.setName("Swordsman");
		swordsman_image_label.setBounds(10,265,75,75);
		swordsman.setBounds(100,285,100,25);
		
		JLabel tank = new JLabel("Tank");
		JLabel tank_image_label = new JLabel(new ImageIcon(tank_image));
		tank_image_label.setName("Tank");
		tank_image_label.setBounds(10,350,75,75);
		tank.setBounds(100,375,75,25);
		
		JLabel wizard = new JLabel("Wizard");
		JLabel wizard_image_label = new JLabel(new ImageIcon(wizard_image));
		wizard_image_label.setName("Wizard");
		wizard_image_label.setBounds(10,435,75,75);
		wizard.setBounds(100,455,75,25);
		
	//--Add to panel
		unitPanel.add(archer_image_label);
		unitPanel.add(archer);
		
		unitPanel.add(artillery_image_label);
		unitPanel.add(artillery);
		
		unitPanel.add(scout_image_label);
		unitPanel.add(scout);
		
		unitPanel.add(swordsman_image_label);
		unitPanel.add(swordsman);
		
		unitPanel.add(tank_image_label);
		unitPanel.add(tank);
		
		unitPanel.add(wizard_image_label);
		unitPanel.add(wizard);		
		
	//--Add listeners to images	
		archer_image_label.addMouseListener(icl);
		artillery_image_label.addMouseListener(icl);
		scout_image_label.addMouseListener(icl);
		swordsman_image_label.addMouseListener(icl);
		tank_image_label.addMouseListener(icl);
		wizard_image_label.addMouseListener(icl);
		
		return unitPanel;
	}
	private JPanel itemPanel() {
		JPanel itemPanel = new JPanel(); 
		itemPanel.setLayout(null);
		
		ImageClickListener icl = new ImageClickListener();
		
		JLabel attack = new JLabel("Attack");
		JLabel attack_image_label = new JLabel(new ImageIcon(attack_image));
		attack_image_label.setBounds(10,0,75,75);
		attack_image_label.setName("Attack");
		attack.setBounds(100,25,75,40);
		
		
		JLabel defense = new JLabel("Defense");
		JLabel defense_image_label = new JLabel(new ImageIcon(defense_image));
		defense_image_label.setName("Defense");
		defense_image_label.setBounds(10,85,75,75);
		defense.setBounds(100,110,75,25);
		
		JLabel energy = new JLabel("Energy");
		JLabel energy_image_label = new JLabel(new ImageIcon(energy_image));
		energy_image_label.setName("Energy");
		energy_image_label.setBounds(10,170,75,75);
		energy.setBounds(100,195,75,25);
		
		JLabel flag = new JLabel("Flag");
		JLabel flag_image_label = new JLabel(new ImageIcon(flag_image));
		flag_image_label.setName("Flag");
		flag_image_label.setBounds(10,255,75,75);
		flag.setBounds(100,275,100,25);
		
		JLabel health = new JLabel("Health");
		JLabel health_image_label = new JLabel(new ImageIcon(health_image));
		health_image_label.setName("Health");
		health_image_label.setBounds(10,340,75,75);
		health.setBounds(100,365,75,25);
		
		JLabel mana = new JLabel("Mana");
		JLabel mana_image_label = new JLabel(new ImageIcon(mana_image));
		mana_image_label.setName("Mana");
		mana_image_label.setBounds(8,425,75,75);
		mana.setBounds(100,445,75,25);
		
		JLabel trap = new JLabel("Trap");
		JLabel trap_image_label = new JLabel(new ImageIcon(trap_image));
		trap_image_label.setName("Trap");
		trap_image_label.setBounds(10,515,75,75);
		trap.setBounds(100,540,75,25);
		
		JLabel brick = new JLabel("Brick");
		JLabel brick_image_label = new JLabel(new ImageIcon(brick_image));
		brick_image_label.setName("Brick");
		brick_image_label.setBounds(10,590,75,75);
		brick.setBounds(100,615,75,25);
		
	//--Add to panel
		itemPanel.add(attack_image_label);
		itemPanel.add(attack);
		
		itemPanel.add(defense_image_label);
		itemPanel.add(defense);
		
		itemPanel.add(energy_image_label);
		itemPanel.add(energy);
		
		itemPanel.add(flag_image_label);
		itemPanel.add(flag);
		
		itemPanel.add(health_image_label);
		itemPanel.add(health);
		
		itemPanel.add(mana_image_label);
		itemPanel.add(mana);		
		
		itemPanel.add(trap_image_label);
		itemPanel.add(trap);
		
		itemPanel.add(brick_image_label);
		itemPanel.add(brick);
		
	//--Add listeners to images	
		attack_image_label.addMouseListener(icl);
		defense_image_label.addMouseListener(icl);
		energy_image_label.addMouseListener(icl);
		flag_image_label.addMouseListener(icl);
		health_image_label.addMouseListener(icl);
		mana_image_label.addMouseListener(icl);
		trap_image_label.addMouseListener(icl);
		brick_image_label.addMouseListener(icl);
		
		return itemPanel;
	}
	private JPanel terrainPanel() {
		JPanel tPanel = new JPanel();
		tPanel.setLayout(null);
		
		ImageClickListener icl = new ImageClickListener();
		
		JLabel grassland = new JLabel("Grassland");
		JLabel grassland_image_label = new JLabel(new ImageIcon(grassland_image));
		grassland_image_label.setBounds(10,10,75,75);
		grassland_image_label.setName("Grassland");
		grassland.setBounds(100,35,75,40);
		
		
		JLabel hill = new JLabel("Deep Grass");
		JLabel hill_image_label = new JLabel(new ImageIcon(hill_image));
		hill_image_label.setName("Deep Grass");
		hill_image_label.setBounds(10,95,75,75);
		hill.setBounds(100,120,75,25);
		
		JLabel base = new JLabel("Homebase");
		JLabel base_image_label = new JLabel(new ImageIcon(homebase_image));
		base_image_label.setName("Homebase");
		base_image_label.setBounds(10,180,75,75);
		base.setBounds(100,205,75,25);
		
		JLabel mountain = new JLabel("Mountain");
		JLabel mountain_image_label = new JLabel(new ImageIcon(mountain_image));
		mountain_image_label.setName("Mountain");
		mountain_image_label.setBounds(10,265,75,75);
		mountain.setBounds(100,285,100,25);
		
		JLabel road = new JLabel("Road");
		JLabel road_image_label = new JLabel(new ImageIcon(road_image));
		road_image_label.setName("Road");
		road_image_label.setBounds(10,350,75,75);
		road.setBounds(100,375,75,25);
		
		JLabel wall = new JLabel("Wall");
		JLabel wall_image_label = new JLabel(new ImageIcon(wall_image));
		wall_image_label.setName("Wall");
		wall_image_label.setBounds(10,435,75,75);
		wall.setBounds(100,455,75,25);
		
		JLabel water = new JLabel("Water");
		JLabel water_image_label = new JLabel(new ImageIcon(water_image));
		water_image_label.setName("Water");
		water_image_label.setBounds(10,520,75,75);
		water.setBounds(100,540,75,25);
		
		//--Add to panel
		tPanel.add(grassland_image_label);
		tPanel.add(grassland);
		
		
		tPanel.add(hill_image_label);
		tPanel.add(hill);
		
		tPanel.add(base_image_label);
		tPanel.add(base);
		
		tPanel.add(mountain_image_label);
		tPanel.add(mountain);
		
		tPanel.add(road_image_label);
		tPanel.add(road);
		
		tPanel.add(wall_image_label);
		tPanel.add(wall);		
		
		tPanel.add(water_image_label);
		tPanel.add(water);
		
	//--Add listeners to images	
		grassland_image_label.addMouseListener(icl);
		hill_image_label.addMouseListener(icl);
		base_image_label.addMouseListener(icl);
		mountain_image_label.addMouseListener(icl);
		road_image_label.addMouseListener(icl);
		wall_image_label.addMouseListener(icl);
		water_image_label.addMouseListener(icl);
		
	/* OLD DYNAMIC LOADING CODE
		List<JLabel> terrains = new ArrayList<JLabel>(); 
		File[] terrain_dir = new File("src/map/").listFiles();
		for(File f : terrain_dir) {
			if(f.isFile()) {
				String name = f.getName().split("\\.")[0];
				if(!name.equals("Test") && !name.equals("Tile") && !name.equals("TileMap") && !name.equals("Terrain")) {
					
					terrains.add(new JLabel(name));
				}
			}
		}
		for(JLabel t : terrains) { tPanel.add(t); }
	*/
		return tPanel;
	}
	private void drawGrid() {
		this.show_grid = true;
		content.repaint();
	}
	private void setUpToolBar() {
		toolbar = new JToolBar("Tool Menu");
		JToolBar subtool = new JToolBar("MENU");
		this.add(toolbar, BorderLayout.PAGE_START);
		toolbar.add(createButton("toolbarButtonGraphics/general/New24.gif","New", "New Map", "New Map"));
		toolbar.add(createButton("toolbarButtonGraphics/general/Open24.gif","Import", "Import Map", "Import Map"));
		toolbar.add(createButton("toolbarButtonGraphics/general/SaveAll24.gif","Export", "Export Map", "Export Map"));
		toolbar.addSeparator(new Dimension(50,5));
		toolbar.add(createButton("mapeditor/NoGrid24.gif","Grid", "Hide Grid", "Hide Grid"));
		toolbar.addSeparator(new Dimension(50,5));
		toolbar.add(createButton("toolbarButtonGraphics/general/Delete24.gif","Delete", "Delete Tile", "Delete Tile"));
		
	//	toolbar.add(subtool);
	//	subtool.add(createButton("URL","Delete", "Delete Tile", "Delete Tile"));
	}
	private JButton createButton(String img, String cmd, String toolTip, String altText) {
		JButton button = new JButton();
		String imgPath = "images/" + img;
		button.setActionCommand(cmd);
		button.setToolTipText(toolTip);
		button.setText(altText);
		button.addActionListener(new ToolBarListener());
		button.setIcon(new ImageIcon(imgPath));
		return button;
	}
//--Map Methods
	private void loadDefaultMap() {
		MAP_HEIGHT = DEFAULT_MAP_HEIGHT;
		MAP_WIDTH = DEFAULT_MAP_WIDTH;
		this.map = new Tile[DEFAULT_MAP_HEIGHT][DEFAULT_MAP_WIDTH];
		for(int i = 0; i < DEFAULT_MAP_HEIGHT; i++) {
			for(int j = 0; j < DEFAULT_MAP_WIDTH; j++) {
				Terrain t = new Grassland();
				map[i][j] = new Tile();
				map[i][j].setTerrain(t);
				map[i][j].getTerrain().setImage(grassland_image);
			}
		}
		TILE_WIDTH = CONTENT_WIDTH / MAP_WIDTH;
		TILE_HEIGHT = CONTENT_HEIGHT/MAP_HEIGHT;
		map_loaded = true;
	}
	private void createMap() {
		CONTENT_HEIGHT = content.getHeight();
		map = null;
		map = new Tile[MAP_HEIGHT][MAP_WIDTH];
		for(int i = 0; i < MAP_HEIGHT; i++) {
			for(int j = 0; j < MAP_WIDTH; j++) {
				map[i][j] = new Tile();
				map[i][j].setTerrain(new Grassland());
				map[i][j].getTerrain().setImage(grassland_image);
			}
		}
		TILE_WIDTH = CONTENT_WIDTH / MAP_WIDTH;
		TILE_HEIGHT = CONTENT_HEIGHT/MAP_HEIGHT;
		getGameInformation();
		map_loaded = true;
		checkNew = true;
	}
	private void importMap() {
		final JFrame loader = new JFrame();
		loader.setResizable(false);
		loader.setSize(225,400);
		loader.setLocation(400,400);
		JPanel import_content = new JPanel();
		import_content.setLayout(null);
		
		JLabel s = new JLabel("Select Map");
		s.setBounds(77, 10, 100, 25);
		import_content.add(s);
				
		final MapFileList mfl = new MapFileList();		
		import_content.add(mfl);
		mfl.setBounds(70,35,100,250);
		
		JButton okay = new JButton("Okay");
		okay.setBounds(20, 300, 90, 25);
		okay.setActionCommand("Submit");
		JButton cancel = new JButton("Cancel");
		cancel.setBounds(125, 300, 90, 25);
		cancel.setActionCommand("Cancel");
		ActionListener lst = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getActionCommand().equals("Submit")) {
					map_name = mfl.getSelectedValue();
					TileMap tMap = new TileMap(map_name);
					map = tMap.getTiles();
					MAP_HEIGHT = map.length;
					MAP_WIDTH = map[0].length;
					map_loaded = true;
					GAME_TYPE = tMap.getWinCondition().toString();
					TILE_WIDTH = CONTENT_WIDTH / MAP_WIDTH;
					TILE_HEIGHT = content.getHeight()/MAP_HEIGHT;
					TEAM_NAMES.clear();
					getTeamNames();
					loadImages();
					drawGrid();
					loader.dispose();
				}
				else { loader.dispose(); }
			}			
		};
		okay.addActionListener(lst);
		cancel.addActionListener(lst);
		import_content.add(okay);
		import_content.add(cancel);
		
		loader.add(import_content);
		
		loader.setVisible(true);
		
	}
	private void newMap() {
		String sHeight = JOptionPane.showInputDialog("Please enter the height in tiles :");
		Scanner SIN = new Scanner(sHeight);
		while(!SIN.hasNextInt()) {
			sHeight = JOptionPane.showInputDialog("Please enter the height in tiles :");
		}
		MAP_HEIGHT = Integer.parseInt(sHeight);
		String sWidth = JOptionPane.showInputDialog("Please enter the width in tiles :");
		SIN = new Scanner(sWidth);
		while(!SIN.hasNextInt()) {
			sWidth = JOptionPane.showInputDialog("Please enter the width in tiles :");
		}
		MAP_WIDTH = Integer.parseInt(sWidth);
		createMap();
	}
//--Image Methods
	private void loadDefaultImages() {
	//--Units
		Archer a = new Archer(); a.loadImage();	archer_image = a.getImage();
		Artillery aa = new Artillery(); aa.loadImage(); artillery_image = aa.getImage();
		Scout s = new Scout(); s.loadImage(); scout_image = s.getImage();
		Swordsman ss = new Swordsman(); ss.loadImage(); swordsman_image = ss.getImage();
		Tank t = new Tank(); t.loadImage(); tank_image = t.getImage();
		Wizard w = new Wizard(); w.loadImage(); wizard_image = w.getImage();
	//--Terrain
		Grassland g = new Grassland(); g.loadImage(); grassland_image=g.getImage();
		Homebase h = new Homebase(); h.loadImage(); homebase_image=h.getImage();
		Mountain m = new Mountain(); m.loadImage(); mountain_image=m.getImage();
		Road r = new Road(); r.loadImage(); road_image=r.getImage();
		Wall ww = new Wall(); ww.loadImage(); wall_image=ww.getImage();
		Hill hh = new Hill(); hh.loadImage(); hill_image=hh.getImage();
		Water wa = new Water(); wa.loadImage(); water_image=wa.getImage();
		
	//--Items
		AttackPotion ap = new AttackPotion(); ap.loadImage(); attack_image=ap.getImage();
		DefensePotion dp = new DefensePotion(); dp.loadImage(); defense_image=dp.getImage();
		EnergyPotion ep = new EnergyPotion(); ep.loadImage(); energy_image=ep.getImage();
		Flag f = new Flag(); f.loadImage(); flag_image=f.getImage();
		HealthPotion hp = new HealthPotion(); hp.loadImage(); health_image=hp.getImage();
		ManaPotion mp = new ManaPotion(); mp.loadImage(); mana_image=mp.getImage();
		Trap tr = new Trap(); tr.loadImage(); trap_image = tr.getImage();
		Brick br = new Brick(); br.loadImage(); brick_image = br.getImage();
	}
	private void loadImages() {
		for(int i = 0; i < MAP_HEIGHT; i++) {
			for(int j = 0; j < MAP_WIDTH; j++) {
				if(map[i][j].getTerrain() != null) { map[i][j].getTerrain().loadImage(); }
				if(map[i][j].getUnit() != null) { map[i][j].getUnit().loadImage(); }
				if(map[i][j].getItem() != null) { map[i][j].getItem().loadImage(); }
			}
		}
	}	
	private BufferedImage getImage(String name) {
		return null;
	}
//--Game Methods
	private void getTeamNames() {
		for(int i = 0; i < MAP_HEIGHT; i++) {
			for(int j = 0; j < MAP_WIDTH; j++) {
				if(map[i][j].getUnit() != null) {
					if (!TEAM_NAMES.contains(map[i][j].getUnit().getTeam())) {
						TEAM_NAMES.add(map[i][j].getUnit().getTeam());
					}
				}
			}
		}
	//--TODO:  Figure out what to do is there are no units => no team names to grab
		if(TEAM_NAMES.size() == 0) { getGameInformation(); }
		Collections.sort(TEAM_NAMES);
	}
	private void getGameInformation() {
		Object[] options = {"DeathMatch", "CTF", "Zombies"};
		GAME_TYPE = (String)JOptionPane.showInputDialog(null,"What type of game is this map for?","Game Type",JOptionPane.QUESTION_MESSAGE,null,options,"DeathMatch");
		TEAM_NAMES.add("A");
		TEAM_NAMES.add("B");
	}
	private boolean checkValid() {
		if(GAME_TYPE.equals("")) {
			Object[] options = {"DeathMatch", "CTF", "Zombies"};
			GAME_TYPE = (String)JOptionPane.showInputDialog(null,"What type of game is this map for?","Game Type",JOptionPane.QUESTION_MESSAGE,null,options,"DeathMatch");
		}
		if(!minimumUnits()) {
			JOptionPane.showMessageDialog(content, "You must have at least one unit per team!", "Completion Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		switch(GAME_TYPE) {
			case "DeathMatch":
				return true;
			case "Zombies":
				return true;
			case "CTF":
				if(!findFlag()) {
					JOptionPane.showMessageDialog(content, "You must add a flag!", "Completion Error", JOptionPane.ERROR_MESSAGE); 
					return false;
				}
				if(!findBases()) {
					JOptionPane.showMessageDialog(content, "You must have 1 bases!", "Completion Error", JOptionPane.ERROR_MESSAGE);
					return false;
				}
				return true;
		}
		
		return false;
	}
	private boolean findFlag() {
		for(int i = 0; i < MAP_HEIGHT; i++) {
			for(int j = 0; j < MAP_WIDTH; j++) {
				if(map[i][j].getItem() != null) { if(map[i][j].getItem().getName().equals("Flag")) { return true; } }
			}
		}
		return false;
	}
	private boolean findBases() {
		int bases = 0;
		for(int i = 0; i < MAP_HEIGHT; i++) {
			for(int j = 0; j < MAP_WIDTH; j++) {
				if(map[i][j].getTerrain().getName().equals("Homebase")) { bases++; }
			}
		}
		if(bases == 1) { return true; }
		return false;
	}
	private boolean minimumUnits() {
		int units = 0;
		int team1 = 0;
		int team2 = 0;
		for(int i = 0; i < MAP_HEIGHT; i++) {
			for(int j = 0; j < MAP_WIDTH; j++) {
				if(map[i][j].getUnit() != null) {
					if(map[i][j].getUnit().getTeam().equals("A")) team1++; 
					if(map[i][j].getUnit().getTeam().equals("B")) team2++;
				}
			}
		}
		return (team1 >= 1 && team2 >= 1); 
	}
	
//--Private Classes
	private class ToolBarListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			switch (cmd) {
			case "New":
				newMap();
				break;
			case "Import":
				importMap();
				break;
			case "Export":
				if(!checkValid()) { return; }
				MapGenerator gen = new MapGenerator(map, NUMBER_OF_PLAYERS,GAME_TYPE);
				gen.generate();
				String save_name = JOptionPane
						.showInputDialog("Please enter a save name :", map_name);
				if (save_name != null && !save_name.equals("")) {
					gen.saveMap(save_name);
				}
				break;
			case "Delete":
				content.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
				((JButton) e.getSource()).setText("Cancel Delete");
				((JButton) e.getSource()).setActionCommand("Clear Delete");
				break;
			case "Clear Delete":
				content.setCursor(Cursor.getDefaultCursor());
				((JButton) e.getSource()).setText("Delete");
				((JButton) e.getSource()).setActionCommand("Delete");
				break;
			case "Grid":
				if(show_grid) { 
					show_grid = false; 
					((JButton) e.getSource()).setText("Show Grid"); 
					((JButton) e.getSource()).setIcon(new ImageIcon("images/mapeditor/Grid24.gif"));				
				}
				else {
					show_grid = true; 
					((JButton) e.getSource()).setText("Hide Grid"); 
					((JButton) e.getSource()).setIcon(new ImageIcon("images/mapeditor/NoGrid24.gif"));
					
				}
				break;
			default:
				break;
			}
			repaint();
		}
	}
	private class ContentClickListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			if(content.getCursor() == Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR)) {
				map[e.getY()/TILE_HEIGHT][e.getX()/TILE_WIDTH].setItem(null);
				map[e.getY()/TILE_HEIGHT][e.getX()/TILE_WIDTH].setUnit(null);
				is_unit_selected = is_item_selected = is_terrain_selected = false;
				unit_selected = null; item_selected = null; terrain_selected = null;
			}
			if(is_unit_selected) {
				try {
					/*if(map[e.getY()/TILE_HEIGHT][e.getX()/TILE_WIDTH].getTerrain().getMoveCost() != Double.POSITIVE_INFINITY) { 
						map[e.getY()/TILE_HEIGHT][e.getX()/TILE_WIDTH].setUnit(unit_selected);
						map[e.getY()/TILE_HEIGHT][e.getX()/TILE_WIDTH].getUnit().setLocation(new Point(e.getY()/TILE_HEIGHT,e.getX()/TILE_WIDTH));
					}*/
				if(map[e.getY()/TILE_HEIGHT][e.getX()/TILE_WIDTH].getTerrain().getMoveCost() != Double.POSITIVE_INFINITY) {
					map[e.getY()/TILE_HEIGHT][e.getX()/TILE_WIDTH].setUnit(unit_selected);
                    map[e.getY()/TILE_HEIGHT][e.getX()/TILE_WIDTH].getUnit().setLocation(new Point(e.getY()/TILE_HEIGHT,e.getX()/TILE_WIDTH));
                    String tempTeam = unit_selected.getTeam();
                    unit_selected = Unit.getNewUnitOfType(unit_selected.getClass().getSimpleName());
                    unit_selected.setTeam(tempTeam);
                    unit_selected.loadImage();
                } 
				}
				catch(ArrayIndexOutOfBoundsException aiobe) { /* do nothing */ }
			}
			if(is_item_selected) {
				if(map[e.getY()/TILE_HEIGHT][e.getX()/TILE_WIDTH].getMoveCost() != Double.POSITIVE_INFINITY) {
					map[e.getY()/TILE_HEIGHT][e.getX()/TILE_WIDTH].setItem(item_selected);
					map[e.getY()/TILE_HEIGHT][e.getX()/TILE_WIDTH].getItem().setLocation(new Point(e.getY()/TILE_HEIGHT,e.getX()/TILE_WIDTH));
					item_selected = Item.getNewItemOfType(item_selected.getClass().getSimpleName());
	                item_selected.loadImage();
				}
			}
			if(is_terrain_selected) {
				map[e.getY()/TILE_HEIGHT][e.getX()/TILE_WIDTH].setTerrain(terrain_selected);
				if(terrain_selected instanceof Homebase) terrain_selected = new Homebase();
				else terrain_selected = Terrain.getNewTerrainOfType(terrain_selected.getName());
				terrain_selected.loadImage();
			}
			repaint();
		}

		@Override
		public void mousePressed(MouseEvent e) { /* Disabled by Default */ }

		@Override
		public void mouseReleased(MouseEvent e) { /* Disabled by Default */ }

		@Override
		public void mouseEntered(MouseEvent e) { /* Disabled by Default */  }

		@Override
		public void mouseExited(MouseEvent e) { /* Disabled by Default */  }
		
	}
	private class ImageClickListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			content.setCursor(Cursor.getDefaultCursor());
				is_unit_selected = false;
				unit_selected = null;
				is_item_selected = false;
				item_selected = null;			
				is_terrain_selected = false;
				terrain_selected = null;
			
		//	System.out.println(((JLabel)e.getSource()).getName());
			String name = ((JLabel)e.getSource()).getName();
			switch(name) {
		//--Units
			case "Archer":
				is_unit_selected = true;
				unit_selected = new Archer();
				unit_selected.setImage(archer_image);
				break;
			case "Artillery":
				is_unit_selected = true;
				unit_selected = new Artillery();
				unit_selected.setImage(artillery_image);
				break;
			case "Scout":
				is_unit_selected = true;
				unit_selected = new Scout();
				unit_selected.setImage(scout_image);
				break;
			case "Swordsman":
				is_unit_selected = true;
				unit_selected = new Swordsman();
				unit_selected.setImage(swordsman_image);
				break;
			case "Tank":
				is_unit_selected = true;
				unit_selected = new Tank();
				unit_selected.setImage(tank_image);
				break;
			case "Wizard":
				is_unit_selected = true;
				unit_selected = new Wizard();
				unit_selected.setImage(wizard_image);
				break;
		//--Items
			case "Attack":
				is_item_selected = true;
				item_selected = new AttackPotion();
				item_selected.setImage(attack_image);
				break;
			case "Defense":
				is_item_selected = true;
				item_selected = new DefensePotion();
				item_selected.setImage(defense_image);
				break;
			case "Energy":
				is_item_selected = true;
				item_selected = new EnergyPotion();
				item_selected.setImage(energy_image);
				break;
			case "Flag":
				is_item_selected = true;
				item_selected = new Flag();
				item_selected.setImage(flag_image);
				break;
			case "Health":
				is_item_selected = true;
				item_selected = new HealthPotion();
				item_selected.setImage(health_image);
				break;
			case "Mana":
				is_item_selected = true;
				item_selected = new ManaPotion();
				item_selected.setImage(mana_image);
				break;
			case "Trap":
				is_item_selected = true;
				item_selected = new Trap();
				item_selected.setImage(trap_image);
				break;
			case "Brick":
				is_item_selected = true;
				item_selected = new Brick();
				item_selected.setImage(brick_image);
				break;
		//--Terrain
			case "Grassland":
				is_terrain_selected = true;
				terrain_selected = new Grassland();
				terrain_selected.setImage(grassland_image);
				break;
			case "Deep Grass":
				is_terrain_selected = true;
				terrain_selected = new Hill();
				terrain_selected.setImage(hill_image);
				break;
			case "Homebase":
				is_terrain_selected = true;
				terrain_selected = new Homebase();
				terrain_selected.setImage(homebase_image);
				break;
			case "Mountain":
				is_terrain_selected = true;
				terrain_selected = new Mountain();
				terrain_selected.setImage(mountain_image);
				break;
			case "Road":
				is_terrain_selected = true;
				terrain_selected = new Road();
				terrain_selected.setImage(road_image);
				break;
			case "Wall":
				is_terrain_selected = true;
				terrain_selected = new Wall();
				terrain_selected.setImage(wall_image);
				break;
			case "Water":
				is_terrain_selected = true;
				terrain_selected = new Water();
				terrain_selected.setImage(water_image);
				break;
			default:
				is_unit_selected = false; is_item_selected = false; is_terrain_selected = false; 
				unit_selected = null; item_selected = null; terrain_selected = null;
				break;
			}
			if(is_unit_selected) {
				String[] options = {"A (human)", "B (computer)"};
				int t = JOptionPane.showOptionDialog(MapEditor.this, "Choose a team:", "Team", 0, JOptionPane.INFORMATION_MESSAGE, null, options, 0);
				while(!options[t].contains("A") && !options[t].contains("B")) {
					t = JOptionPane.showOptionDialog(MapEditor.this, "Choose a team:", "Team", 0, JOptionPane.INFORMATION_MESSAGE, null, options, 0);
				}
				if(options[t].contains("A")) { unit_selected.setTeam(TEAM_NAMES.get(0)); }
				else if(options[t].contains("B")) { unit_selected.setTeam(TEAM_NAMES.get(1)); }	
			}
		}

		@Override
		public void mousePressed(MouseEvent e) { /* Disabled by Default */	}
		@Override
		public void mouseReleased(MouseEvent e) { /* Disabled by Default */	}
		@Override
		public void mouseEntered(MouseEvent e) { /* Disabled by Default */	}
		@Override
		public void mouseExited(MouseEvent e) { /* Disabled by Default */	}
		
	}
	private class ContentPane extends JPanel{
		public ContentPane() { super(); }		
		@Override
		protected void paintComponent(Graphics g) {
		    super.paintComponent(g);
		    //g.drawImage(map[0][0].getTerrain().getImage(), 10, 10, null);
		//--Draw Map Images
			if(map_loaded) {
				
				for(int i = 0; i < MAP_HEIGHT; i++) {
					for(int j = 0; j < MAP_WIDTH; j++) {
						g.drawImage(map[i][j].getTerrain().getImage(), j*TILE_WIDTH, i*TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT, null);
						if(show_items && map[i][j].getItem() != null) g.drawImage(map[i][j].getItem().getImage(), j*TILE_WIDTH, i*TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT, null);
						if(show_units && map[i][j].getUnit() != null) { 
							g.drawImage(map[i][j].getUnit().getImage(), j*TILE_WIDTH, i*TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT, null);
							g.setColor(Color.RED);
							g.setFont(new Font("Courier", Font.PLAIN, 16));
							g.drawString(map[i][j].getUnit().getTeam(),j*TILE_WIDTH, i*TILE_HEIGHT + TILE_HEIGHT);
						}
					}
				}
			}
		//--Draw Grid
			if (show_grid) {	
				g.setColor(Color.BLACK);
				if(debug >= 4 ) System.out.println(CONTENT_WIDTH + "\t" + "MW: " + MAP_WIDTH + "MH: " + MAP_HEIGHT + "\t" + TILE_WIDTH + "\t" + TILE_HEIGHT);
				for (int i = 0; i < MAP_WIDTH; i ++) { g.drawLine(i*TILE_WIDTH, 0, i*TILE_WIDTH, 720); }
				for(int i = 0; i < MAP_HEIGHT; i++) { g.drawLine(0,i*TILE_HEIGHT,CONTENT_WIDTH,i*TILE_HEIGHT); }
			}
		}
	}
}
