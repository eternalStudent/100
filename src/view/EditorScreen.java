package view;
import java.awt.Dimension;
import java.awt.Graphics;

import util.IntValue;
import util.Point;
import controller.Mouse;
import model.Grid;
import model.Tile;

@SuppressWarnings("serial")
public class EditorScreen extends Screen{
	
	private Grid grid;
	private TileKeyMap keyMap;
	private IntValue layer;
	
	public EditorScreen(Tileset tileset, TileKeyMap keyMap, Grid grid, IntValue layer, Mouse mouse){
		super(tileset);
		this.grid = grid;
		this.keyMap = keyMap;
		this.layer = layer;
		addMouseListener(mouse);
		addMouseMotionListener(mouse);
		setPreferredSize(new Dimension(grid.width*tileset.tw, grid.height*tileset.th));
	}
	
	@Override
	public void paint(Graphics g){
		super.paint(g);
		setPreferredSize(new Dimension(grid.width*tileset.tw, grid.height*tileset.th));
		for (int x=0; x<grid.width; x++)
			for(int y=0; y<grid.height; y++)
				for(int i=0; i<=layer.value; i++){
					Tile tile = keyMap.get(grid.get(i, x, y));
					if (tile != null){
						draw(g, tile, new Point(x, y));
					}	
			}
	}

}