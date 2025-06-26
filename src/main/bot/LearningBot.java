package main.bot;

import java.io.*;
import java.util.*;

public class LearningBot implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String DATA_FILE = "learning_bot_data.ser";

    // Track heatmaps for player ship placements and attack patterns
    private int[][] shipPlacementHeatmap = new int[10][10];
    private int[][] attackPatternHeatmap = new int[10][10];
    private Set<String> movesMade = new HashSet<>();

    public LearningBot() {
        loadLearningData();
    }

    // Suggest a move based on learned player patterns
    public String suggestMove() {
        int maxScore = Integer.MIN_VALUE;
        int bestX = -1, bestY = -1;
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                String move = toCoordinate(x, y);
                if (!movesMade.contains(move)) {
                    int score = shipPlacementHeatmap[x][y] + attackPatternHeatmap[x][y];
                    if (score > maxScore) {
                        maxScore = score;
                        bestX = x;
                        bestY = y;
                    }
                }
            }
        }
        if (bestX == -1 || bestY == -1) {
            // fallback to random
            return randomMove();
        }
        String move = toCoordinate(bestX, bestY);
        movesMade.add(move);
        return move;
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
        Random rand = new Random();
        String move;
        do {
            int x = rand.nextInt(10);
            int y = rand.nextInt(10);
            move = toCoordinate(x, y);
        } while (movesMade.contains(move));
        movesMade.add(move);
        return move;
    }

    private String toCoordinate(int x, int y) {
        char col = (char) ('A' + x);
        int row = y + 1;
        return "" + col + row;
    }

    private int[] fromCoordinate(String coord) {
        if (coord == null || coord.length() < 2) return null;
        char col = coord.charAt(0);
        int x = col - 'A';
        int y;
        try {
            y = Integer.parseInt(coord.substring(1)) - 1;
        } catch (NumberFormatException e) {
            return null;
        }
        if (x < 0 || x >= 10 || y < 0 || y >= 10) return null;
        return new int[]{x, y};
    }
} 