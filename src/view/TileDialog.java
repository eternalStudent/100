package view;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import model.Tile;

public class TileDialog implements ActionListener{
	
	private final Tileset tileset;
	private JTextField[] text = new JTextField[3];
	private JTextField name = new JTextField(16);
	private JLabel label;
	
	public TileDialog(Tileset tileset){
		this.tileset = tileset;
		label = new JLabel(new ImageIcon(tileset.get(0, 7, 0)));
	}
	
	private int textToInt(JTextField text){
		return Integer.parseInt(text.getText());
	}
	
	public Tile showDialog(Component parent){
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		JPanel namePanel = new JPanel();
		namePanel.add(name);
		panel.add(namePanel);
		JPanel chPanel = new JPanel();
		chPanel.setLayout(null);
		chPanel.setPreferredSize(new Dimension(tileset.tw*16,tileset.th*16));
		for (int ch=0; ch<256; ch++){
			JButton button = new JButton();
			button.setIcon(new ImageIcon(tileset.get(ch, 7, 0)));
			chPanel.add(button);
			button.setBounds(0, 0, tileset.tw, tileset.th);
			button.setLocation((ch%16)*tileset.tw, (ch/16)*tileset.th);
			button.setActionCommand(Integer.toString(ch));
			button.addActionListener(this);
		}
		panel.add(chPanel);
		JPanel[] clPanel = new JPanel[2];
		for (int i=0; i<2; i++){
			clPanel[i] = new JPanel();
			clPanel[i].setLayout(null);
			clPanel[i].setPreferredSize(new Dimension(tileset.tw*16,tileset.th));
			for(int cl=0; cl<16; cl++){
				JButton button = new JButton();
				button.setIcon(new ImageIcon(tileset.get(0, 0, cl)));
				clPanel[i].add(button);
				button.setBounds(0, 0, tileset.tw, tileset.th);
				button.setLocation(cl*tileset.tw, 0);
				button.setActionCommand(Integer.toString(256+(16*i)+cl));
				button.addActionListener(this);
			}
			panel.add(clPanel[i]);
		}	
		JPanel textPanel = new JPanel();
		for (int i=0; i<3; i++){
			text[i] = i==1? new JTextField("7"): new JTextField("0");
			text[i].setColumns(3);
			textPanel.add(text[i]);
		}
		
		textPanel.add(label);
		panel.add(textPanel);
		int option = JOptionPane.showConfirmDialog(parent, panel, "Tile Chooser", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (option == JOptionPane.OK_OPTION && !name.getText().equals("")){
			try{
				return new Tile(textToInt(text[0]), textToInt(text[1]), textToInt(text[2]));
			}
			catch(Exception e){}
		}
		return null;
	}
	
	public String getName(){
		return name.getText();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		int i = Integer.parseInt(e.getActionCommand());
		if (i<256)
			text[0].setText(e.getActionCommand());
		else if (i<272)
			text[1].setText(Integer.toString(i-256));
		else
			text[2].setText(Integer.toString(i-256-16));
		label.setIcon(new ImageIcon(tileset.get(textToInt(text[0]), textToInt(text[1]), textToInt(text[2]))));
	}

}
