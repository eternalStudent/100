package view;

import java.awt.Graphics;
import java.util.ConcurrentModificationException;

import util.Line;
import util.Point;
import model.Grid;
import model.mobs.MOB;
import model.Log;
import model.Tile;

@SuppressWarnings("serial")
public class GameScreen extends Screen{
	
	private final Grid grid;
	private final MOB player;
	private final Log log;
	private final TileKeyMap keyMap;
	
	public GameScreen(Grid grid, MOB player, Log log, Tileset tileset, TileKeyMap keyMap){
		super(tileset);
		this.grid = grid;
		this.player = player;
		this.log = log;
		this.keyMap = keyMap;
	}
	
	
	@Override
	public void paint(Graphics g){
		super.paint(g);
		try{
			grid.visual.addAll(player.visual);
			
		}
		catch (ConcurrentModificationException e){}
		try{
			for (Point p: grid.visual){
				Tile tile = keyMap.get(grid.get(p.x, p.y));
				if (grid.gas.contains(p))
					tile = tile.bg(2);
				draw(g, tile, p);
			}
		}
		catch (ConcurrentModificationException e){}	
		if (grid.cursor != null){
			if (grid.aim){
				Line line = new Line(player.x, player.y, grid.cursor.x, grid.cursor.y);
				for (int i=0; i<line.distance(); i++){
					Point p = line.get(i);
					Tile tile = keyMap.get(grid.get(p.x, p.y)).bg(11);
					if (grid.visual.contains(p))
						draw(g, tile, p);
				}
			}
			g.setColor(tileset.color(7));
			g.drawRect(grid.cursor.x*tileset.tw+1, (grid.cursor.y+1)*tileset.th-2, tileset.tw-2, 1);
		}
		for (Point p: grid.effects.keySet()){
			if (grid.visual.contains(p))
				draw(g, grid.effects.get(p), p);
		}
		g.setColor(tileset.color(15));
		writeTF(g, "HP: "+player.HP, grid.width, 0);
		writeTF(g, "weapon: "+player.weapon().name, grid.width, 1);
		writeTF(g, "floor: "+grid.floor, grid.width, 3);
		writeTF(g, "level: "+player.level, grid.width, 4);
		writeTF(g, "XP: "+(player.XP*100)/MOB.goal[player.level]+"%", grid.width, 5);
		if (player.ammo()<2 && player.weapon().isRanged())
			g.setColor(tileset.color(12));
		writeTF(g, "ammo: "+player.ammo(), grid.width, 2);
		if (player.weight()==0){
			g.setColor(tileset.color(15));
			writeTF(g, "light weight", grid.width, 7);
		}
		else if (player.weight()==1){
			g.setColor(tileset.color(14));
			writeTF(g, "medium weight", grid.width, 7);
		}
		else{
			g.setColor(tileset.color(12));
			writeTF(g, "heavy weight", grid.width, 7);
		}
		if (player.poison>0){
			g.setColor(tileset.color(10));
			writeTF(g, "poisoned!", grid.width, 8);
		}
		write(g, "need some help", 63, 19, 7);
		write(g, "with that?", 65, 20, 7);
		write(g, "press '?'", 66, 21, 7);
		String st[] = log.getLast();
		for (int i=0; i<log.cap; i++)
			write(g, st[i], 0, grid.height+i*2, 15);
	}


}
