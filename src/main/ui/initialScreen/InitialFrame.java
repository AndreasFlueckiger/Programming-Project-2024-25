package main.ui.initialScreen;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.ToolKit;

import javax.swing.JFrame;

import main.battleship.BattleshipConfiguration

@SuppressWarnings("serial")
public class initialFrame extends JFrame {
    
        static InitialFrame initialFrame;
    
    public static InitialFrame getInitialFrame() {
        if(initialFrame == null)
        	initialFrame = new InitialFrame();
        
        return initialFrame;
        
    }
    
    public void selfDestroy() {
    	initialFrame = null;
    }
	
	private InitialFrame() {
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
		
		getContentPane().add(new InitialScreen());
		
		setTitle("Batalha Naval");
	}

}
