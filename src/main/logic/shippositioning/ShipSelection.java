package main.logic.shippositioning;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

import main.Title;
import main.battleship.BattleshipConfiguration;
import main.rules.designPatterns.Observable;
import main.rules.designPatterns.Observer;
import main.rules.designPatterns.RulesFacade;


@SuppressWarnings("serial")
public class ShipSelection extends JFrame implements KeyListener, Observer{
	
	Title titlePanel = new Title("");
	
	private int currentPlayerNum;
	private String currentPlayerName;
	
	static ShipSelection shipSelection;
    
    public static ShipSelection getShipSelection() {
        if(shipSelection == null)
        	shipSelection = new ShipSelection();
        
        return shipSelection;    
        
    }
    
	public void selfDestroy() {
		shipSelection = null;
	}
	
	private ShipSelection() {
		RulesFacade.getRules().register(this);
		
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
			
		setFocusable(true);
		
		getContentPane().add(titlePanel);
		getContentPane().add(ShipOptions.getShipOptions());
		getContentPane().add(new ShipPlacement());
		getContentPane().add(SelectionUtilities.getSelectionUtilites());
		
		int currentPlayerNum = RulesFacade.getRules().getCurrentPlayer();
		setTitle("Ship Selection - " + RulesFacade.getRules().getPlayerName(currentPlayerNum));
		
		addKeyListener(this);
	}
	
	public void setTitle(String title) {
		titlePanel.setText(title);
	}
	
	@Override
	public void keyPressed(KeyEvent k) {
		// TODO Auto-generated method stub
	}

	@Override
	public void keyReleased(KeyEvent k) {
		// TODO Auto-generated method stub
	}

	@Override
	public void keyTyped(KeyEvent k) {
		int VK_ESCAPE = 27;
		
		if((int)BattleshipConfiguration.getKeyChar() == VK_ESCAPE) {			
			RulesFacade.getRules().unsetSelectedShip();
		}
		
		if(BattleshipConfiguration.getKeyChar() == 'r') {
			RulesFacade.getRules().resetGrid();
		}		
	}
	
	@Override
	public void notify(Observable o) {
		// TODO Auto-generated method stub
		Object lob[] = (Object []) o.get();
		
		currentPlayerNum = (Integer) lob[BattleshipConfiguration.objectValues.CURRENT_PLAYER.getValue()];
		
		if(currentPlayerNum == 1)
			currentPlayerName = (String) lob[BattleshipConfiguration.objectValues.PLAYER_1_NAME.getValue()];
		else
			currentPlayerName = (String) lob[BattleshipConfiguration.objectValues.PLAYER_2_NAME.getValue()];
			
		setTitle("Ship Selection - " + currentPlayerName);
	}

}
