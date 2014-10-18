import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.UIManager;

import model.Grid;
import model.Tile;
import util.IntValue;
import util.Point;
import view.AttributesDialog;
import view.Board;
import view.EditorMenuBar;
import view.EditorToolBar;
import view.TileDialog;
import view.TileKeyMap;
import view.Tileset;
import controller.Mouse;

public class MapEditor implements Runnable, ActionListener {
	
	private final Mouse mouse = new Mouse();
	private final Tileset tileset = new Tileset("Tileset.png");
	private final TileKeyMap keyMap = new TileKeyMap();
	private final Grid grid = new Grid(63, 25);
	private final EditorMenuBar menuBar;
	private final EditorToolBar toolBar;
	private final Board display;
	private final IntValue layer = new IntValue(0);
	private Point player;
	
	public static void main(String[] args){
		new MapEditor();
	}
	
	public MapEditor(){
		try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } 
		catch (Exception e) { e.printStackTrace(); }
		menuBar = new EditorMenuBar(this);
		toolBar = new EditorToolBar(keyMap, tileset, this);
		display = new Board(menuBar, toolBar, tileset, keyMap, grid, layer, mouse);
		new Thread(this).start();
	}
	
	private int layer(String st){
		if (st=="Terrain")
			return Grid.TERRAIN;
		if (st=="Items")
			return Grid.ITEMS;
		if (st=="Foes")
			return Grid.MOBS;
		return 3;
	}

	@Override
	public void run() {
		while(true){
			while (mouse.leftHeld){
				Point p = mouse.point;
				int x = p.x/tileset.tw;
				int y = p.y/tileset.th;
				if (toolBar.tile.equals("eraser") || toolBar.tile.equals("floor")){
					grid.remove(layer.value, x, y);
				}
				else{
					if (toolBar.tile.equals("player")){
						if (player != null)
							if (!player.equals(new Point(x, y)))
								grid.remove(Grid.MOBS, player.x, player.y);
						player = new Point(x, y);
					}	
					grid.set(layer.value, x, y, toolBar.tile);
				}	
				display.repaint();
			}	
		}
		
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		String cmd = ae.getActionCommand();
		JFileChooser chooser = new JFileChooser();
		if (layer(cmd)<3){
			layer.value = layer(cmd);
			toolBar.setButton(layer.value);
		}
		if (cmd.equals("+")){
			TileDialog dialog = new TileDialog(tileset);
			Tile tile = dialog.showDialog(toolBar);
			if (tile != null){
				keyMap.set(layer.value, dialog.getName(), tile);
				toolBar.setButton(layer.value);
				toolBar.tile=dialog.getName();
			}
		}
		if (cmd.equals("New")){
			Dimension dim = AttributesDialog.showDialog(menuBar);
			if (dim!= null)
				grid.clear(dim);
		}
		if (cmd.equals("Save")){
			if (chooser.showSaveDialog(display) == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                try{grid.writeFile(file);}
                catch(Exception e){}
			}    
		}
		if (cmd.equals("Open")){
			if (chooser.showSaveDialog(display) == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                try{grid.readFile(file, true);}
                catch(Exception e){}
			}    
		}
		if (cmd.equals("Save Key Map")){
			try {keyMap.writeFile(new File("TileKeyMap.txt"));}
			catch (IOException e) {}
		}
		if (cmd.equals("Change Tileset")){
			if (chooser.showSaveDialog(display) == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                try{tileset.readFile(file);}
                catch(Exception e){}
			}    
		}
		display.repaint();
	}

}
