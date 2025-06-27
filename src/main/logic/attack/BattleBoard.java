package main.logic.attack;

import main.logic.board.Board;
import main.logic.board.Grid;
import main.logic.victory.*;
import main.rules.designPatterns.*;
import main.battleship.BattleshipConfiguration;



@SuppressWarnings("serial")
public class BattleBoard extends Board implements Observer{
	
	private int player;
	public Grid battleGrid;
	
	private int[][] shownCells;
	private int[][] hiddenCells;
	
	private java.util.List<String> attackCoords = null;

	public BattleBoard(int player) {
		RulesFacade.getRules().register(this);
		
		this.player = player;
		
		setPreferredSize(new java.awt.Dimension(this.BOARD_SIZE, this.BOARD_SIZE));
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
		
		// Controlla se è Human vs Human
		String player2Type = main.rules.designPatterns.RulesFacade.player2Type;
		boolean isHumanVsHuman = "Human".equals(player2Type);
		
		if(isHumanVsHuman) {
			// In Human vs Human, ogni player vede sempre le sue navi quando è il suo turno
			if(player == currentPlayer) {
				showHiddenCells(); // Mostra le navi del player di turno
			} else {
				hideHiddenCells(); // Nasconde le navi dell'avversario
			}
		} else {
			// Logica originale per Human vs Bot
			if(player != currentPlayer) {
				hideHiddenCells();
			}
		}
		
		if (player == 2 && currentPlayer == 1) {
			setEnabled(true);
		}
		
		if(result && currentPlayer == player) {
			System.out.println("\n**********\nVITORIA\n**********\n");
			(Victory.getVictoryFrame(player1Name, player2Name)).setVisible(true);
			(Attack.getAttackFrame()).setVisible(false);
			return;
		}
		
	}	

	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if (battleGrid != null) {
			battleGrid.setEnabled(enabled);
			for (int i = 0; i < BattleshipConfiguration.SQUARE_COUNT; i++) {
				for (int j = 0; j < BattleshipConfiguration.SQUARE_COUNT; j++) {
					if (battleGrid.grid[i][j] != null) {
						battleGrid.grid[i][j].setEnabled(enabled);
					}
				}
			}
		}
	}

	public void setAttackCoords(java.util.List<String> coords) {
		this.attackCoords = coords;
		repaintAttackCoords();
	}

	private void repaintAttackCoords() {
		if (attackCoords == null) return;
		int size = main.battleship.BattleshipConfiguration.SQUARE_COUNT;
		int[][] temp = new int[size][size];
		for (String coord : attackCoords) {
			int[] xy = main.logic.shippositioning.ShipPlacementValidator.convertCoordinateToIndices(coord);
			if (xy != null) {
				temp[xy[0]][xy[1]] = 99; // 99 = colpo fatto
			}
		}
		battleGrid.repaintCells(temp);
	}
}
