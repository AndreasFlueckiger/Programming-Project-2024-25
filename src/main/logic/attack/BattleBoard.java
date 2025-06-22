package main.logic.attack;

import main.logic.board.Board;
import main.logic.board.Grid;
import main.logic.victory.*;
import main.rules.designPatterns.*;
import main.battleship.BattleshipConfiguration;



@SuppressWarnings("serial")
public class BattleBoard extends Board implements Observer{
	
	private int player;
	private Grid battleGrid;
	
	private int[][] shownCells;
	private int[][] hiddenCells;

	public BattleBoard(int player) {
		RulesFacade.getRules().register(this);
		
		this.player = player;
		
		setLayout(null);
		setBounds(0, 0, BOARD_SIZE, BOARD_SIZE);
		setOpaque(false);
				
		battleGrid = new Grid(player);
		
		addLabels();
		
		shownCells = BattleshipConfiguration.createEmptyGrid();
		hiddenCells = BattleshipConfiguration.createEmptyGrid();
		
		if(battleGrid!=null)
			add(battleGrid);
		else {
			System.out.println("Grid zero");
		}
	}
	
	public void hideHiddenCells() {
		getShownCells();
		battleGrid.repaintCells(shownCells);
	}
	
	public void showHiddenCells(){
		battleGrid.repaintCells(hiddenCells);
	}
	
	private void getShownCells() {
		for(int i = 0; i < BattleshipConfiguration.SQUARE_COUNT; i++)
		{
			for(int j = 0; j < BattleshipConfiguration.SQUARE_COUNT; j++)
			{
				if(hiddenCells[j][i] < 0 || hiddenCells[j][i] == 10) {
					shownCells[j][i] = hiddenCells[j][i];
				}
			}
		}
	}
	
	public void resetBoard() {
		shownCells = BattleshipConfiguration.createEmptyGrid();
		hiddenCells = BattleshipConfiguration.createEmptyGrid();
		repaint();
	}
	
	@Override
	public void notify(Observable o) {
		Object lob[] = (Object []) o.get();
		
		String player1Name = "";
		String player2Name = "";
		
		boolean result = (boolean) lob[BattleshipConfiguration.objectValues.RESULT.getValue()];
		int currentPlayer = (int) lob[BattleshipConfiguration.objectValues.CURRENT_PLAYER.getValue()];
		
		if(player == 1) {
			hiddenCells = (int[][]) lob[BattleshipConfiguration.objectValues.BOARD_1.getValue()];
			player1Name = (String) lob[BattleshipConfiguration.objectValues.PLAYER_1_NAME.getValue()];
			player2Name = (String) lob[BattleshipConfiguration.objectValues.PLAYER_2_NAME.getValue()];
		}
		else {
			hiddenCells = (int[][]) lob[BattleshipConfiguration.objectValues.BOARD_2.getValue()];
			player1Name = (String) lob[BattleshipConfiguration.objectValues.PLAYER_2_NAME.getValue()];
			player2Name = (String) lob[BattleshipConfiguration.objectValues.PLAYER_1_NAME.getValue()];
		}
			
		if(player != currentPlayer) {
			hideHiddenCells();
		}	
		
		if(result && currentPlayer == player) {
			System.out.println("\n**********\nVITORIA\n**********\n");
			(Victory.getVictoryFrame(player1Name, player2Name)).setVisible(true);
			(Attack.getAttackFrame()).setVisible(false);
			return;
		}
		
	}	
}
