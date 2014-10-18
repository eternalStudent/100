package view;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

import model.Tile;

@SuppressWarnings("serial")
public class EditorToolBar extends JToolBar implements ActionListener{
	
	private TileKeyMap keyMap;
	private Tileset tileset;
	private ActionListener listener;
	public String tile = "wall";
	
	public EditorToolBar(TileKeyMap keyMap, Tileset tileset, ActionListener al){
		this.keyMap = keyMap;
		this.tileset = tileset;
		setLayout(new FlowLayout());
		listener = al;
		setButton(0);
	}
	
	public void setButton(int i){
		removeAll();
		if (i != 0)
			addButton("eraser");
		for (String st: keyMap.getSet(i))
			addButton(st);
		addButton("+");
	}

	private void addButton(String st){
		JButton button;
		button = new JButton(new ImageIcon(tileset.get(keyMap.get(st))));
		if (st.equals("+"))
			button = new JButton(new ImageIcon(tileset.get(new Tile(43, 0, 15))));
		button.setToolTipText(st);
		button.setActionCommand(st);
		button.addActionListener(this);
		button.addActionListener(listener);
		add(button);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		tile = e.getActionCommand();
	}	

}
