package main.logic.ships;

import java.awt.geom.Rectangle2D;

import main.battleship.BattleshipConfiguration;


@SuppressWarnings("serial")
public class SeaPlane extends Ship {

	private final int SEAPLANE_SIZE_X = 3; 
	private final int SEAPLANE_SIZE_Y = 2;
	
	private static final int SEAPLANE_POSITION = 1;
	
	private static SeaPlane seaplane;
	
	public static SeaPlane getSeaplane() {
		if(seaplane == null) {
			seaplane = new SeaPlane(OFFSET_X, OFFSET_Y*SEAPLANE_POSITION-25);
		}
		
		return seaplane;
	}
	
	
	
	public void selfDestroy() {
		seaplane = null;
	}


	private void Seaplane(int x, int y) {

		setBounds(x, y, BattleshipConfiguration.SQUARE_SIZE*SEAPLANE_SIZE_X, BattleshipConfiguration.SQUARE_SIZE*SEAPLANE_SIZE_Y);
		setOpaque(false);
		
		paintSquares(3);		
	}
	
	@Override
	public void paintSquares(int squareNumbers) {
		
		squares = new Rectangle2D.Double[squareNumbers];
		
		squares[0] = new Rectangle2D.Double(0, BattleshipConfiguration.SQUARE_SIZE, BattleshipConfiguration.SQUARE_SIZE, BattleshipConfiguration.SQUARE_SIZE);
		squares[1] = new Rectangle2D.Double(BattleshipConfiguration.SQUARE_SIZE, 0, BattleshipConfiguration.SQUARE_SIZE, BattleshipConfiguration.SQUARE_SIZE);
		squares[2] = new Rectangle2D.Double(2*BattleshipConfiguration.SQUARE_SIZE, BattleshipConfiguration.SQUARE_SIZE, BattleshipConfiguration.SQUARE_SIZE, BattleshipConfiguration.SQUARE_SIZE);
		
		shipSize = squares.length;
		
		setColor(getOriginalColor());
		setBorderColor(getOriginalColor().darker());
		
		addMouseListener(this);

		repaint();
		
	}

}
