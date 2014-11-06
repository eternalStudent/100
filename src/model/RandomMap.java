package model;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;

import util.Random;
import model.mobs.Item;

public class RandomMap {
	
	public Grid grid;
	int width = 63;
	int height = 25;
	
	protected RandomMap(boolean addRooms){
		grid = new Grid(width, height);
		walls();
		if (addRooms) 
			try {rooms();} 
			catch (IOException | ParseException | URISyntaxException e) {}
		floor();
	}
	
	protected RandomMap(){
		this(true);
	}
	
	void walls(){
		int s = width*height;
		for (int i=0; i<Random.nextInt(s/5, s/4); i++)
			grid.set(Grid.TERRAIN, Random.nextInt(1, width-2), Random.nextInt(1, height-2), "wall");
		for (int x=1; x<width-1; x++)
			for (int y=1; y<height-1; y++){
				if (isWall(x+1, y) && isWall(x-1, y) && !Random.isNext(3) && isFloor(x, y+1) && isFloor(x, y-1))
					grid.set(Grid.TERRAIN, x, y, "wall");
				if (isWall(x, y+1) && isWall(x, y-1) && !Random.isNext(3) && isFloor(x+1, y) && isFloor(x-1, y))
					grid.set(Grid.TERRAIN, x, y, "wall");
				if (isFloor(x+1, y) && isFloor(x-1, y) && isWall(x, y) && isFloor(x, y+1) && isFloor(x, y-1))
					grid.set(Grid.TERRAIN, x, y, "floor");
				if (isWall(x+1, y) && isWall(x-1, y) && isFloor(x, y) && isWall(x, y+1) && isWall(x, y-1))
					grid.set(Grid.TERRAIN, x, y, "wall");
			}
	}
	
	private boolean isWall(int x, int y){
		return grid.get(Grid.TERRAIN, x, y).equals("wall");
	}
	
	private boolean isFloor(int x, int y){
		return grid.get(Grid.TERRAIN, x, y).equals("floor");
	}
	
	void rooms() throws ParseException, URISyntaxException, IOException{
		for(int i=0; i<Random.nextInt(0, 4); i++){
			int x0 = Random.nextInt(0, grid.width-11);
			int y0 = Random.nextInt(0, grid.height-11);
			int x1 = Random.nextInt(x0+10, grid.width-1);
			int y1 = Random.nextInt(y0+10, grid.height-1);
			room(x0, y0, x1, y1);
		}	
		
		if (Random.isNext(3))
			grid.readFile("bathrooms.map", false);
		if (Random.isNext(3))
			grid.readFile("kitchen.map", false);
		if (Random.isNext(6))
			grid.readFile("meetings room.map", false);
		if (Random.isNext(6))
			grid.readFile("round room.map", false);
		if (Random.isNext(6)){
			grid.readFile("arsenal.map", false);
			for (int x=1; x<=4; x+=2)
				for (int y=20; y<=23; y++){
					Item item = Item.randomItem();
					grid.set(Grid.ITEMS, x, y, item.name);
					if (item.isRanged())
						grid.set(Grid.ITEMS, x+1, y, item.cartridge);
				}	
		}	
	}
	
	void room(int x0, int y0, int x1, int y1){
		for (int x=x0; x<=x1; x++)
			for (int y=y0; y<=y1; y++)
				if ((x-x0)*(y-y0)*(x-x1)*(y-y1)==0)
					grid.set(Grid.TERRAIN, x, y, "wall");
				else
					grid.set(Grid.TERRAIN, x, y, "floor");
		if (x0!=0)
			grid.set(Grid.TERRAIN, x0, Random.nextInt(y0+1, y1-1), "closed door");
		if (x1!=grid.width-1)
			grid.set(Grid.TERRAIN, x1, Random.nextInt(y0+1, y1-1), "closed door");
		if (y0!=0)
			grid.set(Grid.TERRAIN, Random.nextInt(x0+1, x1-1), y0, "closed door");
		if (y1!=grid.height-1)
			grid.set(Grid.TERRAIN, Random.nextInt(x0+1, x1-1), y1, "closed door");
	}
	
	void floor(){
		for (int i=0; i<6; i++)
			lightFloor(Random.nextInt(1, width-2), Random.nextInt(0, height-2), Random.nextInt(2, 6));
		for (int i=0; i<14; i++){
			int x0 = Random.nextInt(1, width-2);
			int y0 = Random.nextInt(1, height-2);
			if (grid.get(Grid.TERRAIN,  x0, y0).endsWith("floor")){
				grid.set(Grid.TERRAIN, x0, y0, "bloody floor");
				for (int x=-1; x<2; x++)
					for (int y=-1; y<2; y++){
						if (isWall(x+x0, y+y0))
							grid.set(Grid.TERRAIN, x+x0, y+y0, "bloody wall");
						if (grid.get(Grid.TERRAIN, x+x0, y+y0).equals("bloody floor") && (x!=0 || y!=0))
							grid.set(Grid.TERRAIN, x+x0, y+y0, "blood");
					}	
			}
		}
	}
	
	private void lightFloor(int x0, int y0, int r){
		if (r>0){
			String name = r==1? "light floor": "lighter floor";
			if (isFloor(x0, y0))
				  grid.set(Grid.TERRAIN, x0, y0, name);
			for (int x=-1; x<2; x++)
				for (int y=-1; y<2; y++)
					if (isFloor(x0+x, y0+y) && !Random.isNext(5))
						lightFloor(x0+x, y0+y, r-1);
		}
	}
	
	

}
