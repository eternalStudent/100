package view;

import java.awt.Graphics;

@SuppressWarnings("serial")
public class TextScreen extends Screen{
	
	private Text text;
	
	public TextScreen(Text text, Tileset tileset){
		super(tileset);
		this.text = text;
	}
	
	public void paint(Graphics g){
		super.paint(g);
		write(g, text, 0, 0);
	}
	
	private void write(Graphics g, Text text, int x0, int y0){
		for (int y=0; y<getHeight()/tileset.th; y++){
			String st = text.getNextLine();
			char[] charrArr = st.toCharArray();
			for (int x=0; x<charrArr.length; x++)
				writeChar(g, charrArr[x], x0+x, y0+y, 7);
		}
		
	}
	
	

}
