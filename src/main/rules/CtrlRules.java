package main.rules;

import java.io.Serializable;
import java.util.*;

import main.battleship.BattleshipConfiguration;
import main.battleship.BattleshipConfiguration.*;
import main.logic.ships.*;
import main.rules.designPatterns.*;
import main.rules.designPatterns.Observable;
import main.rules.designPatterns.Observer;
import main.logic.shippositioning.*;
import main.bot.BotManager;

public class CtrlRules implements Observable, Serializable {

	private static final long serialVersionUID = 1L;

	// Observer Attributes
	private int board1[][];
	private int board2[][];
	private int currentPlayer;
	private boolean result = false;
	private boolean isValid;
	private int cellsToPaint[][];
	private String player1;
	private String player2;
	List<String> messages = new ArrayList<String>();

	// List of Observers
	
	List<Observer> lob = new ArrayList<Observer>();

	
	// Non-Observer Attributes
	
	private PHASE phase;
	private Ship selectedShip;
	private int pointsPlayer1 = 0;
	private int pointsPlayer2 = 0;
	private int currentAttackCount = 1;

	// Track player 1's ship placements and attack moves for LearningBot
	private java.util.List<String> player1ShipCoords = new java.util.ArrayList<>();
	private java.util.List<String> player1AttackCoords = new java.util.ArrayList<>();
	private java.util.List<String> player2AttackCoords = new java.util.ArrayList<>();

	// Flag per evitare attacchi multipli del bot nello stesso ciclo
	private boolean botHasAttacked = false;

	// Contatore attacchi per il player umano
	private int humanAttackCount = 0;

	
	// Constructor
	
	public CtrlRules() {
		newGame();
	}

	public void newGame() {
		phase = PHASE.POSITION;
		board1 = BattleshipConfiguration.createEmptyGrid();
		board2 = BattleshipConfiguration.createEmptyGrid();
		currentPlayer = 1;
		refreshBoard();
	}

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

		phase = PHASE.POSITION;
		selectedShip = null;
		pointsPlayer1 = 0;
		pointsPlayer2 = 0;
		currentAttackCount = 1;

		resetGrid();

		refreshBoard();
	}

	// Public Functions for postioning on the board

	public void shipRotate() {
		if(selectedShip == null) return;
		selectedShip.rotate();
		refreshBoard();
	}
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
		
		// Track player 1 ship placement
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
	public void repositionShip(int x, int y, int[][] definedCells) {
		
		isValid = true;
		addMessage("Repositioning Ship");
		
		int[][] cellsToRemove = BattleshipConfiguration.createEmptyGrid();
		
		setSelectedShipBySize(definedCells[x][y]);
		cellsToRemove = removeShip(x, y, definedCells);
						
		PositioningGrid.getGrid().repositionRepaint(cellsToRemove);
		ShipOptions.getShipOptions().increaseShipCount(selectedShip);		
	}
	public void resetGrid() {
		setIsValid(true);
		
		addMessage("Reseting Grid");
		
		PositioningGrid.getGrid().reset();
		
		ShipOptions.getShipOptions().resetShipCount();
		
		unsetSelectedShip();
	}
	public void checkPos(int x, int y, int[][] definedCells) {
		
		if(selectedShip == null) {
			return;
		}
				
		if(selectedShip.getClass().getName() == "main.logic.ships.Seaplane") {
			checkPosSeaplane(x, y, definedCells);
		}
		else{
			checkPosShip(x, y, definedCells);
		}
		
		refreshBoard();
	}
	

//////////////////////////////////////

	// Private Functions for the position of the board


	private int[][] removeShip(int x, int y, int[][] cellsToRemove){
		
		if(cellsToRemove[x][y] == SHIPS.SEAPLANE.getValue()) {
			return removeSeaplane(x, y, cellsToRemove);
		}
		
		if(cellsToRemove[x][y] == SHIPS.SUBMARINE.getValue()) {
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
	private int[][] removeSeaplane(int x, int y, int[][] cellsToRemove) {
		
		try {
			if(cellsToRemove[x+1][y+1] == SHIPS.SEAPLANE.getValue()) {
				//Check if the block on middle of Seaplane
				try {
					if(cellsToRemove[x+1][y-1] == SHIPS.SEAPLANE.getValue()) {
						cellsToRemove[x][y] = 100;
						cellsToRemove[x+1][y+1] = 100;
						cellsToRemove[x+1][y-1] = 100;
						return cellsToRemove;
					}
				}catch(ArrayIndexOutOfBoundsException e) {}
				//Check if the block on end of Seaplane
				try {
					if(cellsToRemove[x][y+2] == SHIPS.SEAPLANE.getValue()) {
						cellsToRemove[x][y] = 100;
						cellsToRemove[x+1][y+1] = 100;
						cellsToRemove[x][y+2] = 100;
						return cellsToRemove;
					}
				}catch(ArrayIndexOutOfBoundsException e) {}
				try {
					if(cellsToRemove[x+2][y] == SHIPS.SEAPLANE.getValue()) {
						cellsToRemove[x][y] = 100;
						cellsToRemove[x+1][y+1] = 100;
						cellsToRemove[x+2][y] = 100;
						return cellsToRemove;
					}
				}catch(ArrayIndexOutOfBoundsException e) {}
			}
		}catch(ArrayIndexOutOfBoundsException e) {}
		
		try {
			if(cellsToRemove[x+1][y-1] == SHIPS.SEAPLANE.getValue()) {
				//Check if the block on middle of Seaplane
				try {
					if(cellsToRemove[x-1][y-1] == SHIPS.SEAPLANE.getValue()) {
						cellsToRemove[x][y] = 100;
						cellsToRemove[x+1][y-1] = 100;
						cellsToRemove[x-1][y-1] = 100;
						return cellsToRemove;
					}
				}catch(ArrayIndexOutOfBoundsException e) {}
				//Check if the block on end of Seaplane
				try {
					if(cellsToRemove[x+2][y] == SHIPS.SEAPLANE.getValue()) {
						cellsToRemove[x][y] = 100;
						cellsToRemove[x+1][y-1] = 100;
						cellsToRemove[x+2][y] = 100;
						return cellsToRemove;
					}
				}catch(ArrayIndexOutOfBoundsException e) {}
				try {
					if(cellsToRemove[x][y-2] == SHIPS.SEAPLANE.getValue()) {
						cellsToRemove[x][y] = 100;
						cellsToRemove[x+1][y-1] = 100;
						cellsToRemove[x][y-2] = 100;
						return cellsToRemove;
					}
				}catch(ArrayIndexOutOfBoundsException e) {}
			}
		}catch(ArrayIndexOutOfBoundsException e) {}
		try {
			if(cellsToRemove[x-1][y-1] == SHIPS.SEAPLANE.getValue()) {
				//Check if the block is on middle of the Seaplane
				try {
					if(cellsToRemove[x-1][y+1] == SHIPS.SEAPLANE.getValue()) {
						cellsToRemove[x][y] = 100;
						cellsToRemove[x-1][y-1] = 100;
						cellsToRemove[x-1][y+1] = 100;
						return cellsToRemove;
					}
				}catch(ArrayIndexOutOfBoundsException e) {}
				//Check if the block is on end of the Seaplane
				try {
					if(cellsToRemove[x][y-2] == SHIPS.SEAPLANE.getValue()) {
						cellsToRemove[x][y] = 100;
						cellsToRemove[x-1][y-1] = 100;
						cellsToRemove[x][y-2] = 100;
						return cellsToRemove;
					}
				}catch(ArrayIndexOutOfBoundsException e) {}
				try {
					if(cellsToRemove[x-2][y] == SHIPS.SEAPLANE.getValue()) {
						cellsToRemove[x][y] = 100;
						cellsToRemove[x-1][y-1] = 100;
						cellsToRemove[x-2][y] = 100;
						return cellsToRemove;
					}
				}catch(ArrayIndexOutOfBoundsException e) {}
			}
		}catch(ArrayIndexOutOfBoundsException e) {}
		try {
			if(cellsToRemove[x-1][y+1] == SHIPS.SEAPLANE.getValue()) {
				//Check if the block is on middle of the Seaplane
				try {
					if(cellsToRemove[x+1][y+1] == SHIPS.SEAPLANE.getValue()) {
						cellsToRemove[x][y] = 100;
						cellsToRemove[x-1][y+1] = 100;
						cellsToRemove[x+1][y+1] = 100;
						return cellsToRemove;
					}
				}catch(ArrayIndexOutOfBoundsException e) {}
				//Check if the block is on the end of the Seaplane
				try {
					if(cellsToRemove[x-2][y] == SHIPS.SEAPLANE.getValue()) {
						cellsToRemove[x][y] = 100;
						cellsToRemove[x-1][y+1] = 100;
						cellsToRemove[x-2][y] = 100;
						return cellsToRemove;
					}
				}catch(ArrayIndexOutOfBoundsException e) {}
				
				try {
					if(cellsToRemove[x][y+2] == SHIPS.SEAPLANE.getValue()) {
						cellsToRemove[x][y] = 100;
						cellsToRemove[x-1][y+1] = 100;
						cellsToRemove[x][y+2] = 100;
						return cellsToRemove;
					}
				}catch(ArrayIndexOutOfBoundsException e) {}
			}
		}catch(ArrayIndexOutOfBoundsException e) {}
				
		
		return cellsToRemove;
	}
	private void setSelectedShipBySize(int shipSize) {
		if(shipSize == 1) {
			setSelectedShip(Submarine.getSubmarine());
		}
		else if(shipSize == 2) {
			setSelectedShip(Destroyer.getDestroyer());
		}
		//else if(shipSize == 3) {
		//	setSelectedShip(Seaplane.getSeaplane());
		//}
		else if(shipSize == 4) {
			setSelectedShip(Cruiser.getCruiser());
		}
		else if(shipSize == 5) {
			setSelectedShip(Battleship.getBattleship());
		}
	}
	private void checkPosShip(int x, int y, int[][] definedCells){
		
		cellsToPaint = BattleshipConfiguration.createEmptyGrid();
		setIsValid(true);
		
		if(!selectedShip.getAvailability()) {
			setIsValid(false);
			return;
		}
		
		if(selectedShip.orientation == ORIENTATION.TOP) {
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
		else if(selectedShip.orientation == ORIENTATION.RIGHT) {
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
		else if(selectedShip.orientation == ORIENTATION.DOWN) {
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
		else if(selectedShip.orientation == ORIENTATION.LEFT) {
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
	private void checkPosSeaplane(int x, int y, int [][] definedCells){
		
		cellsToPaint = BattleshipConfiguration.createEmptyGrid();
		
		setIsValid(true);
		
		if(!selectedShip.getAvailability()) {
			setIsValid(false);
			return;
		}
		
		if(definedCells[x][y] != 0) {
			setIsValid(false);
		}
		
		if(selectedShip.orientation == ORIENTATION.TOP) {
			cellsToPaint[x][y] = selectedShip.shipSize;
			try {
				if(definedCells[x-1][y-1] != 0) setIsValid(false);
				cellsToPaint[x-1][y-1] = selectedShip.shipSize;
			}
			catch (ArrayIndexOutOfBoundsException e){
				setIsValid(false);
			}
			try {
				if(definedCells[x][y-2] != 0) setIsValid(false);
				cellsToPaint[x][y-2] = selectedShip.shipSize;
			}
			catch (ArrayIndexOutOfBoundsException e){
				setIsValid(false);
			}
		}
		else if(selectedShip.orientation == ORIENTATION.RIGHT) {
			cellsToPaint[x][y] = selectedShip.shipSize;
			try {
				if(definedCells[x+1][y-1] != 0) setIsValid(false);
				cellsToPaint[x+1][y-1] = selectedShip.shipSize;
			}
			catch (ArrayIndexOutOfBoundsException e){
				setIsValid(false);
			}
			try {
				if(definedCells[x+2][y] != 0) setIsValid(false);
				cellsToPaint[x+2][y] = selectedShip.shipSize;
			}
			catch (ArrayIndexOutOfBoundsException e){
				setIsValid(false);
			}
		}
		else if(selectedShip.orientation == ORIENTATION.DOWN) {
			cellsToPaint[x][y] = selectedShip.shipSize;
			try {
				if(definedCells[x+1][y+1] != 0) setIsValid(false);
				cellsToPaint[x+1][y+1] = selectedShip.shipSize;
			}
			catch (ArrayIndexOutOfBoundsException e){
				setIsValid(false);
			}
			try {
				if(definedCells[x][y+2] != 0) setIsValid(false);
				cellsToPaint[x][y+2] = selectedShip.shipSize;
			}
			catch (ArrayIndexOutOfBoundsException e){
				setIsValid(false);
			}
		}
		else if(selectedShip.orientation == ORIENTATION.LEFT) {
			cellsToPaint[x][y] = selectedShip.shipSize;
			try {
				if(definedCells[x-1][y+1] != 0) setIsValid(false);
				cellsToPaint[x-1][y+1] = selectedShip.shipSize;
			}
			catch (ArrayIndexOutOfBoundsException e){
				setIsValid(false);
			}
			try {
				if(definedCells[x-2][y] != 0) setIsValid(false);
				cellsToPaint[x-2][y] = selectedShip.shipSize;
			}
			catch (ArrayIndexOutOfBoundsException e){
				setIsValid(false);
			}
		}
		
		if(isValid) {
			setIsValid( checkSurroundingsSeaplane(x, y, definedCells) );
		}
				
	}
	private boolean checkSurroundingsShip(int x, int y, int[][] definedCells) {
		
		if(selectedShip.orientation == ORIENTATION.TOP) {
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
		else if(selectedShip.orientation == ORIENTATION.RIGHT) {
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
		else if(selectedShip.orientation == ORIENTATION.DOWN) {
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
		else if(selectedShip.orientation == ORIENTATION.LEFT) {
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
	private boolean checkSurroundingsSeaplane(int x, int y, int[][] definedCells) {
		
		try { if(definedCells[x+1][y] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
		try { if(definedCells[x][y+1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
		try { if(definedCells[x-1][y] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
		try { if(definedCells[x][y-1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
		
		try { if(definedCells[x+1][y+1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
		try { if(definedCells[x-1][y+1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
		try { if(definedCells[x+1][y-1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
		try { if(definedCells[x-1][y-1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
		
		if(selectedShip.orientation == ORIENTATION.TOP) {
			try { if(definedCells[x-1+1][y-1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			try { if(definedCells[x-1][y-1+1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			try { if(definedCells[x-1-1][y-1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			try { if(definedCells[x-1][y-1-1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			
			try { if(definedCells[x-1+1][y-1+1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			try { if(definedCells[x-1-1][y-1+1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			try { if(definedCells[x-1+1][y-1-1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			try { if(definedCells[x-1-1][y-1-1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			
			try { if(definedCells[x+1][y-2] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			try { if(definedCells[x][y-2+1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			try { if(definedCells[x-1][y-2] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			try { if(definedCells[x][y-2-1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			
			try { if(definedCells[x+1][y-2+1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			try { if(definedCells[x-1][y-2+1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			try { if(definedCells[x+1][y-2-1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			try { if(definedCells[x-1][y-2-1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}

		}
		else if(selectedShip.orientation == ORIENTATION.RIGHT) {
			try { if(definedCells[x+1+1][y-1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			try { if(definedCells[x+1][y-1+1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			try { if(definedCells[x+1-1][y-1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			try { if(definedCells[x+1][y-1-1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			
			try { if(definedCells[x+1+1][y-1+1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			try { if(definedCells[x+1-1][y-1+1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			try { if(definedCells[x+1+1][y-1-1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			try { if(definedCells[x+1-1][y-1-1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			
			try { if(definedCells[x+2+1][y] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			try { if(definedCells[x+2][y+1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			try { if(definedCells[x+2-1][y] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			try { if(definedCells[x+2][y-1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			
			try { if(definedCells[x+2+1][y+1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			try { if(definedCells[x+2-1][y+1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			try { if(definedCells[x+2+1][y-1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			try { if(definedCells[x+2-1][y-1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
		}
		else if(selectedShip.orientation == ORIENTATION.DOWN) {
			try { if(definedCells[x+1+1][y+1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			try { if(definedCells[x+1][y+1+1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			try { if(definedCells[x+1-1][y+1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			try { if(definedCells[x+1][y+1-1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			
			try { if(definedCells[x+1+1][y+1+1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			try { if(definedCells[x+1+1][y+1-1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			try { if(definedCells[x+1-1][y+1+1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			try { if(definedCells[x+1-1][y+1-1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			
			try { if(definedCells[x+1][y+2] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			try { if(definedCells[x][y+2+1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			try { if(definedCells[x-1][y+2] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			try { if(definedCells[x][y+2-1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			
			try { if(definedCells[x+1][y+2+1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			try { if(definedCells[x-1][y+2+1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			try { if(definedCells[x+1][y+2-1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			try { if(definedCells[x-1][y+2-1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
		}
		else if(selectedShip.orientation == ORIENTATION.LEFT) {
			try { if(definedCells[x-1+1][y+1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			try { if(definedCells[x-1][y+1+1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			try { if(definedCells[x-1-1][y+1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			try { if(definedCells[x-1][y+1-1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			
			try { if(definedCells[x-1+1][y+1+1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			try { if(definedCells[x-1-1][y+1+1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			try { if(definedCells[x-1+1][y+1-1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			try { if(definedCells[x-1-1][y+1-1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			
			try { if(definedCells[x-2+1][y] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			try { if(definedCells[x-2][y+1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			try { if(definedCells[x-2-1][y] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			try { if(definedCells[x-2][y-1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			
			try { if(definedCells[x-2+1][y+1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			try { if(definedCells[x-2-1][y+1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			try { if(definedCells[x-2+1][y-1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
			try { if(definedCells[x-2-1][y-1] != 0) return false; } catch(ArrayIndexOutOfBoundsException e) {}
		}
		
		return true;
	}



//////////////////////////////////////////////////////////////////////


// Public Functions for the attacking phase

		public void startGame() {
		phase = PHASE.ATTACK;
	}
	public void nextPlayer() {
		checkResult();
		if(result) {
			System.out.printf("Player %d wins!\n", currentPlayer);
			refreshBoard();
			return;
		}
		currentPlayer = getNextPlayer();
		refreshBoard();
		// Bot move logic: il bot attacca solo dopo che il player umano ha attaccato
		if(currentPlayer == 2 && !"Human".equals(main.rules.designPatterns.RulesFacade.player2Type)) {
			int botAttacks = 0;
			while (botAttacks < 3 && !result) {
				String move = main.bot.BotManager.getBotMove(main.rules.designPatterns.RulesFacade.player2Type);
				if(move != null && move.length() >= 2) {
					char col = move.charAt(0);
					int x = col - 'A';
					int y;
					try {
						y = Integer.parseInt(move.substring(1)) - 1;
					} catch (NumberFormatException e) {
						continue;
					}
					int size = main.battleship.BattleshipConfiguration.SQUARE_COUNT;
					if (x >= 0 && x < size && y >= 0 && y < size) {
						attack(y, x);
						checkResult();
						botAttacks++;
						if(result) break;
					}
				}
			}
			botHasAttacked = true;
			if (!result) {
				currentPlayer = getNextPlayer();
				// Sblocca la board per il player umano
				main.logic.attack.Attack.getAttackFrame().blockCells = false;
				main.logic.attack.AttackUtilities.getAttackUtilites().buttonDisable();
				refreshBoard();
			}

			
		} else if(currentPlayer == 1) {
			botHasAttacked = false;
			humanAttackCount = 0;
			// Sblocca la board per il player umano e aggiorna la UI
			main.logic.attack.Attack.getAttackFrame().blockCells = false;
			main.logic.attack.AttackUtilities.getAttackUtilites().buttonDisable();
			refreshBoard();
		}
	}
	public void attack(int x, int y) {
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
		if(getOppositeBoard(currentPlayer)[x][y] == SHIPS.D_WATER.getValue() || getOppositeBoard(currentPlayer)[x][y] < 0) {
			addMessage("This cell was already clicked!");
			return;
		}
		else if(getOppositeBoard(currentPlayer)[x][y] > 0 && getOppositeBoard(currentPlayer)[x][y] < SHIPS.D_WATER.getValue()) {
			addMessage(getPlayerName(currentPlayer) + " hit a " + BattleshipConfiguration.getShipNameBySize(getOppositeBoard(currentPlayer)[x][y]) + "!");
			attackShip(x, y);
		}
		else if(getOppositeBoard(currentPlayer)[x][y] == 0) {			
			addMessage(getPlayerName(currentPlayer) + " missed!");
			attackShip(x, y);
		}

		// Gestione attacchi multipli per player umano
		if (currentPlayer == 1 ||
		(currentPlayer == 2 && "Human".equals(main.rules.designPatterns.RulesFacade.player2Type)))  {
			humanAttackCount++;
			checkResult();
			if(result) {
				return;
			}
			if(humanAttackCount == 3) {
				humanAttackCount = 0;
				main.logic.attack.Attack.getAttackFrame().blockCells = true;
				main.logic.attack.AttackUtilities.getAttackUtilites().buttonEnable();
				return;
			} else {
				main.logic.attack.Attack.getAttackFrame().blockCells = false;
				main.logic.attack.AttackUtilities.getAttackUtilites().buttonDisable();
				refreshBoard();
			}
		} else {
			checkResult();
			if(result) return;
			refreshBoard();
		}
	}


	// Private Functions for the attacking phase

		private void attackShip(int x, int y) {
		int[][] currentBoard = getOppositeBoard(currentPlayer);
		int currentPlayerPoints = 0;
		
		if(currentBoard[x][y] == 0) {
			currentBoard[x][y] = SHIPS.D_WATER.getValue();
		}
		else if(currentBoard[x][y] > 0) {
			currentPlayerPoints += 1;
			currentBoard[x][y] = -currentBoard[x][y];
			
			if(currentBoard[x][y] == SHIPS.D_SEAPLANE.getValue()) {
				if(checkAndDestroySeaplane(x, y)) {
					addMessage(getPlayerName(currentPlayer) + " sinked a Seaplane !");
				}
			}
			else if(checkIfShipDestroyed(x, y)) {
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
	private boolean checkIfShipDestroyed(int x, int y) {
		
		int[][] currentBoard = getOppositeBoard(currentPlayer);
		int destroyedCellsNum = 0;
		
		int shipSize = -currentBoard[x][y];
		
		if(currentBoard[x][y] == SHIPS.D_SUBMARINE.getValue()) {
			return true;
		}

		try { 
			//LEFT-RIGHT -> Reach the left end and go to the right end
			if(currentBoard[x+1][y] < 0) {
				try {
					while(currentBoard[x][y] != 0 && currentBoard[x][y] != SHIPS.D_WATER.getValue()) {
						x--;
					}
				} catch(ArrayIndexOutOfBoundsException e) {   }

				//Reached end => sum 1 to x to get back to ship
				x += 1;

				//Beginning left to right check
				try {
					while(currentBoard[x][y] != 0 && currentBoard[x][y] != SHIPS.D_WATER.getValue()) {
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
					while(currentBoard[x][y] != 0 && currentBoard[x][y] != SHIPS.D_WATER.getValue()) {
						x--;
					}
				} catch(ArrayIndexOutOfBoundsException e) { }

				//Reached end => sum 1 to x to get back to ship
				x += 1;

				//Beginning left to right check
				try {
					while(currentBoard[x][y] != 0 && currentBoard[x][y] != SHIPS.D_WATER.getValue()) {
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
					while(currentBoard[x][y] != 0 && currentBoard[x][y] != SHIPS.D_WATER.getValue()) {
						y--;
					}
				} catch(ArrayIndexOutOfBoundsException e) { }

				//Reached end => sum 1 to y to get back to ship
				y += 1;

				//Beginning bottom to top check
				try {
					while(currentBoard[x][y] != 0 && currentBoard[x][y] != SHIPS.D_WATER.getValue()) {
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
					while(currentBoard[x][y] != 0 && currentBoard[x][y] != SHIPS.D_WATER.getValue()) {
						y--;
					}
				} catch(ArrayIndexOutOfBoundsException e) { }

				//Reached end => sum 1 to y to get back to ship
				y += 1;

				//Beginning bottom to top check
				try {
					while(currentBoard[x][y] != 0 && currentBoard[x][y] != SHIPS.D_WATER.getValue()) {
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
	private void destroyShip(int x, int y) {
		int[][] currentBoard = getOppositeBoard(currentPlayer);

		addMessage(getPlayerName(currentPlayer) + " sinked a " + BattleshipConfiguration.getShipNameBySize(getOppositeBoard(currentPlayer)[x][y]) + "!");
		
		if(currentBoard[x][y] == SHIPS.D_SUBMARINE.getValue()) {
			currentBoard[x][y] -= BattleshipConfiguration.DESTROYED_SHIP_LIMIT;
			return;
		}	

		try { 
			//LEFT-RIGHT -> Reach the left end and delete
			if(currentBoard[x+1][y] != 0 && currentBoard[x+1][y] != SHIPS.D_WATER.getValue()) {
				try {
					while(currentBoard[x][y] != 0 && currentBoard[x][y] != SHIPS.D_WATER.getValue()) {
						x--;
					}
				} catch(ArrayIndexOutOfBoundsException e) {}

				//Reached end => sum 1 to x to get back to ship
				x += 1;

				//Beginning left to right removal
				try {
					while(currentBoard[x][y] != 0 && currentBoard[x][y] != SHIPS.D_WATER.getValue()) {
						currentBoard[x][y] -= BattleshipConfiguration.DESTROYED_SHIP_LIMIT;
						x++;
					}
					return;
				} catch(ArrayIndexOutOfBoundsException e) {}
			}; 
		} catch(ArrayIndexOutOfBoundsException e) {}
		try { 
			//LEFT-RIGHT -> Reach the left end and delete
			if(currentBoard[x-1][y] != 0 && currentBoard[x-1][y] != SHIPS.D_WATER.getValue()) {
				try {
					while(currentBoard[x][y] != 0 && currentBoard[x][y] != SHIPS.D_WATER.getValue()) {
						x--;
					}
				} catch(ArrayIndexOutOfBoundsException e) {}

				//Reached end => sum 1 to x to get back to ship
				x += 1;

				//Beginning left to right removal
				try {
					while(currentBoard[x][y] != 0 && currentBoard[x][y] != SHIPS.D_WATER.getValue()) {
						currentBoard[x][y] -= BattleshipConfiguration.DESTROYED_SHIP_LIMIT;
						x++;
					}
					return;
				} catch(ArrayIndexOutOfBoundsException e) {}
			}; 
		} catch(ArrayIndexOutOfBoundsException e) {}
		try { 
			//BOTTOM-TOP -> Reach bottom end and delete
			if(currentBoard[x][y+1] != 0 && currentBoard[x][y+1] != SHIPS.D_WATER.getValue()) {
				try {
					while(currentBoard[x][y] != 0 && currentBoard[x][y] != SHIPS.D_WATER.getValue()) {
						y--;
					}
				} catch(ArrayIndexOutOfBoundsException e) {}

				//Reached end => sum 1 to y to get back to ship
				y += 1;

				//Beginning bottom to top removal
				try {
					while(currentBoard[x][y] != 0 && currentBoard[x][y] != SHIPS.D_WATER.getValue()) {
						currentBoard[x][y] -= BattleshipConfiguration.DESTROYED_SHIP_LIMIT;
						y++;
					}
					return;
				} catch(ArrayIndexOutOfBoundsException e) {}
			}; 
		} catch(ArrayIndexOutOfBoundsException e) {}	
		try { 
			//BOTTOM-TOP -> Reach bottom end and delete
			if(currentBoard[x][y-1] != 0 && currentBoard[x][y-1] != SHIPS.D_WATER.getValue()) {
				try {
					while(currentBoard[x][y] != 0 && currentBoard[x][y] != SHIPS.D_WATER.getValue()) {
						y--;
					}
				} catch(ArrayIndexOutOfBoundsException e) {}

				//Reached end => sum 1 to y to get back to ship
				y += 1;

				//Beginning bottom to top removal
				try {
					while(currentBoard[x][y] != 0 && currentBoard[x][y] != SHIPS.D_WATER.getValue()) {
						currentBoard[x][y] -= BattleshipConfiguration.DESTROYED_SHIP_LIMIT;
						y++;
					}
					return;
				} catch(ArrayIndexOutOfBoundsException e) {}
			}; 
		} catch(ArrayIndexOutOfBoundsException e) {}
	}
	private boolean checkAndDestroySeaplane(int x, int y) {
		int[][] currentBoard = getOppositeBoard(currentPlayer);
		
		try {
			if(currentBoard[x+1][y+1] == SHIPS.D_SEAPLANE.getValue()) {
				//Check if block on middle of Seaplane
				try {
					if(currentBoard[x+1][y-1] == SHIPS.D_SEAPLANE.getValue()) {
						currentBoard[x][y] -= BattleshipConfiguration.DESTROYED_SHIP_LIMIT;
						currentBoard[x+1][y+1] -= BattleshipConfiguration.DESTROYED_SHIP_LIMIT;
						currentBoard[x+1][y-1] -= BattleshipConfiguration.DESTROYED_SHIP_LIMIT;
						return true;
					}
				}catch(ArrayIndexOutOfBoundsException e) {}
				//Check if block on end of Seaplane
				try {
					if(currentBoard[x][y+2] == SHIPS.D_SEAPLANE.getValue()) {
						currentBoard[x][y] -= BattleshipConfiguration.DESTROYED_SHIP_LIMIT;
						currentBoard[x+1][y+1] -= BattleshipConfiguration.DESTROYED_SHIP_LIMIT;
						currentBoard[x][y+2] -= BattleshipConfiguration.DESTROYED_SHIP_LIMIT;
						return true;
					}
				}catch(ArrayIndexOutOfBoundsException e) {}
				try {
					if(currentBoard[x+2][y] == SHIPS.D_SEAPLANE.getValue()) {
						currentBoard[x][y] -= BattleshipConfiguration.DESTROYED_SHIP_LIMIT;
						currentBoard[x+1][y+1] -= BattleshipConfiguration.DESTROYED_SHIP_LIMIT;
						currentBoard[x+2][y] -= BattleshipConfiguration.DESTROYED_SHIP_LIMIT;
						return true;
					}
				}catch(ArrayIndexOutOfBoundsException e) {}
			}
		}catch(ArrayIndexOutOfBoundsException e) {}
		
		try {
			if(currentBoard[x+1][y-1] == SHIPS.D_SEAPLANE.getValue()) {
				//Check if block on middle of Seaplane
				try {
					if(currentBoard[x-1][y-1] == SHIPS.D_SEAPLANE.getValue()) {
						currentBoard[x][y] -= BattleshipConfiguration.DESTROYED_SHIP_LIMIT;
						currentBoard[x+1][y-1] -= BattleshipConfiguration.DESTROYED_SHIP_LIMIT;
						currentBoard[x-1][y-1] -= BattleshipConfiguration.DESTROYED_SHIP_LIMIT;
						return true;
					}
				}catch(ArrayIndexOutOfBoundsException e) {}
				//Check if the block is at the end of the Seaplane
				try {
					if(currentBoard[x+2][y] == SHIPS.D_SEAPLANE.getValue()) {
						currentBoard[x][y] -= BattleshipConfiguration.DESTROYED_SHIP_LIMIT;
						currentBoard[x+1][y-1] -= BattleshipConfiguration.DESTROYED_SHIP_LIMIT;
						currentBoard[x+2][y] -= BattleshipConfiguration.DESTROYED_SHIP_LIMIT;
						return true;
					}
				}catch(ArrayIndexOutOfBoundsException e) {}
				try {
					if(currentBoard[x][y-2] == SHIPS.D_SEAPLANE.getValue()) {
						currentBoard[x][y] -= BattleshipConfiguration.DESTROYED_SHIP_LIMIT;
						currentBoard[x+1][y-1] -= BattleshipConfiguration.DESTROYED_SHIP_LIMIT;
						currentBoard[x][y-2] -= BattleshipConfiguration.DESTROYED_SHIP_LIMIT;
						return true;
					}
				}catch(ArrayIndexOutOfBoundsException e) {}
			}
		}catch(ArrayIndexOutOfBoundsException e) {}
		try {
			if(currentBoard[x-1][y-1] == SHIPS.D_SEAPLANE.getValue()) {
				//Check if block on middle of Seaplane
				try {
					if(currentBoard[x-1][y+1] == SHIPS.D_SEAPLANE.getValue()) {
						currentBoard[x][y] -= BattleshipConfiguration.DESTROYED_SHIP_LIMIT;
						currentBoard[x-1][y-1] -= BattleshipConfiguration.DESTROYED_SHIP_LIMIT;
						currentBoard[x-1][y+1] -= BattleshipConfiguration.DESTROYED_SHIP_LIMIT;
						return true;
					}
				}catch(ArrayIndexOutOfBoundsException e) {}
				//Check if the block is at the end of the Seaplane
				try {
					if(currentBoard[x][y-2] == SHIPS.D_SEAPLANE.getValue()) {
						currentBoard[x][y] -= BattleshipConfiguration.DESTROYED_SHIP_LIMIT;
						currentBoard[x-1][y-1] -= BattleshipConfiguration.DESTROYED_SHIP_LIMIT;
						currentBoard[x][y-2] -= BattleshipConfiguration.DESTROYED_SHIP_LIMIT;
						return true;
					}
				}catch(ArrayIndexOutOfBoundsException e) {}
				try {
					if(currentBoard[x-2][y] == SHIPS.D_SEAPLANE.getValue()) {
						currentBoard[x][y] -= BattleshipConfiguration.DESTROYED_SHIP_LIMIT;
						currentBoard[x-1][y-1] -= BattleshipConfiguration.DESTROYED_SHIP_LIMIT;
						currentBoard[x-2][y] -= BattleshipConfiguration.DESTROYED_SHIP_LIMIT;
						return true;
					}
				}catch(ArrayIndexOutOfBoundsException e) {}
			}
		}catch(ArrayIndexOutOfBoundsException e) {}
		try {
			if(currentBoard[x-1][y+1] == SHIPS.D_SEAPLANE.getValue()) {
				//Check if the block is in the middle of the Seaplane
				try {
					if(currentBoard[x+1][y+1] == SHIPS.D_SEAPLANE.getValue()) {
						currentBoard[x][y] -= BattleshipConfiguration.DESTROYED_SHIP_LIMIT;
						currentBoard[x-1][y+1] -= BattleshipConfiguration.DESTROYED_SHIP_LIMIT;
						currentBoard[x+1][y+1] -= BattleshipConfiguration.DESTROYED_SHIP_LIMIT;
						return true;
					}
				}catch(ArrayIndexOutOfBoundsException e) {}
				//Check if the block is at the end of the Seaplane
				try {
					if(currentBoard[x-2][y] == SHIPS.D_SEAPLANE.getValue()) {
						currentBoard[x][y] -= BattleshipConfiguration.DESTROYED_SHIP_LIMIT;
						currentBoard[x-1][y+1] -= BattleshipConfiguration.DESTROYED_SHIP_LIMIT;
						currentBoard[x-2][y] -= BattleshipConfiguration.DESTROYED_SHIP_LIMIT;
						return true;
					}
				}catch(ArrayIndexOutOfBoundsException e) {}
				
				try {
					if(currentBoard[x][y+2] == SHIPS.D_SEAPLANE.getValue()) {
						currentBoard[x][y] -= BattleshipConfiguration.DESTROYED_SHIP_LIMIT;
						currentBoard[x-1][y+1] -= BattleshipConfiguration.DESTROYED_SHIP_LIMIT;
						currentBoard[x][y+2] -= BattleshipConfiguration.DESTROYED_SHIP_LIMIT;
						return true;
					}
				}catch(ArrayIndexOutOfBoundsException e) {}
			}
		}catch(ArrayIndexOutOfBoundsException e) {}
		
		return false;
		
	}
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
				
		if(currentPlayerPoints == 38) {
			result = true;
		}
		
		refreshBoard();
	}
	
//////////////////////////////////////

// list of messages:


	public void addMessage(String message) {
		messages.add(message);
		refreshBoard();
	}
	public void emptyMessagesList() {
		messages.clear();
	}

////////////////////////////////////////////////////////////////////

// methods "get" and "set"

	public PHASE getPhase() {
		return phase;
	}
	public void setSelectedShip(Ship ship) {
		selectedShip = ship;
    }
	public void unsetSelectedShip() {
		if(selectedShip == null) return;
    	selectedShip.unselectPreviousShip();
		selectedShip = null;
		
		cellsToPaint = BattleshipConfiguration.createEmptyGrid();
		refreshBoard();
    }
	public int getCurrentPlayer() {
		return currentPlayer;
	}
	public int getNextPlayer() {
		if(currentPlayer == 1) {
			return 2;
		}
		else {
			return 1;
		}
	}
	public void setBoard(int playerNum) {
		switch(playerNum) {
			case 1: board1 = PositioningGrid.getGrid().getFinalGrid();
			case 2: board2 = PositioningGrid.getGrid().getFinalGrid();
		}
	}
	public String getPlayerName(int playerNum) {
		switch(playerNum) {
			case 1: return player1;
			case 2: return player2;
		}
		return null;
	}
	public void setPlayerName(int playerNum, String playerName) {
		switch(playerNum) {
		case 1: player1 = playerName;
		case 2: player2 = playerName;
		}
	}
	public Ship getSelectedShip() {
		return selectedShip;
	}
	
	private void setIsValid(boolean validation) {
		isValid = validation;
	}
	private int[][] getOppositeBoard(int playerNum) {
		switch(playerNum) {
			case 1: return board2;
			case 2: return board1;
		}
		return null;
	}

//////////////////////////////////////////////////////
	
	// Observer Functions
	@Override
	public void addObserver(Observer o) {
		for (Observer ob : lob)
			if (o == ob)
				return;
		lob.add(o);
	}

	@Override
	public void removeObserver(Observer o) {
		lob.remove(o);
	}

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

	public void refreshBoard() {
		for (Observer o : lob)
			o.notify(this);
	}

	// Place bot ships on the board for the given player
	public void placeBotShips(int playerNum, Map<String, List<String>> placements) {
		int[][] board = (playerNum == 1) ? board1 : board2;
		// Ship sizes mapping (should match BattleshipConfiguration)
		Map<String, Integer> shipSizes = new HashMap<>();
		shipSizes.put("Battleship", 5);
		shipSizes.put("Cruiser", 4);
		shipSizes.put("Destroyer", 3);
		shipSizes.put("Submarine", 2);
		shipSizes.put("Seaplane", 3);
		Map<String, Integer> shipCodes = new HashMap<>();
		shipCodes.put("Battleship", BattleshipConfiguration.SHIPS.BATTLESHIP.getValue());
		shipCodes.put("Cruiser", BattleshipConfiguration.SHIPS.CRUISER.getValue());
		shipCodes.put("Destroyer", BattleshipConfiguration.SHIPS.DESTROYER.getValue());
		shipCodes.put("Submarine", BattleshipConfiguration.SHIPS.SUBMARINE.getValue());
		shipCodes.put("Seaplane", BattleshipConfiguration.SHIPS.SEAPLANE.getValue());
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
				if (x >= 0 && x < 10 && y >= 0 && y < 10) {
					board[y][x] = code;
				}
			}
		}
		if (playerNum == 1) board1 = board;
		else board2 = board;
		refreshBoard();
	}

	// Getters for LearningBot
	public java.util.List<String> getPlayer1ShipCoords() {
		return player1ShipCoords;
	}
	public java.util.List<String> getPlayer1AttackCoords() {
		return player1AttackCoords;
	}
	public java.util.List<String> getPlayer2AttackCoords() {
		return player2AttackCoords;
	}
}
