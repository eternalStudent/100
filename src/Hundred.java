import java.awt.event.KeyEvent;
import java.awt.im.InputContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import model.Game;
import model.mobs.ListOfTraits;
import model.mobs.MOB;
import model.mobs.Trait;
import util.IntValue;
import util.Point;
import view.Board;
import controller.Keyboard;

public class Hundred{
	
	private static final Keyboard keyboard = new Keyboard();
	private static final Board board = new Board(keyboard);
	private final Game data;
	
	public static void main(String[] args){
		readkey();	
		while(true){
			IntValue option = new IntValue(0);
			board.setMenuScreen(option);
			int key;
			do{
				key=readkey();
				if (key == KeyEvent.VK_UP && option.value>0)
					option.value--;
				if (key == KeyEvent.VK_DOWN && option.value<3)
					option.value++;
				if (key == KeyEvent.VK_ENTER){
					if (option.value==3)
						System.exit(0);
					if (option.value == 2){ 
						try {help();} 
						catch (IOException e) {}
						board.setMenuScreen(option);
					}	
				}
				board.repaint();
			}while(key != KeyEvent.VK_ENTER || option.value != 0);
			new Hundred();
		}		
	}
	
	private static int readkey(){
		InputContext context = InputContext.getInstance();  
		context.selectInputMethod(new Locale("en", "US"));
		return keyboard.get();
	}
		
	private static void help() throws IOException{
		board.setHelp();
		readkey();
		board.repaint();
		readkey();
	}
	
	private Hundred(){
//		------prologue-----
		try {
			board.setPrologue();
			readkey();
			board.repaint();
			readkey();
			board.repaint();
			readkey();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
//		------game---------
		data = new Game(chooseTraits());
		data.setGameScreen(board);
		MOB player = data.player;
		do{
			while (player.hisTurn()){
				data.sight(player);
				board.repaint();
				player.target = null; //for dodging bullets
				act(player,  readkey());
			}	
			for (MOB m: data.foes)
				if(m.hisTurn() && !player.dead()){
					data.sight(m);
					act(m, data.AI(m));
				}
			if (!player.dead())
				data.endOfRound(board);
		}while(!data.player.dead());
		
//		----post-mortem------		
		board.repaint();
		while(readkey()!=KeyEvent.VK_ENTER){}
		board.setLogScreen(data.log);
		readkey();
	}
	
	private List<Trait> chooseTraits(){
		IntValue option = new IntValue(0);
		List<Trait> traits = new ArrayList<>();
		ListOfTraits list = new ListOfTraits();
		board.setTraitsScreen(option, traits, list);
		int key;
		do{
			key=readkey();
			if (key == KeyEvent.VK_UP && option.value>0)
				option.value--;
			if (key == KeyEvent.VK_DOWN && option.value<list.size()-1)
				option.value++;
			if (key == KeyEvent.VK_ENTER){
				Trait trait = list.get(option.value);
				if (traits.contains(trait))
						traits.remove(trait);
				else
					traits.add(trait);
			}
			board.repaint();
		}while(key != KeyEvent.VK_GREATER || traits.size() != 3);
		return traits;
	}
	
	private void act(MOB m, int key){
		switch (key){
		case KeyEvent.VK_ESCAPE:
			data.suicide(keyboard);
			break;
		case KeyEvent.VK_HOME:
		case KeyEvent.VK_UP:
		case KeyEvent.VK_PAGE_UP:
		case KeyEvent.VK_LEFT:
		case KeyEvent.VK_RIGHT:
		case KeyEvent.VK_END:
		case KeyEvent.VK_DOWN:
		case KeyEvent.VK_PAGE_DOWN:
			Point p = Keyboard.ArrowToPoint(key);
			MOB m1 = data.findFoe(m.x+p.x, m.y+p.y);
			if (m1 != null){
				if ((m1 == data.player || m == data.player) && !m.weapon().isRanged())
					data.melee(board, m, m1);
			}
			else
				data.move(m, p.x, p.y);
			break;
		case KeyEvent.VK_F:
			if (m.weapon().isRanged() && m.ammo()>0){
				p = m == data.player? data.aim(board, keyboard): new Point(data.player.x, data.player.y);
				data.fire(board, m, p);		
			}
			break;
		case KeyEvent.VK_G:
			data.get(m);
			break;
		case KeyEvent.VK_D:
		case KeyEvent.VK_I:
			data.inventory(board, keyboard);
			break;
		case KeyEvent.VK_R:
			data.reload(m);
			break;
		case KeyEvent.VK_Z:
			data.swap(m);
			break;
		case KeyEvent.VK_E:
			data.arm(m);
			break;
		case KeyEvent.VK_GREATER:
			data.stairway();
			break;
		case KeyEvent.VK_ENTER:
			m.hold();
			break;
		case KeyEvent.VK_SLASH:
			try {help();}
			catch (IOException e) {e.printStackTrace();}
			data.setGameScreen(board);
			break;
		case KeyEvent.VK_L:
			data.look(board, keyboard);
			break;
		case KeyEvent.VK_S:
			if (m != data.player)
				data.scream(m);
			break;
		}
		
	}
}
