package model.mobs;
import java.util.ArrayList;
import java.util.List;

import util.Random;

public class Inventory {
	
	private List<Item> list = new ArrayList<>();
	private int selected = 0;
	public List<Item> grenades = new ArrayList<>();
	protected Item readied;
	protected Item weapon;
	protected Item armor;
	protected Item gasMask;
	protected Item brassKnuckles;
	protected int weight = 0;
	protected int space = 0;
	
	protected Inventory(){}
	
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
	
	private void unequip(Item item){
		if (item == null)
			return;
		item.equiped = false;
		grenades.remove(item);
		if (item == gasMask)
			gasMask = null;
		if (item == brassKnuckles)
			brassKnuckles = null;
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
		}
	
	protected void remove(Item item){
		if (item == null)
			return;
		unequip(item);
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
		if (item.equiped){
			unequip(item);
			return false;
		}
		if (item.isEquipable()){
			if (!contains(item))
				add(item);
			if (item.name.equals("gas mask")){
				if (gasMask != null)
					gasMask.equiped = false;
				gasMask = item;
			}	
			if (item.name.equals("brass knuckles")){
				if (brassKnuckles != null)
					brassKnuckles.equiped = false;
				brassKnuckles = item;
			}	
			if (item.isGrenade()){
				if (grenades.size()<4)
					grenades.add(item);
				else
					return false;
			}
			else if (item.isWeapon()){
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
		if(item.isWeapon() & !item.isGrenade()){
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
	
	protected Item use(Item item){
		if (item == null)
			return null;
		if (item.isUsable()){
			item.playSound();
			remove(item);
			return item;
		}
		return null;
	}
	
	protected Item use(){
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
	
	protected void throwGrenade(){
		getGrenade().playSound();
		grenades.remove(0);
	}
	
	protected void fire(){
	    weapon.playSound();	
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
			if (weapon == null)
				return;
			weapon.equiped = false;
			weapon = null;
		}	
		else
			equip(readied);
		if (item != null)
			ready(item);
	}
	
	public Item getGrenade(){
		if (grenades.size() == 0)
			return null;
		return grenades.get(0);
	}
	
	protected boolean isMasked(){
		return gasMask != null;
	}
	
	protected boolean hasKnuckles(){
		return brassKnuckles != null;
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
