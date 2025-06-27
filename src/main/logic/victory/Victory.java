package main.logic.victory;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

import main.battleship.BattleshipConfiguration;


@SuppressWarnings("serial")
public class Victory extends JFrame{
	
	static Victory victoryFrame;
    
    public static Victory getVictoryFrame(String winner, String looser) {
        if(victoryFrame == null)
        	victoryFrame = new Victory(winner, looser);
        
        return victoryFrame;
        
    }
    
    public void selfDestroy() {
    	victoryFrame = null;
    }

	private Victory(String winner, String looser) {
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSize = tk.getScreenSize();
		int sl = screenSize.width;
		int sa = screenSize.height;
		int x = sl/2-BattleshipConfiguration.LARG_DEFAULT/2;
		int y = sa/2-BattleshipConfiguration.ALT_DEFAULT/2;
		setBounds(x,y,BattleshipConfiguration.LARG_DEFAULT,BattleshipConfiguration.ALT_DEFAULT);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		setLayout(null);
		getContentPane().setBackground(new Color(250, 250, 250));
		
		getContentPane().add(new VictoryPanel(winner, looser));
		
		setTitle("VICTORY!");
	}

}
