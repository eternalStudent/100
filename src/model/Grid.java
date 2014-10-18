package model;
import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.io.Resources;

import util.Point;
import util.Random;
import model.mobs.MOB;

public class Grid {
	
	public static final int TERRAIN=0;
	public static final int ITEMS=1;
	public static final int MOBS=2;
	
	private final List<Map<Point, String>> map = new ArrayList<Map<Point, String>>();
	public final Map<Point, Tile> effects = new HashMap<>();
	public final Set<Point> visual = new HashSet<>();
	public final Set<Point> gas = new HashSet<>();
	public Point cursor;
	public boolean aim = false;
	public int width, height;
	public int floor = 0;
	
	public Grid(int width, int height){
		clear(new Dimension(width, height));
	}
	
	public Grid(File file) throws FileNotFoundException, ParseException, URISyntaxException{
		readFile(file, true);
	}
	
	public Grid(String path) throws FileNotFoundException, ParseException, URISyntaxException{
		this(new File(path));
	}

	public void clear(Dimension dim){
		map.clear();
		for (int i=0; i<4; i++)
			map.add(i, new HashMap<Point, String>());
		width = dim.width;
		height = dim.height;
		for (int x=0; x<width; x++)
			for (int y=0; y<height; y++)
				if (x*y*(x-(width-1))*(y-(height-1)) == 0)
					set(Grid.TERRAIN, x, y, "wall");
	}
	
	public void copy(Grid other){
		width = other.width;
		height = other.height;
		for (int i=0; i<3; i++){
			map(i).clear();
			map(i).putAll(other.map(i));
		}
	}
	
	public Map<Point, String> map(int layer){
		return map.get(layer);
	}
	
	public void set(int layer, int x, int y, String name){
		map(layer).put(new Point(x, y),  name);
	}
	
	public void remove(int layer, int x, int y){
		map(layer).remove(new Point(x, y));
	}
	
	public void move(MOB m, int x, int y){
		remove(MOBS, m.x, m.y);
		set(MOBS, x, y, m.name);
	}
	
	public String get(int layer, int x, int y){
		if (layer == TERRAIN && !map(layer).containsKey(new Point(x, y))){
			if (x<0 || y<0 || x>=width || y>=height)
				return "wall";
			return "floor";
		}	
		return map(layer).get(new Point(x, y));
	}
	
	public String get(int x, int y){
		if (map(MOBS).containsKey(new Point(x, y)))
			return get(MOBS, x, y);
		if (map(ITEMS).containsKey(new Point(x, y)))
			return get(ITEMS, x, y);
		return get(TERRAIN, x, y);
	}
	
	public boolean isSolid(int x, int y){
		Point p = new Point(x, y);
		if (map.get(MOBS).containsKey(p))
			return true;
		return Terrain.solid(get(TERRAIN, x, y));
	}
	
	public boolean isHalfSolid(int x, int y){
		return Terrain.halfSolid(get(TERRAIN, x, y));
	}
	
	public boolean isTransparent(int x, int y){
		return Terrain.transparent(get(TERRAIN, x, y));
	}
	
	public boolean path(int x0, int y0, int x1, int y1){
		Queue<Point> Q = new LinkedList<>();
		Set<Point> set = new HashSet<>();
		Point p = new Point(x0, y0);
		Q.add(p);
		while(!Q.isEmpty()){
			p = Q.remove();
			if (set.contains(p))
				continue;
			set.add(p);
			if (p.x == x1 && p.y == y1)
				return true;
			if (!isHalfSolid(p.x, p.y)){
				int w = p.x;
				int e = p.x;
				while (!isHalfSolid(w-1, p.y))
					w--;
				while (!isHalfSolid(e+1, p.y))
					e++;
				for (int x=w; x<=e; x++){
					if (!isHalfSolid(p.x, p.y-1) && !set.contains(new Point(p.x, p.y-1)))
						Q.add(new Point(x, p.y-1));
					if (!isHalfSolid(p.x, p.y+1) && !set.contains(new Point(p.x, p.y+1)))
						Q.add(new Point(x, p.y+1));
				}
			}
		}
		return false;
	}
	
	private String fileToString(File file) throws FileNotFoundException{
		Scanner sc = new Scanner(file);
		StringBuilder sb = new StringBuilder();
		while(sc.hasNextLine())
			sb.append(sc.nextLine());
		sc.close();
		return sb.toString();
	}
	
	private int ObjToInt(Object obj){
		return Integer.parseInt(obj.toString());
	}
	
	public void readFile(String name, boolean clearFirst) throws ParseException, URISyntaxException, IOException{
		String text = Resources.toString(getClass().getResource("/maps/"+name),  StandardCharsets.UTF_8);
		read(text, clearFirst);
	}
	
	public void readFile(File file, boolean clearFirst) throws ParseException, FileNotFoundException, URISyntaxException{
		String text = fileToString(file);
		read(text, clearFirst);
	}
	public void read(String text, boolean clearFirst) throws ParseException{
		Pattern pattern = Pattern.compile("grid\\(\\d+, \\d+\\):");
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()){
			Object[] values = new MessageFormat("grid({0, number, integer}, {1, number, Integer}):").parse(matcher.group());
			if (clearFirst)
				clear(new Dimension(ObjToInt(values[0]), ObjToInt(values[1])));
		}
		else
			return;
		pattern = Pattern.compile("\\{([^//}])*\\}");
		matcher = pattern.matcher(text);
		int layer=0;
		while (matcher.find() && layer<3){
			String temp = matcher.group();
			Pattern p = Pattern.compile("\\(\\(\\d+, \\d+\\), [a-zA-Z \\d\\.]+\\)");
			Matcher m = p.matcher(temp);
			while(m.find()){
				Object[] values = new MessageFormat("(({0,number,integer}, {1,number,integer}), {2})").parse(m.group());
				set(layer, ObjToInt(values[0]), ObjToInt(values[1]), values[2].toString());			
			}
			layer++;
		}
	}
	
	private void addFoes(int amount, String name, MOB player){
		for (int i=0; i<amount; i++){
			Point p;
			do{
				p = Random.nextPoint(1, width-2, 1, height-2);
			}while(isHalfSolid(p.x, p.y) || Point.distance2(p.x,  p.y,  player.x,  player.y)<8);
			set(Grid.MOBS, p.x, p.y, name);
		}
	}
	
	public void setStairs(MOB player){
		for (int x=-1; x<2; x++)
			for (int y=-1; y<2; y++)
				if (!get(TERRAIN, player.x+x, player.y+y).endsWith("floor"))
					set(TERRAIN, player.x+x, player.y+y, "floor");
		Point p;
		do {
			p= Random.nextPoint(1, width-2, 1, height-2);
		}while(!get(TERRAIN, p.x, p.y).endsWith("floor") || !path(player.x, player.y, p.x, p.y));	
		set(TERRAIN, p.x, p.y, "stairway");
		if (floor == 8 || floor == 39){
			do {
				p= Random.nextPoint(1, width-2, 1, height-2);
			}while(!get(TERRAIN, p.x, p.y).endsWith("floor") || !path(player.x, player.y, p.x, p.y));	
			set(TERRAIN, p.x, p.y, "elevator");
		}
		set(MOBS, player.x, player.y, "player");
		if (floor<7) addFoes(Random.nextInt(4, 6), "zed", player);
		if (floor<12) addFoes(Random.nextInt(2, 3), "guard", player);
		if (floor>=3 && floor<23) addFoes(Random.nextInt(5, 8), "herdling", player);
		if (floor>=4 && floor<15) addFoes(1, "officer", player);
		if (floor>15 && floor<23) addFoes(Random.nextInt(0,1), "officer", player);
		if (floor>7 && floor<34) addFoes(Random.nextInt(4, 6), "ogg", player);
		if (floor>7 && floor<20) addFoes(Random.normal(0,  2), "screamer", player);
		if (floor>=11 && floor<23) addFoes(Random.nextInt(2,3), "inspector", player);
		if (floor>=11 && floor<23) addFoes(Random.nextInt(2, 4), "serpent", player);
		if (floor>15 && floor<31) addFoes(Random.nextInt(1, 2), "soldier", player);
		if (floor>15 && floor<31) addFoes(Random.nextInt(1, 2), "giant herdling", player);
		if (floor>23 && floor<35) addFoes(Random.nextInt(2, 3), "overseer", player);
		if (floor>23) addFoes(Random.nextInt(1, 2), "guardian drone", player);
		if (floor>23) addFoes(Random.normal(0, 2), "tingler", player);
		if (floor>30) addFoes(Random.nextInt(1, 2), "swordsman", player);
		if (floor>30) addFoes(Random.nextInt(3, 5), "detonation drone", player);
		if (floor>=34) addFoes(Random.nextInt(4, 6), "kapre", player);
	}
	
	public boolean next() throws ParseException, URISyntaxException, IOException{
		floor++;
		visual.clear();
		gas.clear();
		if (floor == 1){
			readFile("entrancehall1.map", true);
			return false;
		}	
		if (floor == 7){
			readFile("cubicles.map", true);
			return false;
		}
		if (floor == 15){
			readFile("first boss.map", true);
			return false;
		}
		if (floor == 23){
			readFile("chasm.map", true);
			return false;
		}
		if (floor == 30){
			readFile("killer.map", true);
			return false;
		}
		if (floor == 36){
			readFile("laboratory.map", true);
			return false;
		}
		copy(new RandomMap(width, height).grid);		
		return true;
	}
	
	public void writeFile(File file) throws IOException{
		FileWriter writer = new FileWriter(file);
    	writer.write(toString());
    	writer.close();
	}
	
	public Point dropSpot(int x0, int y0){
		int r=0;
		while (true){
//			for (int dx=0; dx<=r; dx++){
//				int x = x0+dx;
//				int y = y0+(r-dx);
//				if (!isHalfSolid(x, y) && get(Grid.ITEMS, x, y) == null)
//					return new Point(x, y);
//				x = x0-dx;
//				if (!isHalfSolid(x, y) && get(Grid.ITEMS, x, y) == null)
//					return new Point(x, y);
//				x = x0-dx;
//				y = y0-(r-dx);
//				if (!isHalfSolid(x, y) && get(Grid.ITEMS, x, y) == null)
//					return new Point(x, y);
//				x = x0+dx;
//				if (!isHalfSolid(x, y) && get(Grid.ITEMS, x, y) == null)
//					return new Point(x, y);
//			}
			for (Point p: Point.sphere(x0, y0, r)){
				if (!isHalfSolid(p.x, p.y) && get(Grid.ITEMS, p.x, p.y) == null)
					return new Point(p.x, p.y);
			}
			r++;
		}
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder("grid("+width+", "+height+"):");
		for (int layer=0; layer<3; layer++){
			sb.append("\n{");
			for (Point p: map(layer).keySet()){
				sb.append("(("+p.x+", "+p.y+"), "+get(layer, p.x, p.y)+")");
			}
			sb.append("}");
		}
		return sb.toString();
	}
	
}	
