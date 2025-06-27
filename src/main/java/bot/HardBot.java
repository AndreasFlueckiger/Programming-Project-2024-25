package main.bot;

import java.util.*;
import main.logic.shippositioning.ShipPlacementValidator;
import main.battleship.BattleshipConfiguration;

public class HardBot {
    private Set<String> moveSet;
    private Queue<String> targetQueue;
    private String lastHit;
    private static final List<String> ALL_COORDS_15x15 = new ArrayList<>();
    static {
        for (char col = 'A'; col <= 'O'; col++) {
            for (int row = 1; row <= 15; row++) {
                ALL_COORDS_15x15.add("" + col + row);
            }
        }
    }
    private List<String> allCoords;
    private int currentIndex;

    public HardBot() {
        moveSet = new HashSet<>();
        targetQueue = new LinkedList<>();
        lastHit = null;
        allCoords = new ArrayList<>(ALL_COORDS_15x15);
        Collections.shuffle(allCoords);
        currentIndex = 0;
    }

    // HardBot: uses a basic targeting strategy. If it hits a ship, it targets adjacent cells next.
    // Call this after each attack to inform the bot if it hit a ship. If so, it will target adjacent cells.
    public void notifyHit(String coord, boolean hit) {
        if (hit) {
            lastHit = coord;
            for (String adj : getAdjacentCoords(coord)) {
                if (!moveSet.contains(adj)) {
                    targetQueue.add(adj);
                }
            }
        }
    }

    // Returns the next move for the bot. Prioritizes target queue if there was a recent hit.
    public String Move() {
        String coordinate = null;
        if (!targetQueue.isEmpty()) {
            do {
                coordinate = targetQueue.poll();
            } while (coordinate != null && moveSet.contains(coordinate));
        }
        if (coordinate == null) {
            // Pesca la prossima coordinata dalla lista mescolata
            while (currentIndex < allCoords.size() && moveSet.contains(allCoords.get(currentIndex))) {
                currentIndex++;
            }
            if (currentIndex < allCoords.size()) {
                coordinate = allCoords.get(currentIndex);
                currentIndex++;
            } else {
                return null;
            }
        }
        moveSet.add(coordinate);
        return coordinate;
    }

    // Returns a list of adjacent coordinates to the given cell (for targeting after a hit).
    private List<String> getAdjacentCoords(String coord) {
        List<String> adj = new ArrayList<>();
        int size = BattleshipConfiguration.SQUARE_COUNT;
        int[] rc = ShipPlacementValidator.convertCoordinateToIndices(coord);
        if (rc == null) return adj;
        int row = rc[0];
        int col = rc[1];
        int[] dRow = {-1, 1, 0, 0};
        int[] dCol = {0, 0, -1, 1};
        for (int i = 0; i < 4; i++) {
            int newRow = row + dRow[i];
            int newCol = col + dCol[i];
            if (newRow >= 0 && newRow < size && newCol >= 0 && newCol < size) {
                String adjCoord = ShipPlacementValidator.convertIndicesToCoordinate(newRow, newCol);
                if (adjCoord != null) {
                    adj.add(adjCoord);
                }
            }
        }
        return adj;
    }
}
