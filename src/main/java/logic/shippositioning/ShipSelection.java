package main.logic.shippositioning;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import main.Title;
import main.battleship.BattleshipConfiguration;
import main.logic.powers.AirAttack;
import main.logic.powers.ScatterBomb;
import main.rules.designPatterns.Observable;
import main.rules.designPatterns.Observer;
import main.rules.designPatterns.RulesFacade;

// GUI class for selecting and positioning ships.
// Also handles selection and execution of power abilities (AirAttack, ScatterBomb).

@SuppressWarnings("serial")
public class ShipSelection extends JFrame implements KeyListener, Observer{
	
	// GUI title bar panel
	Title titlePanel = new Title("");
	
	// Track current player information
	private int currentPlayerNum;
	private String currentPlayerName;

	// Track power usage per player
	private boolean airUsedP1 = false;
	private boolean bombUsedP1 = false;
	private boolean airUsedP2 = false;
	private boolean bombUsedP2 = false;
	
	// Stores which power is selected ("air" or "bomb")
	private final String[] selectedPower = {""};
 
	// Singleton instance
	static ShipSelection shipSelection;
    
	// Return singleton instance
    public static ShipSelection getShipSelection() {
        if(shipSelection == null)
        	shipSelection = new ShipSelection();
        
        return shipSelection;    
        
    }
    
	// Destroy instance for garbage collection
	public void selfDestroy() {
		shipSelection = null;
	}
	
	// Constructor initializes the GUI and logic
	private ShipSelection() {
		RulesFacade.getRules().register(this);
		
		// Window size and position
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSize = tk.getScreenSize();
		int sl = screenSize.width;
		int sa = screenSize.height;
		int x = sl/2-BattleshipConfiguration.LARG_DEFAULT/2;
		int y = sa/2-BattleshipConfiguration.ALT_DEFAULT/2;
		setBounds(x,y,BattleshipConfiguration.LARG_DEFAULT,BattleshipConfiguration.ALT_DEFAULT);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		setLayout(null);
		getContentPane().setBackground(new Color(250, 250, 250));
			
		setFocusable(true);
		
		// Add GUI panels
		getContentPane().add(titlePanel);
		getContentPane().add(ShipOptions.getShipOptions());
		getContentPane().add(new ShipPlacement());
		getContentPane().add(SelectionUtilities.getSelectionUtilites());
		
		// Set window title
		int currentPlayerNum = RulesFacade.getRules().getCurrentPlayer();
		setTitle("Ship Selection - " + RulesFacade.getRules().getPlayerName(currentPlayerNum));
		
		// Skip screen if current player is a bot
		String botType = main.rules.designPatterns.RulesFacade.player2Type;
		boolean isBot = "EasyBot".equals(botType) || "HardBot".equals(botType);
		if (currentPlayerNum == 2 && isBot) {
			setVisible(false);
		}
		
		// Create and place buttons for powers
		JButton bomberButton = new JButton("Bomber plane");
		bomberButton.setBounds(650,50,140,40);
		getContentPane().add(bomberButton);

		// Register button listeners
		JButton bombdropButton = new JButton("Bomb Drop");
		bombdropButton.setBounds(650, 100, 150, 40);
		getContentPane().add(bombdropButton);

		// Handle power usage on mouse click
		bomberButton.addActionListener(e -> selectedPower[0] = "air");
		bombdropButton.addActionListener(e -> selectedPower [0] = "bomb");



		getContentPane().addMouseListener(new java.awt.event.MouseAdapter() {
    public void mouseClicked(java.awt.event.MouseEvent evt) {
        int cellSize = 40; // Adjust to match your board cell size
        int gridOffsetX = 50, gridOffsetY = 100; // Adjust to match your grid's top-left corner

        int x = (evt.getX() - gridOffsetX) / cellSize;
        int y = (evt.getY() - gridOffsetY) / cellSize;

        // Convert to coordinate format
        char col = (char) ('A' + x);
        int row = y + 1;
        String coordinate = "" + col + row;

        System.out.println("Clicked: " + coordinate);

        if (selectedPower[0].equals("air")) {
            if ((currentPlayerNum == 1 && !airUsedP1) || (currentPlayerNum == 2 && !airUsedP2)) {
                new AirAttack() {}.use(col);
                if (currentPlayerNum == 1) airUsedP1 = true;
                else airUsedP2 = true;
                selectedPower[0] = "";
            }
        } else if (selectedPower[0].equals("bomb")) {
            if ((currentPlayerNum == 1 && !bombUsedP1) || (currentPlayerNum == 2 && !bombUsedP2)) {
                new ScatterBomb() {}.use(coordinate);
                if (currentPlayerNum == 1) bombUsedP1 = true;
                else bombUsedP2 = true;
                selectedPower[0] = "";
            }
        }

        repaint(); // Refresh GUI
    }
});

		addKeyListener(this);
	}
	
	// Update GUI title
	public void setTitle(String title) {
		titlePanel.setText(title);
	}
	
	// Unused key event methods (placeholders)
	@Override
	public void keyPressed(KeyEvent k) {
		// TODO Auto-generated method stub
	}

	@Override
	public void keyReleased(KeyEvent k) {
		// TODO Auto-generated method stub
	}

	@Override
	public void keyTyped(KeyEvent k) {
		int VK_ESCAPE = 27;
		
		if((int)k.getKeyChar() == VK_ESCAPE) {			
			RulesFacade.getRules().unsetSelectedShip();
		}
		
		if(k.getKeyChar() == 'r') {
			RulesFacade.getRules().resetGrid();
		}		
	}
	
	@Override
	public void notify(Observable o) {
		// TODO Auto-generated method stub
		Object lob[] = (Object []) o.get();
		
		currentPlayerNum = (Integer) lob[BattleshipConfiguration.objectValues.CURRENT_PLAYER.getValue()];
		
		if(currentPlayerNum == 1)
			currentPlayerName = (String) lob[BattleshipConfiguration.objectValues.PLAYER_1_NAME.getValue()];
		else
			currentPlayerName = (String) lob[BattleshipConfiguration.objectValues.PLAYER_2_NAME.getValue()];
			
		setTitle("Ship Selection - " + currentPlayerName);
	}

}
