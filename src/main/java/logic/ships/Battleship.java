package logic.ships;

import battleship.main.battleship.*;


@SuppressWarnings("serial")
public class Battleship extends Ship {

	private static final int BATTLESHIP_SIZE = 5;
	private static final int BATTLESHIP_POSITION = 2;
	
	private static Battleship battleship;
	
	public static Battleship getBattleship() {
		if(battleship == null) {
			battleship = new Battleship(OFFSET_X, OFFSET_Y*BATTLESHIP_POSITION);
		}
		
		return battleship;
	}
	
	public void selfDestroy() {
		battleship = null;
	}

	private Battleship(int x, int y) {

		setBounds(OFFSET_X, OFFSET_Y*BATTLESHIP_POSITION, BattleshipConfiguration.SQUARE_SIZE*BATTLESHIP_SIZE, BattleshipConfiguration.SQUARE_SIZE);
		setOpaque(false);

		super.paintSquares(BATTLESHIP_SIZE);
	}

}
