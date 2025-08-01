package rules;

import java.io.Serializable;
import java.util.*;

import battleship.main.battleship.*;
import logic.ships.*;
import rules.designPatterns.*;
import rules.designPatterns.Observable;
import rules.designPatterns.Observer;
import logic.shippositioning.*;


public class CtrlRules implements Observable, Serializable {

	private static final long serialVersionUID = 1L;

	// Observer Attributes - Game state data
	private int board1[][]; // Player 1's board
	private int board2[][]; // Player 2's board
	private int currentPlayer; // Current player (1 or 2)
	private boolean result = false; // Game result (true if game is over)
	private boolean isValid; // Validation state for ship positioning
	private int cellsToPaint[][]; // Temporary cells to paint during positioning
	private String player1; // Player 1 name
	private String player2; // Player 2 name
	List<String> messages = new ArrayList<String>(); // Game messages

	// List of Observers - UI components to notify
	List<Observer> lob = new ArrayList<Observer>();

	// Non-Observer Attributes - Game logic state
	private BattleshipConfiguration.PHASE phase; // Current game phase (POSITION or ATTACK)
	private Ship selectedShip; // Currently selected ship for positioning
	private int pointsPlayer1 = 0; // Player 1's score
	private int pointsPlayer2 = 0; // Player 2's score
	private int currentAttackCount = 1; // Current attack count for multi-attack powers

	// Track player 1's ship placements and attack moves
	private java.util.List<String> player1ShipCoords = new java.util.ArrayList<>();
	private java.util.List<String> player1AttackCoords = new java.util.ArrayList<>();
	private java.util.List<String> player2AttackCoords = new java.util.ArrayList<>();

	// Flag to prevent multiple bot attacks in the same cycle
	private boolean botHasAttacked = false;

	// Attack counter for human player
	private int humanAttackCount = 0;

	/**
	 * Constructor - initializes a new game
	 */
	public CtrlRules() {
		newGame();
	}

	/**
	 * Initializes a new game with default settings
	 */
	public void newGame() {
		phase = BattleshipConfiguration.PHASE.POSITION;
		board1 = BattleshipConfiguration.createEmptyGrid();
		board2 = BattleshipConfiguration.createEmptyGrid();
		currentPlayer = 1;
		refreshBoard();
	}

	/**
	 * Resets the game to initial state
	 */
	public void resetGame() {
		board1 = BattleshipConfiguration.createEmptyGrid();
		board2 = BattleshipConfiguration.createEmptyGrid();
		currentPlayer = 1;
		result = false;
		isValid = false;
		cellsToPaint = BattleshipConfiguration.createEmptyGrid();
		player1 = "";
		player2 = "";
		messages.clear();
		lob.clear();

		phase = BattleshipConfiguration.PHASE.POSITION;
		selectedShip = null;
		pointsPlayer1 = 0;
		pointsPlayer2 = 0;
		currentAttackCount = 1;

		resetGrid();

		refreshBoard();
	}

	// Public Functions for positioning on the board

	/**
	 * Rotates the currently selected ship
	 */
	public void shipRotate() {
		if(selectedShip == null) return;
		selectedShip.rotate();
		refreshBoard();
	}

	/**
	 * Attempts to position a ship at the given coordinates
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param definedCells Current board state
	 */
	public void positionShip(int x, int y, int[][] definedCells) {
		if(selectedShip == null) {
			setIsValid(false);
			addMessage("Select a ship");
			return;
		}
				
		checkPos(x, y, definedCells);
		
		if(!isValid) {
			addMessage("Invalid position");
			refreshBoard();
			return;
		}
		
		addMessage("Positioning ship");
		PositioningGrid.getGrid().paintCells(cellsToPaint);
		ShipOptions.getShipOptions().reduceShipCount(selectedShip);
		
		// Track player 1 ship placement for LearningBot
		if (currentPlayer == 1) {
			// Find all cells for the selected ship and add to the list
			for (int i = 0; i < definedCells.length; i++) {
				for (int j = 0; j < definedCells[i].length; j++) {
					if (definedCells[i][j] == selectedShip.shipSize) {
						char col = (char) ('A' + i);
						int row = j + 1;
						String coord = "" + col + row;
						if (!player1ShipCoords.contains(coord)) {
							player1ShipCoords.add(coord);
						}
					}
				}
			}
		}
		
		if(!selectedShip.getAvailability()) {
			unsetSelectedShip();
			refreshBoard();
			return;
		}
		
		cellsToPaint = BattleshipConfiguration.createEmptyGrid();
		refreshBoard();
	}

	/**
	 * Repositions a ship by removing it from the current position and allowing repositioning
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param definedCells Current board state
	 */
	public void repositionShip(int x, int y, int[][] definedCells) {
		isValid = true;
		addMessage("Repositioning Ship");
		
		int[][] cellsToRemove = BattleshipConfiguration.createEmptyGrid();
		
		setSelectedShipBySize(definedCells[x][y]);
		cellsToRemove = removeShip(x, y, definedCells);
						
		PositioningGrid.getGrid().repositionRepaint(cellsToRemove);
		ShipOptions.getShipOptions().increaseShipCount(selectedShip);		
	}

	/**
	 * Resets the positioning grid to initial state
	 */
	public void resetGrid() {
		setIsValid(true);
		PositioningGrid.getGrid().reset();
		ShipOptions.getShipOptions().resetShipCount();
		unsetSelectedShip();
	}

	/**
	 * Checks if a position is valid for the selected ship
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param definedCells Current board state
	 */
	public void checkPos(int x, int y, int[][] definedCells) {
		if(selectedShip == null) {
			return;
		}
				
		checkPosShip(x, y, definedCells);
		
		refreshBoard();
	}

	// Private Functions for the position of the board

	/**
	 * Removes a ship from the board at the given coordinates
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param cellsToRemove Board to remove ship from
	 * @return Updated board with ship removed
	 */
	private int[][] removeShip(int x, int y, int[][] cellsToRemove){
		
		if(cellsToRemove[x][y] == BattleshipConfiguration.SHIPS.SUBMARINE.getValue()) {
			cellsToRemove[x][y] = 100;
			return cellsToRemove;
		}
		
		try { 
			//LEFT-RIGHT -> Reach the left end and go to the right end
			if(cellsToRemove[x+1][y] > 0) {
				try {
					while(cellsToRemove[x][y] != 0) {
						x--;
					}
				} catch(ArrayIndexOutOfBoundsException e) {   }

				//Reached end => sum 1 to x to get back to ship
				x += 1;

				//Beginning left to right removal
				try {
					while(cellsToRemove[x][y] != 0) {
						cellsToRemove[x][y] = 100;
						x++;
					}
					return cellsToRemove;
				} catch(ArrayIndexOutOfBoundsException e) { }
			}; 
		} catch(ArrayIndexOutOfBoundsException e) { }
		try { 
			//LEFT-RIGHT -> Reach the left end and go to the right end
			if(cellsToRemove[x-1][y] > 0) {
				try {
					while(cellsToRemove[x][y] != 0) {
						x--;
					}
				} catch(ArrayIndexOutOfBoundsException e) { }

				//Reached end => sum 1 to x to get back to ship
				x += 1;

				//Beginning the left to right removal
				try {
					while(cellsToRemove[x][y] != 0) {
						cellsToRemove[x][y] = 100;
						x++;
					}
					return cellsToRemove;
				} catch(ArrayIndexOutOfBoundsException e) { }
			}; 
		} catch(ArrayIndexOutOfBoundsException e) { }
		try { 
			//BOTTOM-TOP -> Reach the bottom and go to the top end
			if(cellsToRemove[x][y+1] > 0) {
				try {
					while(cellsToRemove[x][y] != 0) {
						y--;
					}
				} catch(ArrayIndexOutOfBoundsException e) { }

				//Reached end => sum 1 to y to get back to ship
				y += 1;

				//Beginning bottom to top removal
				try {
					while(cellsToRemove[x][y] != 0) {
						cellsToRemove[x][y] = 100;
						y++;
					}
					return cellsToRemove;
				} catch(ArrayIndexOutOfBoundsException e) { }
			}; 
		} catch(ArrayIndexOutOfBoundsException e) { }	
		try { 
			//BOTTOM-TOP -> Reach the bottom and go to the top end
			if(cellsToRemove[x][y-1] > 0) {
				try {
					while(cellsToRemove[x][y] != 0) {
						y--;
					}
				} catch(ArrayIndexOutOfBoundsException e) { }

				//Reached end => sum 1 to y to get back to ship
				y += 1;

				//Beginning the bottom to top removal
				try {
					while(cellsToRemove[x][y] != 0) {
						cellsToRemove[x][y] = 100;
						y++;
					}
					return cellsToRemove;
				} catch(ArrayIndexOutOfBoundsException e) { }
			};
		} catch(ArrayIndexOutOfBoundsException e) { }
		
		return null;
	}

	/**
	 * Sets the selected ship based on ship size
	 * @param shipSize Size of the ship to select
	 */
	private void setSelectedShipBySize(int shipSize) {
		if(shipSize == 1) {
			selectedShip = Submarine.getSubmarine();
		}
		else if(shipSize == 2) {
			selectedShip = Destroyer.getDestroyer();
		}
		else if(shipSize == 3) {
			selectedShip = Cruiser.getCruiser();
		}
		else if(shipSize == 4) {
			selectedShip = Battleship.getBattleship();
		}
	}

	/**
	 * Checks if a position is valid for ship placement
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param definedCells Current board state
	 */
	private void checkPosShip(int x, int y, int[][] definedCells){
		cellsToPaint = BattleshipConfiguration.createEmptyGrid();
		setIsValid(true);
		
		if(!selectedShip.getAvailability()) {
			setIsValid(false);
			return;
		}
		
		if(selectedShip.orientation == BattleshipConfiguration.ORIENTATION.TOP) {
			for(int i = 0; i < selectedShip.shipSize; i++) {
				try {
					if(definedCells[x][y-i] != 0) setIsValid(false);
					cellsToPaint[x][y-i] = selectedShip.shipSize;
				}
				catch(ArrayIndexOutOfBoundsException e) {
					setIsValid(false);
				}
			}
		}
		else if(selectedShip.orientation == BattleshipConfiguration.ORIENTATION.RIGHT) {
			for(int i = 0; i < selectedShip.shipSize; i++) {
				try {
					if(definedCells[x+i][y] != 0) setIsValid(false);
					cellsToPaint[x+i][y] = selectedShip.shipSize;
				}
				catch(ArrayIndexOutOfBoundsException e) {
					setIsValid(false);
				}
			}
		}
		else if(selectedShip.orientation == BattleshipConfiguration.ORIENTATION.DOWN) {
			for(int i = 0; i < selectedShip.shipSize; i++) {
				try {
					if(definedCells[x][y+i] != 0) setIsValid(false);
					cellsToPaint[x][y+i] = selectedShip.shipSize;
				}
				catch(ArrayIndexOutOfBoundsException e) {
					setIsValid(false);
				}
			}
		}
		else if(selectedShip.orientation == BattleshipConfiguration.ORIENTATION.LEFT) {
			for(int i = 0; i < selectedShip.shipSize; i++) {
				try {
					if(definedCells[x-i][y] != 0) setIsValid(false);
					cellsToPaint[x-i][y] = selectedShip.shipSize;
				}
				catch(ArrayIndexOutOfBoundsException e) {
					setIsValid(false);
				}
			}
		}
		
		if(isValid) {
			setIsValid( checkSurroundingsShip(x, y, definedCells) );
		}
	}

	/**
	 * Checks if the surroundings of a position are valid for ship placement
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param definedCells Current board state
	 * @return true if surroundings are valid, false otherwise
	 */
	private boolean checkSurroundingsShip(int x, int y, int[][] definedCells) {
		
		if(selectedShip.orientation == BattleshipConfiguration.ORIENTATION.TOP) {
			for(int i = selectedShip.shipSize-1; i >= 0; i--) {
				try { if(definedCells[x+1][y-i] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
				try { if(definedCells[x][y-i+1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
				try { if(definedCells[x-1][y-i] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
				try { if(definedCells[x][y-i-1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
				try { if(definedCells[x+1][y-i+1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
				try { if(definedCells[x-1][y-i+1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
				try { if(definedCells[x+1][y-i-1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
				try { if(definedCells[x-1][y-i-1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			}
		}
		else if(selectedShip.orientation == BattleshipConfiguration.ORIENTATION.RIGHT) {
			for(int i = 0; i < selectedShip.shipSize; i++) {
				try { if(definedCells[x+i+1][y] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
				try { if(definedCells[x+i][y+1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
				try { if(definedCells[x+i-1][y] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
				try { if(definedCells[x+i][y-1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
				try { if(definedCells[x+i+1][y+1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
				try { if(definedCells[x+i-1][y+1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
				try { if(definedCells[x+i+1][y-1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
				try { if(definedCells[x+i-1][y-1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			}
		}
		else if(selectedShip.orientation == BattleshipConfiguration.ORIENTATION.DOWN) {
			for(int i = 0; i < selectedShip.shipSize; i++) {
				try { if(definedCells[x+1][y+i] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
				try { if(definedCells[x][y+i+1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
				try { if(definedCells[x-1][y+i] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
				try { if(definedCells[x][y+i-1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
				try { if(definedCells[x+1][y+i+1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
				try { if(definedCells[x-1][y+i+1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
				try { if(definedCells[x+1][y+i-1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
				try { if(definedCells[x-1][y+i-1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}	
			}
		}
		else if(selectedShip.orientation == BattleshipConfiguration.ORIENTATION.LEFT) {
			for(int i = selectedShip.shipSize-1; i >= 0; i--) {
				try { if(definedCells[x-i+1][y] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
				try { if(definedCells[x-i][y+1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
				try { if(definedCells[x-i-1][y] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
				try { if(definedCells[x-i][y-1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
				try { if(definedCells[x-i+1][y+1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
				try { if(definedCells[x-i-1][y+1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
				try { if(definedCells[x-i+1][y-1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
				try { if(definedCells[x-i-1][y-1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			}
		}
		
		return true;
	}

	// Public Functions for the attacking phase

	/**
	 * Starts the attack phase of the game
	 */
	public void startGame() {
		phase = BattleshipConfiguration.PHASE.ATTACK;
	}

	/**
	 * Switches to the next player and handles bot moves
	 */
	public void nextPlayer() {
		checkResult();
		if(result) {
			System.out.printf("Player %d wins!\n", currentPlayer);
			refreshBoard();
			return;
		}
		
		// Handle positioning phase
		if(phase == BattleshipConfiguration.PHASE.POSITION) {
			currentPlayer = getNextPlayer();
			// Reset grid and ship options for the next player
			if(currentPlayer == 2) {
				resetGrid();
				// Reset ship counts for Player 2
				ShipOptions.getShipOptions().resetShipCount();
			}
			refreshBoard();
			return;
		}
		
		// Handle attack phase
		currentPlayer = getNextPlayer();
		refreshBoard();
		// Bot move logic: bot attacks only after human player has attacked
		if(currentPlayer == 2 && !"Human".equals(rules.designPatterns.RulesFacade.player2Type)) {
			// Make bot use a power (only HardBot)
			if ("HardBot".equals(rules.designPatterns.RulesFacade.player2Type)) {
				logic.attack.AttackUtilities.botUsePower(1); // 1 = bot (player 2)
			}
			int botAttacks = 0;
			while (botAttacks < 3 && !result) {
				String move = bot.BotManager.getBotMove(rules.designPatterns.RulesFacade.player2Type);
				if(move != null && move.length() >= 2) {
					char col = move.charAt(0);
					int x = col - 'A';
					int y;
					try {
						y = Integer.parseInt(move.substring(1)) - 1;
					} catch (NumberFormatException e) {
						continue;
					}
					int size = battleship.main.battleship.BattleshipConfiguration.SQUARE_COUNT;
					if (x >= 0 && x < size && y >= 0 && y < size) {
						attack(y, x);
						checkResult();
						botAttacks++;
						// Forza l'aggiornamento della board per mostrare l'attacco del bot
						refreshBoard();
						// Piccola pausa per rendere visibili gli attacchi del bot
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
						}
						if(result) break;
					}
				}
			}
			botHasAttacked = true;
			if (!result) {
				currentPlayer = getNextPlayer();
				// Unlock board for human player
				logic.attack.Attack.getAttackFrame().blockCells = false;
				logic.attack.AttackUtilities.getAttackUtilites().buttonDisable();
				// Forza l'aggiornamento finale della board
				refreshBoard();
			}
		} else {
			botHasAttacked = false;
			humanAttackCount = 0;
			// Unlock board for human player and update UI
			logic.attack.Attack.getAttackFrame().blockCells = false;
			logic.attack.AttackUtilities.getAttackUtilites().buttonDisable();
			refreshBoard();
		}
	}

	/**
	 * Handles an attack at the given coordinates
	 * @param x X coordinate
	 * @param y Y coordinate
	 */
	public void attack(int x, int y) {
		// Early check: if cells are blocked, don't allow attack
		if(logic.attack.Attack.getAttackFrame().blockCells) {
			addMessage("Cells are blocked! Click Next to switch turns.");
			refreshBoard();
			return;
		}
		
		// Track player 1 and player 2 attack moves
		if (currentPlayer == 1) {
			char col = (char) ('A' + y);
			int row = x + 1;
			String coord = "" + col + row;
			player1AttackCoords.add(coord);
		} else if (currentPlayer == 2) {
			char col = (char) ('A' + y);
			int row = x + 1;
			String coord = "" + col + row;
			player2AttackCoords.add(coord);
		}
		if(getOppositeBoard(currentPlayer)[x][y] == BattleshipConfiguration.SHIPS.D_WATER.getValue() || getOppositeBoard(currentPlayer)[x][y] < 0) {
			addMessage("This cell was already clicked!");
			refreshBoard(); // Update board immediately
			return;
		}
		else if(getOppositeBoard(currentPlayer)[x][y] > 0 && getOppositeBoard(currentPlayer)[x][y] < BattleshipConfiguration.SHIPS.D_WATER.getValue()) {
			addMessage(getPlayerName(currentPlayer) + " hit a " + BattleshipConfiguration.getShipNameBySize(getOppositeBoard(currentPlayer)[x][y]) + "!");
			attackShip(x, y);
		}
		else if(getOppositeBoard(currentPlayer)[x][y] == 0) {			
			addMessage(getPlayerName(currentPlayer) + " missed!");
			attackShip(x, y);
		}

		// Check if this attack was triggered by a power usage
		boolean isPowerAttack = false;
		try {
			String selectedPower = logic.attack.AttackUtilities.getAttackUtilites().getSelectedPower();
			isPowerAttack = selectedPower != null;
		} catch(Exception ex) {}

		// Handle multiple attacks for human player (only for regular attacks, not power attacks)
		if (!isPowerAttack && (currentPlayer == 1 ||
		(currentPlayer == 2 && "Human".equals(rules.designPatterns.RulesFacade.player2Type))))  {
			humanAttackCount++;
			checkResult();
			if(result) {
				refreshBoard(); // Update board immediately
				return;
			}
			if(humanAttackCount == 3) {
				humanAttackCount = 0;
				logic.attack.Attack.getAttackFrame().blockCells = true;
				logic.attack.AttackUtilities.getAttackUtilites().buttonEnable();
				refreshBoard(); // Update board immediately
				// Auto-click Next button after 3 attacks
				nextPlayer();
				return;
			} else {
				logic.attack.Attack.getAttackFrame().blockCells = false;
				logic.attack.AttackUtilities.getAttackUtilites().buttonDisable();
				refreshBoard(); // Update board immediately
			}
		} else {
			checkResult();
			if(result) {
				refreshBoard(); // Update board immediately
				return;
			}
			refreshBoard(); // Update board immediately
		}
		
		// Always update board at the end to show hits immediately
		refreshBoard();
	}

	// Private Functions for the attacking phase

	/**
	 * Processes the attack on a ship at the given coordinates
	 * @param x X coordinate
	 * @param y Y coordinate
	 */
	private void attackShip(int x, int y) {
		int[][] currentBoard = getOppositeBoard(currentPlayer);
		int currentPlayerPoints = 0;
		
		if(currentBoard[x][y] == 0) {
			currentBoard[x][y] = BattleshipConfiguration.SHIPS.D_WATER.getValue();
		}
		else if(currentBoard[x][y] > 0) {
			currentPlayerPoints += 1;
			currentBoard[x][y] = -currentBoard[x][y];
			
			if(checkIfShipDestroyed(x, y)) {
				destroyShip(x, y);
			}
		}
		
		switch(currentPlayer) {
			case 1: 
				pointsPlayer1 += currentPlayerPoints;
				break;
			case 2: 
				pointsPlayer2 += currentPlayerPoints;
				break;
		}
	}

	/**
	 * Checks if a ship at the given coordinates is completely destroyed
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @return true if ship is destroyed, false otherwise
	 */
	private boolean checkIfShipDestroyed(int x, int y) {
		
		int[][] currentBoard = getOppositeBoard(currentPlayer);
		int destroyedCellsNum = 0;
		
		int shipSize = -currentBoard[x][y];
		
		if(currentBoard[x][y] == BattleshipConfiguration.SHIPS.D_SUBMARINE.getValue()) {
			return true;
		}

		try { 
			//LEFT-RIGHT -> Reach the left end and go to the right end
			if(currentBoard[x+1][y] < 0) {
				try {
					while(currentBoard[x][y] != 0 && currentBoard[x][y] != BattleshipConfiguration.SHIPS.D_WATER.getValue()) {
						x--;
					}
				} catch(ArrayIndexOutOfBoundsException e) {   }

				//Reached end => sum 1 to x to get back to ship
				x += 1;

				//Beginning left to right check
				try {
					while(currentBoard[x][y] != 0 && currentBoard[x][y] != BattleshipConfiguration.SHIPS.D_WATER.getValue()) {
						if(currentBoard[x][y] < 0) {
							destroyedCellsNum++;
						}
						x++;
					}
				} catch(ArrayIndexOutOfBoundsException e) { }
			}; 
		} catch(ArrayIndexOutOfBoundsException e) { }
		try { 
			//LEFT-RIGHT -> Reach the left end and go to the right end
			if(currentBoard[x-1][y] < 0) {
				try {
					while(currentBoard[x][y] != 0 && currentBoard[x][y] != BattleshipConfiguration.SHIPS.D_WATER.getValue()) {
						x--;
					}
				} catch(ArrayIndexOutOfBoundsException e) { }

				//Reached end => sum 1 to x to get back to ship
				x += 1;

				//Beginning left to right check
				try {
					while(currentBoard[x][y] != 0 && currentBoard[x][y] != BattleshipConfiguration.SHIPS.D_WATER.getValue()) {
						if(currentBoard[x][y] < 0) {
							destroyedCellsNum++;
						}
						x++;
					}
				} catch(ArrayIndexOutOfBoundsException e) { }
			}; 
		} catch(ArrayIndexOutOfBoundsException e) { }
		try { 
			//BOTTOM-TOP -> Reach the bottom and go to the top end
			if(currentBoard[x][y+1] < 0) {
				try {
					while(currentBoard[x][y] != 0 && currentBoard[x][y] != BattleshipConfiguration.SHIPS.D_WATER.getValue()) {
						y--;
					}
				} catch(ArrayIndexOutOfBoundsException e) { }

				//Reached end => sum 1 to y to get back to ship
				y += 1;

				//Beginning bottom to top check
				try {
					while(currentBoard[x][y] != 0 && currentBoard[x][y] != BattleshipConfiguration.SHIPS.D_WATER.getValue()) {
						if(currentBoard[x][y] < 0) {
							destroyedCellsNum++;
						}
						y++;
					}
				} catch(ArrayIndexOutOfBoundsException e) { }
			}; 
		} catch(ArrayIndexOutOfBoundsException e) { }	
		try { 
			//BOTTOM-TOP -> Reach the bottom and go to the top end
			if(currentBoard[x][y-1] < 0) {
				try {
					while(currentBoard[x][y] != 0 && currentBoard[x][y] != BattleshipConfiguration.SHIPS.D_WATER.getValue()) {
						y--;
					}
				} catch(ArrayIndexOutOfBoundsException e) { }

				//Reached end => sum 1 to y to get back to ship
				y += 1;

				//Beginning the bottom to top check
				try {
					while(currentBoard[x][y] != 0 && currentBoard[x][y] != BattleshipConfiguration.SHIPS.D_WATER.getValue()) {
						if(currentBoard[x][y] < 0) {
							destroyedCellsNum++;
						}
						y++;
					}
				} catch(ArrayIndexOutOfBoundsException e) { }
			};
		} catch(ArrayIndexOutOfBoundsException e) { }
		
		return shipSize == destroyedCellsNum;
	}

	/**
	 * Destroys a ship at the given coordinates
	 * @param x X coordinate
	 * @param y Y coordinate
	 */
	private void destroyShip(int x, int y) {
		int[][] currentBoard = getOppositeBoard(currentPlayer);
		int originalX = x;
		int originalY = y;

		addMessage(getPlayerName(currentPlayer) + " sinked a " + BattleshipConfiguration.getShipNameBySize(Math.abs(currentBoard[x][y])) + "!");
		
		// Handle submarine (single cell ship)
		if(currentBoard[x][y] == BattleshipConfiguration.SHIPS.D_SUBMARINE.getValue()) {
			currentBoard[x][y] -= BattleshipConfiguration.DESTROYED_SHIP_LIMIT;
			return;
		}	

		// Find all cells of the ship and mark them as destroyed
		// First, find the ship boundaries
		int startX = x, endX = x, startY = y, endY = y;
		
		// Find left boundary
		while(startX > 0 && currentBoard[startX-1][y] != 0 && currentBoard[startX-1][y] != BattleshipConfiguration.SHIPS.D_WATER.getValue()) {
			startX--;
		}
		// Find right boundary
		while(endX < BattleshipConfiguration.SQUARE_COUNT-1 && currentBoard[endX+1][y] != 0 && currentBoard[endX+1][y] != BattleshipConfiguration.SHIPS.D_WATER.getValue()) {
			endX++;
		}
		// Find top boundary
		while(startY > 0 && currentBoard[x][startY-1] != 0 && currentBoard[x][startY-1] != BattleshipConfiguration.SHIPS.D_WATER.getValue()) {
			startY--;
		}
		// Find bottom boundary
		while(endY < BattleshipConfiguration.SQUARE_COUNT-1 && currentBoard[x][endY+1] != 0 && currentBoard[x][endY+1] != BattleshipConfiguration.SHIPS.D_WATER.getValue()) {
			endY++;
		}
		
		// Determine if ship is horizontal or vertical
		boolean isHorizontal = (endX - startX) > (endY - startY);
		
		if(isHorizontal) {
			// Destroy horizontal ship
			for(int i = startX; i <= endX; i++) {
				if(currentBoard[i][y] != 0 && currentBoard[i][y] != BattleshipConfiguration.SHIPS.D_WATER.getValue()) {
					currentBoard[i][y] -= BattleshipConfiguration.DESTROYED_SHIP_LIMIT;
				}
			}
		} else {
			// Destroy vertical ship
			for(int j = startY; j <= endY; j++) {
				if(currentBoard[x][j] != 0 && currentBoard[x][j] != BattleshipConfiguration.SHIPS.D_WATER.getValue()) {
					currentBoard[x][j] -= BattleshipConfiguration.DESTROYED_SHIP_LIMIT;
				}
			}}
		}

	/**
	 * Checks if the game has ended and determines the winner
	 */
	private void checkResult() {
		
		int currentPlayerPoints = 0;
		
		switch(currentPlayer) {
			case 1: 
				currentPlayerPoints = pointsPlayer1;
				break;
			case 2: 
				currentPlayerPoints = pointsPlayer2;
				break;
		}
				
		if(currentPlayerPoints == 23) {
			result = true;
		}
		
		refreshBoard();
	}
	
	// List of messages

	/**
	 * Adds a message to the game message list
	 * @param message Message to add
	 */
	public void addMessage(String message) {
		messages.add(message);
		refreshBoard();
	}

	/**
	 * Clears the message list
	 */
	public void emptyMessagesList() {
		messages.clear();
	}

	// Methods "get" and "set"

	/**
	 * Gets the current game phase
	 * @return Current phase (POSITION or ATTACK)
	 */
	public BattleshipConfiguration.PHASE getPhase() {
		return phase;
	}

	/**
	 * Sets the selected ship for positioning
	 * @param ship Ship to select
	 */
	public void setSelectedShip(Ship ship) {
		selectedShip = ship;
    }

	/**
	 * Unsets the currently selected ship
	 */
	public void unsetSelectedShip() {
		if(selectedShip == null) return;
    	selectedShip.unselectPreviousShip();
		selectedShip = null;
		
		cellsToPaint = BattleshipConfiguration.createEmptyGrid();
		refreshBoard();
    }

	/**
	 * Gets the current player number
	 * @return Current player (1 or 2)
	 */
	public int getCurrentPlayer() {
		return currentPlayer;
	}

	/**
	 * Gets the next player number
	 * @return Next player (1 or 2)
	 */
	public int getNextPlayer() {
		if(currentPlayer == 1) {
			return 2;
		}
		else {
			return 1;
		}
	}

	/**
	 * Sets the board for the specified player
	 * @param playerNum Player number (1 or 2)
	 */
	public void setBoard(int playerNum) {
		switch(playerNum) {
			case 1: board1 = PositioningGrid.getGrid().getFinalGrid();
			case 2: board2 = PositioningGrid.getGrid().getFinalGrid();
		}
	}

	/**
	 * Gets the name of the specified player
	 * @param playerNum Player number (1 or 2)
	 * @return Player name
	 */
	public String getPlayerName(int playerNum) {
		switch(playerNum) {
			case 1: return player1;
			case 2: return player2;
		}
		return null;
	}

	/**
	 * Sets the name of the specified player
	 * @param playerNum Player number (1 or 2)
	 * @param playerName Player name
	 */
	public void setPlayerName(int playerNum, String playerName) {
		switch(playerNum) {
		case 1: player1 = playerName;
		case 2: player2 = playerName;
		}
	}

	/**
	 * Gets the currently selected ship
	 * @return Selected ship or null
	 */
	public Ship getSelectedShip() {
		return selectedShip;
	}
	
	/**
	 * Sets the validation state
	 * @param validation Validation state
	 */
	private void setIsValid(boolean validation) {
		isValid = validation;
	}

	/**
	 * Gets the opposite player's board
	 * @param playerNum Current player number
	 * @return Opposite player's board
	 */
	private int[][] getOppositeBoard(int playerNum) {
		switch(playerNum) {
			case 1: return board2;
			case 2: return board1;
		}
		return null;
	}

	// Observer Functions
	
	/**
	 * Adds an observer to the notification list
	 * @param o Observer to add
	 */
	@Override
	public void addObserver(Observer o) {
		for (Observer ob : lob)
			if (o == ob)
				return;
		lob.add(o);
	}

	/**
	 * Removes an observer from the notification list
	 * @param o Observer to remove
	 */
	@Override
	public void removeObserver(Observer o) {
		lob.remove(o);
	}

	/**
	 * Gets the current game state for observers
	 * @return Object array containing game state data
	 */
	@Override
	public Object get() {
		Object[] dice = new Object[BattleshipConfiguration.objectValues.values().length];

		dice[BattleshipConfiguration.objectValues.BOARD_1.getValue()] = board1;
		dice[BattleshipConfiguration.objectValues.BOARD_2.getValue()] = board2;
		dice[BattleshipConfiguration.objectValues.CURRENT_PLAYER.getValue()] = currentPlayer;
		dice[BattleshipConfiguration.objectValues.RESULT.getValue()] = result;
		dice[BattleshipConfiguration.objectValues.IS_VALID.getValue()] = isValid;
		dice[BattleshipConfiguration.objectValues.CELLS_TO_PAINT.getValue()] = cellsToPaint;
		dice[BattleshipConfiguration.objectValues.PLAYER_1_NAME.getValue()] = player1;
		dice[BattleshipConfiguration.objectValues.PLAYER_2_NAME.getValue()] = player2;
		dice[BattleshipConfiguration.objectValues.MESSAGES.getValue()] = messages;

		return dice;
	}

	/**
	 * Notifies all observers of state changes
	 */
	public void refreshBoard() {
		for (Observer o : lob)
			o.notify(this);
	}

	/**
	 * Places bot ships on the board for the given player
	 * @param playerNum Player number (1 or 2)
	 * @param placements Map of ship placements
	 */
	public void placeBotShips(int playerNum, Map<String, List<String>> placements) {
		int[][] board = (playerNum == 1) ? board1 : board2;
		// Ship sizes mapping (should match BattleshipConfiguration)
		Map<String, Integer> shipSizes = new HashMap<>();
		shipSizes.put("Battleship", 4);
		shipSizes.put("Cruiser", 3);
		shipSizes.put("Destroyer", 2);
		shipSizes.put("Submarine", 1);
		Map<String, Integer> shipCodes = new HashMap<>();
		shipCodes.put("Battleship", BattleshipConfiguration.SHIPS.BATTLESHIP.getValue());
		shipCodes.put("Cruiser", BattleshipConfiguration.SHIPS.CRUISER.getValue());
		shipCodes.put("Destroyer", BattleshipConfiguration.SHIPS.DESTROYER.getValue());
		shipCodes.put("Submarine", BattleshipConfiguration.SHIPS.SUBMARINE.getValue());
		for (Map.Entry<String, List<String>> entry : placements.entrySet()) {
			String ship = entry.getKey();
			int code = shipCodes.getOrDefault(ship, 0);
			for (String coord : entry.getValue()) {
				if (coord.length() < 2) continue;
				char col = coord.charAt(0);
				int x = col - 'A';
				int y;
				try {
					y = Integer.parseInt(coord.substring(1)) - 1;
				} catch (NumberFormatException e) {
					continue;
				}
				if (x >= 0 && x < BattleshipConfiguration.SQUARE_COUNT && y >= 0 && y < BattleshipConfiguration.SQUARE_COUNT) {
					board[y][x] = code;
				}
			}
		}
		if (playerNum == 1) board1 = board;
		else board2 = board;
		refreshBoard();
	}

	// Getters for player data
	
	/**
	 * Gets player 1's ship coordinates
	 * @return List of ship coordinates
	 */
	public java.util.List<String> getPlayer1ShipCoords() {
		return player1ShipCoords;
	}

	/**
	 * Gets player 1's attack coordinates
	 * @return List of attack coordinates
	 */
	public java.util.List<String> getPlayer1AttackCoords() {
		return player1AttackCoords;
	}

	/**
	 * Gets player 2's attack coordinates
	 * @return List of attack coordinates
	 */
	public java.util.List<String> getPlayer2AttackCoords() {
		return player2AttackCoords;
	}
}
