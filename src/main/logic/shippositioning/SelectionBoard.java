package main.logic.shippositioning;

import main.logic.board.Board

@SuppressWarnings("serial")
public class SelectionBoard extends Board{
	
	public SelectionBoard() {
		super();
		addGrid();
	}
	
	public void addGrid() {
		add(PositioningGrid.getGrid());
	}

}
