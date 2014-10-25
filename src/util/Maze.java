package util;

import java.util.ArrayList;
import java.util.List;

public class Maze {
	
	public Point[][] array;
	public List<Rectangle> passage = new ArrayList<>();
	private final int HEIGHT;
	private final int WIDTH;
	
	public Maze(int width, int height){
		HEIGHT=height;
		WIDTH=width;
		array = new Point[WIDTH][HEIGHT];
		for (int x=0; x<WIDTH; x++)
			for (int y=0; y<HEIGHT; y++)
				array[x][y]=new Point(x,y);
		
		int count=0;
		do{
			count++;
			Point r=pickARoot();
			Point n=pickANeighbour(r);
			
			if (getRoot(n).x!=r.x || getRoot(n).y!=r.y)
				array[r.x][r.y]=n;
			
		}while (numORoots()>1 && count<HEIGHT*WIDTH*2);
		
		Point[][] temp = new Point[WIDTH][HEIGHT];
		for (int y=0; y<HEIGHT; y++)
			for (int x=0; x<WIDTH; x++)
				temp[x][y]= getRoot(x,y);	
		int roots=numORoots();
		while (roots>1){
			Point r=new Point(Random.nextInt(WIDTH),Random.nextInt(HEIGHT));
			Point n=pickANeighbour(r);
			Point rootR=temp[r.x][r.y];
			Point rootN=temp[n.x][n.y];
			if (!rootR.equals(rootN)){
				passage.add(new Rectangle(r.x,r.y,n.x,n.y));
				roots--;
				for (int x=0; x<WIDTH; x++)
					for (int y=0; y<HEIGHT; y++)
						if (temp[x][y].equals(rootR))
							temp[x][y]=new Point(rootN.x,rootN.y);
			}
		}

		
	}
	
	private int numORoots(){
		int sum=0;
		for (int x=0; x<WIDTH; x++)
			for (int y=0; y<HEIGHT; y++)
				if (isRoot(x,y))
					sum++;
		return sum;
	}
	
	private Point pickARoot(){
		int x,y;
		do{
			x = Random.nextInt(WIDTH);
			y = Random.nextInt(HEIGHT);
		} while (!isRoot(x,y));
		return new Point(x,y);
	}
	
	private boolean isRoot(int x, int y){
		return (array[x][y].x==x && array[x][y].y==y);
	}
	
	private Point pickANeighbour(int x, int y){
		int r=Random.nextInt(4);
		switch(r){
			case 0: if (y>0)
				return new Point(x,y-1);
			else
				return pickANeighbour(x,y);
			case 1: if (x>0)
				return new Point(x-1,y);
			else
				return pickANeighbour(x,y);
			case 2: if (x<WIDTH-1)
				return new Point(x+1,y);
			else
				return pickANeighbour(x,y);
			case 3: if (y<HEIGHT-1)
				return new Point(x,y+1);
			else 
				return pickANeighbour(x,y);
		}
		return null;
	}
	
	private Point pickANeighbour(Point c){
		return pickANeighbour(c.x,c.y);
	}
	
	private Point getRoot(int x, int y){
		if (isRoot(x,y))
			return new Point (x,y);
		return getRoot(array[x][y]);
	}
	
	private Point getRoot(Point c){
		return getRoot(c.x,c.y);
	}	
	
	public void print(){
		for(int y=0; y<HEIGHT; y++){
			for(int x=0; x<WIDTH; x++)
				System.out.print(array[x][y]);
			System.out.println();
		}
	}
	
}
