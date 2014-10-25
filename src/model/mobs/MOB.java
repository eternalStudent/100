package model.mobs;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import util.Point;
import util.Random;

public class MOB{
	
	public static FoesMap foes = new FoesMap();
	public static int[] goal = {40, 40, 80, 120, 200, 320, 520, 840, 1360, 2200, 3560, 5760, 9320, 15080, 24400};
	
	public String name;
	public int x,y;
	public int HP;
	public int maxHP;
	public int XP = 0;
	public int vision;
	public int precision = 10;
	private int move;
	public int dodge;
	private int wait = 0;
	public int healingFactor = 0;
	public int level = 1;
	public int poison = 0;
	public int stun = 0;
	public Set<Point> visual = new HashSet<>();
	public Inventory inv = new Inventory();
	public Point target;
	public List<Trait> traits = new ArrayList<>();
	
	protected MOB(String name, int HP, int XP, int vision, int move, int dodge, 
			String weapon, String ready, String armor){
		this.name =  name;
		this.HP=HP;
		maxHP = HP;
		this.XP =XP;
		this.vision = vision;
		this.move = move;
		this.dodge = dodge;
		arm(ready);
		ready(weapon());
		arm(weapon);
		if (armor != null)
			equip(Item.clone(armor));
		
	}
	
	public MOB(List<Trait> traits){
		this.name = "player";
		this.traits = traits;
		HP = inTraits("Endurance")? 60: 40;
		maxHP = HP;
		precision = 30;
		dodge = inTraits("Incredible Speed")? 20: 6;
		vision = inTraits("Eagle Eye")? 8: 7;
		move = inTraits("Incredible Speed")? 3: 7;
		if (!inTraits("Claws")){
			if (inTraits("Inhuman Strength"))
				arm("brass knuckles");
			if (inTraits("Thrill of the Kill"))
				arm("machete");
			if (inTraits("Anatomy Expert"))
				arm("axe");
		}	
		if (inTraits("Blade Dancer"))
			arm("sabre");
		if (inTraits("Rapid Fire") || inTraits("Eagle Eye") || inTraits("Headshot")){
			arm("9mm handgun");
			get(Item.clone(weapon().cartridge));
			get(Item.clone(weapon().cartridge));
		}
		if (weapon() == Item.UNARMED){
			arm("revolver");
			get(Item.clone(weapon().cartridge));
			get(Item.clone(weapon().cartridge));
		}
		if (inTraits("Explosives Expert")){
			for (int i=0; i<3; i++)
				get(Item.clone("type I frag grenade"));
			get(Item.clone("type II frag grenade"));
			get(Item.clone("type II frag grenade"));
			get(Item.clone("plastic explosive"));
		}	
		
	}
	
	public static MOB foe(String name){
		return foes.get(name);
	}

	public static MOB clone(MOB other){
		String ready = other.grenade()==null? other.readied().name: other.grenade().name;
		return new MOB(other.name, other.HP, other.XP, other.vision, other.move, other.dodge, 
				other.weapon().name, ready, other.armor().name);
	}
	
	public static MOB clone(String name){
		return clone(foe(name));
	}
	
	public static MOB newInstance(String name, int x, int y){
		MOB m = clone(name);
		m.move(x, y);
		m.target = new Point(x, y);
		return m;
	}
	
	public void arm(String weapon){
		if (weapon !=null){
			equip(Item.clone(weapon));
			if (weapon().isRanged() && !weapon().isGrenade()){
				get(Item.clone(weapon().cartridge));
				reload(false);
				if (weapon().cartridge.equals("12ga shell"))
					get(Item.clone("12ga shell"));
			}	
		}
	}
	
	protected URL sound(){
		String name = this.name;
		if (name.startsWith("."))
			name = name.substring(1);
		return getClass().getResource("/sound/"+name+".wav");
	}
	
	public void playSound(){
		try{
			URL url = sound();
		    Clip clip = AudioSystem.getClip();
		    AudioInputStream ais = AudioSystem.getAudioInputStream( url );
		    clip.open(ais);
		    clip.start();
		}
		catch(Exception e){
			System.out.println(name);
		}
	}
	
	public void move(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public boolean get(Item item){
		if (inv.space+item.space<=25){
			inv.add(item);
			return true;
		}
		return false;
	}
	
	public Item drop(){
		return inv.remove();
	}
	
	public Item drop(int i){
		return inv.remove(i);
	}
	
	public void drop(Item item){
		inv.remove(item);
	}
	
	public Item weapon(){
		if (inv.weapon == null){
			if (name.equals("player")){
				if (inTraits("Claws"))
					return Item.CLAWS;
				if (hasKnuckles())
					return Item.KNUCKLES;
				return Item.UNARMED;
			}	
			return Item.NO_WEAPON;
		}	
		return inv.weapon;
	}
	
	public int ammo(){
		if (!weapon().isRanged())
			return 0;
		if (weapon().cartridge.equals("12ga shell"))
			return weapon().rounds;
		if (inv.magazine()==null)
			return 0;
		return inv.rounds();
	}
	
	public Item readied(){
		if (inv.readied == null){
			if (name.equals("player")){
				if (inTraits("Claws"))
					return Item.CLAWS;
				return Item.UNARMED;
			}	
			return Item.NO_WEAPON;
		}	
		return inv.readied;
	}
	
	public Item armor(){
		if (inv.armor == null)
			return Item.NO_ARMOR;
		return inv.armor;
	}
	
	public Item grenade(){
		return inv.getGrenade();
	}
	
	public void fire(Item weapon){
		if (weapon.isGrenade())
			inv.throwGrenade();
		else	
			inv.fire();
	}
	
	public boolean equip(){
		return inv.equip();
	}
	
	public boolean equip(Item item){
		return inv.equip(item);
	}
	
	public boolean reload(boolean playSound){
		return inv.reload(playSound);
	}
	
	public boolean reload(){
		return inv.reload();
	}
	
	public boolean ready(Item item){
		return inv.ready(item);
	}
	
	public boolean ready(){
		return inv.ready();
	}
	
	public Item use(Item item){
		inv.use(item);
		if (item != null){
			if (item.name.equals("bandage"))
				heal(5);
			if (item.name.equals("adrenaline syringe")){
				maxHP -= 20;
				heal(20);
			}
		}
		return item;
	}
	
	public Item use(){
		return use(inv.get());
	}
	
	public void swap(){
		inv.swap();
	}
	
	public int weight(){
		if (inTraits("Inhuman Strength"))
			return 0;
		return inv.weight();
	}
	
	public int movement(){
		return move+weight();
	}
	
	public void hold(int i){
		wait = i;
	}
	
	public void hold(){
		hold(movement());
	}
	
	public void waited(){
		if (wait>0)
			wait--;
	}
	
	public boolean isMasked(){
		return inv.isMasked();
	}
	
	public boolean hasKnuckles(){
		return inv.hasKnuckles();
	}
	
	public boolean hisTurn(){
		return wait == 0;	
	}
	
	public boolean isWounded(){
		return HP < maxHP;
	}
	
	public boolean dead(){
		return HP<=0;
	}
	
	public int damage(){
		int bonus = !weapon().isRanged() && inTraits("Inhuman Strength")? 7: 0;
		if (weapon().name.equals("claws"))
			bonus+=level;
		if (weapon().name.equals("sabre") && inTraits("Blade Dancer"))
			bonus+=6;
		return weapon().damage()+bonus;
	}
	
	public void takeDamage(int amount){
		amount -= armor().damage;
		if (armor()!=Item.NO_ARMOR){
			if (amount>0)
				armor().HP -= Math.min(armor().damage, amount);
			if (armor().HP<=0){
				inv.remove(armor());
			}
		}	
		if (inTraits("Natural Armor"))
			amount -= 1+level/3;
		if (amount>0)
			HP -= amount;
	}
	
	public void heal(int amount){
		HP+=amount;
		if (HP>maxHP)
			HP=maxHP;
	}
	
	public void healingFator(){
		int factor = inTraits("Faster Healing Factor")? 9: 12;
		healingFactor++;
		if (healingFactor>=factor){
			heal(1);
			healingFactor-=factor;
		}	
	}
	
	public boolean poison(){
		if (poison>0){
			poison--;
			if (Random.isNext(18))
				HP--;	
		}	
		return dead();
	}
	
	public boolean levelUp(int XP){
		this.XP+=XP;
		if (this.XP>=goal[level]){
			this.XP-=goal[level];
			level++;
			maxHP+=10;
			heal(10);
			precision+=2;
			return true;
		}	
		return false;
	}
	
	public boolean inTraits(String trait){
		if (name != "player")
			return false;
		return traits.get(0).name.equals(trait) || traits.get(1).name.equals(trait)
				|| traits.get(2).name.equals(trait);  
	}
	
	public String toString(){
		return name;
	}

}
