package main.bot;

// Difficulty: represents the available bot difficulty levels and provides a helper for parsing from string.
public enum Difficulty {
    EASY,
    HARD;

    // Converts a string (e.g., "EasyBot") to the corresponding Difficulty enum value.
    public static Difficulty fromString(String name) {
        switch (name.toUpperCase()) {
            case "EASYBOT":
                return EASY;
            case "HARDBOT":
                return HARD;
            default:
                return EASY;
        }
    }
}
