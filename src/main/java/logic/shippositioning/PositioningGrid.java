package main.logic.shippositioning;

import java.awt.Color;
import main.battleship.BattleshipConfiguration;
import main.logic.board.Cell;
import main.logic.board.Grid;
import main.rules.designPatterns.Observable;
import main.rules.designPatterns.Observer;
import main.rules.designPatterns.RulesFacade;

/**
 * PositioningGrid is a singleton class responsible for displaying and managing
 * ship placement on the player's grid during the setup phase of the Battleship game.
 * It visually indicates valid and invalid positions using color feedback and implements
 * the Observer pattern to react to state changes (like mouse hover on ships).
 */
public class PositioningGrid extends Grid implements Observer {

    // Singleton instance
    static PositioningGrid positioningGrid = null;

    // Stores the temporary cells being hovered or previewed
    private int[][] cellsToPaint;

    // Indicates whether the current ship placement is valid
    private boolean validation = false;

    /**
     * Returns the singleton instance of the PositioningGrid.
     */
    public static PositioningGrid getGrid() {
        if (positioningGrid == null)
            positioningGrid = new PositioningGrid();
        return positioningGrid;
    }

    /**
     * Destroys the singleton instance so it can be reinitialized.
     * Useful when resetting the game.
     */
    public void selfDestroy() {
        positioningGrid = null;
    }

    /**
     * Private constructor initializes the grid and registers this component as an observer.
     */
    private PositioningGrid() {
        super(0); // Initializes the Grid (0: player grid)
        RulesFacade.getRules().register(this); // Observe game rule changes
        cellsToPaint = BattleshipConfiguration.createEmptyGrid(); // Initialize empty preview grid
    }

    /**
     * Paints the current temporary ship preview on the grid.
     * Cells are green if placement is valid, red otherwise.
     */
    public void paintTemporaryCells() {
        if (cellsToPaint == null) return;

        Cell cell;
        for (int i = 0; i < BattleshipConfiguration.SQUARE_COUNT; i++) {
            for (int j = 0; j < BattleshipConfiguration.SQUARE_COUNT; j++) {
                if (cellsToPaint[j][i] != 0) {
                    cell = grid[j][i];
                    cell.setColor(validation ? Color.GREEN : Color.RED);
                    cell.repaint();
                }
            }
        }
    }

    /**
     * Removes the temporary preview colors and resets the cells to their original color.
     */
    public void unpaintTemporaryCells() {
        if (cellsToPaint == null) return;

        Cell cell;
        for (int i = 0; i < BattleshipConfiguration.SQUARE_COUNT; i++) {
            for (int j = 0; j < BattleshipConfiguration.SQUARE_COUNT; j++) {
                if (cellsToPaint[j][i] != 0) {
                    cell = grid[j][i];
                    cell.setColor(cell.getOriginalColor());
                    cell.repaint();
                }
            }
        }
    }

    /**
     * Completely resets the positioning grid by clearing all defined ships and colors.
     */
    public void reset() {
        Cell cell;
        for (int i = 0; i < BattleshipConfiguration.SQUARE_COUNT; i++) {
            for (int j = 0; j < BattleshipConfiguration.SQUARE_COUNT; j++) {
                cell = grid[j][i];
                cell.setShipColor(null); // Removes assigned ship reference
                cell.setColor(cell.getOriginalColor());
                cell.repaint();
                definedCellsToPaint[j][i] = 0;
            }
        }
    }

    /**
     * Returns a deep copy of the final ship layout grid, used later during gameplay.
     */
    public int[][] getFinalGrid() {
        return BattleshipConfiguration.cloneGrid(definedCellsToPaint);
    }

    /**
     * Repaints cells after a repositioning event (e.g., rotating or moving a ship),
     * clearing old positions marked with 100.
     */
    public void repositionRepaint(int[][] cellsToRemove) {
        Cell cell;
        for (int i = 0; i < BattleshipConfiguration.SQUARE_COUNT; i++) {
            for (int j = 0; j < BattleshipConfiguration.SQUARE_COUNT; j++) {
                if (cellsToRemove[j][i] == 100) {
                    definedCellsToPaint[j][i] = 0;

                    cell = grid[j][i];
                    cell.setShipColor(null);
                    cell.repaint();
                }
            }
        }
    }

    /**
     * Observer update method — called whenever the observable (Rules) changes.
     * Updates the temporary placement preview and validation state.
     *
     * @param o Observable object (Rules)
     */
    @Override
    public void notify(Observable o) {
        unpaintTemporaryCells(); // Clear previous preview

        // Get new ship preview data from observable (Rules)
        Object[] lob = (Object[]) o.get();
        cellsToPaint = (int[][]) lob[BattleshipConfiguration.objectValues.CELLS_TO_PAINT.getValue()];
        validation = (boolean) lob[BattleshipConfiguration.objectValues.IS_VALID.getValue()];

        paintTemporaryCells(); // Draw new preview
    }
}