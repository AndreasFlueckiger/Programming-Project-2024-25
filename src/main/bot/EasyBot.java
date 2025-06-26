package main.bot;

import java.util.HashSet;
import java.util.Set;

// The EasyBot generates random valid moves and avoids repeating them
public class EasyBot {
    private Set<String> moveSet;
    
    public EasyBot() {
        moveSet = new HashSet<>();
    }

    public String Move() {
        String coordinate;
        do {
            int randomRow = (int) (Math.random() * 10) + 1; // 1-10
            char randomColumn;
            do {
                int colIndex = (int) (Math.random() * 10); // 0-9
                randomColumn = (char) ('A' + colIndex);
            } while (randomColumn == 'J' || randomColumn == 'K'); // skip J and K
            coordinate = randomColumn + Integer.toString(randomRow);
        } while (moveSet.contains(coordinate));
        moveSet.add(coordinate);
        return coordinate;
    }
}
