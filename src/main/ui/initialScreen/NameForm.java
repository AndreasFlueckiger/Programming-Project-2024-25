package main.ui.initialScreen;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;


import main.logic.shippositioning.ShipSelection;
import main.rules.designPatterns.RulesFacade;

@SuppressWarnings("serial")
public class NameForm extends JFrame{
    
    	final int LARG_DEFAULT = 350;
	final int ALT_DEFAULT = 180;
	
	private JLabel player1Lbl = new JLabel("Player 1:");
	private JTextArea player1Txt = new JTextArea("Player 1 name");
	
	private JLabel player2Lbl = new JLabel("Player 2:");
	private JTextArea player2Txt = new JTextArea("Player 2 name");
	
	private JComboBox<String> player2TypeCombo = new JComboBox<>(new String[]{"Human", "EasyBot", "HardBot", "LearningBot"});
	
	private JButton startBtn = new JButton("Start!");
	
	private JPanel labelsPnl = new JPanel();
	private JPanel textsPnl = new JPanel();
	private JPanel buttonPnl = new JPanel();
	private JPanel containerPnl = new JPanel();
	
	
	public NameForm() {
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSize = tk.getScreenSize();
		int sl = screenSize.width;
		int sa = screenSize.height;
		int x = sl/2-LARG_DEFAULT/2;
		int y = sa/2-ALT_DEFAULT/2;
		setBounds(x,y,LARG_DEFAULT,ALT_DEFAULT);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setResizable(false);
		setTitle("Players");
		
		containerPnl.setLayout(null);
		
		labelsPnl.setLayout(new BoxLayout(labelsPnl, BoxLayout.Y_AXIS));
		labelsPnl.setBounds(0,5,100,100);
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
		
		Dimension txtDimension = new Dimension(180, 25);
		
		textsPnl.setLayout(new BoxLayout(textsPnl, BoxLayout.Y_AXIS));
		textsPnl.setBounds(105,5,200,140);
		player1Txt.setForeground(Color.GRAY);
		player1Txt.setAlignmentX(LEFT_ALIGNMENT);
		player1Txt.setPreferredSize(txtDimension);
		player1Txt.setMinimumSize(txtDimension);
		player1Txt.setMaximumSize(txtDimension);
		player1Txt.addFocusListener(new FocusListener() {
		    @Override
		    public void focusGained(FocusEvent e) {
		        if (player1Txt.getText().equals("Player 1 name")) {
		        	player1Txt.setText("");
		        	player1Txt.setForeground(Color.BLACK);
		        }
		    }
		    @Override
		    public void focusLost(FocusEvent e) {
		        if (player1Txt.getText().isEmpty()) {
		        	player1Txt.setForeground(Color.GRAY);
		        	player1Txt.setText("Player 1 name");
		        }
		    }
		});
		player2Txt.setForeground(Color.GRAY);
		player2Txt.setAlignmentX(LEFT_ALIGNMENT);
		player2Txt.setPreferredSize(txtDimension);
		player2Txt.setMinimumSize(txtDimension);
		player2Txt.setMaximumSize(txtDimension);
		player2Txt.addFocusListener(new FocusListener() {
		    @Override
		    public void focusGained(FocusEvent e) {
		        if (player2Txt.getText().equals("Player 2 name")) {
		        	player2Txt.setText("");
		        	player2Txt.setForeground(Color.BLACK);
		        }
		    }
		    @Override
		    public void focusLost(FocusEvent e) {
		        if (player2Txt.getText().isEmpty()) {
		        	player2Txt.setForeground(Color.GRAY);
		        	player2Txt.setText("Player 2 name");
		        }
		    }
		});
		textsPnl.add(Box.createRigidArea(new Dimension(0, 20)));
		textsPnl.add(player1Txt);
		textsPnl.add(Box.createRigidArea(new Dimension(0, 8)));
		textsPnl.add(player2Txt);
		textsPnl.add(Box.createRigidArea(new Dimension(0, 38)));
		player2TypeCombo.setMaximumSize(new Dimension(180, 25));
		textsPnl.add(player2TypeCombo);
		textsPnl.setOpaque(false);
		containerPnl.add(textsPnl);
		
		Dimension btnDimension = new Dimension(150, 30);
		
		buttonPnl.setLayout(new GridBagLayout());
		buttonPnl.setBounds(0,60,230,70);
		startBtn.setPreferredSize(btnDimension);
		startBtn.setMinimumSize(btnDimension);
		startBtn.setMaximumSize(btnDimension);
		startBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
		startBtn.setBackground(new Color(0, 218, 60));
		startBtn.setForeground(new Color(0, 100, 10));
		startBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String player1Name = player1Txt.getText();
                String player2Name = player2Txt.getText();
                String player2Type = (String) player2TypeCombo.getSelectedItem();

                // Caso 2 giocatori umani: entrambi i nomi obbligatori
                if (player2Type.equals("Human")) {
                    if (player1Name.isEmpty() || player1Name.equals("Player 1 name") || player2Name.isEmpty() || player2Name.equals("Player 2 name")) {
                        javax.swing.JOptionPane.showMessageDialog(null, "Please enter both player names!");
                        return;
                    }
                    main.rules.designPatterns.RulesFacade.player2Type = "Human";
                    main.rules.designPatterns.RulesFacade.getRules().setPlayerName(1, player1Name);
                    main.rules.designPatterns.RulesFacade.getRules().setPlayerName(2, player2Name);
                    (main.logic.shippositioning.ShipSelection.getShipSelection()).setVisible(true);
                    (InitialFrame.getInitialFrame()).setVisible(false);
                    setVisible(false);
                }
                // Caso single player contro bot: solo il nome del player 1 obbligatorio
                else {
                    if (player1Name.isEmpty() || player1Name.equals("Player 1 name")) {
                        javax.swing.JOptionPane.showMessageDialog(null, "Please enter Player 1's name!");
                        return;
                    }
                    main.rules.designPatterns.RulesFacade.player2Type = player2Type;
                    main.rules.designPatterns.RulesFacade.getRules().setPlayerName(1, player1Name);
                    main.rules.designPatterns.RulesFacade.getRules().setPlayerName(2, player2Type); // nome automatico per il bot
                    // Mostra ShipSelection solo per il player umano
                    (main.logic.shippositioning.ShipSelection.getShipSelection()).setVisible(true);
                    (InitialFrame.getInitialFrame()).setVisible(false);
                    setVisible(false);
                    // Il resto (posizionamento bot e attacco) sarà gestito in SelectionUtilities
                }
            }
        });
		buttonPnl.add(startBtn);
		buttonPnl.setOpaque(false);
		containerPnl.add(buttonPnl);
		
		containerPnl.setBackground(new Color(250, 250, 250));
		
		getContentPane().add(containerPnl);
		
	}

}
