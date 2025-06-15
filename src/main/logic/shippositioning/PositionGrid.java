package main.logic.shippositioning;

import java.awt.Color;

import logic.board.Cell;
import logic.board.Grid;
import main.battleship.BattleshipConfiguration;
import rules.designPatterns.Observable;
import rules.designPatterns.Observer;
import rules.designPatterns.RulesFacade



public class PositionGrid extends Grid implements Observer{

    static PositioningGrid positioningGrid = null;
		
	private int[][] cellsToPaint;
	private boolean validation = false;
	
	public static PositioningGrid getGrid() {
        if(positioningGrid==null)
            positioningGrid=new PositioningGrid();
        
        return positioningGrid;
    }
	
	public void selfDestroy() {
		positioningGrid = null;
	}
	
	private PositioningGrid() {
		super(0);
		RulesFacade.getRules().register(this);
		cellsToPaint = BattleshipConfiguration.createEmptyGrid();
	}
	
	public void paintTemporaryCells() {
		
		Cell cell;
		
		if(cellsToPaint == null) return;
				
		for(int i = 0; i < BattleshipConfiguration.SQUARE_COUNT; i++)
		{
			for(int j = 0; j < BattleshipConfiguration.SQUARE_COUNT; j++)
			{
				if(cellsToPaint[j][i] != 0) {
					cell = grid[j][i];
					if(validation) {
						cell.setColor(Color.GREEN);
						cell.repaint();
					}
					else {
						cell.setColor(Color.RED);
						cell.repaint();
					}
				}
			}
		}
		
	}
	public void unpaintTemporaryCells() {
		
		Cell cell;
		
		if(cellsToPaint == null) return;
		
		for(int i = 0; i < BattleshipConfiguration.SQUARE_COUNT; i++)
		{
			for(int j = 0; j < BattleshipConfiguration.SQUARE_COUNT; j++)
			{
				if(cellsToPaint[j][i] != 0) {
					cell = grid[j][i];
					cell.setColor(cell.getOriginalColor());
					cell.repaint();
				}
			}
		}
	}
	public void reset(){
		
		Cell cell;
		
		for(int i = 0; i < BattleshipConfiguration.SQUARE_COUNT; i++)
		{
			for(int j = 0; j < BattleshipConfiguration.SQUARE_COUNT; j++)
			{
				cell = grid[j][i];
				cell.setShipColor(null);
				cell.setColor(cell.getOriginalColor());
				cell.repaint();
				
				definedCellsToPaint[j][i] = 0;
			}
		}
	}
	
	public int[][] getFinalGrid() {
		return BattleshipConfiguration.cloneGrid(definedCellsToPaint);
	}
	
	public void repositionRepaint(int [][] cellsToRemove){
		Cell cell;
		
		for(int i = 0; i < BattleshipConfiguration.SQUARE_COUNT; i++)
		{
			for(int j = 0; j < BattleshipConfiguration.SQUARE_COUNT; j++)
			{					
				if(cellsToRemove[j][i] == 100) {
					definedCellsToPaint[j][i] = 0;
					
					cell = grid[j][i];
					cell.setShipColor(null);
					cell.repaint();
				}
			}
		}
	}
	
	@Override
	public void notify(Observable o) {
		
		unpaintTemporaryCells();
		
		Object lob[] = (Object []) o.get();
		
		cellsToPaint = (int[][]) lob[BattleshipConfiguration.objectValues.CELLS_TO_PAINT.getValue()];
		validation = (boolean) lob[BattleshipConfiguration.objectValues.IS_VALID.getValue()];
			
		paintTemporaryCells();
		
	}
    
}
