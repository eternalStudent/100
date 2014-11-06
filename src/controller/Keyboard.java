package controller;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;
import java.util.Queue;

import util.Point;

public class Keyboard implements KeyListener{
	
	Queue<Integer> queue = new LinkedList<>();

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyChar() == '>')
			queue.add(KeyEvent.VK_GREATER);
		else
			queue.add(e.getKeyCode());
	}

	@Override
	public void keyReleased(KeyEvent e) {}

	@Override
	public void keyTyped(KeyEvent e) {}
	
	public boolean keypressed(){
		return !queue.isEmpty();
	}
	
	public int get(){
		while (true){
			if (keypressed())
				return queue.remove();
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}	
	}
	
	public static Point ArrowToPoint(int arrow){
		switch(arrow){
		case KeyEvent.VK_HOME:
			return new Point(-1, -1);
		case KeyEvent.VK_UP:
			return new Point(0, -1);
		case KeyEvent.VK_PAGE_UP:
			return new Point(1, -1);
		case KeyEvent.VK_LEFT:
			return new Point(-1, 0);
		case KeyEvent.VK_RIGHT:
			return new Point(1, 0);
		case KeyEvent.VK_END:
			return new Point(-1, 1);
		case KeyEvent.VK_DOWN:
			return new Point(0, 1);
		case KeyEvent.VK_PAGE_DOWN:
			return new Point(1, 1);
		}	
		return null;
	}
	

}
