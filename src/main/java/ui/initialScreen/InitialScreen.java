package ui.initialScreen;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import saveload.*;

/**
 * The InitialScreen class defines the game's main menu interface.
 * It provides options to start a new game, load an existing one, or exit the application.
 * This panel is placed in the main application frame and uses a combination of
 * GridBagLayout and BoxLayout for layout management.
 */
@SuppressWarnings("serial")
public class InitialScreen extends JPanel {

    // Container panel to hold buttons vertically
    private JPanel containerPnl = new JPanel();

    // Action buttons
    private JButton startBtn = new JButton("New Game");
    private JButton loadBtn = new JButton("Load Game");
    private JButton exitBtn = new JButton("Exit");

    /**
     * Constructs the InitialScreen panel and initializes the UI components.
     */
    public InitialScreen() {
        // Use GridBagLayout to center containerPnl
        setLayout(new GridBagLayout());
        setBounds(0, 0, 1024, 768);
        setBackground(new Color(250, 250, 250));  // Light gray/white background

        Dimension btnDimension = new Dimension(150, 50);  // Consistent button sizing

        // Configure container panel to stack buttons vertically
        containerPnl.setLayout(new BoxLayout(containerPnl, BoxLayout.Y_AXIS));
        containerPnl.setOpaque(false); // Transparent background to inherit parent color

        // ====== Start Button Configuration ======
        startBtn.setBackground(new Color(0, 218, 60)); // Green background
        startBtn.setForeground(new Color(0, 100, 10)); // Dark green text
        startBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        startBtn.setPreferredSize(btnDimension);
        startBtn.setMinimumSize(btnDimension);
        startBtn.setMaximumSize(btnDimension);
        startBtn.setAlignmentX(Component.CENTER_ALIGNMENT); // Center horizontally
        startBtn.setToolTipText("Start a new game.");
        startBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Open name input form to start a new game
                new ui.initialScreen.NameForm().setVisible(true);
            }
        });

        // ====== Load Button Configuration ======
        loadBtn.setBackground(new Color(0, 203, 231)); // Blue background
        loadBtn.setForeground(new Color(0, 103, 131)); // Darker blue text
        loadBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        loadBtn.setPreferredSize(btnDimension);
        loadBtn.setMinimumSize(btnDimension);
        loadBtn.setMaximumSize(btnDimension);
        loadBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loadBtn.setToolTipText("Load an archive of a saved game.");
        loadBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Invoke SaveLoadManager to load game state from disk
                SaveLoadManager.get().Load();
            }
        });

        // ====== Exit Button Configuration ======
        exitBtn.setBackground(new Color(223, 21, 26)); // Red background
        exitBtn.setForeground(new Color(100, 5, 9));   // Dark red text
        exitBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        exitBtn.setPreferredSize(btnDimension);
        exitBtn.setMinimumSize(btnDimension);
        exitBtn.setMaximumSize(btnDimension);
        exitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitBtn.setToolTipText("Leave the game :(");
        exitBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Terminate the application
                System.exit(0);
            }
        });

        // Add buttons and spacing to the container panel
        containerPnl.add(startBtn);
        containerPnl.add(Box.createRigidArea(new Dimension(0, 15))); // vertical space
        containerPnl.add(loadBtn);
        containerPnl.add(Box.createRigidArea(new Dimension(0, 15)));
        containerPnl.add(exitBtn);

        // Use GridBagConstraints to center the container panel in the main panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        add(containerPnl, gbc);
    }
}
