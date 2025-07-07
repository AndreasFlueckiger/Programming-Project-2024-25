package logic.attack;

import logic.board.Board;
import logic.board.Grid;
import logic.victory.*;
import rules.designPatterns.*;
import battleship.main.battleship.*;

/**
 * BattleBoard represents the game board during the attack phase.
 * It extends Board and implements Observer to receive game state updates.
 * Handles the display of ships, hits, misses, and attack coordinates.
 */
@SuppressWarnings("serial")
public class BattleBoard extends Board implements Observer{
	
	private int player; // Player number (1 or 2)
	public Grid battleGrid; // The grid containing the cells
	
	private int[][] shownCells; // Cells to show to the player
	private int[][] hiddenCells; // Complete board state (hidden from opponent)
	
	private java.util.List<String> attackCoords = null; // Attack coordinates for display

	/**
	 * Constructor for BattleBoard
	 * @param player Player number (1 or 2)
	 */
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
	
	/**
	 * Hides the opponent's ships and shows only hits/misses
	 */
	public void hideHiddenCells() {
		getShownCells();
		battleGrid.repaintCells(shownCells);
	}
	
	/**
	 * Shows all ships (for the player's own board)
	 */
	public void showHiddenCells(){
		battleGrid.repaintCells(hiddenCells);
	}
	
	/**
	 * Determines which cells to show to the player
	 * Only shows hits (negative values) and misses (D_WATER), not live ships
	 */
	private void getShownCells() {
		for(int i = 0; i < BattleshipConfiguration.SQUARE_COUNT; i++)
		{
			for(int j = 0; j < BattleshipConfiguration.SQUARE_COUNT; j++)
			{
				// Show hits (negative values) and destroyed ships (values < -10)
				if(hiddenCells[j][i] < 0 || hiddenCells[j][i] == BattleshipConfiguration.SHIPS.D_WATER.getValue()) {
					shownCells[j][i] = hiddenCells[j][i];
				}
			}
		}
	}
	
	/**
	 * Resets the board to initial state
	 */
	public void resetBoard() {
		shownCells = BattleshipConfiguration.createEmptyGrid();
		hiddenCells = BattleshipConfiguration.createEmptyGrid();
		repaint();
	}
	
	/**
	 * Receives game state updates and updates the board display accordingly
	 * @param o Observable object containing game state
	 */
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
		
		// Check if it's Human vs Human
		String player2Type = rules.designPatterns.RulesFacade.player2Type;
		boolean isHumanVsHuman = "Human".equals(player2Type);
		
		if(isHumanVsHuman) {
			// In Human vs Human, each player always sees their ships when it's their turn
			if(player == currentPlayer) {
				showHiddenCells(); // Show the current player's ships
			} else {
				hideHiddenCells(); // Hide the opponent's ships
			}
		} else {
			// Original logic for Human vs Bot
			if(player != currentPlayer) {
				hideHiddenCells();
			}
		}
		
		// Always update visualization to show hits
		if(player != currentPlayer) {
			hideHiddenCells();
		}
		
		if (player == 2 && currentPlayer == 1) {
			setEnabled(true);
		}
		
		if(result && currentPlayer == player) {
			System.out.println("\n**********\nVICTORY\n**********\n");
			(Victory.getVictoryFrame(player1Name, player2Name)).setVisible(true);
			(Attack.getAttackFrame()).setVisible(false);
			return;
		}
		
	}	

	/**
	 * Enables or disables the board for interaction
	 * @param enabled true to enable, false to disable
	 */
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

	/**
	 * Sets the attack coordinates to display on the board
	 * @param coords List of attack coordinates
	 */
	public void setAttackCoords(java.util.List<String> coords) {
		this.attackCoords = coords;
		repaintAttackCoords();
	}

	/**
	 * Repaints the attack coordinates on the board
	 */
	private void repaintAttackCoords() {
		if (attackCoords == null) return;
		int size = battleship.main.battleship.BattleshipConfiguration.SQUARE_COUNT;
		int[][] temp = new int[size][size];
		for (String coord : attackCoords) {
			int[] xy = logic.shippositioning.ShipPlacementValidator.convertCoordinateToIndices(coord);
			if (xy != null) {
				temp[xy[0]][xy[1]] = 99; // 99 = shot made
			}
		}
		battleGrid.repaintCells(temp);
	}
}
