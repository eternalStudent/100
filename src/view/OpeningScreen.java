package view;

import java.awt.Graphics;

@SuppressWarnings("serial")
public class OpeningScreen extends Screen{
	
	public OpeningScreen(Tileset tileset){
		super(tileset);
	}
	
	public void paint(Graphics g){
		super.paint(g);
		write(g, "HUNDRED", 36, 6, 7);
		write(g, "Original Concept by Kiltan Kettlezrar", 21, 14, 7);
		write(g, "Programmed by Noam Weisberg", 26, 15, 7);
		write(g, "Original Music by Omer Tito", 26, 16, 7);
		write(g, "Press any key to continue...", 26, 24, 7);
	}

}
