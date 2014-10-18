package util;

import java.util.HashSet;
import java.util.Set;

public class Point{
	
	public final int x;
	public final int y;
	private final java.awt.Point p;
	
	public Point(int x, int y){
		this.x = x;
		this.y = y;
		p = new java.awt.Point(x, y);
	}
	
	@Override
	public boolean equals(Object obj){
		if (obj == null)
			return false;
		if (!(obj instanceof Point))
			return false;
		Point other = (Point)obj;
		return (x == other.x && y == other.y);
	}
	
	public boolean equals(int x, int y){
		return (this.x == x && this.y == y);
	}
	
	@Override
	public int hashCode(){
		return p.hashCode();
	}
	
	public Point add(Point other){
		return new Point(x+other.x, y+other.y);
	}
	
	public Point add(int x, int y){
		return new Point(this.x+x, this.y+y);
	}
	
	public String toString(){
		return "("+x+", "+y+")";
	}
	
	public static int distance(int x0, int y0, int x1, int y1){
		return Math.max(Math.abs(x1-x0), Math.abs(y1-y0));
	}
	
	public static int distance1(int x0, int y0, int x1, int y1){
		return Math.abs(x1-x0)+Math.abs(y1-y0);
	}
	
	public static int distance2(int x0, int y0, int x1, int y1){
		double sqrdx = (double)((x1-x0)*(x1-x0));
		double sqrdy = (double)((y1-y0)*(y1-y0));
		return Math.round((float)(Math.sqrt(sqrdx+sqrdy)));
	}
	
	public int distance(int x, int y){
		return distance(this.x, this.y, x, y);
	}
	
	public int distance1(int x, int y){
		return distance1(this.x, this.y, x, y);
	}
	
	public int distance2(int x, int y){
		return distance2(this.x, this.y, x, y);
	}
	
	public static Set<Point> sphere(int x0, int y0, int r){
		Set<Point> set = new HashSet<>();
		for (int x=x0-r; x<=x0+r; x++)
			for (int y=y0-r; y<=y0+r; y++)
				if (distance2(x0, y0, x, y)<=r)
					set.add(new Point(x, y));
		return set;
	}
	
	public Set<Point> sphere(int r){
		return sphere(x, y, r);
	}

}
