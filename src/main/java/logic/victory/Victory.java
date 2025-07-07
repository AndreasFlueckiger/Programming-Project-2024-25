package logic.victory;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

import battleship.main.battleship.*;

/**
 * Victory is a JFrame that represents the victory screen
 * shown at the end of the game. It uses the Singleton pattern
 * to ensure only one instance exists at a time.
 */
@SuppressWarnings("serial")
public class Victory extends JFrame {

    // Singleton instance of the Victory frame
    static Victory victoryFrame;

    /**
     * Returns the singleton instance of the Victory screen,
     * initializing it if it hasn't been created yet.
     *
     * @param winner the name of the player who won
     * @param looser the name of the player who lost
     * @return singleton instance of the Victory screen
     */
    public static Victory getVictoryFrame(String winner, String looser) {
        if (victoryFrame == null)
            victoryFrame = new Victory(winner, looser);
        return victoryFrame;
    }
    
    public void selfDestroy() {
    	victoryFrame = null;
    }

    /**
     * Private constructor that initializes and centers the victory window,
     * sets layout and background, and adds the victory content.
     *
     * @param winner the name of the winner
     * @param looser the name of the loser
     */
    private Victory(String winner, String looser) {
        // Center the frame on the screen using screen dimensions
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screenSize = tk.getScreenSize();
        int sl = screenSize.width;
        int sa = screenSize.height;
        int x = sl / 2 - BattleshipConfiguration.LARG_DEFAULT / 2;
        int y = sa / 2 - BattleshipConfiguration.ALT_DEFAULT / 2;

        setBounds(x, y, BattleshipConfiguration.LARG_DEFAULT, BattleshipConfiguration.ALT_DEFAULT);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(null); // No layout manager; manual component positioning

        // Set the background color of the frame
        getContentPane().setBackground(new Color(250, 250, 250));

        // Add the victory panel
        getContentPane().add(new VictoryPanel(winner, looser));

        setTitle("VICTORY!");
    }
}