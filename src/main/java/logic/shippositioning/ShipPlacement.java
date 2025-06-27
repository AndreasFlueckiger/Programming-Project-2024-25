package main.logic.shippositioning;

import javax.swing.JPanel;

import main.battleship.BattleshipConfiguration;

import java.util.List;

@SuppressWarnings("serial")
public class ShipPlacement extends JPanel{
	
	SelectionBoard board = new SelectionBoard();

	public ShipPlacement() {

		setBounds(BattleshipConfiguration.LARG_DEFAULT/3, 0, BattleshipConfiguration.LARG_DEFAULT-(BattleshipConfiguration.LARG_DEFAULT/3), BattleshipConfiguration.ALT_DEFAULT - 150);
		setLayout(null);
		setOpaque(false);
		
		board.setBounds((this.getBounds().width - board.BOARD_SIZE)/2, (this.getBounds().height - board.BOARD_SIZE)/2, board.BOARD_SIZE, board.BOARD_SIZE);
		
		add(board);
		
	}

}
