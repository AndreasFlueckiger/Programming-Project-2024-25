package main.bot;

import java.util.*;

public class BotManager {
    private static EasyBot easyBot = new EasyBot();
    private static LearningBot learningBot = new LearningBot();
    // Add HardBot when implemented

    public static String getBotMove(String botType) {
        switch (botType) {
            case "EasyBot":
                return easyBot.Move();
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
        learningBot.resetForNewGame();
        // Reset other bots as needed
    }

    // Ship placement for bots (returns a map of ship type to list of coordinates)
    public static Map<String, List<String>> placeShips(String botType) {
        switch (botType) {
            case "EasyBot":
                // TODO: Implement EasyBot ship placement
                return new HashMap<>();
            case "LearningBot":
                // For now, random placement; can be improved to pattern-based
                return randomShipPlacement();
            // case "HardBot":
            //     return hardBot.placeShips();
            default:
                return new HashMap<>();
        }
    }

    // Example random ship placement (to be improved)
    private static Map<String, List<String>> randomShipPlacement() {
        Map<String, Integer> shipSizes = new HashMap<>();
        shipSizes.put("Battleship", 5);
        shipSizes.put("Cruiser", 4);
        shipSizes.put("Destroyer", 3);
        shipSizes.put("Submarine", 2);
        shipSizes.put("Seaplane", 3);
        Map<String, List<String>> placements = new HashMap<>();
        Random rand = new Random();
        for (Map.Entry<String, Integer> entry : shipSizes.entrySet()) {
            List<String> coords = new ArrayList<>();
            int x = rand.nextInt(10);
            int y = rand.nextInt(10);
            for (int i = 0; i < entry.getValue(); i++) {
                coords.add((char)('A' + ((x + i) % 10)) + Integer.toString((y % 10) + 1));
            }
            placements.put(entry.getKey(), coords);
        }
        return placements;
    }

    public static LearningBot getLearningBot() {
        return learningBot;
    }
}
