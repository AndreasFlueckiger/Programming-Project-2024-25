package main.rules;

import java.io.Serializable;
import java.util.*;

import main.battleship.*;
import main.logic.ships.*;
import main.rules.designPatterns.*;
import main.rules.designPatterns.Observable;
import main.logic.shippositioning.*;


public class CtrlRules implements Observable, Serializable{
 
    
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

// Public functions to position the ships


/* 
 * 
 * the rest of the methods!!!!!!!
 * 
 * Private FUnctions for the Attack Phase:
*/


//Observer Functions:
    
	@Override
	public void addObserver(Observer o) {
		for(Observer ob:lob)
			if(o == ob)
				return;
				
		lob.add(o);
	}
	@Override
	public void removeObserver(Observer o) {
		lob.remove(o);
	}
	@Override
	public Object get() {
				
		Object dice[] = new Object[ battleshipconfiguration.objectValues.values().length ];
		
		dice[ BattleshipConfiguration.objectValues.BOARD_1.getValue() ] 			= board1;
		dice[ BattleshipConfiguration.objectValues.BOARD_2.getValue() ] 			= board2;
		dice[ BattleshipConfiguration.objectValues.CURRENT_PLAYER.getValue() ] 	= currentPlayer;
		dice[ BattleshipConfiguration.objectValues.RESULT.getValue() ] 			= result;
		dice[ BattleshipConfiguration.objectValues.IS_VALID.getValue() ] 		= isValid;
		dice[ BattleshipConfiguration.objectValues.CELLS_TO_PAINT.getValue() ] 	= cellsToPaint;
		dice[ BattleshipConfiguration.objectValues.PLAYER_1_NAME.getValue() ] 	= player1;
		dice[ BattleshipConfiguration.objectValues.PLAYER_2_NAME.getValue() ] 	= player2;
		dice[ BattleshipConfiguration.objectValues.MESSAGES.getValue() ] 		= messages;
		
		return dice;
	}
	public void refreshBoard() {
		for(Observer o:lob)
			o.notify(this);
	}



}
