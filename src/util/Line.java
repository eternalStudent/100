package util;
import java.util.ArrayList;
import java.util.List;

public class Line {
	
	private List<Point> list = new ArrayList<>();
	private Point current = null;
	private float slope;  // -1<=slope<=1
	private int dirX = 0; // -1 or 0 or 1
	private int dirY = 0; // -1 or 0 or 1
	private int x0, y0;
	
	public Line(int x0, int y0, int x1, int y1){
		if (x0==x1 && y0==y1)
			return;
		
		int dx = x1 - x0;
		int dy = y1 - y0;
		if ((dx-1)*(dx+1)*dx==0 && (dy-1)*(dy+1)*dy==0){
			if (dx==0 || dy == 0){
				slope = 0;
				if (dx == 0)
					dirY = dy;
				else
					dirX = dx;
			}	
			else{
				slope = dx/dy;
				dirX = dx;
			}
			this.x0 = x1;
			this.y0 = y1;
			add(x1, y1);
			return;
		}
		
		x0 += sgn(x1-x0);
		y0 += sgn(y1-y0);
		this.x0 = x0;
		this.y0 = y0;
		
		dx = x1 - x0;
		dy = y1 - y0;
		
		if (Math.abs(dx)>=Math.abs(dy)){
			slope = (float)dy/(float)dx;
			dirX = x1>=x0? 1: -1;
		}	
		else{
			slope = (float)dx/(float)dy;
			dirY = y1>=y0? 1: -1;	
		}
		for (int i=0; i<=Math.max(Math.abs(dx), Math.abs(dy)); i++)
			add(next());
		current=null;
	}
	
	private static int sgn(int i){
		if (i<0)
			return -1;
		if (i>0)
			return 1;
		return 0;
	}
	
	private void add(Point p){
		list.add(p);
	}
	
	private void add(int x, int y){
		list.add(new Point(x, y));
	}

	
	public Point get(int i){
		return list.get(i);
	}
	
	public int distance(){
		return list.size();
	}
	
	private Point next(int x, int y){
		x += dirX;
		y += dirY;
		if (dirY == 0)
			y=Math.round(slope*((float)(x-x0))+(float)y0);
		else
			x=Math.round(slope*((float)(y-y0))+(float)x0);	
		return new Point(x, y);
	}
	
	public Point next(){
		if (current == null)
			current = new Point(x0, y0);
		else
			current = next(current.x, current.y);
		return current;
	}
	
	public String toString(){
		return list.toString();
	}

}
