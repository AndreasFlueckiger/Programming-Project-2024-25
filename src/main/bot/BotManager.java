package main.bot;

import java.util.*;
import main.logic.shippositioning.ShipPlacementValidator;

public class BotManager {
    private static EasyBot easyBot = new EasyBot();
    private static HardBot hardBot = new HardBot();
    private static LearningBot learningBot = new LearningBot();
    // Add HardBot when implemented

    // BotManager handles the creation, move selection, and ship placement for all bot types (Easy, Hard, Learning).
    // It also provides utility methods for resetting bots and debugging ship placement.

    // Returns the next move for the specified bot type.
    public static String getBotMove(String botType) {
        switch (botType) {
            case "EasyBot":
                return easyBot.Move();
            case "HardBot":
                return hardBot.Move();
            case "LearningBot":
                return learningBot.suggestMove();
            // case "HardBot":
            //     return hardBot.move();
            default:
                return null;
        }
    }

    // Resets all bots for a new game (clears state and learning data if needed).
    public static void resetBotsForNewGame() {
        easyBot = new EasyBot();
        hardBot = new HardBot();
        learningBot.resetForNewGame();
        // Reset other bots as needed
    }

    // Notifies the HardBot of a hit result, so it can update its targeting logic.
    public static void notifyHitForHardBot(String coord, boolean hit) {
        hardBot.notifyHit(coord, hit);
    }

    // Places ships for the specified bot type, using the same rules and counts as the human player.
    public static Map<String, List<String>> placeShips(String botType) {
        int maxRetries = 10;
        for (int attempt = 0; attempt < maxRetries; attempt++) {
            Map<String, List<String>> placements = placeShipsInternal(botType);
            if (placements != null && !placements.isEmpty()) {
                // DEBUG: Stampa la board dopo il posizionamento
                debugPrintBoard(placements);
                return placements;
            }
        }
        throw new RuntimeException("Impossibile posizionare le navi del bot dopo molti tentativi.");
    }

    // Internal helper for ship placement. Tries to place all ships randomly, respecting adjacency rules.
    private static Map<String, List<String>> placeShipsInternal(String botType) {
        int size = main.battleship.BattleshipConfiguration.SQUARE_COUNT;
        int[][] board = new int[size][size];
        // Navi e quantità come per il player umano
        Map<String, Integer> shipCounts = new LinkedHashMap<>();
        shipCounts.put("Battleship", 1);
        shipCounts.put("Cruiser", 2);
        shipCounts.put("Destroyer", 3);
        shipCounts.put("Submarine", 4);
        shipCounts.put("Seaplane", 5);
        Map<String, Integer> shipSizes = new LinkedHashMap<>();
        shipSizes.put("Battleship", 5);
        shipSizes.put("Cruiser", 4);
        shipSizes.put("Destroyer", 3);
        shipSizes.put("Submarine", 2);
        shipSizes.put("Seaplane", 3);
        Map<String, List<String>> placements = new HashMap<>();
        Random rand = new Random();
        int maxTries = 1000;
        int tries = 0;
        // Prepara la lista di tutte le navi da posizionare (con quantità)
        List<String> shipsToPlace = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : shipCounts.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                shipsToPlace.add(entry.getKey());
            }
        }
        while (tries < maxTries) {
            tries++;
            boolean failed = false;
            board = new int[size][size];
            placements.clear();
            List<String> shuffledShips = new ArrayList<>(shipsToPlace);
            Collections.shuffle(shuffledShips, rand);
            for (String ship : shuffledShips) {
                int shipLen = shipSizes.get(ship);
                boolean placed = false;
                int attempts = 0;
                List<String> zones = Arrays.asList("center", "edge", "corner");
                List<String> shuffledZones = new ArrayList<>(zones);
                Collections.shuffle(shuffledZones, rand);
                for (String zone : shuffledZones) {
                    attempts = 0;
                    while (!placed && attempts < 60) {
                        attempts++;
                        boolean horizontal = rand.nextBoolean();
                        int row, col;
                        if (zone.equals("center")) {
                            int margin = size / 4;
                            row = margin + rand.nextInt(size / 2);
                            col = margin + rand.nextInt(size / 2);
                        } else if (zone.equals("edge")) {
                            if (rand.nextBoolean()) {
                                row = (rand.nextBoolean() ? 0 : size - 1);
                                col = rand.nextInt(size);
                            } else {
                                col = (rand.nextBoolean() ? 0 : size - 1);
                                row = rand.nextInt(size);
                            }
                        } else { // corner
                            List<int[]> corners = Arrays.asList(
                                new int[]{0, 0}, new int[]{0, size - 1},
                                new int[]{size - 1, 0}, new int[]{size - 1, size - 1}
                            );
                            int[] corner = corners.get(rand.nextInt(4));
                            row = corner[0];
                            col = corner[1];
                        }
                        List<int[]> coords = new ArrayList<>();
                        boolean valid = true;
                        if (ship.equals("Seaplane")) {
                            int[][] deltas = new int[][]{
                                {0,0, 1,-1, 0,-2}, // UP
                                {0,0, 1,1, 2,0},   // RIGHT
                                {0,0, -1,1, 0,2},  // DOWN
                                {0,0, -1,-1, -2,0} // LEFT
                            };
                            int[] d = deltas[rand.nextInt(4)];
                            int[][] seaplaneCoords = new int[][]{
                                {row, col}, {row+d[2], col+d[3]}, {row+d[4], col+d[5]}
                            };
                            coords.clear();
                            for (int[] c : seaplaneCoords) coords.add(c);
                            valid = main.logic.shippositioning.ShipPlacementValidator.isValidPlacement(board, coords);
                            if (valid) {
                                for (int[] c : coords) board[c[0]][c[1]] = 1;
                                List<String> coordStrs = new ArrayList<>();
                                for (int[] c : coords) {
                                    coordStrs.add(main.logic.shippositioning.ShipPlacementValidator.convertIndicesToCoordinate(c[0], c[1]));
                                }
                                // Per distinguere le seaplane, aggiungi un id
                                String key = ship;
                                int count = 1;
                                while (placements.containsKey(key)) key = ship + (count++);
                                placements.put(key, coordStrs);
                                placed = true;
                            }
                        } else {
                            for (int i = 0; i < shipLen; i++) {
                                int ri = row + (horizontal ? 0 : i);
                                int ci = col + (horizontal ? i : 0);
                                coords.add(new int[]{ri, ci});
                            }
                            valid = main.logic.shippositioning.ShipPlacementValidator.isValidPlacement(board, coords);
                            if (valid) {
                                for (int[] c : coords) board[c[0]][c[1]] = 1;
                                List<String> coordStrs = new ArrayList<>();
                                for (int[] c : coords) {
                                    coordStrs.add(main.logic.shippositioning.ShipPlacementValidator.convertIndicesToCoordinate(c[0], c[1]));
                                }
                                // Per distinguere le navi multiple, aggiungi un id
                                String key = ship;
                                int count = 1;
                                while (placements.containsKey(key)) key = ship + (count++);
                                placements.put(key, coordStrs);
                                placed = true;
                            }
                        }
                    }
                    if (placed) break;
                }
                if (!placed) { failed = true; break; }
            }
            if (!failed && placements.size() == shipsToPlace.size()) {
                return placements;
            }
        }
        return new HashMap<>();
    }

    // Prints a debug representation of the bot's ship placement to the console.
    private static void debugPrintBoard(Map<String, List<String>> placements) {
        int size = main.battleship.BattleshipConfiguration.SQUARE_COUNT;
        char[][] debugBoard = new char[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                debugBoard[i][j] = '.';
            }
        }
        for (Map.Entry<String, List<String>> entry : placements.entrySet()) {
            char symbol = entry.getKey().charAt(0);
            for (String coord : entry.getValue()) {
                int[] rc = main.logic.shippositioning.ShipPlacementValidator.convertCoordinateToIndices(coord);
                if (rc != null) {
                    debugBoard[rc[0]][rc[1]] = symbol;
                }
            }
        }
        System.out.println("[BOT DEBUG] Ship placement:");
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.out.print(debugBoard[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    // Returns the singleton instance of the LearningBot (for learning data updates).
    public static LearningBot getLearningBot() {
        return learningBot;
    }
}
