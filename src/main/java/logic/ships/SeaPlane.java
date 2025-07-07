package logic.ships;

import java.awt.geom.Rectangle2D;

import battleship.main.battleship.*;

/**
 * Class representing the Seaplane ship.
 * The Seaplane has a T-shape composed of 3 cells:
 *  - A central cell (the "node" of the T)
 *  - A cell extending above (or below, or right, or left, depending on orientation)
 *  - A cell extending to the left or right (or above/below, depending on orientation)
 * The default shape (RIGHT orientation) is:
 *    [ ]
 *  [ ] [ ]
 * where the central cell is at the top center, the second at the top right, the third at the bottom center.
 * The actual placement and validation logic for the T-shape is handled in CtrlRules.checkPosSeaplane().
 */
@SuppressWarnings("serial")
public class SeaPlane extends Ship {

	// Maximum dimensions of the Seaplane bounding box (for drag & drop)
	private final int SEAPLANE_SIZE_X = 3; 
	private final int SEAPLANE_SIZE_Y = 2;
	
	private static final int SEAPLANE_POSITION = 1;
	
	private static SeaPlane seaplane;
	
	/**
	 * Returns the singleton instance of the Seaplane for the selection panel.
	 */
	public static SeaPlane getSeaplane() {
		if(seaplane == null) {
			seaplane = new SeaPlane(OFFSET_X, OFFSET_Y*SEAPLANE_POSITION-25);
		}
		return seaplane;
	}
	
	/**
	 * Destroys the singleton instance (used for reset).
	 */
	public void selfDestroy() {
		seaplane = null;
	}

	/**
	 * Constructor: sets the bounding box and draws the T-shape with 3 cells.
	 */
	public SeaPlane(int x, int y) {
		setBounds(x, y, BattleshipConfiguration.SQUARE_SIZE*SEAPLANE_SIZE_X, BattleshipConfiguration.SQUARE_SIZE*SEAPLANE_SIZE_Y);
		setOpaque(false);
		paintSquares(3);
	}
	
	/**
	 * Draws the T-shape of the Seaplane:
	 *  - squares[0]: central cell (top center)
	 *  - squares[1]: cell to the right (top right)
	 *  - squares[2]: cell below (bottom center)
	 * The actual ship rotation and placement logic is handled in CtrlRules.checkPosSeaplane().
	 */
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