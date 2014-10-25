package model.mobs;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.io.Resources;

public class ItemsMap {
	
	private Map<String, Item> map = new HashMap<>();
	
	protected ItemsMap(){
		try {readFile(Resources.toString(getClass().getResource("/raw/items.txt"), StandardCharsets.UTF_8));} 
		catch (IOException | ParseException e) {}
	}
	
	private void set(String st, Item item){
		map.put(st,  item);
	}
	
	public Item get(String st){
		if (map.containsKey(st))
			return map.get(st);
		return new Item(st);
	}
	
	protected Set<String> keySet(){
		return map.keySet();
	}
	
	private String read(String text, String field, String initialValue){
		Pattern p = Pattern.compile("\\["+field+"\\]: \"[^\"]*\"");
		Matcher m = p.matcher(text);			
		if (m.find())try{
			Object[] values = new MessageFormat("["+field+"]: \"{0}\"").parse(m.group());
			return values[0].toString();
		}
		catch(ParseException e){
			e.printStackTrace();
		}
		return initialValue;
	}
	
	private char read(String text, String field, char initialValue){
		Pattern p = Pattern.compile("\\["+field+"\\]: [a-zA-Z]");
		Matcher m = p.matcher(text);			
		if (m.find())try{
			Object[] values = new MessageFormat("["+field+"]: {0}").parse(m.group());
			return values[0].toString().charAt(0);
		}
		catch(ParseException e){
			e.printStackTrace();
		}
		return initialValue;
	}
	
	private int read(String text, String field, int initialValue){
		Pattern p = Pattern.compile("\\["+field+"\\]: \\d+");
		Matcher m = p.matcher(text);			
		if (m.find()) try{
			Object[] values = new MessageFormat("["+field+"]: {0, number, integer}").parse(m.group());
			return Integer.parseInt(values[0].toString());
		}
		catch(ParseException e){
			e.printStackTrace();
		}
		return initialValue;
	}
	
	private void readFile(String text) throws FileNotFoundException, ParseException{
		Pattern pattern = Pattern.compile("\\{([^//}])*\\}");
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()){
			String elem = matcher.group();
			String name = read(elem, "name", "");
			char type = read(elem, "type", 'U');
			String desc = read(elem, "desc", "");
			int weight = read(elem, "weight", 0);
			int space = read(elem, "space", 0);
			Item item = new Item(name, type, desc, weight, space);
			item.rounds = read(elem, "rounds", 0);
			item.damage = read(elem, "damage", 0);
			item.time = read(elem, "time", 0);
			item.cartridge = read(elem, "cartridge", null);
			item.accuracy = read(elem, "accuracy", 0);
			item.capacity = read(elem, "capacity", 0);
			item.reloadTime = read(elem, "reload", 0);
			item.radius = read(elem, "radius", 0);
			set(name, item);
		}

	}
	
	public String toString(){
		return map.toString();
	}

}
