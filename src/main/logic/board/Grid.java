package main.logic.board;

import java.awt.Color;

import javax.swing.JPanel;

import main.battleship.BattleshipConfiguration;


@SuppressWarnings("serial")
public class Grid extends JPanel{
	
	public Cell grid[][] = new Cell[BattleshipConfiguration.SQUARE_COUNT][BattleshipConfiguration.SQUARE_COUNT];
	
	protected int[][] definedCellsToPaint;
	
	private int owner;
			
	public Grid(int owner) {
		
		setLayout(null);
		setOpaque(false);
		setBounds(BattleshipConfiguration.JPANEL_BORDER, BattleshipConfiguration.JPANEL_BORDER, BattleshipConfiguration.SQUARE_SIZE * BattleshipConfiguration.SQUARE_COUNT, BattleshipConfiguration.SQUARE_SIZE * BattleshipConfiguration.SQUARE_COUNT);
		
		definedCellsToPaint = BattleshipConfiguration.createEmptyGrid();
		
		this.owner = owner;
		
		addCells();
		
	}
	
	private void addCells() {
		
		for(int i = 0; i < BattleshipConfiguration.SQUARE_COUNT; i++) {
			for(int j = 0; j < BattleshipConfiguration.SQUARE_COUNT; j++) {
				
				grid[i][j] = new Cell(i*BattleshipConfiguration.SQUARE_SIZE, j*BattleshipConfiguration.SQUARE_SIZE, owner);
				add(grid[i][j]);
				
			}
		}
	}
	
	public void paintCells(int cellsToPaint[][]) {
				
		Cell cell;
		Color shipColor;
		
		for(int i = 0; i < BattleshipConfiguration.SQUARE_COUNT; i++)
		{
			for(int j = 0; j < BattleshipConfiguration.SQUARE_COUNT; j++)
			{
				if(cellsToPaint[j][i] != 0) {
					
					
					if(cellsToPaint[j][i] < 0) {
						shipColor = Color.GRAY;
					}
					else {
						shipColor = BattleshipConfiguration.getShipColorBySize(cellsToPaint[j][i]);
					}
					definedCellsToPaint[j][i] = cellsToPaint[j][i];
					
					cell = grid[j][i];
					cell.setShipColor(shipColor);
					cell.repaint();
				}
			}
		}
	}
	
	public void repaintCells(int cellsToPaint[][]) {
				
		Cell cell;
		
		for(int i = 0; i < BattleshipConfiguration.SQUARE_COUNT; i++)
		{
			for(int j = 0; j < BattleshipConfiguration.SQUARE_COUNT; j++)
			{
				definedCellsToPaint[j][i] = cellsToPaint[j][i];
				cell = grid[j][i];
				
				if(cellsToPaint[j][i] == 0) {
					cell.setShipColor(null);	
					cell.repaint();
					continue;
				}
				
				if(cellsToPaint[j][i] == 10) {
					cell.setShipColor(Color.WHITE);
					cell.setUnclickable(true);
				}
				else if(cellsToPaint[j][i] < -BattleshipConfiguration.DESTROYED_SHIP_LIMIT) {
					cell.setShipColor(Color.RED);
				}
				else if(cellsToPaint[j][i] < 0) {
					cell.setShipColor(Color.BLACK);
					cell.setUnclickable(true);
				}
				else {
					cell.setShipColor(BattleshipConfiguration.getShipColorBySize(cellsToPaint[j][i]));
				}
				
				cell.repaint();
			}
		}
	}
	
}