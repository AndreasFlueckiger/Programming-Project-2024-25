package main.logic.attack;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import main.Title;
import main.rules.designPatterns.Observable;
import main.rules.designPatterns.Observer;
import main.rules.designPatterns.RulesFacade;
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
		int x = sl/2-BattleshipConfiguration.LARG_DEFAULT/2;
		int y = sa/2-BattleshipConfiguration.ALT_DEFAULT/2;
		setBounds(x,y,BattleshipConfiguration.LARG_DEFAULT, BattleshipConfiguration.ALT_DEFAULT);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		setLayout(new BorderLayout());
		getContentPane().setBackground(new Color(250, 250, 250));
		
		// Title in alto
		getContentPane().add(titlePanel, BorderLayout.NORTH);
		
		// Pannello centrale con le due griglie
		JPanel centerPanel = new JPanel(new GridBagLayout());
		centerPanel.setOpaque(false);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridy = 0;
		gbc.insets = new Insets(0, 40, 0, 40); // spazio tra le griglie
		
		board1 = new BattleBoard(1);
		board2 = new BattleBoard(2);
		
		gbc.gridx = 0;
		centerPanel.add(board1, gbc);
		gbc.gridx = 1;
		centerPanel.add(board2, gbc);
		
		getContentPane().add(centerPanel, BorderLayout.CENTER);
		
		// Utility panel in basso
		getContentPane().add(AttackUtilities.getAttackUtilites(), BorderLayout.SOUTH);
		
		changeTitle("ATTACKING PHASE");
		
		RulesFacade.getRules().startGame();
	}
	
	public void showBoard(int player) {
		System.out.println("[DEBUG] SHOWING BOARD " + Integer.toString(player));
		if(player == 1) {
			board2.hideHiddenCells();
		} else {
			board1.showHiddenCells();
		}
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
		String player2Type = main.rules.designPatterns.RulesFacade.player2Type;
		boolean isHumanVsHuman = "Human".equals(player2Type);
		if(currentPlayer == 1)
			currentPlayerName = (String) lob[ BattleshipConfiguration.objectValues.PLAYER_1_NAME.getValue() ];
		else
			currentPlayerName = (String) lob[ BattleshipConfiguration.objectValues.PLAYER_2_NAME.getValue() ];
		if(isHumanVsHuman) {
			java.util.List<String> player1AttackCoords = main.rules.designPatterns.RulesFacade.getRules().getPlayer1AttackCoords();
			java.util.List<String> player2AttackCoords = main.rules.designPatterns.RulesFacade.getRules().getPlayer2AttackCoords();
			if(currentPlayer == 1) {
				// Player 1 attacca su board2, vede le sue navi su board1
				board2.setAttackCoords(player1AttackCoords);
				board2.setEnabled(true);
				board1.showHiddenCells();
				board1.setEnabled(false);
			} else {
				// Player 2 attacca su board1, vede le sue navi su board2
				board1.setAttackCoords(player2AttackCoords);
				board1.setEnabled(true);
				board2.showHiddenCells();
				board2.setEnabled(false);
			}
			getContentPane().repaint();
			getContentPane().revalidate();
		}
	}
	
}
