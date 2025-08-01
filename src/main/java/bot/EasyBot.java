package bot;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

// The EasyBot generates random valid moves and avoids repeating them
// EasyBot: selects random valid moves and avoids repeating them. No advanced strategy.
public class EasyBot {
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

    public EasyBot() {
        allCoords = new ArrayList<>(ALL_COORDS_15x15);
        Collections.shuffle(allCoords);
        currentIndex = 0;
    }

    // Returns the next available move (randomized order, no repeats).
    public String Move() {
        if (currentIndex >= allCoords.size()) return null;
        String coord = allCoords.get(currentIndex);
        currentIndex++;
        return coord;
    }
}

