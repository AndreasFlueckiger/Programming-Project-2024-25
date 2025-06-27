package main.ui.initialScreen;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import main.logic.shippositioning.ShipSelection;
import main.rules.designPatterns.RulesFacade;

/**
 * NameForm is a form-based JFrame that allows players to input names
 * and select the type of the second player (human or bot). It supports
 * both multiplayer and single-player game modes and initializes the game setup.
 */
@SuppressWarnings("serial")
public class NameForm extends JFrame {

    // Default size of the name input window
    final int LARG_DEFAULT = 350;
    final int ALT_DEFAULT = 180;

    // UI components for player input
    private JLabel player1Lbl = new JLabel("Player 1:");
    private JTextArea player1Txt = new JTextArea("Player 1 name");

    private JLabel player2Lbl = new JLabel("Player 2:");
    private JTextArea player2Txt = new JTextArea("Player 2 name");

    // Dropdown to choose between Human or Bot for Player 2
    private JComboBox<String> player2TypeCombo = new JComboBox<>(
        new String[]{"Human", "EasyBot", "HardBot", "LearningBot"}
    );

    // Start button to begin the game
    private JButton startBtn = new JButton("Start!");

    // Layout panels to structure the components
    private JPanel labelsPnl = new JPanel();
    private JPanel textsPnl = new JPanel();
    private JPanel buttonPnl = new JPanel();
    private JPanel containerPnl = new JPanel();

    /**
     * Constructs the name form, centers it on screen, and initializes all UI elements.
     */
    public NameForm() {
        // Center the window on screen
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screenSize = tk.getScreenSize();
        int sl = screenSize.width;
        int sa = screenSize.height;
        int x = sl / 2 - LARG_DEFAULT / 2;
        int y = sa / 2 - ALT_DEFAULT / 2;
        setBounds(x, y, LARG_DEFAULT, ALT_DEFAULT);

        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setResizable(false);
        setTitle("Players");

        containerPnl.setLayout(null); // Manual layout positioning

        // === Label Panel ===
        labelsPnl.setLayout(new BoxLayout(labelsPnl, BoxLayout.Y_AXIS));
        labelsPnl.setBounds(0, 5, 100, 100);
        player1Lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        player2Lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        player1Lbl.setAlignmentX(RIGHT_ALIGNMENT);
        player2Lbl.setAlignmentX(RIGHT_ALIGNMENT);
        labelsPnl.add(Box.createRigidArea(new Dimension(0, 10)));
        labelsPnl.add(player1Lbl);
        labelsPnl.add(Box.createRigidArea(new Dimension(0, 20)));
        labelsPnl.add(player2Lbl);
        labelsPnl.setOpaque(false);
        containerPnl.add(labelsPnl);

        // === Text Input Panel ===
        Dimension txtDimension = new Dimension(180, 25);
        textsPnl.setLayout(new BoxLayout(textsPnl, BoxLayout.Y_AXIS));
        textsPnl.setBounds(105, 5, 200, 140);

        // Placeholder behavior for Player 1 name input
        player1Txt.setForeground(Color.GRAY);
        player1Txt.setPreferredSize(txtDimension);
        player1Txt.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                if (player1Txt.getText().equals("Player 1 name")) {
                    player1Txt.setText("");
                    player1Txt.setForeground(Color.BLACK);
                }
            }
            public void focusLost(FocusEvent e) {
                if (player1Txt.getText().isEmpty()) {
                    player1Txt.setForeground(Color.GRAY);
                    player1Txt.setText("Player 1 name");
                }
            }
        });

        // Placeholder behavior for Player 2 name input
        player2Txt.setForeground(Color.GRAY);
        player2Txt.setPreferredSize(txtDimension);
        player2Txt.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                if (player2Txt.getText().equals("Player 2 name")) {
                    player2Txt.setText("");
                    player2Txt.setForeground(Color.BLACK);
                }
            }
            public void focusLost(FocusEvent e) {
                if (player2Txt.getText().isEmpty()) {
                    player2Txt.setForeground(Color.GRAY);
                    player2Txt.setText("Player 2 name");
                }
            }
        });

        // Add input fields and combo box to the text panel
        textsPnl.add(Box.createRigidArea(new Dimension(0, 20)));
        textsPnl.add(player1Txt);
        textsPnl.add(Box.createRigidArea(new Dimension(0, 8)));
        textsPnl.add(player2Txt);
        textsPnl.add(Box.createRigidArea(new Dimension(0, 38)));
        player2TypeCombo.setMaximumSize(new Dimension(180, 25));
        textsPnl.add(player2TypeCombo);
        textsPnl.setOpaque(false);
        containerPnl.add(textsPnl);

        // === Button Panel ===
        Dimension btnDimension = new Dimension(150, 30);
        buttonPnl.setLayout(new GridBagLayout());
        buttonPnl.setBounds(0, 60, 230, 70);
        startBtn.setPreferredSize(btnDimension);
        startBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        startBtn.setBackground(new Color(0, 218, 60));
        startBtn.setForeground(new Color(0, 100, 10));

        // === Start Button Logic ===
        startBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String player1Name = player1Txt.getText();
                String player2Name = player2Txt.getText();
                String player2Type = (String) player2TypeCombo.getSelectedItem();

                if (player2Type.equals("Human")) {
                    // Multiplayer: both names are required
                    if (player1Name.isEmpty() || player1Name.equals("Player 1 name") ||
                        player2Name.isEmpty() || player2Name.equals("Player 2 name")) {
                        JOptionPane.showMessageDialog(null, "Please enter both player names!");
                        return;
                    }

                    RulesFacade.player2Type = "Human";
                    RulesFacade.getRules().setPlayerName(1, player1Name);
                    RulesFacade.getRules().setPlayerName(2, player2Name);
                } else {
                    // Single player vs bot: only player 1 name is required
                    if (player1Name.isEmpty() || player1Name.equals("Player 1 name")) {
                        JOptionPane.showMessageDialog(null, "Please enter Player 1's name!");
                        return;
                    }

                    RulesFacade.player2Type = player2Type;
                    RulesFacade.getRules().setPlayerName(1, player1Name);
                    RulesFacade.getRules().setPlayerName(2, player2Type); // Bot gets its type as name
                }

                // Open ship selection panel and hide both this form and the initial frame
                ShipSelection.getShipSelection().setVisible(true);
                InitialFrame.getInitialFrame().setVisible(false);
                setVisible(false);

                // Note: bot positioning and battle logic are handled later in SelectionUtilities
            }
        });

        buttonPnl.add(startBtn);
        buttonPnl.setOpaque(false);
        containerPnl.add(buttonPnl);

        // Set background and add container to window
        containerPnl.setBackground(new Color(250, 250, 250));
        getContentPane().add(containerPnl);
    }
}
