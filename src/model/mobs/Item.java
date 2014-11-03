package model.mobs;

import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import util.Random;

public class Item{
	
	private static final ItemsMap items = new ItemsMap();
	private static final char MELEE   = 'M';
	private static final char RANGED  = 'R';
	private static final char AMMO    = 'a';
	private static final char ARMOR   = 'A';
	private static final char USABLE  = 'U';
	private static final char NATURAL = 'N';
	private static final char GRENADE = 'G';
	private static final char EXPLOSIVE='X';
	private static final char EQUIPABLE='E';
	
	public static final Item UNARMED;
	public static final Item CLAWS;
	public static final Item NO_WEAPON;
	public static final Item NO_ARMOR;
	static{
		UNARMED = clone("unarmed");
		CLAWS = clone("claws");
		NO_WEAPON = new Item("none", 'N',"no weapon", 0, 0);
		NO_ARMOR = new Item(null, 'N', "no armor", 0, 0);
	}
	
	public final String name;
	private final char type;
	public final String desc;
	public final int weight;
	public final int space;
	public int x, y;
	public int rounds;
	protected int damage = 0;
	public int time;
	public String cartridge;
	public int accuracy;
	public int capacity;
	public int reloadTime;
	public int radius;
	public boolean equiped = false;
	public boolean armed = false;
	public Item magazine = null;
	public int timer = 0;
	public int HP;
	
	protected Item(String st){
		this(st, 'U', "unknown object", 0, 0);
	}
	
	protected Item(String name, char type, String desc, int weight, int space){
		this.name = name;
		this.type = type;
		this.desc = desc;
		this.weight = weight;
		this.space = space;
	}
	
	private static Item item(String st){
		return items.get(st);
	}

	protected static Item clone(Item other){
		Item item = new Item(other.name, other.type, other.desc, other.weight, other.space);
		item.accuracy = other.accuracy;
		item.capacity = other.capacity;
		item.cartridge = other.cartridge;
		item.damage = other.damage;
		item.radius = other.radius;
		item.reloadTime = other.reloadTime;
		item.rounds = other.rounds;
		item.time = other.time;
		if (item.isExplosive())
			item.timer = 60;
		if (item.isArmor())
			item.HP = 40;
		return item;
	}
	
	protected static Item clone(String st){
		return clone(item(st));
	}
	
	public static Item newInstance(String name, int x, int y){
		Item item = clone(name);
		item.move(x,  y);
		return item;
	}
	
	public static Item randomItem(){
		Item item = null;
		do{
			item = clone(Random.nextElement(items.keySet()));
		}while(item.isNatural());
		return item;
	}

	private static int normal(int x){
		return Random.normal(x-x/2, x+x/2);
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
			e.printStackTrace();
			System.out.println(name);
		}
	}
	
	public void move(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public boolean isWeapon(){
		return isRanged() || type == MELEE || type == NATURAL;
	}
	
	public boolean isEquipable(){
		return isWeapon() || type == ARMOR || type == EQUIPABLE;
	}
	
	public boolean isArmor(){
		return type == ARMOR;
	}
	
	public boolean isRanged(){
		return type == RANGED || type == GRENADE;
	}
	
	public boolean isNatural(){
		return type == NATURAL;
	}
	
	public boolean isAmmo(){
		return type == AMMO;
	}
	
	public boolean isUsable(){
		return type == USABLE;
	}
	
	public boolean isGrenade(){
		return type == GRENADE;
	}
	
	public boolean isExplosive(){
		return type == EXPLOSIVE;
	}
	
	public int damage(){
		return normal(damage);
	}
	
	public String toString(){
		String st = name;
		if (isAmmo() || name.endsWith("shotgun"))
			st += " ("+rounds+")";
		if (isArmor())
			st += " ("+HP+")";
		return st;
	}

}
