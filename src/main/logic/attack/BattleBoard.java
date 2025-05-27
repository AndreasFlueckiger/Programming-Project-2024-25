package main.logic.attack;

import main.logic.board.Board;
import main.logic.board.Grid;
import main.logic.victory.*;
import main.battleship.*;
import main.rules.designPatterns.*;



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
		
		shownCells = battleshipconfiguration.createEmptyGrid();
		hiddenCells = battleshipconfiguration.createEmptyGrid();
		
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
		for(int i = 0; i < battleshipconfiguration.SQUARE_COUNT; i++)
		{
			for(int j = 0; j < battleshipconfiguration.SQUARE_COUNT; j++)
			{
				if(hiddenCells[j][i] < 0 || hiddenCells[j][i] == 10) {
					shownCells[j][i] = hiddenCells[j][i];
				}
			}
		}
	}
	
	public void resetBoard() {
		shownCells = battleshipconfiguration.createEmptyGrid();
		hiddenCells = battleshipconfiguration.createEmptyGrid();
		repaint();
	}
	
	@Override
	public void notify(Observable o) {
		Object lob[] = (Object []) o.get();
		
		String player1Name = "";
		String player2Name = "";
		
		boolean result = (boolean) lob[battleshipconfiguration.objectValues.RESULT.getValue()];
		int currentPlayer = (int) lob[battleshipconfiguration.objectValues.CURRENT_PLAYER.getValue()];
		
		if(player == 1) {
			hiddenCells = (int[][]) lob[battleshipconfiguration.objectValues.BOARD_1.getValue()];
			player1Name = (String) lob[battleshipconfiguration.objectValues.PLAYER_1_NAME.getValue()];
			player2Name = (String) lob[battleshipconfiguration.objectValues.PLAYER_2_NAME.getValue()];
		}
		else {
			hiddenCells = (int[][]) lob[battleshipconfiguration.objectValues.BOARD_2.getValue()];
			player1Name = (String) lob[battleshipconfiguration.objectValues.PLAYER_2_NAME.getValue()];
			player2Name = (String) lob[battleshipconfiguration.objectValues.PLAYER_1_NAME.getValue()];
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
