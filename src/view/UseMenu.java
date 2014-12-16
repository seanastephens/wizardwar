package view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import model.WizardWar;
import Items.Item;
import Units.Unit;
/**
 * 
 * @author N R Callahan
 * 
 */
public class UseMenu extends JPopupMenu {
	WizardWar game;
	Unit u;
	public UseMenu(WizardWar game) {
		super();
		this.game = game;
		
	}
	
	public void setUnit(Unit u) { this.u = u; }
	
	public void setUpMenu() {
		if(u == null) {	return; }
		List<Item> items = u.getItemsList();
		if(items.size() == 0) { add(new JMenuItem("No items to use")); }
		else { 
			for (Item i : items) {
				JMenuItem menuItem = new JMenuItem("Use " + i.getName());
				menuItem.setActionCommand(i.getUID() +"");
				menuItem.addMouseListener(new MenuListener());
				add(menuItem);
			}
		}
	}
	public void clearMenu() { this.removeAll(); }
	
	private class MenuListener extends MouseAdapter {
		@Override
		public void mousePressed (MouseEvent e) {
			for(Item i : u.getItemsList()) {
				if((i.getUID() +"").equals(((JMenuItem)e.getSource()).getActionCommand())) {
					System.err.println("Use Item" + i.getName() + "  " + i.getUID());
					game.UseItem(u, i);
				}
			}
		}
	}
}
