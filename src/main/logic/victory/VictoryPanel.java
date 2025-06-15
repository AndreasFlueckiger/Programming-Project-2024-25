package main.logic.victory;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import javax.swing.Timer;

import main.logic.attack.Attack;
import main.logic.attack.AttackUtilities;
import main.logic.initialScreen.InitialFrame;
import main.logic.shipPositioning.ShipSelection;
import main.logic.shipPositioning.PositioningGrid;
import main.logic.shipPositioning.SelectionUtilities;
import main.logic.shipPositioning.ShipOptions;
import main.logic.ships.Battleship;
import main.logic.ships.Cruiser;
import main.logic.ships.Destroyer;
import main.logic.ships.Seaplane;
import main.logic.ships.Submarine;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import main.battleship.BattleshipConfiguration;
import rules.designPatterns.RulesFacade;


@SuppressWarnings("serial")
public class VictoryPanel extends JPanel{
	
    private static final float HUE_MIN = 4f/6;
    private static final float HUE_MAX = 5f/6;
    private final Timer timer;
    private float hue = HUE_MIN;
    private Color color1 = Color.white;
    private Color color2 = Color.black;
    private float delta = 0.01f;
	
	public VictoryPanel(String winner, String looser) {
		setLayout(null);
		setBounds(0,0,1024,768);
		
		JPanel containerPnl = new JPanel();
		containerPnl.setBounds(0, BattleshipConfiguration.ALT_DEFAULT/2 - 300, BattleshipConfiguration.LARG_DEFAULT, 120);
		containerPnl.setLayout(new BoxLayout(containerPnl, BoxLayout.Y_AXIS));
		containerPnl.setOpaque(false);
		
		JLabel winnerTxt = new JLabel("<html><center>Congratulations! " + winner + ", you win!</center></html>");
		JLabel looserTxt= new JLabel("<html><center>Sorry " + looser + ", you'll get it next time!</center></html>");
		
		winnerTxt.setBounds(0, 0, BattleshipConfiguration.LARG_DEFAULT, 70);
		winnerTxt.setForeground(Color.WHITE);
		winnerTxt.setFont(new Font("SansSerif", Font.BOLD, 30));
		winnerTxt.setHorizontalAlignment(SwingConstants.CENTER);
		winnerTxt.setVerticalAlignment(SwingConstants.CENTER);
		
		looserTxt.setBounds(0, 70, BattleshipConfiguration.LARG_DEFAULT, 50);
		looserTxt.setForeground(Color.WHITE);
		looserTxt.setFont(new Font("SansSerif", Font.PLAIN, 20));
		looserTxt.setHorizontalAlignment(SwingConstants.CENTER);
		looserTxt.setVerticalAlignment(SwingConstants.CENTER);
		
		containerPnl.add(winnerTxt);
		containerPnl.add(looserTxt);
	
		add(containerPnl);
		
		JButton exitBtn = new JButton();

		exitBtn.setBounds(BattleshipConfiguration.LARG_DEFAULT/2 - 160, 680, 150, 50);
		exitBtn.setBackground(new Color(223, 21, 26));
		exitBtn.setForeground(new Color(223, 21, 26).darker());
		exitBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
		exitBtn.setText("Exit");
		exitBtn.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) { 
				  System.exit(0);
			  } 
			} );
		
		JButton resetBtn = new JButton();

		resetBtn.setBounds(BattleshipConfiguration.LARG_DEFAULT/2 + 10, 680, 150, 50);
		resetBtn.setBackground(new Color(0, 203, 231));
		resetBtn.setForeground(new Color(0, 203, 231).darker());
		resetBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
		resetBtn.setText("Reset");
		resetBtn.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) {
				  RulesFacade.getRules().resetGame();
				  
				  ShipSelection.getShipSelection().selfDestroy();
				  Attack.getAttackFrame().selfDestroy();
				  AttackUtilities.getAttackUtilites().selfDestroy();
				  InitialFrame.getInitialFrame().selfDestroy();
				  PositioningGrid.getGrid().selfDestroy();
				  SelectionUtilities.getSelectionUtilites().selfDestroy();
				  ShipOptions.getShipOptions().selfDestroy();
				  RulesFacade.getRules().selfDestroy();
				  Battleship.getBattleship().selfDestroy();
				  Cruiser.getCruiser().selfDestroy();
				  Destroyer.getDestroyer().selfDestroy();
				  Seaplane.getSeaplane().selfDestroy();
				  Submarine.getSubmarine().selfDestroy();
				  
				  InitialFrame.getInitialFrame().setVisible(true);
				  Victory.getVictoryFrame("", "").setVisible(false);
			  } 
			} );
		
		add(resetBtn);
		add(exitBtn);
	
		URL url = this.getClass().getResource("fireworks_gif.gif");
		Icon myImgIcon = new ImageIcon(url);
		JLabel imageLbl = new JLabel(myImgIcon);
		imageLbl.setBounds(0,768-620,1024,500);
		add(imageLbl);
		
		
		ActionListener action = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {
            	hue += delta;
                if (hue > HUE_MAX) {
                    hue = HUE_MAX;
                    delta = -delta;
                }
                if (hue < HUE_MIN) {
                    hue = HUE_MIN;
                    delta = -delta;
                }
                color1 = Color.getHSBColor(hue, 1, 1).darker();
                color2 = Color.getHSBColor(hue, 3f/4 + delta, 3f/4 + delta).brighter();
                repaint();
            }
        };
        timer = new Timer(100, action);
        timer.start();
	}
	
	@Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        GradientPaint p = new GradientPaint(0, 0, color1, 0, getWidth(), color2);
        g2d.setPaint(p);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }
	
}
