package main.bot;

import java.io.*;
import java.util.*;
import main.logic.shippositioning.ShipPlacementValidator;
import main.battleship.BattleshipConfiguration;

public class LearningBot implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String DATA_FILE = "learning_bot_data.ser";
    private static final List<String> ALL_COORDS_15x15 = new ArrayList<>();
    static {
        for (char col = 'A'; col <= 'O'; col++) {
            for (int row = 1; row <= 15; row++) {
                ALL_COORDS_15x15.add("" + col + row);
            }
        }
    }

    // Track heatmaps for player ship placements and attack patterns
    private int[][] shipPlacementHeatmap = new int[10][10];
    private int[][] attackPatternHeatmap = new int[10][10];
    private Set<String> movesMade = new HashSet<>();
    private List<String> allCoords;
    private int currentIndex;
    private Queue<String> targetQueue = new LinkedList<>();
    private String lastHit = null;

    public LearningBot() {
        loadLearningData();
        movesMade = new HashSet<>();
        targetQueue = new LinkedList<>();
        lastHit = null;
        allCoords = new ArrayList<>(ALL_COORDS_15x15);
        Collections.shuffle(allCoords);
        currentIndex = 0;
    }

    // Suggest a move based on learned player patterns
    public String suggestMove() {
        String coordinate = null;
        if (!targetQueue.isEmpty()) {
            do {
                coordinate = targetQueue.poll();
            } while (coordinate != null && movesMade.contains(coordinate));
        }
        if (coordinate == null) {
            // Pesca la prossima coordinata dalla lista mescolata
            while (currentIndex < allCoords.size() && movesMade.contains(allCoords.get(currentIndex))) {
                currentIndex++;
            }
            if (currentIndex < allCoords.size()) {
                coordinate = allCoords.get(currentIndex);
                currentIndex++;
            } else {
                return null;
            }
        }
        movesMade.add(coordinate);
        return coordinate;
    }

    // Update learning data after a game
    public void learnFromGame(List<String> playerShipCoords, List<String> playerAttackCoords) {
        for (String coord : playerShipCoords) {
            int[] xy = fromCoordinate(coord);
            if (xy != null) shipPlacementHeatmap[xy[0]][xy[1]]++;
        }
        for (String coord : playerAttackCoords) {
            int[] xy = fromCoordinate(coord);
            if (xy != null) attackPatternHeatmap[xy[0]][xy[1]]++;
        }
        saveLearningData();
    }

    // Reset moves for a new game
    public void resetForNewGame() {
        movesMade.clear();
    }

    // --- Persistence ---
    private void saveLearningData() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            out.writeObject(shipPlacementHeatmap);
            out.writeObject(attackPatternHeatmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadLearningData() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            shipPlacementHeatmap = (int[][]) in.readObject();
            attackPatternHeatmap = (int[][]) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            // Use default (empty) heatmaps if file not found
        }
    }

    // --- Helpers ---
    private String randomMove() {
        int size = BattleshipConfiguration.SQUARE_COUNT;
        List<String> available = new ArrayList<>();
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                String coord = ShipPlacementValidator.convertIndicesToCoordinate(row, col);
                if (coord != null && !movesMade.contains(coord)) {
                    available.add(coord);
                }
            }
        }
        if (available.isEmpty()) return null;
        return available.get((int)(Math.random() * available.size()));
    }

    private int[] fromCoordinate(String coord) {
        return ShipPlacementValidator.convertCoordinateToIndices(coord);
    }

    public void notifyHit(String coord, boolean hit) {
        if (hit) {
            lastHit = coord;
            for (String adj : getAdjacentCoords(coord)) {
                if (!movesMade.contains(adj)) {
                    targetQueue.add(adj);
                }
            }
        }
    }

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