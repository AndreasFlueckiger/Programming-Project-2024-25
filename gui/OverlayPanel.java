package Programming-Project-2024-25.gui;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class OverlayPanel extends JPanel {
    private final List<int[]> highlights;

    public OverlayPanel(List<int[]> highlights) {
        this.highlights = highlights;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(new Color(255, 255, 0, 120));
        for (int[] coord : highlights) {
            int x = coord[1] * 40;
            int y = coord[0] * 40;
            g.fillRect(x, y, 40, 40);
        }
    }
}
