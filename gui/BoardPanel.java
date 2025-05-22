package Programming-Project-2024-25.gui;

import javax.swing.*;
import java.awt.*;
import java.util.function.BiConsumer;

public class BoardPanel extends JPanel {
    private static final int SIZE = 10;
    private final JButton[][] gridButtons = new JButton[SIZE][SIZE];

    public BoardPanel(String title, boolean isPlayerBoard) {
        this(title, isPlayerBoard, null);
    }

    public BoardPanel(String title, boolean isPlayerBoard, BiConsumer<Integer, Integer> clickHandler) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder(title));

        JPanel grid = new JPanel(new GridLayout(SIZE, SIZE));
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                JButton btn = new JButton();
                btn.setPreferredSize(new Dimension(40, 40));
                if (!isPlayerBoard && clickHandler != null) {
                    int finalRow = row;
                    int finalCol = col;
                    btn.addActionListener(e -> {
                        clickHandler.accept(finalRow, finalCol);
                        btn.setEnabled(false);
                    });
                }
                gridButtons[row][col] = btn;
                grid.add(btn);
            }
        }
        add(grid, BorderLayout.CENTER);
    }

    public void updateCell(int row, int col, boolean hit) {
        gridButtons[row][col].setText(hit ? "O" : "X");
        gridButtons[row][col].setEnabled(false);
    }
}

