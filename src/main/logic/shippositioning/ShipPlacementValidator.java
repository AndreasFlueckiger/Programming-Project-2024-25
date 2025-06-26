package main.logic.shippositioning;

import java.util.List;

public class ShipPlacementValidator {
    // Verifica che tutte le celle siano libere e che nessuna nave tocchi un'altra (nemmeno in diagonale)
    public static boolean isValidPlacement(int[][] board, List<int[]> coords) {
        int size = board.length;
        for (int[] c : coords) {
            int x = c[0], y = c[1];
            if (x < 0 || x >= size || y < 0 || y >= size) return false;
            if (board[x][y] != 0) return false;
            // Check 1 cell spacing around
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    int nx = x + dx;
                    int ny = y + dy;
                    if (nx >= 0 && nx < size && ny >= 0 && ny < size) {
                        if (board[nx][ny] != 0) {
                            if (dx != 0 || dy != 0) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    // Conversione centralizzata
    public static int[] convertCoordinateToIndices(String coord) {
        if (coord == null || coord.length() < 2) return null;
        char colChar = coord.charAt(0);
        int col = colChar - 'A';
        int row;
        try {
            row = Integer.parseInt(coord.substring(1)) - 1;
        } catch (NumberFormatException e) {
            return null;
        }
        int size = main.battleship.BattleshipConfiguration.SQUARE_COUNT;
        if (col < 0 || col >= size || row < 0 || row >= size) return null;
        return new int[]{row, col};
    }

    public static String convertIndicesToCoordinate(int row, int col) {
        int size = main.battleship.BattleshipConfiguration.SQUARE_COUNT;
        if (row < 0 || row >= size || col < 0 || col >= size) return null;
        char colChar = (char) ('A' + col);
        int rowNum = row + 1;
        return "" + colChar + rowNum;
    }
} 