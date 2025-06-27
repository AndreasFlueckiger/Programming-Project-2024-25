package main.logic.board;

import java.awt.*;
import javax.swing.*;
import main.battleship.BattleshipConfiguration;

/**
 * The Board class is a visual container that represents the static labels
 * (numbers and letters) surrounding a Battleship game grid.
 * It uses manual layout to position labels and prepares the base for the game grid.
 */
@SuppressWarnings("serial")
public class Board extends JPanel {

    // Total pixel size of the board (including labels and grid)
    public final int BOARD_SIZE = 500;

    // Default color for grid labels and strokes
    final Color mainColor = new Color(120, 120, 120);

    // Arrays to hold label containers for top (numbers) and left side (letters)
    private JPanel[] numbersContainers = new JPanel[BattleshipConfiguration.SQUARE_COUNT];
    private JPanel[] charactersContainers = new JPanel[BattleshipConfiguration.SQUARE_COUNT];

    /**
     * Constructor: Initializes the board, disables layout manager,
     * sets size, and adds axis labels (A–J, 1–10).
     */
    public Board() {
        setLayout(null);               // Manual positioning
        setBounds(0, 0, BOARD_SIZE, BOARD_SIZE);
        setOpaque(false);              // Transparent background
        addLabels();                   // Adds number and letter labels
    }

    /**
     * Paints the static board background.
     * Currently sets stroke and color, but actual grid lines are likely drawn elsewhere.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setPaint(mainColor);

        // Sets stroke style — used if grid lines or frames are painted here
        g2d.setStroke(new BasicStroke(
            BattleshipConfiguration.STROKE_WIDTH,
            BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER,
            10.0f
        ));
    }

    /**
     * Adds the row and column labels around the board.
     * Top: Numbers 1–10; Left: Letters A–J.
     */
    public void addLabels() {
        int labelSpace = BattleshipConfiguration.JPANEL_BORDER;

        for (int i = 0; i < BattleshipConfiguration.SQUARE_COUNT; i++) {
            if (i > 0) labelSpace += BattleshipConfiguration.SQUARE_SIZE;

            // === Top Labels (Numbers) ===
            numbersContainers[i] = new JPanel();
            numbersContainers[i].setLayout(new GridBagLayout()); // Center-align text
            numbersContainers[i].setBounds(
                labelSpace,
                BattleshipConfiguration.JPANEL_BORDER - BattleshipConfiguration.LABELS_SIZE,
                BattleshipConfiguration.SQUARE_SIZE,
                BattleshipConfiguration.LABELS_SIZE
            );
            numbersContainers[i].setOpaque(false);

            JLabel number = new JLabel(Integer.toString(i + 1));
            number.setFont(new Font("SansSerif", Font.PLAIN, 13));
            number.setForeground(mainColor);
            numbersContainers[i].add(number);

            // === Side Labels (Letters A–J) ===
            charactersContainers[i] = new JPanel();
            charactersContainers[i].setLayout(new GridBagLayout());
            charactersContainers[i].setBounds(
                BattleshipConfiguration.JPANEL_BORDER - BattleshipConfiguration.LABELS_SIZE,
                labelSpace,
                BattleshipConfiguration.LABELS_SIZE,
                BattleshipConfiguration.SQUARE_SIZE
            );
            charactersContainers[i].setOpaque(false);

            char ch = (char) (i + 65); // Converts index to ASCII A–J
            JLabel letter = new JLabel(Character.toString(ch));
            letter.setFont(new Font("SansSerif", Font.PLAIN, 13));
            letter.setForeground(mainColor);
            charactersContainers[i].add(letter);

            // Add both labels to the board
            add(numbersContainers[i]);
            add(charactersContainers[i]);
        }
    }
}