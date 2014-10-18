package model;
public class Tile {
	
	public int ch, cl, bg;
	
	public Tile(int ch, int cl, int bg){
		this.ch = ch;
		this.cl = cl;
		this.bg = bg;
	}
	
	public Tile(int ch, int cl){
		this(ch, cl, 0);
	}
	
	public void copy(Tile other){
		ch = other.ch;
		cl = other.cl;
		bg = other.bg;
	}
	
	public Tile bg(int bg){
		return new Tile(this.ch, this.cl, bg);
	}
	
	@Override
	public boolean equals(Object obj){
		if (obj == null)
			return false;
		if (!(obj instanceof Tile))
			return false;
		Tile other = (Tile)obj;
		return (ch == other.ch && cl == other.cl && bg == other.bg);
	}
	
	@Override 
	public int hashCode(){
		return bg*(256*16)+cl*256+ch;
	}
	
	@Override
	public String toString(){
		return "("+ch+", "+cl+", "+bg+")";
	}

}
