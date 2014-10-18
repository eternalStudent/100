package view;

import java.awt.Graphics;
import java.util.List;
import model.Tile;
import model.mobs.ListOfTraits;
import model.mobs.Trait;
import util.IntValue;

@SuppressWarnings("serial")
public class TraitsScreen extends Screen{
	
	private final IntValue option;
	private final List<Trait> traits;
	private final ListOfTraits list;

	protected TraitsScreen(Tileset tileset, IntValue option, List<Trait> traits, ListOfTraits list) {
		super(tileset);
		this.option = option;
		this.traits = traits;
		this.list = list;
	}
	
	public void paint(Graphics g){
		super.paint(g);
		write(g, "TRAITS", 37, 0, 15);
		g.setColor(tileset.color(7));
		writeTF(g, "The corruption has touched you as well, not only that you heal faster than others you also have a unique traits that will help you in your quest, choose three of the following", 1, 1);
		writeTF(g, "using the up/down arrow keys and enter to select. When you're done press '>'", 1, 2);
		for (int i=0; i<list.size(); i++){
			int cl =7;
			if (traits.contains(list.get(i))){
				cl = 3;
				draw(g, new Tile(7, 3, 0), 3, i*2+4);
			}	
			if (i==option.value)
				cl=14;
			write(g, list.get(i).name, 5, i*2+4, cl);
			if (i==option.value)
				writeDesc(g, list.get(i).desc, 30, 4, 40);
		}
	}


}
