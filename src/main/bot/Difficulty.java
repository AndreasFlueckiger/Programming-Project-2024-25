package main.bot;

public enum Difficulty {
    EASY,
    HARD,
    LEARNING;

    public static Difficulty fromString(String name) {
        switch (name.toUpperCase()) {
            case "EASYBOT":
                return EASY;
            case "HARDBOT":
                return HARD;
            case "LEARNINGBOT":
                return LEARNING;
            default:
                return EASY;
        }
    }
}
