package view;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButton;
import javax.swing.KeyStroke;

@SuppressWarnings("serial")
public class EditorMenuBar extends JMenuBar{
	
	private ActionListener listener;
	
	public EditorMenuBar(ActionListener al){
		super();
		listener = al;
		JMenu file = new JMenu("File");
		add(file);
		addMenuItem(file, "New", KeyEvent.VK_N);
		addMenuItem(file, "Save", KeyEvent.VK_S);
		addMenuItem(file, "Open", KeyEvent.VK_O);
		addMenuItem(file, "Save Key Map", 0);
		addMenuItem(file, "Change Tileset", 0);
		ButtonGroup group = new ButtonGroup();
		addRadioButton(group, "Terrain", true);
		addRadioButton(group, "Items", false);
		addRadioButton(group, "Foes", false);
	}
	
	private void addMenuItem(JMenu parent, String text, int key){
		JMenuItem item = new JMenuItem(text);
		item.setActionCommand(text);
	    item.addActionListener(listener);
	    if (key !=0)
	    	item.setAccelerator(KeyStroke.getKeyStroke(key, ActionEvent.CTRL_MASK));
		parent.add(item);
	}
	
	private void addRadioButton(ButtonGroup group, String str, boolean b){
		JRadioButton radio = new JRadioButton(str);
		radio.setActionCommand(str);
		radio.addActionListener(listener);
		radio.setSelected(b);
		group.add(radio);
		add(radio);
	}

}
