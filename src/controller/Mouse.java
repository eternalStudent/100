package controller;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import util.Point;

public class Mouse implements MouseMotionListener, MouseListener{
	
	public int x, y;
	public Point point;
	public boolean rightHeld, leftHeld;

	@Override
	public void mouseDragged(MouseEvent e) {	
		x=e.getX();
		y=e.getY();
		point = new Point(x, y);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		x=e.getX();
		y=e.getY();
		point = new Point(x, y);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton()==MouseEvent.BUTTON1)
			leftHeld=true;	
		if (e.getButton()==MouseEvent.BUTTON3)
			rightHeld=true;
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		leftHeld=false;
		rightHeld=false;
		
	}

}