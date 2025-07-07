package ui.initialScreen;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

import battleship.main.battleship.*;

/**
 * The InitialFrame class defines the main window displayed when the game starts.
 * It follows the Singleton pattern to ensure only one instance is used during the game's lifecycle.
 * The frame is centered on screen, non-resizable, and displays the InitialScreen panel.
 */
@SuppressWarnings("serial")
public class InitialFrame extends JFrame {

    // Singleton instance of the InitialFrame
    static InitialFrame initialFrame;

    /**
     * Returns the single instance of InitialFrame, creating it if necessary.
     * @return InitialFrame singleton instance
     */
    public static InitialFrame getInitialFrame() {
        if (initialFrame == null)
            initialFrame = new InitialFrame();
        return initialFrame;
    }

    /**
     * Nullifies the singleton instance, allowing it to be garbage collected.
     * Can be used when switching to another frame permanently.
     */
    public void selfDestroy() {
        initialFrame = null;
    }

    /**
     * Private constructor to enforce Singleton pattern.
     * Initializes and configures the game’s startup frame.
     */
    private InitialFrame() {
        // Get screen size to center the frame
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screenSize = tk.getScreenSize();
        int sl = screenSize.width;
        int sa = screenSize.height;

        // Calculate top-left corner to center the window
        int x = sl / 2 - BattleshipConfiguration.LARG_DEFAULT / 2;
        int y = sa / 2 - BattleshipConfiguration.ALT_DEFAULT / 2;

        // Set window position and size based on BattleshipConfiguration constants
        setBounds(x, y, BattleshipConfiguration.LARG_DEFAULT, BattleshipConfiguration.ALT_DEFAULT);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false); // Disallow resizing to maintain layout integrity
        setLayout(null);     // No layout manager; manual positioning if needed

        // Set background color to light gray
        getContentPane().setBackground(new Color(250, 250, 250));

        // Add the initial menu screen to the frame
        getContentPane().add(new InitialScreen());

        // Set window title
        setTitle("Battleship");
    }
}