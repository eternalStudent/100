package model;
import java.awt.event.KeyEvent;

import util.Point;
import util.Random;
import model.mobs.Item;
import model.mobs.MOB;

public class AI {
	
	private final Grid grid;
	private final MOB foe;
	
	public AI(Grid grid, MOB foe){
		this.grid = grid;
		this.foe = foe;
	}
	
	public int next(){
		if (foe.stun>0)
			return toKey(Random.nextInt(-1,1), Random.nextInt(-1,1));
		Point player = playerInSight();
		if ((foe.weapon().isRanged() && foe.ammo()==0) || foe.weapon() ==  Item.NO_WEAPON)
			return KeyEvent.VK_Z;
		if (player != null){
			if (foe.weapon().isRanged())
				if (foe.weapon().cartridge.equals("12ga shell") && foe.weapon().rounds==0)
					return KeyEvent.VK_R;
			foe.target = player;
			if (foe.weapon().isRanged())
				return KeyEvent.VK_F;
			if (foe.name.equals("screamer")){ 
				if (Random.isNext())
					return KeyEvent.VK_S;
				return 0;
			}	
		}	
		else{
			if (foe.name.endsWith("drone"))
				return 0;
			if (foe.weapon().isRanged())
				if (foe.weapon().cartridge.equals("12ga shell") && foe.weapon().rounds<foe.weapon().capacity)
					return KeyEvent.VK_R;
			if (foe.target.equals(foe.x,  foe.y))
			foe.target = randomTarget();
		}	
		int x = sgn(foe.target.x-foe.x);
		int y = sgn(foe.target.y-foe.y);
		if (grid.isHalfSolid(x+foe.x, y+foe.y) && !foe.target.equals(foe.x+x, foe.y+y)){
			if (x==0){
				if (!grid.isHalfSolid(foe.x-1, y+foe.y))
					x=-1;
				else
					x=1;
			}
			else if (y==0){
				if (!grid.isHalfSolid(foe.x+x, foe.y-1))
					y=-1;
				else
					y=1;
			}
			else{
				if (!grid.isHalfSolid(foe.x+x, foe.y))
					y=0;
				else
					x=0;
			}
			
		}
		return toKey(x, y);
	}
	
	private Point playerInSight(){
		if (foe.stun>0)
			return null;
		for(Point p: foe.visual){
			String st = grid.get(Grid.MOBS, p.x, p.y);
			if (st == null)
				continue;
			if (st.equals("player"))
				return p;
		}	
		return null;
	}
	
	private Point randomTarget(){
		Point target = null;
		do{
			target = Random.nextElement(foe.visual);
		}while(grid.isHalfSolid(target.x,  target.y));	
		return target;
	}
	
	private static int toKey(int x, int y){
		if (x==-1 & y==-1)
			return KeyEvent.VK_HOME;
		if (x==0 && y==-1)
			return KeyEvent.VK_UP;
		if (x==1 && y==-1)
			return KeyEvent.VK_PAGE_UP;
		if (x==-1 && y==0)
			return KeyEvent.VK_LEFT;
		if (x==1 && y==0)
			return KeyEvent.VK_RIGHT;
		if (x==-1 && y==1)
			return KeyEvent.VK_END;
		if (x==0 && y==1)
			return KeyEvent.VK_DOWN;
		if (x==1 && y==1)
			return KeyEvent.VK_PAGE_DOWN;
		return 0;
	}
	
	private static int sgn(int i){
		if (i>0)
			return 1;
		if (i<0)
			return -1;
		return 0;
	}

}
