package view;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.io.Resources;

import model.Tile;

public class TileKeyMap {

	private final Map<String, Tile> map = new HashMap<>();
	@SuppressWarnings("unchecked")
	public Set<String>[] set = new Set[3];
	
	public TileKeyMap(){
		for (int i=0; i<3; i++)
			set[i]=new HashSet<String>();
		try {
			read(Resources.toString(getClass().getResource("/raw/TileKeyMap.txt"), StandardCharsets.UTF_8));
		} 
		catch (ParseException | IOException e) {}
	}
	
	private void set(String st, Tile tile){
		map.put(st,  tile);
	}
	
	public void set(int i, String st, Tile tile){
		set(st, tile);
		set[i].add(st);
	}
	
	private int ObjToInt(Object obj){
		return Integer.parseInt(obj.toString());
	}
	
	private void read(String text) throws ParseException{
		String pattern = "({0}, ({1,number,integer}, {2,number,integer}, {3,number,integer}))";
		Pattern p0 = Pattern.compile("\\{([^//}])*\\}");
		Matcher m0 = p0.matcher(text);
		int i=0;
		while (m0.find() && i<3){
			String temp = m0.group();
			Pattern p = Pattern.compile("\\([a-zA-Z \\d\\.]+, \\(\\d+, \\d+, \\d+\\)\\)");
			Matcher m = p.matcher(temp);
			while(m.find()){
				Object[] values;
				temp = m.group();
				values = new MessageFormat(pattern).parse(temp);
				Tile tile = new Tile(ObjToInt(values[1]), ObjToInt(values[2]), ObjToInt(values[3]));
				set(values[0].toString(), tile);
				set[i].add(values[0].toString());
			}
			i++;
		}
	}
	
	public Tile get(String st){
		return map.get(st);
	}
	
	public Set<String> getSet(int i){
		if (i>= 0 && i<3)
			return set[i];
		return null;
	}
	
	public void writeFile(File file) throws IOException{
		FileWriter writer = new FileWriter(file);
    	writer.write(toString());
    	writer.close();
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder("tile key map:");
		for (int i=0; i<3; i++){
			sb.append("\n{");
			for (String s: getSet(i)){
				Tile t = get(s);
				sb.append("("+s+", ("+t.ch+", "+t.cl+", "+t.bg+"))");
			}
			sb.append("}");
		}
		return sb.toString();
	}
	
}
