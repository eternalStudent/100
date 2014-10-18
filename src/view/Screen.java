package view;

import java.awt.Font;
import java.awt.Graphics;
import java.util.Scanner;

import javax.swing.JPanel;

import util.Point;
import model.Tile;

@SuppressWarnings("serial")
public abstract class Screen extends JPanel{
	
	protected final Tileset tileset;
	
	protected Screen(Tileset tileset){
		super();
		this.tileset = tileset;
		setBackground(tileset.color(0));
	}
	
	protected void writeTF(Graphics g, String st, int x, int y){
		g.drawString(st, x*tileset.tw, (y+1)*tileset.th-2);
	}
	
	protected void draw(Graphics g, Tile tile, int x, int y){
		g.drawImage(tileset.get(tile), x*tileset.tw, y*tileset.th, null);
	}
	
	protected void draw(Graphics g, Tile tile, Point p){
		draw(g, tile, p.x, p.y);
	}
	
	protected void writeChar(Graphics g, char ch, int x, int y, int cl){
		int width = getWidth()/tileset.tw;
		g.drawImage(tileset.get(new Tile((int)ch, cl, 0)), (x%width)*tileset.tw, (y+x/width)*tileset.th, null);
	}
	
	protected void write(Graphics g, String st, int x0, int y0, int cl){
		char[] charrArr = st.toCharArray();
		for (int x=0; x<charrArr.length; x++)
			writeChar(g, charrArr[x], x0+x, y0, cl);
	}
	
	public void paint(Graphics g){
		super.paint(g);;
		g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, tileset.th));
	}
	
	protected void writeDesc(Graphics g, String text, int x, int y, int width){
		Scanner sc = new Scanner(text);
		StringBuilder sb = new StringBuilder();
		while (sc.hasNext()){
			String st = sc.next();
			if (g.getFontMetrics().stringWidth(sb.toString()+" "+st)<width*tileset.tw)
				sb.append(" "+st);
			else{
				writeTF(g, sb.toString(), x, y);
				y+=2;
				sb = new StringBuilder(st);
			}
		}
		writeTF(g, sb.toString(), x, y);
		sc.close();
	}

}
