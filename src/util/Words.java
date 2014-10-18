package util;

import model.mobs.Item;

public class Words {
	
	public static String article(String word){
		if (word.equals("blood"))
			return "";
		if (word.equals("axe") || word.equals("adrenaline syringe"))
			return "an ";
		return "a ";
	}
	
	public static String article(Item item){
		return article(item.name);
	}
	
	public static String ordinal(int i){
		if ((i%100)/10 == 1)
			return "th";
		if (i%10 == 1)
			return "st";
		if (i%10 == 2)
			return "nd";
		if (i%10 == 3)
			return "rd";
		return "th";
	}

}
