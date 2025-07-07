package logic.ships;

import battleship.main.battleship.*;


@SuppressWarnings("serial")
public class Submarine extends Ship {

	private final int SUBMARINE_SIZE = 1;
	private static final int SUBMARINE_POSITION = 5;
	
	private static Submarine submarine;
	
	public static Submarine getSubmarine() {
		if(submarine == null) {
			submarine = new Submarine(OFFSET_X, OFFSET_Y*SUBMARINE_POSITION);
		}
		
		return submarine;
	}
	
	public void selfDestroy() {
		submarine = null;
	}


	private Submarine(int x, int y) {

		setBounds(x, y, BattleshipConfiguration.SQUARE_SIZE*SUBMARINE_SIZE, BattleshipConfiguration.SQUARE_SIZE);
		setOpaque(false);

		super.paintSquares(SUBMARINE_SIZE);
	}

}
