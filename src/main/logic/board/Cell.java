package main.logic.board;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import main.logic.attack.Attack;
import main.logic.shippositioning.PositioningGrid;
import main.battleship.BattleshipConfiguration.PHASE;
import main.battleship.BattleshipConfiguration;
import main.rules.designPatterns.RulesFacade;
import main.logic.ships.Ship;


@SuppressWarnings("serial")
public class Cell extends JPanel implements MouseListener{
	
	private int x;
	private int y;
	
	private int owner;
	
	private Rectangle2D.Double square;
	
	private Color cellColor;
	private Color borderColor;
	private Color shipColor;
	
	private boolean unclickable = false;
		
	public Cell(int x, int y, int owner) {
			
		this.x = x;
		this.y = y;
		
		setBounds(x, y, BattleshipConfiguration.SQUARE_SIZE, BattleshipConfiguration.SQUARE_SIZE);
		setOpaque(false);
		
		cellColor = new Color(150,150,150);
		borderColor = new Color(250,250,250);
		square = new Rectangle2D.Double(0, 0, BattleshipConfiguration.SQUARE_SIZE, BattleshipConfiguration.SQUARE_SIZE);
		
		this.owner = owner;
		
		addMouseListener(this);
		
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		
		g2d.setStroke(new BasicStroke(BattleshipConfiguration.STROKE_WIDTH));
		
		g2d.setColor( cellColor );
		g2d.fill(square);
		
		g2d.setColor( borderColor );
		g2d.draw(square);
	}
	
	public Color getOriginalColor() {
		
		if(shipColor != null) {
			return shipColor;
		}
		
		return new Color(150,150,150);
	}
	
	public void setColor(Color color) {
		cellColor = color;
		repaint();
	}
	
	public void setShipColor(Color color) {
		if(color == null) {
			shipColor = null;
			cellColor = getOriginalColor();
			repaint();
			return;
		}
		cellColor = color;
		shipColor = color;
		repaint();
	}
	
	private void paintSelectedCells() {
		RulesFacade.getRules().checkPos(x/BattleshipConfiguration.SQUARE_SIZE, y/BattleshipConfiguration.SQUARE_SIZE, PositioningGrid.getGrid().getFinalGrid());
	}
	
	public void setUnclickable(boolean clickable){
		unclickable = clickable;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(SwingUtilities.isMiddleMouseButton(e)) {
			return;
		}
		if(SwingUtilities.isRightMouseButton(e)) {
			RulesFacade.getRules().shipRotate();
			paintSelectedCells();
			return;
		}
		// POWER USAGE (in qualsiasi modalità, solo in fase di ATTACK)
		if(RulesFacade.getRules().getPhase() == PHASE.ATTACK) {
			String selectedPower = null;
			boolean[][] powerUsed = null;
			try {
				selectedPower = main.logic.attack.AttackUtilities.getAttackUtilites().getSelectedPower();
				java.lang.reflect.Field f = main.logic.attack.AttackUtilities.getAttackUtilites().getClass().getDeclaredField("powerUsed");
				f.setAccessible(true);
				powerUsed = (boolean[][]) f.get(main.logic.attack.AttackUtilities.getAttackUtilites());
			} catch(Exception ex) {}
			int idx = main.logic.attack.AttackUtilities.getAttackUtilites().getCurrentPlayerIndex();
			int powerIdx = -1;
			if ("AirAttack".equals(selectedPower)) powerIdx = 0;
			else if ("ScatterBomb".equals(selectedPower)) powerIdx = 1;
			if(selectedPower != null && powerIdx != -1 && !powerUsed[idx][powerIdx]) {
				int gridX = x / BattleshipConfiguration.SQUARE_SIZE;
				int gridY = y / BattleshipConfiguration.SQUARE_SIZE;
				if(selectedPower.equals("AirAttack")) {
					for(int row = 0; row < BattleshipConfiguration.SQUARE_COUNT; row++) {
						RulesFacade.getRules().attack(row, gridX);
						Cell c = getCellAt(gridX, row);
						if(c != null) c.highlightTemporarily(new Color(255, 100, 100));
					}
					main.logic.attack.AttackUtilities.getAttackUtilites().markPowerUsed();
					System.out.println("[POWER] AirAttack used on column: " + (char)('A'+gridX));
					main.rules.designPatterns.RulesFacade.getRules().nextPlayer();
					return;
				} else if(selectedPower.equals("ScatterBomb")) {
					for(int dx = -1; dx <= 1; dx++) {
						for(int dy = -1; dy <= 1; dy++) {
							int tx = gridX + dx;
							int ty = gridY + dy;
							if(tx >= 0 && tx < BattleshipConfiguration.SQUARE_COUNT && ty >= 0 && ty < BattleshipConfiguration.SQUARE_COUNT) {
								RulesFacade.getRules().attack(ty, tx);
								Cell c = getCellAt(tx, ty);
								if(c != null) c.highlightTemporarily(new Color(255, 180, 80));
							}
						}
					}
					main.logic.attack.AttackUtilities.getAttackUtilites().markPowerUsed();
					System.out.println("[POWER] ScatterBomb used at: " + (char)('A'+gridX)+(gridY+1));
					main.rules.designPatterns.RulesFacade.getRules().nextPlayer();
					return;
				}
			}
		}
		
		if(RulesFacade.getRules().getPhase() == PHASE.POSITION) {
			Ship selectedShip = RulesFacade.getRules().getSelectedShip();
			if(selectedShip == null && cellColor.getRed() != 105) {
				RulesFacade.getRules().repositionShip(x/BattleshipConfiguration.SQUARE_SIZE, y/BattleshipConfiguration.SQUARE_SIZE, 
						PositioningGrid.getGrid().getFinalGrid());
				return;
			}
			RulesFacade.getRules().positionShip(x/BattleshipConfiguration.SQUARE_SIZE, y/BattleshipConfiguration.SQUARE_SIZE, 
				PositioningGrid.getGrid().getFinalGrid());
			return;
		}
		
		if(Attack.getAttackFrame().blockCells) {
			System.out.println("Cells blocked, click the button!");
			return;
		}
		
		if(owner == RulesFacade.getRules().getCurrentPlayer()) {
			System.out.println("Can't attack your own ships!");
			return;
		}
		
		RulesFacade.getRules().attack(x/BattleshipConfiguration.SQUARE_SIZE, y/BattleshipConfiguration.SQUARE_SIZE);
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if(unclickable) return;
		
		if(RulesFacade.getRules().getPhase() == PHASE.POSITION 
				&& RulesFacade.getRules().getSelectedShip() != null) {
			paintSelectedCells();
			return;
		}
		
		if(owner == RulesFacade.getRules().getCurrentPlayer()) {
			return;
		}
		
		setColor(getOriginalColor().darker());
		repaint();
	}
	@Override
	public void mouseExited(MouseEvent e) {	
		if(unclickable) return;
		
		if(RulesFacade.getRules().getPhase() == PHASE.POSITION) {
			PositioningGrid.getGrid().unpaintTemporaryCells();
		}
		
		setColor(getOriginalColor());
		repaint();
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}

	// Utility per ottenere la cella dalla griglia
	private Cell getCellAt(int x, int y) {
		try {
			return (Cell) getParent().getComponent(y * BattleshipConfiguration.SQUARE_COUNT + x);
		} catch(Exception e) { return null; }
	}

	// Highlight temporaneo per powers
	public void highlightTemporarily(Color color) {
		Color old = getBackground();
		setBackground(color);
		javax.swing.Timer timer = new javax.swing.Timer(1000, evt -> setBackground(old));
		timer.setRepeats(false);
		timer.start();
	}

	// Restituisce lo stato della cella come stringa
	public String getCellStateString() {
		// Se la cella è nera: nave colpita
		if (cellColor != null && cellColor.equals(Color.BLACK)) return "hit";
		// Se la cella è rossa: nave distrutta
		if (cellColor != null && cellColor.equals(Color.RED)) return "destroyed";
		// Se la cella è bianca: mancato
		if (cellColor != null && cellColor.equals(Color.WHITE)) return "miss";
		// Se la cella ha un colore nave
		if (shipColor != null) return "ship";
		return "empty";
	}

}
