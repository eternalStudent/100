package view;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import model.Tile;

public class Tileset {
	
	private BufferedImage image = null;
	private int[] palette = {-16777216, -16777088, -16744448, -16744320, -8388608, -8388480, -8355840, -4144960, -8355712, -16776961, -16711936, -16711681, -65536, -65281, -256, -1};
	private Map<Tile, BufferedImage> map = new HashMap<>();
	public int th,tw;
	
	public Tileset(String pathname){
		readFile(new File(pathname));
	}
	
	public void readFile(File file){
		map.clear();
		try { image = ImageIO.read(file); } 
		catch (IOException e) { e.printStackTrace(); }
		
		th = image.getHeight()/16;
		tw = image.getWidth()/16;
	}
	
	private BufferedImage duplicate(BufferedImage image) {
		BufferedImage j = new BufferedImage(tw, th, BufferedImage.TYPE_INT_ARGB);
		for (int x=0; x<tw; x++)
			for (int y=0; y<th; y++)
				j.setRGB(x,y,image.getRGB(x,y));
		return j;
	}
		
	private BufferedImage get(int ch){
		return image.getSubimage((ch%16)*tw, (ch/16)*th, tw, th);
	}
	
	protected BufferedImage get(int ch, int cl, int bg){
		BufferedImage img = duplicate(get(ch));
		for (int x=0; x<tw; x++)
			for(int y=0; y<th; y++)
				if (img.getRGB(x, y)!=palette[0])
					img.setRGB(x, y, palette[cl]);
				else if (bg!=palette[0])
					img.setRGB(x, y, palette[bg]);
		return img;
	}
	
	protected BufferedImage get(Tile tile){
		if (tile == null)
			return get(0, 0, 0);
		if (!map.containsKey(tile))
			map.put(tile, get(tile.ch, tile.cl,tile.bg));
		return map.get(tile);
	}
	
	protected Color color(int i){
		return new Color(palette[i]);
	}

}