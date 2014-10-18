package view;

import java.awt.Graphics;

import util.IntValue;
import model.Tile;

@SuppressWarnings("serial")
public class MenuScreen extends Screen{
	
	private final IntValue option;
	
	protected MenuScreen(Tileset tileset, IntValue option){
		super(tileset);
		this.option = option;
	}
	
	public void paint(Graphics g){
		super.paint(g);
		write(g, "HUNDRED", 36, 6, 7);
		write(g, "New Game", 36, 16, 7);
		write(g, "Load Game", 36, 18, 8);
		write(g, "Help", 36, 20, 7);
		write(g, "Quit", 36, 22, 7);
		draw(g, new Tile(16, 14, 0), 33, 16+option.value*2);
	}

}
