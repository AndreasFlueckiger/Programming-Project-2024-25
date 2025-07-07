package logic.shippositioning;

import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

import logic.ships.Battleship;
import logic.ships.Cruiser;
import logic.ships.Destroyer;
import logic.ships.Ship;
import logic.ships.Submarine;

@SuppressWarnings("serial")
public class ShipOptions extends JPanel {

	private final int LABEL_OFFSET_X = 250;
	private final int LABEL_OFFSET_Y = 100;
	
	private static final int BATTLESHIP_POSITION = 2;
	private final int CRUISER_POSITION = 3;
	private final int DESTROYER_POSITION = 4;
	private final int SUBMARINE_POSITION = 5;
	
	public int battleship_count = 1;
	public int cruiser_count = 2;
	public int destroyer_count = 3;
	public int submarine_count = 4;
	public int ship_count = battleship_count + cruiser_count + destroyer_count + submarine_count;
	
	private JLabel battleshipCount;
	private JLabel cruiserCount;
	private JLabel destroyerCount;
	private JLabel submarineCount;
	
	private Battleship battleship;
	private Cruiser cruiser;
	private Destroyer destroyer;
	private Submarine submarine;

	static ShipOptions shipOptions;
    
    public static ShipOptions getShipOptions() {
        if(shipOptions == null) {
            shipOptions = new ShipOptions();
        }
        return shipOptions;    
    }
    
    public void selfDestroy() {
    	shipOptions = null;
    }

	private ShipOptions() {
		setLayout(null);
		setBounds(0,0,1024/3,618);
		setOpaque(false);
		
		displayShipOptions();
	}

	public void displayShipOptions() {
		addBattleships();
		addCruisers();
		addDestroyers();
		addSubmarines();
		paintLabels();
	}
	
	private JLabel createLabel(int pos, int count) {
		JLabel label = new JLabel( "x" + Integer.toString(count) );
		label.setFont(new Font("SansSerif", Font.PLAIN, 13));
		label.setBounds(LABEL_OFFSET_X, LABEL_OFFSET_Y*pos+8, 30, 15);
		return label;
	}
	
	public void paintLabels() {
		battleshipCount = createLabel(BATTLESHIP_POSITION, battleship_count);
		cruiserCount = createLabel(CRUISER_POSITION, cruiser_count);
		destroyerCount = createLabel(DESTROYER_POSITION, destroyer_count);
		submarineCount = createLabel(SUBMARINE_POSITION, submarine_count);
		add(battleshipCount);
		add(cruiserCount);
		add(destroyerCount);
		add(submarineCount);
		repaint();
	}
	
	private void repaintLabels() {
		remove(battleshipCount);
		remove(cruiserCount);
		remove(destroyerCount);
		remove(submarineCount);
		battleshipCount = createLabel(BATTLESHIP_POSITION, battleship_count);
		cruiserCount = createLabel(CRUISER_POSITION, cruiser_count);
		destroyerCount = createLabel(DESTROYER_POSITION, destroyer_count);
		submarineCount = createLabel(SUBMARINE_POSITION, submarine_count);
		add(battleshipCount);
		add(cruiserCount);
		add(destroyerCount);
		add(submarineCount);
		repaint();
	}

	public void addBattleships() {
		battleship = Battleship.getBattleship();
		add(battleship);
	}
	public void addCruisers() {
		cruiser = Cruiser.getCruiser();
		add(cruiser);
	}
	public void addDestroyers() {
		destroyer = Destroyer.getDestroyer();
		add(destroyer);
	}
	public void addSubmarines() {
		submarine = Submarine.getSubmarine();
		add(submarine);
	}
	
	public void reduceShipCount(Ship ship) {
		String shipName = ship.getClass().getName();
		
		// Check if we can reduce the count for this ship type
		boolean canReduce = false;
		if(shipName.equals("main.logic.ships.Battleship")) {
			if(battleship_count > 0) {
				battleship_count--;
				canReduce = true;
				if(battleship_count == 0) {
					battleship.setUnavailable();
				}
			}
		}
		else if(shipName.equals("main.logic.ships.Cruiser")) {
			if(cruiser_count > 0) {
				cruiser_count--;
				canReduce = true;
				if(cruiser_count == 0) {
					cruiser.setUnavailable();
				}
			}
		}
		else if(shipName.equals("main.logic.ships.Destroyer")) {
			if(destroyer_count > 0) {
				destroyer_count--;
				canReduce = true;
				if(destroyer_count == 0) {
					destroyer.setUnavailable();
				}
			}
		}
		else if(shipName.equals("main.logic.ships.Submarine")) {
			if(submarine_count > 0) {
				submarine_count--;
				canReduce = true;
				if(submarine_count == 0) {
					submarine.setUnavailable();
				}
			}
		}
		
		// Only reduce total ship count if we actually reduced a specific ship count
		if(canReduce) {
			ship_count--;
			System.out.println("[DEBUG] Ship count after placement: " + ship_count);
			repaintLabels();
			if(ship_count == 0) { // Abilita Next quando tutte le 10 navi sono posizionate (1+2+3+4=10)
				System.out.println("[DEBUG] (TEMP) Calling buttonEnable() in SelectionUtilities con ship_count == 0");
				SelectionUtilities.getSelectionUtilites().buttonEnable();
			}
		}
	}
	
	public void increaseShipCount(Ship ship) {
		String shipName = ship.getClass().getName();
		boolean canIncrease = false;
		
		if(shipName.equals("main.logic.ships.Battleship")) {
			battleship_count++;
			canIncrease = true;
			if(battleship_count == 1) {
				battleship.setAvailable();
			}
		}
		else if(shipName.equals("main.logic.ships.Cruiser")) {
			cruiser_count++;
			canIncrease = true;
			if(cruiser_count == 1) {
				cruiser.setAvailable();
			}
		}
		else if(shipName.equals("main.logic.ships.Destroyer")) {
			destroyer_count++;
			canIncrease = true;
			if(destroyer_count == 1) {
				destroyer.setAvailable();
			}
		}
		else if(shipName.equals("main.logic.ships.Submarine")) {
			submarine_count++;
			canIncrease = true;
			if(submarine_count == 1) {
				submarine.setAvailable();
			}
		}
		
		if(canIncrease) {
			ship_count++;
			repaintLabels();
		}
	}
	
	
	public void resetShipCount() {
		battleship_count = 1;
		cruiser_count = 2;
		destroyer_count = 3;
		submarine_count = 4;
		ship_count = battleship_count + cruiser_count + destroyer_count + submarine_count;
		battleship.setAvailable();
		cruiser.setAvailable();
		destroyer.setAvailable();
		submarine.setAvailable();
		repaintLabels();
	}
}
