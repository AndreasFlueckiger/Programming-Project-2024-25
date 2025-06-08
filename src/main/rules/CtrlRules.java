package main.rules;

import java.io.Serializable;
import java.util.*;

import main.battleship.*;
import main.logic.ships.*;
import main.rules.designPatterns.*;
import main.rules.designPatterns.Observable;
import main.logic.shippositioning.*;

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

	public void startGame() {
		if (phase == PHASE.POSITION) {
			phase = PHASE.ATTACK;
			currentPlayer = 1;
			refreshBoard();
		}
	}

	public void nextPlayer() {
		currentPlayer = (currentPlayer == 1) ? 2 : 1;
		refreshBoard();
	}

	public void attack(int x, int y) {
		int[][] targetBoard = (currentPlayer == 1) ? board2 : board1;
		isValid = false;
		result = false;

		if (targetBoard[x][y] == BattleshipConfiguration.SHIP) {
			targetBoard[x][y] = BattleshipConfiguration.HIT;
			result = true;
			addMessages("Hit!");
			if (currentPlayer == 1) pointsPlayer1++;
			else pointsPlayer2++;
		} else if (targetBoard[x][y] == BattleshipConfiguration.EMPTY) {
			targetBoard[x][y] = BattleshipConfiguration.MISS;
			result = false;
			addMessages("Miss!");
		} else {
			addMessages("Cell already attacked.");
			return;
		}

		isValid = true;
		nextPlayer();
		refreshBoard();
	}

	public void shipRotate() {
		if (selectedShip != null) {
			selectedShip.rotate();
			refreshBoard();
		}
	}

	public void positionShip(int x, int y, int[][] definedCells) {
		checkPos(x, y, definedCells);
		if (isValid && selectedShip != null) {
			selectedShip.setPosition(x, y);
			BattleshipConfiguration.placeShip(getCurrentBoard(), selectedShip, definedCells);
			unsetSelectedShip();
		}
		refreshBoard();
	}

	public void repositionShip(int x, int y, int[][] definedCells) {
		if (selectedShip != null) {
			BattleshipConfiguration.removeShip(getCurrentBoard(), selectedShip);
			positionShip(x, y, definedCells);
		}
	}

	public void resetGrid() {
		BattleshipConfiguration.clearBoard(board1);
		BattleshipConfiguration.clearBoard(board2);
		refreshBoard();
	}

	public void checkPos(int x, int y, int[][] definedCells) {
		isValid = BattleshipConfiguration.canPlaceShip(getCurrentBoard(), x, y, definedCells);
	}

	public PHASE getPhase() {
		return phase;
	}

	public int getCurrentPhase() {
		return (phase == PHASE.POSITION) ? 0 : 1;
	}

	public int getNextPlayer() {
		return (currentPlayer == 1) ? 2 : 1;
	}

	public void setBoard(int playerNum) {
		currentPlayer = playerNum;
		refreshBoard();
	}

	public String getPlayerName(int playerNum) {
		return (playerNum == 1) ? player1 : player2;
	}

	public void setPlayerName(int playerNum, String playerName) {
		if (playerNum == 1) player1 = playerName;
		else if (playerNum == 2) player2 = playerName;
		refreshBoard();
	}

	public void setSelectedShip(Ship ship) {
		this.selectedShip = ship;
	}

	public void unsetSelectedShip() {
		this.selectedShip = null;
	}

	public Ship getSelectedShip() {
		return selectedShip;
	}

	public void addMessages(String message) {
		messages.add(message);
		refreshBoard();
	}

	public void emptyMessagesList() {
		messages.clear();
		refreshBoard();
	}

	private int[][] getCurrentBoard() {
		return (currentPlayer == 1) ? board1 : board2;
	}

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
}
