package model.mobs;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.io.Resources;

public class FoesMap {
	
private Map<String, MOB> map = new HashMap<>();
	
	protected FoesMap(){
		try {readFile(Resources.toString(getClass().getResource("/raw/foes.txt"), StandardCharsets.UTF_8));} 
		catch (IOException e) {}
	}
	
	private void set(String st, MOB m){
		map.put(st,  m);
	}
	
	public MOB get(String st){
		return map.get(st);
	}
	
	private int toInt(Object obj){
		return Integer.parseInt(obj.toString());
	}
	
	private String read(String text, String field, String initialValue){
		Pattern p = Pattern.compile("\\["+field+"\\]: \"[a-zA-Z \\d\\.\\,\\-']*\"");
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
	
	private int read(String text, String field, int initialValue){
		Pattern p = Pattern.compile("\\["+field+"\\]: \\d+");
		Matcher m = p.matcher(text);			
		if (m.find()) try{
			Object[] values = new MessageFormat("["+field+"]: {0, number, integer}").parse(m.group());
			return toInt(values[0]);
		}
		catch(ParseException e){
			e.printStackTrace();
		}
		return initialValue;
	}
	
	private void readFile(String text){
		Pattern pattern = Pattern.compile("\\{([^//}])*\\}");
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()){
			String elem = matcher.group();
			String name = read(elem, "name", null);
			int HP = read(elem, "HP", 10);
			int XP = read(elem, "XP", 4);
			int vision = read(elem, "vision", 7);
			int move = read(elem, "move", 10);
			int dodge = read(elem, "dodge", 0);
			String weapon = read(elem, "weapon", null);
			String ready = read(elem, "ready", null);
			String armor = read(elem, "armor", null);
			set(name, new MOB(name, HP, XP, vision, move, dodge, weapon, ready, armor));
		}

	}
	
	public String toString(){
		return map.toString();
	}

}
