package Programming-Project-2024-25.gui;

import javax.swing.*;
import java.awt.*;

public class TurnSwapScreen extends JFrame {
    public TurnSwapScreen(String playerName, Runnable onContinue) {
        setTitle("Next Turn");
        setSize(400, 200);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        JLabel message = new JLabel("Pass the turn to: " + playerName, SwingConstants.CENTER);
        JButton next = new JButton("Ready");

        next.addActionListener(e -> {
            dispose();
            onContinue.run();
        });

        add(message, BorderLayout.CENTER);
        add(next, BorderLayout.SOUTH);
        setVisible(true);
    }
}
