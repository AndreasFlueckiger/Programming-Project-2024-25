package main.bot;

import java.util.*;
import main.logic.shippositioning.ShipPlacementValidator;

public class BotManager {
    private static EasyBot easyBot = new EasyBot();
    private static HardBot hardBot = new HardBot();
    private static LearningBot learningBot = new LearningBot();
    // Add HardBot when implemented

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

    public static void resetBotsForNewGame() {
        easyBot = new EasyBot();
        hardBot = new HardBot();
        learningBot.resetForNewGame();
        // Reset other bots as needed
    }

    public static void notifyHitForHardBot(String coord, boolean hit) {
        hardBot.notifyHit(coord, hit);
    }

    // Ship placement for bots (returns a map of ship type to list of coordinates)
    public static Map<String, List<String>> placeShips(String botType) {
        int maxRetries = 10;
        for (int attempt = 0; attempt < maxRetries; attempt++) {
            Map<String, List<String>> placements = placeShipsInternal(botType);
            if (placements != null && !placements.isEmpty()) {
                return placements;
            }
        }
        throw new RuntimeException("Impossibile posizionare le navi del bot dopo molti tentativi.");
    }

    private static Map<String, List<String>> placeShipsInternal(String botType) {
        int size = main.battleship.BattleshipConfiguration.SQUARE_COUNT;
        int[][] board = new int[size][size];
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
        // Miglioramento: mescola l'ordine delle navi
        List<Map.Entry<String, Integer>> shipsToPlace = new ArrayList<>(shipSizes.entrySet());
        Collections.shuffle(shipsToPlace, rand);
        // Definisci zone: centro, bordo, angoli
        List<String> zones = Arrays.asList("center", "edge", "corner");
        while (tries < maxTries) {
            tries++;
            boolean failed = false;
            board = new int[size][size];
            placements.clear();
            for (Map.Entry<String, Integer> entry : shipsToPlace) {
                String ship = entry.getKey();
                int shipLen = entry.getValue();
                boolean placed = false;
                int attempts = 0;
                // Scegli una zona random per questa nave
                List<String> shuffledZones = new ArrayList<>(zones);
                Collections.shuffle(shuffledZones, rand);
                for (String zone : shuffledZones) {
                    attempts = 0;
                    while (!placed && attempts < 60) {
                        attempts++;
                        boolean horizontal = rand.nextBoolean();
                        int row, col;
                        // Scegli coordinate in base alla zona
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
                                placements.put(ship, coordStrs);
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
                                placements.put(ship, coordStrs);
                                placed = true;
                            }
                        }
                    }
                    if (placed) break;
                }
                if (!placed) { failed = true; break; }
            }
            if (!failed && placements.size() == shipSizes.size()) {
                return placements;
            }
        }
        return new HashMap<>();
    }

    public static LearningBot getLearningBot() {
        return learningBot;
    }
}
