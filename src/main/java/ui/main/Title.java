package ui.main;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import battleship.main.battleship.*;

@SuppressWarnings("serial")
public class Title extends JPanel{

	JLabel titleLabel = new JLabel();
	
	public Title(String title) { 
		
		setLayout(null);
		setBounds(0, 0, BattleshipConfiguration.LARG_DEFAULT, 50);
		setBackground(Color.CYAN);
		setOpaque(false);
		
		titleLabel.setText(title);
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		titleLabel.setVerticalAlignment(SwingConstants.CENTER);
		titleLabel.setBounds(0, 5, 1024, 40);
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
		titleLabel.setForeground(new Color(0, 0, 0));
		
		add(titleLabel);
		
	}
	
	public void setText(String text) {
		titleLabel.setText(text);
		repaint();
	}
	
}
