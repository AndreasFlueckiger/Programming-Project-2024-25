package main.logic.attack;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

import main.Title;
import rules.designPatterns.Observable;
import rules.designPatterns.Observer;
import rules.designPatterns.RulesFacade;
import main.battleship.BattleshipConfiguration;

@SuppressWarnings("serial")
public class Attack extends JFrame implements Observer{
	
	private String currentPlayerName;
	
	Title titlePanel = new Title("");
	
	BattleBoard board1;
	BattleBoard board2;
	
	public boolean blockCells = true;
	
	static Attack attackFrame;
    
    public static Attack getAttackFrame() {
        if(attackFrame == null)
        	attackFrame = new Attack();
        
        return attackFrame;    
    }
	
	private Attack() {
		RulesFacade.getRules().register(this);
		RulesFacade.getRules().emptyMessagesList();
		
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSize = tk.getScreenSize();
		int sl = screenSize.width;
		int sa = screenSize.height;
		int x = sl/2-K.LARG_DEFAULT/2;
		int y = sa/2-K.ALT_DEFAULT/2;
		setBounds(x,y,K.LARG_DEFAULT, K.ALT_DEFAULT);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		setLayout(null);
		getContentPane().setBackground(new Color(250, 250, 250));
		
		board1 = new BattleBoard(1);
		board1.setBounds(8, 60, board1.BOARD_SIZE, board1.BOARD_SIZE);
		
		board2 = new BattleBoard(2);
		board2.setBounds(8 + board2.BOARD_SIZE + 8, 60, board2.BOARD_SIZE, board2.BOARD_SIZE);
		
		getContentPane().add(AttackUtilities.getAttackUtilites());
		getContentPane().add(titlePanel);
		getContentPane().add(board1);
		getContentPane().add(board2);
		
		changeTitle("ATTACKING PHASE");
		
		RulesFacade.getRules().startGame();
	}
	
	public void showBoard(int player) {
		System.out.println("[DEBUG] SHOWING BOARD " + Integer.toString(player));
		
		if(player == 1)
			board1.showHiddenCells();
		else 
			board2.showHiddenCells();
		
		changeTitle("ATTACKING PLAYER - " + currentPlayerName);
		AttackUtilities.getAttackUtilites().buttonDisable();
		blockCells = false;
	}
	
	public void changeTitle(String title) {
		titlePanel.setText(title);
	}

	public void selfDestroy() {
		attackFrame = null;
	}
	
	@Override
	public void notify(Observable o) {
		Object lob[] = (Object []) o.get();
		
		int currentPlayer = (int) lob[BattleshipConfiguration.objectValues.CURRENT_PLAYER.getValue()];

		if(currentPlayer == 1)
			currentPlayerName = (String) lob[ BattleshipConfiguration.objectValues.PLAYER_1_NAME.getValue() ];
		else
			currentPlayerName = (String) lob[ BattleshipConfiguration.objectValues.PLAYER_2_NAME.getValue() ];
		
	}
	
}
