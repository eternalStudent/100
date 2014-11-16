package view;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.util.List;

import javax.swing.JFrame;

import texts.Text;
import util.IntValue;
import controller.Keyboard;
import controller.Mouse;
import model.Grid;
import model.Log;
import model.mobs.Inventory;
import model.mobs.ListOfTraits;
import model.mobs.MOB;
import model.mobs.Trait;

@SuppressWarnings("serial")
public class Board extends JFrame{
	
	public final TileKeyMap keyMap = new TileKeyMap();
	private final Tileset tileset = new Tileset("tileset.png");
	
	public Board(){
		super();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("HUNDRED");
		setVisible(true);
	}
	
	public Board(Keyboard keyboard){
		this();
		setResizable(false);
		setContentPane(new OpeningScreen(tileset));
		getContentPane().setPreferredSize(new Dimension(80*tileset.tw, 31*tileset.th));
		addKeyListener(keyboard);
		pack();
	}
	
	public Board(EditorMenuBar menuBar, EditorToolBar toolBar, Tileset tileset, TileKeyMap keyMap, Grid grid, IntValue layer, Mouse mouse){
		this();
		setJMenuBar(menuBar);
		add(toolBar, BorderLayout.SOUTH);
		add(new EditorScreen (tileset, keyMap, grid, layer, mouse), BorderLayout.CENTER);
		pack();
	}
	
	public void setGameScreen(Grid grid, MOB player, Log log){
		setContentPane(new GameScreen(grid, player, log, tileset, keyMap));
		revalidate();
	}
	
	public void setInventory(Inventory inv){
		setContentPane(new InventoryScreen(inv, tileset, keyMap));
		revalidate();
	}
	
	public void setLogScreen(Log log){
		setContentPane(new LogScreen(log, tileset));
		revalidate();
	}
	
	public void setTextScreen(String name) throws IOException{
		Text text = new Text(name);
		setContentPane(new TextScreen(text, tileset));
		revalidate();
	}
	
	public void setPrologue() throws IOException{
		setTextScreen("prologue");
	}
	
	public void setHelp() throws IOException{
		setTextScreen("help");
	}
	
	public void setMenuScreen(IntValue option){
		setContentPane(new MenuScreen(tileset, option));
		revalidate();
	}
	
	public void setTraitsScreen(IntValue option, List<Trait> traits, ListOfTraits list){
		setContentPane(new TraitsScreen(tileset, option, traits, list));
		revalidate();
	}

	public void setOutroScreen() throws IOException{
		setTextScreen("outro");
	}
	
}
