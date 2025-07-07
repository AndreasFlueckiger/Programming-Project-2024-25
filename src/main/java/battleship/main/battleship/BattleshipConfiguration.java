package battleship.main.battleship;

import java.awt.*;

/**
 * This class holds all static configuration values, enums, 
 * and helper methods used throughout the Battleship game.
 */
public class BattleshipConfiguration {

	// ─────────────────────────────────────────────
	// Constants for game cell values (used in logic/rendering)
	// ─────────────────────────────────────────────

	public static final int SHIP = 1;     // Cell contains a ship
	public static final int HIT = 2;      // Cell has been hit
	public static final int MISS = 3;     // Cell was clicked and missed
	public static final int EMPTY = 0;    // Cell is empty

	// ─────────────────────────────────────────────
	// Window and grid dimensions
	// ─────────────────────────────────────────────

	public static final int LARG_DEFAULT = 1400;   // Default window width
	public static final int ALT_DEFAULT = 950;     // Default window height

	public static final int SQUARE_SIZE = 32;      // Size of each cell in pixels
	public static final int SQUARE_COUNT = 15;     // Grid is 15x15

	public static final int LINE_COUNT = SQUARE_COUNT + 1; // Used for drawing lines
	public static final float STROKE_WIDTH = 1.0f;         // Thickness of lines on board

	public static final int LABELS_SIZE = 20;      // Font size for labels on grid
	public static final int JPANEL_BORDER = 20;    // Border space around panels

	public static final int DESTROYED_SHIP_LIMIT = 10; // Offset to distinguish destroyed ships in the grid

	// ─────────────────────────────────────────────
	// Enum defining ship types and values on the grid
	// ─────────────────────────────────────────────

	public static enum SHIPS {
		// Alive ships (positive), Destroyed ships (negative)
		BATTLESHIP(5),
		D_BATTLESHIP(-5),
		CRUISER(4),
		D_CRUISER(-4),
		DESTROYER(2),
		D_DESTROYER(-2),
		SUBMARINE(1),
		D_SUBMARINE(-1),
		// SEAPLANE(3),
		// D_SEAPLANE(-3),
		WATER(0),
		D_WATER(10);
		
		private final int value;

		// Constructor for enum
		SHIPS(final int newValue) {
			value = newValue;
		}

		// Returns the integer value associated with the ship
		public int getValue() { return value; }
	}

	// ─────────────────────────────────────────────
	// Enum for ship orientation
	// ─────────────────────────────────────────────

	public static enum ORIENTATION { 
		TOP, RIGHT, DOWN, LEFT;

		// Array of enum values to cycle through
		private static ORIENTATION[] vals = values();

		// Returns the next orientation in the cycle
		public ORIENTATION next() {
			return vals[(this.ordinal() + 1) % vals.length];
		}
	}

	// ─────────────────────────────────────────────
	// Enum for phase of the game
	// ─────────────────────────────────────────────

	public static enum PHASE {
		POSITION,  // Ship placement phase
		ATTACK     // Attack phase
	}

	// ─────────────────────────────────────────────
	// Enum used to organize data passed to observers
	// ─────────────────────────────────────────────

	public enum objectValues {
		BOARD_1(0),           // Player 1 board
		BOARD_2(1),           // Player 2 board
		CURRENT_PLAYER(2),    // Which player's turn it is
		RESULT(3),            // Has the game ended?
		MESSAGES(4),          // Game log / notifications
		IS_VALID(5),          // Is a move/placement valid?
		CELLS_TO_PAINT(6),    // Cells used to preview ship
		PLAYER_1_NAME(7),
		PLAYER_2_NAME(8);

		private final int value;

		objectValues(final int newValue) {
			value = newValue;
		}

		public int getValue() { return value; }
	}

	// ─────────────────────────────────────────────
	// Utility methods for creating, cloning, and printing grids
	// ─────────────────────────────────────────────

	// Creates a 15x15 grid filled with 0s
	public static int[][] createEmptyGrid() {
		int newGrid[][] = new int[SQUARE_COUNT][SQUARE_COUNT];
		for (int i = 0; i < SQUARE_COUNT; i++)
			for (int j = 0; j < SQUARE_COUNT; j++)
				newGrid[j][i] = 0;
		return newGrid;
	}

	// Returns a deep copy of a given 2D grid
	public static int[][] cloneGrid(int grid[][]) {
		int newGrid[][] = new int[SQUARE_COUNT][SQUARE_COUNT];
		for (int i = 0; i < SQUARE_COUNT; i++) {
			for (int j = 0; j < SQUARE_COUNT; j++) {
				newGrid[j][i] = grid[j][i];
			}
		}
		return newGrid;
	}

	// Prints a 2D grid to the console (used for debugging)
	public static void printGrid(int grid[][]) {
		for (int i = 0; i < SQUARE_COUNT; i++) {
			for (int j = 0; j < SQUARE_COUNT; j++) {
				System.out.printf("%2d ", grid[j][i]);
			}
			System.out.println();
		}
	}

	// ─────────────────────────────────────────────
	// GUI helpers for ship visuals (color and name)
	// ─────────────────────────────────────────────

	// Returns a color associated with ship size
	public static Color getShipColorBySize(int size) {
		switch (size) {
			case 1: return new Color(106, 221, 221); // Submarine - Cyan
			case 2: return new Color(57, 170, 99);   // Destroyer - Green
			case 3: return new Color(235, 235, 52);  // Seaplane - Yellow
			case 4: return new Color(34, 95, 167);   // Cruiser - Dark Blue
			case 5: return new Color(253, 64, 117);  // Battleship - Pink
		}
		return Color.BLACK; // Default fallback color
	}

	// Returns the name of the ship based on its size
	public static String getShipNameBySize(int size) {
		if(size < 0) size = -size;
		
		switch(size) {
		case 1: return "Submarine";
		case 2: return "Destroyer";
		case 3: return "Seaplane";
		case 4: return "Cruiser";
		case 5: return "Battleship";
		default: return "Unknown";
		}
	}
}
