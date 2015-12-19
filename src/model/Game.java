package model;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import controller.Keyboard;
import sound.Sound;
import util.Line;
import util.Point;
import util.Random;
import util.Words;
import view.Board;
import model.mobs.Inventory;
import model.mobs.Item;
import model.mobs.MOB;
import model.mobs.Trait;
import music.BackgroundMusic;

public class Game {
	
	public final MOB player;
	public Point[] darkEater = new Point[3];
	public final List<MOB> foes = new ArrayList<>();
	public final List<Item> items = new ArrayList<>();
	private final Grid grid = new Grid(80, 25);
	public final Log log = new Log(3);
	private final BackgroundMusic music;
	private MOB summoned;
	public boolean win = false;
	
	
	public Game(List<Trait> traits, BackgroundMusic music){
		this.music = music;
		player = new MOB(traits);
		this.music.play("main theme1", "main theme2");
		try {newFloor();} 
		catch (IOException | ParseException | URISyntaxException e) {}	
		if (player.weapon().isRanged())
			log.write("Press 'f' to aim and shot, when you see enemies approaching.");
		else
			log.write("To use melee attack simply walk toward your enemy's tile");
		log.write("Press 'd' to view your inventory.");
	}
	
	
	public void setGameScreen(Board board){
		board.setGameScreen(grid, player, log);
	}
	
	
	public int AI(MOB m){
		return new AI(grid, m).next();
	}
	
	
	public void newFloor() throws ParseException, URISyntaxException, IOException{
		if (grid.next())
			grid.setStairsAndFoes(player);
		foes.clear();
		darkEater[0] = null;
		items.clear();
		for (Point p: grid.map(Grid.MOBS).keySet()){
			String name = grid.get(Grid.MOBS, p.x, p.y);
			if (name.equals("player")){
				player.move(p.x, p.y);
				sight(player);
			}
			else{
				MOB m = MOB.newInstance(name, p.x, p.y);
				foes.add(m);
				sight(m);
				if (m.name.equals("Dark Eater")){
					darkEater[0] = new Point(m.x, m.y);
					darkEater[1] = new Point(m.x+1, m.y);
					darkEater[2] = new Point(m.x+2, m.y);		
				}
			}	
		}
		grid.set(Grid.MOBS, player.x, player.y, "player");
		if (darkEater[0] != null){
			grid.set(Grid.MOBS, darkEater[1].x, darkEater[1].y, "Dark Eater");
			grid.set(Grid.MOBS, darkEater[2].x, darkEater[2].y, "Dark Eater");
		}
		for (Point p: grid.map(Grid.ITEMS).keySet())
			items.add(Item.newInstance(grid.get(Grid.ITEMS, p.x, p.y), p.x, p.y));
		if (grid.floor>1)
			log.write("You reached the "+grid.floor+Words.ordinal(grid.floor)+" floor.");
	}
	
	
	public MOB findFoe(int x, int y){
		if (darkEater[0] != null)
			if ((darkEater[1].x == x && darkEater[1].y == y) || (darkEater[2].x == x && darkEater[2].y == y)){ 
				x = darkEater[0].x; 
				y = darkEater[0].y;
			}
		if (player.x==x && player.y==y)
			return player;
		for (MOB m: foes)
			if (m.x==x && m.y==y)
				return m;
		return null;
	}
	
	
	public Item findItem(int x, int y){
		for (Item item: items)
			if (item.x==x && item.y == y)
				return item;
		return null;
	}	
	
	
	public void death(MOB m){
		m.playSound();
		if (m!=player){
			log.write("The "+m+" is slain.");
			if (player.levelUp(m.XP))
				log.write("You are now level "+player.level+".");
		}
		else{
			m.inv.select(0);
			log.write("You are dead. Press ENTER to continue...");
			grid.set(Grid.TERRAIN, m.x, m.y, "corpse");
		}	
		if (grid.get(Grid.ITEMS, m.x, m.y) == null)
			grid.set(Grid.ITEMS, m.x, m.y, "blood"); //items won't appear at the same spot of the dead
		while (m.inv.size()>0)
			drop(m);	
		if (grid.get(Grid.ITEMS, m.x, m.y).equals("blood"))
			grid.remove(Grid.ITEMS, m.x, m.y);
		if (m.name.equals("Dark Eater")){
			for (int i=0; i<3; i++){
				if (grid.get(Grid.TERRAIN, darkEater[i].x, darkEater[i].y).equals("floor"))
					grid.set(Grid.TERRAIN, darkEater[i].x, darkEater[i].y, "blood");
				grid.remove(Grid.MOBS, darkEater[i].x, darkEater[i].y);
			}	
		}
		else{
			String name = grid.get(Grid.TERRAIN, m.x, m.y);
			if (name.endsWith("floor")){
				String terrain = m.name.endsWith("drone")? "scraps of metal": "blood";
				grid.set(Grid.TERRAIN, m.x, m.y, terrain);
			}
		}	
		grid.remove(Grid.MOBS, m.x, m.y);
		if (m.name.equals("Last Defender")){
			log.write("You killed the Last Defender of the tower. press ENTER to continue");
			win = true;
		}
		m.hold(); //will only be removed from list at the end of the round	
	}
	
	
	public boolean inSight(MOB m, int x, int y){
		Line line = new Line(m.x, m.y, x, y);
		Point p = new Point(m.x, m.y);
		int d = line.distance();
		if (d == 0){
			m.visual.add(p);
			return true;
		}	
		for (int i=0; i<d; i++){
			p = line.get(i);
			m.visual.add(p);
			if (!grid.isTransparent(p.x, p.y))
				break;
		}
		return p.equals(x, y);
	}
	
	
	public void sight(MOB m){
		m.visual.clear();
		int vision = m.stun>0? 1: m.vision;
		for (Point p: Point.sphere(m.x,  m.y,  vision))
			inSight(m, p.x, p.y);
		if (m.isWounded() && m.stun==0 && Point.distance2(m.x, m.y, player.x, player.y)<=8){
			inSight(m, player.x, player.y);
		}	
		if (m==player && foes.size()>0){
			boolean b = false;
			for (Point p: m.visual)
				if (!grid.visual.contains(p))
					b=true;
			if (b)
				m.healingFator();
		}	
	}
	
	
	public void move(MOB m, int dx, int dy){
		// door
		if (grid.get(Grid.TERRAIN, m.x+dx, m.y+dy).equals("closed door")){
			grid.set(Grid.TERRAIN, m.x+dx, m.y+dy, "open door");
			if (m==player)
				log.write("You open the door");
			m.hold();
			return;
		}
		
		// actual movement
		if (  !grid.isHalfSolid(m.x+dx, m.y+dy) || 
				( m.name.equals("unfolder") && grid.get(Grid.MOBS, m.x+dx, m.y+dy) == null )  ){
			if (m == player)
				m.target = new Point(dx, dy);
			if (m.name.equals("Dark Eater")){
				grid.remove(Grid.MOBS, darkEater[2].x, darkEater[2].y);
				grid.set(Grid.MOBS, m.x+dx, m.y+dy, "Dark Eater");
				darkEater[2] = darkEater[1];
				darkEater[1] = darkEater[0];
				darkEater[0] = new Point(m.x+dx, m.y+dy);
			}
			else
				grid.move(m, m.x+dx, m.y+dy);
			m.move(m.x+dx, m.y+dy);
			if (Math.abs(dx)+Math.abs(dy)==2)
				m.hold(Math.round(1.414f*(float)m.movement()));
			else
				m.hold();
			
			// presence of an item
			Item item = findItem(m.x, m.y);
			if (m==player && item != null){
				String text = "There's "+Words.article(item)+item+" there.";
				if (grid.floor == 1){
					text += " Press  'g' to pick it up.";
					if (item.isEquipable())
						text += " Press 'e' to equip it.";
				}	
				if (item.isExplosive())
					text += " Press 'e' to arm it.";
				if (item.isUsable())
					text += " Press 'u' to use it.";
				log.write(text);
			}	
			if ( m== player){
				String terrain = grid.get(Grid.TERRAIN, m.x, m.y);
				if (terrain.equals("toilet") || terrain.equals("sink"))
					log.write("Theres a "+terrain+" there. Press 'e' to use it.");
				if (terrain.equals("corpse") && grid.floor==19){
					log.write("There is something very familiar about this dead man,");
					log.write("but you ca't quite put your finger on it.");
					log.write("You wonder who this guy was, and what had happened to him.");
				}
			}	
		}
	}
	
	
	private boolean isCritical(MOB m){
		int r = Random.nextInt(20);
		if (r==0)
			return true;
		if (m == player && r<=player.level){
			if (m.weapon().isRanged()){
				if (m.inTraits("Headshot"))
					return true;
			}
			else{
				if (m.inTraits("Anatomy Expert"))
					return true;
			}
		}
		return false;
	}
	
	
	public boolean damage(MOB m, Item weapon, boolean isCritical, int extra){ //returns true if death
		if (m==null)
			return false;
		Item armor = m.armor();
		m.takeDamage(weapon.damage()+extra);
		if (isCritical){
			log.write("Critical Hit!");
			m.takeDamage(weapon.damage()+extra);
			if (!weapon.isRanged())
				m.takeDamage(weapon.damage()+extra);
		}	
		if (armor != m.armor() && m == player){
			Sound.play("armor breaks");
			log.write("You're armor has been destroyed.");
		}	
		if (m == player && (weapon.name.equals("serpent's bite") || weapon.name.equals("vule's bite"))){
			m.poison = Random.normal(80,  100);
			log.write("You feel the serpent's poison running in your veins");
		}
		if (m.name.equals("tingler") && !m.dead()){
			Sound.play("blink");
			do{
				Point p=Random.nextPoint(m.x-5, m.x+5, m.y-5, m.y+5);
				grid.move(m, p.x, p.y);
				m.move(p.x, p.y);
			}while(grid.isHalfSolid(m.x, m.y) || !inSight(m, player.x, player.y));
		}
		if (weapon.name.equals("stun grenade") && !m.name.endsWith("drone"))
			m.stun = 120;	
		if (m.dead()){
			death(m);
			return true;
		}
		return false;
	}
	
	
	public void detonation(Board board, int x0, int y0, Item weapon){
		log.write("The detonation drone explodes");
		int r = weapon.radius;
		boom(board, x0, y0, r);
		for (Point p: Point.sphere(x0, y0, r))
			damage(findFoe(p.x, p.y), weapon, false, 0);	
	}
	
	
	public void melee(Board board, MOB m0, MOB m1){
		if (!m0.weapon().isRanged()){
			int bonus = m0==player && player.inTraits("Incredible Speed")? 3: 0;
			if (m0==player && m0.weapon().name.equals("sabre") && m0.inTraits("Blade Dancer"))
				bonus+=3;
			m0.hold(m0.weapon().time-bonus);
			Sound.play(m0.weapon().name);
			
			//detonation
			if (m0.weapon().name.equals("detonation")){
				detonation(board, m0.x, m0.y, m0.weapon());
				return;
			}
			
			// log
			String text = m0==player? "You attack the "+m1+" with a " : "The "+m0+" attacks you with his ";
			text += m0.weapon()+".";
			log.write(text);
			
			// dodge
			int dodge = m1 == player && m1.weapon().name.equals("sabre") && m1.inTraits("Blade Dancer")? 30: m1.dodge;
			if (Random.nextInt(60)<dodge){
				text = m1==player? "you dodge": "The "+m1+"dodges";
				text += " the hit";
				log.write(text);
				return;
			}
			
			// hit
			if (damage(m1, m0.weapon(), isCritical(m0), m0.extraDamage()) && m0==player && player.inTraits("Thrill of the Kill")){
				if (m0.HP<m0.maxHP)
					m0.heal(3);
				else
					m0.maxHP+=3;
			}
			
			// effect
			grid.effects.put(new Point(m1.x, m1.y), new Tile((int)'\\', 15, 0));
			board.repaint();
			try {Thread.sleep(190);} 
			catch (InterruptedException e) {}
			grid.effects.clear();
			board.repaint();
		}	
	}
	
	
	public boolean get(MOB m, Item item){
		if (item == null)
			return false;
		if (m.get(item)){
			if (m == player){
				String text = "You take the "+item+".";
				if (grid.floor == 1)
					text += " Press 'd' to view inventory and equip.";
				log.write(text);
			}	
			return true;
		}
		if (m == player)
			log.write("You don't have enough room in your inventory for this.");
		return false;
	}
	
	
	public Item get(MOB m){
		Item item = findItem(m.x, m.y);
		boolean b = get(m, item);
		if (b){
			grid.remove(Grid.ITEMS, m.x, m.y);
			items.remove(item);
			m.hold();
			return item;
		}
		return null;
	}
	
	
	public void drop(MOB m, Item item){
		if (item == null)
			return;
		m.drop(item);
		Point p = grid.dropSpot(m.x, m.y);
		grid.set(Grid.ITEMS, p.x, p.y, item.name);
		items.add(item);
		item.move(p.x,  p.y);
		if (item.magazine != null && !item.isGrenade() && !item.magazine.name.equals("12ga shell"))
			drop(m, item.magazine);
		item.magazine = null;
	}
	
	
	public boolean drop(MOB m){
		if (m.inv.size() == 0)
			return false;
		Item item = m.inv.get();
		if (m==player && !m.dead()){
			String text = "You drop a "+item+".";
			if (item.isExplosive())
				text += " Press 'e' to arm it.";
			log.write(text);
		}	
		drop(m, item);
		return true;
	}
	
	
	public boolean equip(MOB m, Item item){
		if (item == null)
			return false;
		if (m.equip(item)){
			if (m == player)
				log.write("You equip the "+item+".");
			return true;
		}
		return false;
	}
	
	
	public boolean equip(MOB m){
		return equip(m, m.inv.get());
	}
	
	
	public void boom(Board board, int x0, int y0, int r){
		Tile[] tile = new Tile[3];
		tile[0] = new Tile(15, 12, 0);
		tile[1] = new Tile((int)'O', 14, 0);
		tile[2] = tile[0];
		for (int i=0; i<3; i++){
			for (Point p: Point.sphere(x0,  y0,  r))
				grid.effects.put(new Point(p.x, p.y), tile[i]);
			board.repaint();
			try {Thread.sleep(100);} 
			catch (InterruptedException e) {};
		}
		grid.effects.clear();
	}
	
	
	public Point aim(Board board, Keyboard keyboard){
		grid.aim = true;
		int x = player.x;
		int y = player.y;
		
		//Auto-Aim
		int minDis = player.vision+1;
		int XP=0;
		for (MOB m: foes){
			int d = Point.distance2(player.x, player.y, m.x, m.y);
			if ((d<minDis || (d == minDis && m.XP>XP)) && inSight(player, m.x, m.y)){
				x = m.x;
				y = m.y;
				minDis = d;
				XP = m.XP;
			}
		}
		
		//Manual-Aim
		int key = 0;
		do{				
			grid.cursor = new Point(x, y);	
			board.repaint();
			key = keyboard.get();
			Point p = Keyboard.ArrowToPoint(key);
			if (p != null){
				x += p.x;
				y += p.y;
			}
		}while (key != KeyEvent.VK_ENTER && key != KeyEvent.VK_F && key != KeyEvent.VK_ESCAPE);
		Point p = key == KeyEvent.VK_ESCAPE? new Point(player.x, player.y): grid.cursor;
		grid.cursor = null;
		grid.aim = false;
		return p;
	}
	
	
	private Point deflection(int dx, int dy, Point p){
		if (dy*(dy+1)*(dy-1)==0){
			if (Random.isNext())
				return p.add(0,-1);
			return p.add(0,+1);
		}	
		if (dx>1){
			if (dy>1){
				if (Random.isNext())
					return p.add(-1, 0);
				return p.add(0,-1);
			}
			if (dy<-1){
				if (Random.isNext())
					return p.add(-1, 0);
				return p.add(0,+1);
			}
		}
		if (dx<-1){
			if (dy>1){
				if (Random.isNext())
					return p.add(+1, 0);
				return p.add(0,-1);
			}
			if (dy<-1){
				if (Random.isNext())
					return p.add(+1, 0);
				return p.add(0,+1);
			}
		}
		if (Random.isNext()){
			return p.add(-1, 0);
		}
		return p.add(+1, 0);
	}
	
	
	public void fire(Board board, MOB m, Point target, Item weapon){
		if (m.x == target.x && m.y == target.y)
			return;
		m.fire(weapon);
		int bonus = m.inTraits("Rapid Fire")? 1: 0;
		m.hold(weapon.time-bonus);
		
		// log
		String text;
		if (weapon.isGrenade())
			text = m==player? "You throw a grenade.": "The "+m.name+" throws a grenade at you";
		else
			text = m==player? "You fire your weapon.": "The "+m.name+" fires his weapon.";
		log.write(text);
		
		// deflection and bullet dodging
		int d = Point.distance2(m.x, m.y, target.x, target.y);
		if (Random.nextInt(d+m.precision+weapon.accuracy)<d && !m.inTraits("Eagle Eye"))
			target = deflection(target.x-m.x, target.y-m.y, target);
		else{
			if (target.equals(player.x, player.y) && player.inTraits("Dodge Bullets") && Random.isNext() 
					&& player.target != null && !weapon.isGrenade() && player.weight()==0){
				move(player, player.target.x, player.target.y);
				log.write("You try to dodge the bullet");
			}
		}
		
		// effect
		Line line = new Line(m.x, m.y, target.x, target.y);
		sight(player);
		int i=Math.min(line.distance(), 5);
		do{
			i--;
			target = line.next();
			grid.effects.clear();
			grid.effects.put(target, new Tile(249, 15, 0));
			board.repaint();
			try {Thread.sleep(10);} 
			catch (InterruptedException e) {}
			if (grid.get(Grid.TERRAIN, target.x, target.y).equals("closed door"))
				grid.set(Grid.TERRAIN, target.x, target.y, "floor");
			if (grid.isSolid(target.x, target.y))
				break;
		}while(i>0 || !weapon.isGrenade());
		int r = weapon.radius;
		boom(board, target.x, target.y, r);
		
		// missing
		if (r==0 && grid.get(Grid.MOBS, target.x, target.y) == null){
			text = m==player? "You miss": "The "+m.name+" misses.";
			log.write(text);
			return;
		}
		
		// hitting
		Point nextInLine = line.next();
		for (Point p: target.sphere(r)){
			boolean isCritical = weapon.isGrenade()? false: isCritical(m);
			MOB m1 = findFoe(p.x, p.y);
			if (!damage(m1, weapon, isCritical, m.extraDamage(weapon))){
				if ((weapon.name.endsWith("shotgun") || weapon.name.equals(".45 ACP handgun")) && 
						!m1.name.equals("kapre") && !m1.name.equals("elfilim") && !m1.name.equals("Dark Eater")
						&& !grid.isHalfSolid(nextInLine.x, nextInLine.y) && grid.get(Grid.MOBS, nextInLine.x, nextInLine.y) == null){
					grid.move(m1, nextInLine.x, nextInLine.y);
					m1.move(nextInLine.x, nextInLine.y);
					board.repaint();
				}
			}
			if (m1 != null && !m1.name.endsWith("drone")){
				if (grid.get(Grid.TERRAIN, nextInLine.x, nextInLine.y).endsWith("floor"))
					grid.set(Grid.TERRAIN, nextInLine.x, nextInLine.y, "bloody floor");
				if (grid.get(Grid.TERRAIN, nextInLine.x, nextInLine.y).endsWith("wall"))
					grid.set(Grid.TERRAIN, nextInLine.x, nextInLine.y, "bloody wall");			
			}
			if (weapon.name.equals("gas grenade") && !grid.get(Grid.TERRAIN, p.x, p.y).endsWith("wall"))
				grid.gas.add(p);
		}	
	}
	
	
	public void inventory(Board board, Keyboard keyboard){
		Inventory inv = player.inv;
		inv.select(0);
		board.setInventory(inv);
		int key;
		boolean hold = false;
		do{
			key = keyboard.get();
			if (key == KeyEvent.VK_UP)
				inv.decSelect();
			if (key == KeyEvent.VK_DOWN)
				inv.incSelect();
			if (key == KeyEvent.VK_D){
				drop(player);
				hold = true;
				break;
			}	
			if (key == KeyEvent.VK_E){
				if (equip(player)){
					hold = true;
					break;
				}	
			}	
			if (key == KeyEvent.VK_Z)
				player.ready();
			if (key == KeyEvent.VK_U){
				if (use(player)){
					hold = true;
					break;
				}	
			}	
			board.repaint();
		}while(key != KeyEvent.VK_ENTER && key!= KeyEvent.VK_ESCAPE);	
		board.setGameScreen(grid, player, log);
		if (hold)
			player.hold();
	}
	
	
	public void reload(MOB m){
		Item magazine = m.weapon().magazine;
		int rounds = m.ammo();
		boolean inSight = grid.visual.contains(new Point(m.x, m.y));
		if (m.reload(inSight)){
			if (inSight){
				String text = m==player? "You reload your weapon.": "The "+m.name+" reloads his weapon.";
				if (m == player && grid.floor == 1)
					text += " Press 'f' to aim and fire.";
				log.write(text);
			}	
			if (magazine != null && rounds>0 && !magazine.name.equals("12ga shell"))
				drop(m, magazine);
			m.hold(m.weapon().reloadTime);
		}
	}
	
	
	public void swap(MOB m){
		m.swap();
		String text = m==player? "You switched your weapon to ": "The "+m.name+" switched his weapon to ";
		text += m.weapon().name+".";
		log.write(text);
		m.hold(4);
	}
	
	
	public boolean use(MOB m, Item item){
		m.use(item);
		if (item != null){
			String text = m==player? "You use ": "The "+m+" uses ";
			text +=Words.article(item)+item+".";
			log.write(text);
			if (item == findItem(m.x, m.y))
				grid.remove(Grid.ITEMS, m.x, m.y);
			return true;
		}
		return false;
	}
	
	
	public boolean use(MOB m){
		return use(m, m.inv.get());
	}
	
	
	public void activate(MOB m){
		Item item = findItem(m.x, m.y);
		if (item != null){
			if (item.isExplosive()){
				item.armed = true;
				m.hold();
				if (m==player)
					log.write("You arm the explosives");
				return;
			}
		}
		String terrain = grid.get(Grid.TERRAIN, m.x, m.y);
		if (terrain.equals("toilet")){
			Sound.play("toilet");
			log.write("You use the toilet, a much needed relief.");
		}
		if (terrain.equals("sink")){
			Sound.play("sink");
			log.write("You wash your hands in the sink.");
		}
	}
	
	
	public void look(Board board, Keyboard keyboard){
		int key = 0;
		int x = player.x;
		int y = player.y;
		do{				
			grid.cursor = new Point(x, y);	
			board.repaint();
			key = keyboard.get();
			Point p = Keyboard.ArrowToPoint(key);
			if (p != null){
				x += p.x;
				y += p.y;
			}
		}while (key != KeyEvent.VK_ENTER && key != KeyEvent.VK_ESCAPE);
		if (key != KeyEvent.VK_ESCAPE){
			String thingy = grid.get(x, y);
			String text = grid.visual.contains(new Point(x, y))? "(that's "+Words.article(thingy)+thingy+")": "(You can't see there yet.)";
			log.write(text);
		}	
		grid.cursor = null;
	}
	
	
	public void stairway(){
		String terrain = grid.get(Grid.TERRAIN, player.x, player.y);
		if (terrain.equals("stairway") || terrain.equals("elevator")){
			try {
				if (terrain.equals("elevator")){
					grid.floor+=5;
					Sound.play("elevator");
				}	
				else
					Sound.play("stairway");
				newFloor();
				if (grid.floor == 30)
					Sound.play("Killer floor");
				if (grid.floor == 45)
					Sound.play("Harbinger floor");
				if (grid.floor == 60)
					Sound.play("Dark Eater floor");
				if (grid.floor == 90)
					Sound.play("Nitemare floor");
				if (grid.floor == 7)
					music.play("office");
				if (grid.floor % 15 == 0 || grid.floor == 100)
					music.play("boss fight1", "boss fight2");
				if (grid.floor % 15 == 1 || grid.floor == 8)
					music.play("main theme1", "main theme2");
			} 
			catch (IOException | ParseException | URISyntaxException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public void suicide(Keyboard keyboard){
		log.write("Press ENTER to kill yourself, or any other key to keep going.");
		if (keyboard.get() == KeyEvent.VK_ENTER){
			player.HP=0;
			death(player);
		}
	}
	
	
	public void scream(MOB screamer){
		if (foes.size()>1){
			log.write("the screamer screams");
			Sound.play(screamer.weapon().name);
			int i;
			do{
				i = Random.nextInt(foes.size());
			}while (screamer==foes.get(i));
			int x, y;
			do{
				x=screamer.x+Random.nextInt(5)-2;
				y=screamer.y+Random.nextInt(5)-2;
			}while(grid.isHalfSolid(x, y) || !screamer.visual.contains(new Point(x, y)) || grid.get(Grid.MOBS, x, y)!=null);	
			grid.move(foes.get(i), x, y);
			foes.get(i).move(x, y);
			screamer.hold(screamer.weapon().time);
		}
	}
	
	public void summon(MOB m){
		String[] minions = {"kapre", "inspector", "officer", "overseer", "giant herdling", "soldier", "swordsman"};
		int r = Random.nextInt(minions.length);
		Point p = grid.summon(minions[r]);
		summoned = MOB.newInstance(minions[r], p.x, p.y);
		sight(summoned);
		m.hold(9);
	}
	
	
	public void clone(MOB m){
		Point p;
		do{
			p = Random.nextPoint(m.x-2, m.x+2, m.y-2, m.y+2);
		}while( grid.isHalfSolid(p.x, p.y) || grid.get(Grid.MOBS, p.x, p.y) != null );
		grid.set(Grid.MOBS, p.x, p.y, "ravelie");
		summoned = MOB.newInstance("ravelie", p.x, p.y);
		sight(summoned);
		m.hold(9);
	}
	
	
	public void breathFire(MOB m, Board board){
		Sound.play(m.weapon().name);
		log.write("The "+m+" attacks you with his fire breath.");
		Line line = new Line(m.x, m.y, player.x, player.y);
		for (int i=0; i<line.distance(); i++){
			Point p = line.get(i);
			grid.effects.put(new Point(p.x, p.y), new Tile(15, 14, 12));
			damage(findFoe(p.x, p.y), m.weapon(), false, 0);
			String terrain = grid.get(Grid.TERRAIN, p.x, p.y);
			if (terrain.equals("table") || terrain.equals("closed door"))
				grid.set(Grid.TERRAIN, p.x, p.y, "floor");
		}
		board.repaint();
		try {Thread.sleep(150);} 
		catch (InterruptedException e) {};
		grid.effects.clear();
		board.repaint();
		m.hold(m.weapon().time);
	}
	
	
	public void heal(MOB m, Board board){
		Sound.play("heal");
		Set<Point> sphere = Point.sphere(m.x, m.y, 7);
		for (Point p: sphere){
			MOB m1 = findFoe(p.x, p.y);
			if (m1 != null && Random.isNext()){
				grid.effects.put(p, board.keyMap.get(m1.name).bg(15));
				m1.heal(10);
			}
		}
		board.repaint();
		try {Thread.sleep(400);} 
		catch (InterruptedException e) {};
		grid.effects.clear();
		board.repaint();
		m.hold();
	}
	
	public void coldTouch(MOB m, Board board){
		Point p;
		do{
			p = Random.nextPoint(-1, 1, -1, 1);
		}while(p.x == 0 || p.y == 0);
		grid.effects.put(p.add(player.x, player.y), board.keyMap.get("Nitemare"));
		grid.effects.put(new Point(m.x, m.y), board.keyMap.get(grid.get(Grid.TERRAIN, m.x, m.y)));
		board.repaint();
		try {Thread.sleep(400);} 
		catch (InterruptedException e) {};
		Sound.play("cold touch");
		grid.effects.put(new Point(player.x, player.y), new Tile((int)'\\', 15, 0));
		board.repaint();
		try {Thread.sleep(190);} 
		catch (InterruptedException e) {};
		grid.effects.remove(new Point(player.x, player.y));
		board.repaint();
		try {Thread.sleep(200);} 
		catch (InterruptedException e) {};
		grid.effects.clear();
		board.repaint();
		damage(player, m.weapon(), false, 0);
		m.HP += 8;
		m.hold(Random.normal(85, 140));
	}
	
	
	public void poison(MOB m){
		if (m.poison())
			death(m);
		else if (grid.gas.contains(new Point(m.x, m.y)) && !m.isMasked() && !m.name.endsWith("drone"))
			m.poison = Random.normal(160, 200);
	}
	
	
	public void relocate(MOB m){
		Point p;
		do{
			p = Random.nextPoint(1, grid.width-2, 1, grid.height-2);
		}while( grid.isHalfSolid(p.x, p.y));
		grid.move(m, p.x, p.y);
		m.move(p.x, p.y);
	}
	
		
	public void endOfRound(Board board){
		player.waited();
		poison(player);
		if (summoned != null){
			foes.add(summoned);
			summoned = null;
		}	
		Iterator<MOB> it1 = foes.iterator();
		while(it1.hasNext()){
			MOB m = it1.next();
			if (m.dead())
				it1.remove();
			else{
				m.waited();
				poison(m);
				if (m.name.equals("Harbinger") && Random.isNext(6) && m.isWounded()){
					log.write("The Harbinger heal himself");
					m.heal(1);
				}
				if (m.name.equals("Last Defender") && Random.isNext(20) && m.isWounded())
					m.heal(1);
				if (m.name.equals("Last Defender") && Random.isNext(90))
					relocate(m);
				if (m.stun>0){
					m.visual.remove(new Point(player.x, player.y));
					m.stun--;
				}
			}	
		}	
		Iterator <Item> it2 = items.iterator();
		while(it2.hasNext()){
			Item item = it2.next();
			if (item.isExplosive() && item.armed){
				if (item.timer>0)
					item.timer--;
				else{
					Sound.play(item.name);;
					int r = item.radius;
					boom(board, item.x, item.y, r);
					for (Point p: Point.sphere(item.x, item.y, r)){
						damage(findFoe(p.x, p.y), item, false, 0);
						if (!grid.get(Grid.TERRAIN, p.x, p.y).equals("stairway"))
							grid.set(Grid.TERRAIN, p.x, p.y, "floor");
						grid.remove(Grid.ITEMS, p.x, p.y);
					}	
					it2.remove();
				}
			}
		}
		Set<Point> gas = new HashSet<Point>();
		for (Point p: grid.gas){
			for (int x=-1; x<2; x++)
				for (int y=-1; y<2; y++)
					if (Random.isNext(76) && !grid.get(Grid.TERRAIN, x+p.x, y+p.y).endsWith("wall"))
						gas.add(new Point(p.x+x, p.y+y));
		}
		grid.gas.addAll(gas);
	}

}
