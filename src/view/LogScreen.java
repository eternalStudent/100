package view;

import java.awt.Graphics;

import model.Log;

@SuppressWarnings("serial")
public class LogScreen extends Screen{
	
	private final Log log;
	
	public LogScreen(Log log, Tileset tileset){
		super(tileset);
		this.log = log;
	}
	
	public void paint(Graphics g){
		super.paint(g);
		g.setColor(tileset.color(10));
		String st[] = log.getLast(getHeight()/tileset.th);
		for (int i=0; i<st.length; i++)
			writeTF(g, st[i], 0, i);
	}

}
