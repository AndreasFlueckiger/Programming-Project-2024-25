package logic.ships;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import battleship.main.battleship.*;
import rules.designPatterns.RulesFacade;

/**
 * Abstract Ship class represents a visual and interactive ship component.
 * Handles drawing, selection logic, color states, rotation, and mouse interaction.
 * Concrete ship types (e.g., Battleship, Cruiser) should extend this class.
 */
@SuppressWarnings("serial")
public abstract class Ship extends JComponent implements MouseListener {

    // Each square represents a segment of the ship
    protected Rectangle2D.Double[] squares;

    // Colors for fill and border
    private Color shipColor;
    private Color shipBorderColor;

    // Offset constants (possibly unused but could assist with grid alignment)
    protected static final int OFFSET_X = 70;
    protected static final int OFFSET_Y = 100;

    // Ship attributes
    public int shipSize;
    public BattleshipConfiguration.ORIENTATION orientation = BattleshipConfiguration.ORIENTATION.RIGHT;
    private boolean available = true;

    /**
     * Initializes the ship with a number of squares and sets default color.
     * Also attaches the mouse listener.
     *
     * @param squareNumbers number of segments the ship has
     */
    public void paintSquares(int squareNumbers) {
        squares = new Rectangle2D.Double[squareNumbers];

        for (int i = 0; i < squareNumbers; i++) {
            squares[i] = new Rectangle2D.Double();
            squares[i].height = BattleshipConfiguration.SQUARE_SIZE;
            squares[i].width = BattleshipConfiguration.SQUARE_SIZE;
            squares[i].x = i * BattleshipConfiguration.SQUARE_SIZE;
            squares[i].y = 0;
        }

        shipSize = squareNumbers;

        setColor(getOriginalColor());
        setBorderColor(getOriginalColor().darker());

        addMouseListener(this);
        repaint();
    }

    /**
     * Custom rendering of the ship and its segments.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        for (Rectangle2D.Double square : squares) {
            g2d.setColor(getColor());
            g2d.fill(square);
            g2d.setColor(getBorderColor());
            g2d.draw(square);
        }
    }

    // === Color handling methods ===

    public void setColor(Color color) {
        shipColor = color;
    }

    public void setBorderColor(Color color) {
        shipBorderColor = color;
    }

    public Color getColor() {
        return shipColor;
    }

    public Color getBorderColor() {
        return shipBorderColor;
    }

    /**
     * Returns the default color based on ship size.
     */
    public Color getOriginalColor() {
        switch (squares.length) {
            case 1: return new Color(106, 221, 221); // Cyan
            case 2: return new Color(57, 170, 99);   // Green
            case 3: return new Color(235, 235, 52);  // Yellow
            case 4: return new Color(34, 95, 167);   // Dark Blue
            case 5: return new Color(253, 64, 117);  // Pink
            default: return Color.BLACK;
        }
    }

    // === Ship state and selection logic ===

    /**
     * Rotates the ship's orientation clockwise.
     */
    public void rotate() {
        orientation = orientation.next();
    }

    public boolean getAvailability() {
        return available;
    }

    /**
     * Marks the ship as available and restores original color.
     */
    public void setAvailable() {
        available = true;
        setColor(getOriginalColor());
        setBorderColor(getOriginalColor().darker());
        repaint();
    }

    /**
     * Marks the ship as unavailable and grays it out.
     */
    public void setUnavailable() {
        available = false;
        setColor(Color.GRAY);
        setBorderColor(Color.GRAY.darker());
        repaint();
    }

    /**
     * Deselects previously selected ship (if available) and resets its color and orientation.
     */
    public void unselectPreviousShip() {
        Ship selectedShip = RulesFacade.getRules().getSelectedShip();
        if (selectedShip == null || !selectedShip.available) {
            return;
        }

        selectedShip.setColor(selectedShip.getOriginalColor());
        selectedShip.setBorderColor(selectedShip.getOriginalColor().darker());
        selectedShip.repaint();
        selectedShip.orientation = BattleshipConfiguration.ORIENTATION.RIGHT;
    }

    // === Mouse Interaction ===

    /**
     * Highlights the ship on mouse hover if available.
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        Ship selectedShip = RulesFacade.getRules().getSelectedShip();
        if (selectedShip == this || !available) {
            return;
        }

        setColor(shipColor.darker());
        setColor(shipBorderColor.darker()); // Minor bug: should be setBorderColor()
        repaint();
    }

    /**
     * Handles mouse click selection and right-click rotation.
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            Ship selectedShip = RulesFacade.getRules().getSelectedShip();
            if (selectedShip == null) return;
            selectedShip.rotate();
        }

        if (!available) return;

        unselectPreviousShip();
        RulesFacade.getRules().setSelectedShip(this);

        setColor(Color.GREEN);
        setBorderColor(Color.GREEN.darker());
        repaint();
    }

    /**
     * Resets color when mouse exits ship (if not selected).
     */
    @Override
    public void mouseExited(MouseEvent e) {
        Ship selectedShip = RulesFacade.getRules().getSelectedShip();
        if (selectedShip == this || !available) {
            return;
        }

        setColor(getOriginalColor());
        setBorderColor(shipColor.darker()); // May be a bug: should be getOriginalColor().darker()
        repaint();
    }

    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
}