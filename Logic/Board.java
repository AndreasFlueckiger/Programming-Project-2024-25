package Programming-Project-2024-25.Logic;

public class Board {
    private final Cell[][] cells;

    public Board() {
        cells = new Cell[10][10];
        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {
                cells[r][c] = new Cell();
            }
        }
    }

    public boolean placeShip(Ship ship) {
        for (int i = 0; i < ship.getSize(); i++) {
            int row = ship.isHorizontal() ? ship.getStartRow() : ship.getStartRow() + i;
            int col = ship.isHorizontal() ? ship.getStartCol() + i : ship.getStartCol();
            if (cells[row][col].hasShip()) {
                return false;
            }
        }
        for (int i = 0; i < ship.getSize(); i++) {
            int row = ship.isHorizontal() ? ship.getStartRow() : ship.getStartRow() + i;
            int col = ship.isHorizontal() ? ship.getStartCol() + i : ship.getStartCol();
            cells[row][col].placeShip(ship);
        }
        return true;
    }

    public boolean attack(int row, int col) {
        return cells[row][col].hit();
    }

    public Cell getCell(int row, int col) {
        return cells[row][col];
    }
}
