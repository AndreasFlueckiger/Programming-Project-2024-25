package rules.designPatterns;


import battleship.main.battleship.*;
import logic.ships.Ship;
import rules.CtrlRules;

/**
 * RulesFacade is a Singleton class that acts as a facade over the CtrlRules class.
 * It provides a simplified, unified interface for accessing and controlling
 * game logic related to both positioning and attack phases.
 *
 * This class follows the *Facade Pattern*, encapsulating interaction with the core controller,
 * and the *Singleton Pattern*, ensuring only one shared instance of the game logic exists.
 */

public class RulesFacade {
    // Reference to the core controller managing game logic
    CtrlRules ctrl;
    // Singleton instance
    static RulesFacade rulesFacade=null;
    
    // Tracks the type of player 2 (e.g., Human, EasyBot, HardBot)
    public static String player2Type = "Human";
    
     /**
     * Private constructor to prevent external instantiation.
     */
    private RulesFacade() {
        ctrl=new CtrlRules();
    }

     /**
     * Returns the singleton instance of the RulesFacade.
     * Instantiates it lazily on first use.
     */
    public static RulesFacade getRules() {
        if(rulesFacade==null)
            rulesFacade=new RulesFacade();
        
        return rulesFacade;    
    }
    
    /**
     * Destroys the singleton instance (used when resetting the game).
     */
    public void selfDestroy() {
    	rulesFacade = null;
    }
    
    /**
     * Replaces the current controller with one loaded from a save file.
     */
    public void overrideCtrl(CtrlRules newCtrl) {
        if(newCtrl==null) {
        	return;
        }
           
        ctrl = newCtrl;
    }
    
     /**
     * Resets the internal game controller.
     */
	public void resetGame() {
		ctrl.resetGame();
	}

    // Positioning on the board

      public void shipRotate() {
		ctrl.shipRotate();
	}
    public void positionShip(int x, int y, int[][] definedCells) {
		ctrl.positionShip(x, y, definedCells);
	}
    public void repositionShip(int x, int y, int[][] definedCells) {
    	ctrl.repositionShip(x, y, definedCells);
    }
    public void resetGrid() {
		ctrl.resetGrid();
	}
    public void checkPos(int x, int y, int[][] definedCells) {
		ctrl.checkPos(x, y, definedCells);
	}
    
// Attacking Phase
	
        public void startGame() {
    	ctrl.startGame();
    }
    public void nextPlayer() {
    	ctrl.nextPlayer();
    }
    public void attack(int x, int y) {
		ctrl.attack(x, y);
	}

// the "Get" and "Set"

        public BattleshipConfiguration.PHASE getPhase() {
    	return ctrl.getPhase();
    }
    public void setSelectedShip(Ship ship) {
    	ctrl.setSelectedShip(ship);
    }
    public void unsetSelectedShip() {
    	ctrl.unsetSelectedShip();
    }
    public int getCurrentPlayer() {
		return ctrl.getCurrentPlayer();
	}
    public int getNextPlayer() {
		return ctrl.getNextPlayer();
	}
	public void setBoard(int player) {
		ctrl.setBoard(player);
	}
	public String getPlayerName(int playerNum) {
		return ctrl.getPlayerName(playerNum);
	}
    public void setPlayerName(int playerNumber, String playerName) {
		ctrl.setPlayerName(playerNumber, playerName);
	}
    public Ship getSelectedShip() {
    	return ctrl.getSelectedShip();
    }    

// list of Messages

    	public void addMessage(String message) {
		ctrl.addMessage(message);
	}
	public void emptyMessagesList() {
		ctrl.emptyMessagesList();
	}
	
// Observer Functions
	
    public void register(Observer o) {
        ctrl.addObserver(o);
    }
    
// Save / Load
	
    /**
     * Returns the controller (used for saving).
     */
    public CtrlRules getCtrl() {
    	return ctrl;
    }

    /**
     * Returns Player 1's attack history (for analytics).
     * @return List of attack coordinates
     */
    public java.util.List<String> getPlayer1AttackCoords() {
        return ctrl.getPlayer1AttackCoords();
    }

    /**
     * Returns Player 2's attack history.
     */
    public java.util.List<String> getPlayer2AttackCoords() {
        return ctrl.getPlayer2AttackCoords();
    }
    public java.util.List<String> getPlayer1ShipCoords() {
        return ctrl.getPlayer1ShipCoords();
    }

}
