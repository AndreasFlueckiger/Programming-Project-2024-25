package logic.shippositioning;

import logic.board.Board;

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
