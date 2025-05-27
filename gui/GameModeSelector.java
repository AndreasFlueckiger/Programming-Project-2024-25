


import javax.swing.*;
import java.awt.*;

public class GameModeSelector extends JFrame {
    public GameModeSelector(){
        setTitle("Battleship - Mode Selection");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4,1));


        JButton pvpClassic = new JButton("Player vs Player (Classic)");
        JButton pvpModern = new JButton("Player vs Player (Modern)");
        JButton pvbClassic = new JButton("Player vs Bot (Classic)");

              pvpClassic.addActionListener(e -> {
            dispose();
            new GameFrame(false, false, null);
        });

        pvpModern.addActionListener(e -> {
            dispose();
            new GameFrame(false, true, null);
        });

        pvbClassic.addActionListener(e -> {
            String[] options = {"Easy", "Hard"};
            String selected = (String) JOptionPane.showInputDialog(
                    this,
                    "Select Bot Difficulty:",
                    "Bot Difficulty",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]
            );
            if (selected != null) {
                Difficulty diff = selected.equals("Easy") ? Difficulty.EASY : Difficulty.HARD;
                dispose();
                new GameFrame(true, false, diff);
            }
        });

        add(pvpClassic);
        add(pvpModern);
        add(pvbClassic);

        setVisible(true);
    }
}
