package view;
import java.awt.Graphics;

import model.mobs.Inventory;
import model.mobs.Item;

@SuppressWarnings("serial")
public class InventoryScreen extends Screen{
	
	private final Inventory inv;
	private final TileKeyMap keyMap;
	
	public InventoryScreen(Inventory inv, Tileset tileset, TileKeyMap keyMap){
		super(tileset);
		this.inv = inv;
		this.keyMap = keyMap;
	}
	
	public void paint(Graphics g){
		super.paint(g);
		write(g, "INVENTORY", 35, 1, 15);
		g.setColor(tileset.color(7));
		writeTF(g, "Press ENTER or ESC to exit screen   Press 'd' to drop and exit   Press 'u' to use and exit   Press 'e' to equip and exit   Press 'z' to ready weapon", 0, getHeight()/tileset.th-1);
		for (int i=0; i<inv.size(); i++){
			Item item = inv.get(i);
			if (inv.isSelected(i)){
				g.setColor(tileset.color(3));
				g.fillRect(tileset.tw*2, (i+3)*tileset.th, tileset.tw*16, tileset.th);
				g.setColor(tileset.color(7));
				writeDesc(g, item.desc, 23, 2, 45);
			}
			String name = item.name;
			draw(g, keyMap.get(name), 1, i+3);
			if (item.isAmmo())
				name += " ("+item.rounds+")";
			if (item.isArmor())
				name += " ("+item.HP+")";
			if (item.equiped){
				if (item.isAmmo())
					writeTF(g, "F", 0, i+3);
				else
					writeTF(g, "E", 0, i+3);
			}	
			if (inv.isReady(item))
				writeTF(g, "R", 0, i+3);
			writeTF(g, name, 2, i+3);
		}
	}

}
