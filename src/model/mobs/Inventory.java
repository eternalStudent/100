package model.mobs;
import java.util.ArrayList;
import java.util.List;

import util.Random;

public class Inventory {
	
	private List<Item> list = new ArrayList<>();
	private int selected = 0;
	protected Item readied;
	protected Item weapon;
	protected Item armor;
	protected int weight = 0;
	protected int space = 0;
	
	protected Inventory(){
		//System.out.println(Item.clone("type I fragmentation grenade").radius);
	}
	
	private Item find(String st){
		for (Item item: list)
			if (item.name.equals(st))
				return item;
		return null;
	}
	
	public boolean contains(Item item){
		return list.contains(item);
	}
	
	protected void add(Item item){
		if (item == null || item.isNatural())
			return;	
		weight += item.weight;
		space += item.space;
		list.add(item);	
	}
	
	protected void remove(Item item){
		if (item == null)
				return;
		item.equiped = false;
		if (item == weapon)
			weapon = null;
		if (item == armor)
			armor = null;
		if (item == readied)
			readied = null;
		if (item.isAmmo())
			for (int i=0; i<size(); i++)
				if (get(i).magazine == item)
					get(i).magazine = null;
		weight -= item.weight;
		space -= item.space;
		list.remove(item);
	}
	
	protected Item remove(int i){
		Item item = get(i);
		remove(item);
		return item;
	}
	
	protected Item remove(){
		return remove(selected);
	}
	
	public Item get(int i){
		if (i>=0 && i<list.size())
			return list.get(i);
		return null;
	}
	
	public Item get(){
		return get(selected);
	}
	
	protected boolean equip(Item item){
		if (item.isEquipable()){
			if (item.name.equals("gas mask") && isMasked())
				return false;
			if (!contains(item))
				add(item);
			if (item.isWeapon()){
				if (weapon != null)
					weapon.equiped = false;
				weapon = item;
				if (item.isGrenade())
					item.magazine = item;
			}
			if (item.isArmor()){
				if (armor != null)
					armor.equiped = false;
				armor = item;
			}
			item.equiped = true;
			if (item == readied)
				readied = null;
			return true;
		}
		return false;
	}
	
	protected boolean equip(){
		return equip(get());
	}
	
	protected boolean ready(Item item){
		if(item.isWeapon()){
			if (!contains(item))
				add(item);
			if (item == weapon)
				weapon = null;
			item.equiped = false;
			readied = item;
			return true;
		}
		return false;
	}
	
	protected String use(Item item){
		if (item.isUsable()){
			item.playSound();
			remove(item);
			return item.name;
		}
		return null;
	}
	
	protected String use(){
		return use(get());
	}
	
	protected boolean ready(){
		return ready(get());
	}
	
	protected Item magazine(){
		return weapon.magazine;
	}
	
	protected int rounds(){
		return magazine().rounds;
	}
	
	protected int capacity(){
		return weapon.capacity;
	}
	
	protected void fire(){
	    weapon.playSound();
	    if (weapon.isGrenade()){
	    	remove(weapon);
	    	return;
	    }	
	    if (weapon.name.equals("submachine gun"))
	    	weapon.rounds -= Math.min(Random.normal(3, 5), weapon.rounds);
		if (weapon.cartridge.equals("12ga shell")){
			weapon.rounds--;
			return;
		}	
		magazine().rounds--;
	}
	
	protected boolean reload(Boolean playSound){
		if (weapon == null)
			return false;
		if (weapon.isRanged() && !weapon.isGrenade()){
			if (magazine() != null && !magazine().name.equals("12ga shell"))
				remove(magazine());
			Item item = find(weapon.cartridge);
			if (item != null){
				weapon.magazine = item;	
				if (magazine().name.equals("12ga shell")){
					if (weapon.rounds==capacity())
						return false;
					weapon.rounds++;
					magazine().rounds--;
					if (magazine().rounds==0)
						remove(magazine());
				}
				else
					magazine().equiped = true;
				if (playSound)
					item.playSound();
				return true;
			}
			weapon.magazine = null;
		}
		return false;
	}
	
	protected boolean reload(){
		return reload(true);
	}
	
	protected void swap(){
		Item item = weapon;
		if (readied == null){
			weapon.equiped = false;
			weapon = null;
		}	
		else
			equip(readied);
		if (item != null)
			ready(item);
	}
	
	protected boolean isMasked(){
		for (int i=0; i<size(); i++){
			Item item = get(i);
			if (item.name.equals("gas mask") && item.equiped)
				return true;
		}
		return false;
	}
	
	public int size(){
		return list.size();
	}
	
	public void incSelect(){
		selected++;
		if (selected>=size())
			selected=0;
	}
	
	public void decSelect(){
		selected--;
		if (selected<0)
			selected=size()-1;
	}
	
	public void select(int i){
		if (i<0 || i>=size())
			selected = 0;
		else
			selected = i;
	}

	public boolean isSelected(int i){
		if (selected<0 || selected>=size())
			selected = 0;
		return i==selected;
	}
	
	public boolean isReady(Item item){
		return item == readied;
	}
	
	public int weight(){
		if (weight>80)
			return 2;
		if (weight>40)
			return 1;
		return 0;
	}
	
	public String toString(){
		return list.toString();
	}

}
