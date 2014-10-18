package view;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class AttributesDialog {
	
	public static Dimension showDialog(Component parent){
		JTextField width = new JTextField(3);
		JTextField height = new JTextField(3);
		JPanel panel = new JPanel();
		panel.add(width);
		panel.add(height);
		int option = JOptionPane.showConfirmDialog(parent, panel, "Set Attributes", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (option == JOptionPane.OK_OPTION){
			try{return new Dimension(Integer.parseInt(width.getText()), Integer.parseInt(height.getText()));}
			catch(Exception e){}
		}
		return null;
	}

}
