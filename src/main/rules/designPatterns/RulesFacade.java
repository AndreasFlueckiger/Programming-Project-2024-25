package main.rules.designPatterns;


import main.logic.ships.Ship;
import main.battleship.BattleshipConfiguration.PHASE;
import main.logic.ships.Ship;
import main.rules.CtrlRules;

public class RulesFacade {
        CtrlRules ctrl;
    static RulesFacade rulesFacade=null;
    
    public static String player2Type = "Human";
    
    private RulesFacade() {
        ctrl=new CtrlRules();
    }
    public static RulesFacade getRules() {
        if(rulesFacade==null)
            rulesFacade=new RulesFacade();
        
        return rulesFacade;    
    }
    
    public void selfDestroy() {
    	rulesFacade = null;
    }
    
    public void overrideCtrl(CtrlRules newCtrl) {
        if(newCtrl==null) {
        	return;
        }
           
        ctrl = newCtrl;
    }
    
	public void resetGame() {
		ctrl.resetGame();
	}

// Positioning in the boad

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

        public PHASE getPhase() {
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
	
    public CtrlRules getCtrl() {
    	return ctrl;
    }

}
