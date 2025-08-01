package logic.attack;

import java.awt.Color;
import java.awt.Font;
import java.util.*;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

import battleship.main.battleship.*;
import rules.designPatterns.Observable;
import rules.designPatterns.Observer;
import rules.designPatterns.RulesFacade;

@SuppressWarnings("serial")
public class AttackUtilities extends JPanel implements Observer{

	public final int UTILITIES_HEIGHT = 150;
	private int turn = 1;
	
	JLabel message1 = new JLabel("");
	JLabel message2 = new JLabel("");
	JLabel message3 = new JLabel("");
	
	JButton nextBtn = new JButton("Next");
	JButton exitBtn = new JButton("Exit");
	JButton saveBtn = new JButton("Save");
	
	// Stato per i powers
	private String selectedPower = null; // "AirAttack", "ScatterBomb"
	// powerUsed[player][power]: 0=AirAttack, 1=ScatterBomb
	private boolean[][] powerUsed = new boolean[2][2];
	private JButton airAttackBtn = new JButton("Air Attack");
	private JButton scatterBombBtn = new JButton("Scatter Bomb");
	
	static AttackUtilities attackUtilities;
    
    public static AttackUtilities getAttackUtilites() {
        if(attackUtilities == null)
        	attackUtilities = new AttackUtilities();
        
        return attackUtilities;    
    }
    
	public void selfDestroy() {
		attackUtilities = null;
	}
	
	private AttackUtilities() {
		RulesFacade.getRules().register(this);
		setLayout(new BorderLayout(0, 0));
		setBounds(0, BattleshipConfiguration.ALT_DEFAULT - UTILITIES_HEIGHT, BattleshipConfiguration.LARG_DEFAULT, UTILITIES_HEIGHT);

		// MESSAGGI (NORTH)
		JPanel messagesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
		message1.setFont(new Font("SansSerif", Font.BOLD, 28));
		message1.setForeground(Color.BLACK);
		message2.setFont(new Font("SansSerif", Font.PLAIN, 15));
		message2.setForeground(Color.GRAY);
		message3.setFont(new Font("SansSerif", Font.PLAIN, 12));
		message3.setForeground(Color.LIGHT_GRAY);
		messagesPanel.add(message1);
		messagesPanel.add(message2);
		messagesPanel.add(message3);
		add(messagesPanel, BorderLayout.NORTH);

		// --- NUOVO PANNELLO UNICO IN BASSO ---
		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
		bottomPanel.setOpaque(false);
		
		// Initialize Next button
		nextBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
		buttonDisable(); // Start with disabled state
		
		bottomPanel.add(nextBtn);
		bottomPanel.add(airAttackBtn);
		bottomPanel.add(scatterBombBtn);
		bottomPanel.add(saveBtn);
		bottomPanel.add(exitBtn);
		add(bottomPanel, BorderLayout.SOUTH);

		updatePowerButtonsState();
		updateSaveButtonVisibility();

		// (RI)AGGIUNGO ACTION LISTENER AI BOTTONI POWERS
		airAttackBtn.addActionListener(e -> usePower("AirAttack"));
		scatterBombBtn.addActionListener(e -> usePower("ScatterBomb"));

		// ActionListener per Exit
		exitBtn.addActionListener(e -> System.exit(0));
		// ActionListener per Save (vero salvataggio)
		saveBtn.addActionListener(e -> {
			saveload.SaveLoadManager.get().Save();
		});

		// ActionListener per Next
		nextBtn.addActionListener(e -> {
			rules.designPatterns.RulesFacade.getRules().nextPlayer();
			onTurnOrPowerChange();
		});
	}
	
	public void buttonEnable() {
		nextBtn.setForeground(new Color(0, 218, 60).darker());
		nextBtn.setEnabled(true);
		Attack.getAttackFrame().changeTitle("ATTACK PHASE");
		this.repaint();
	}
	
	public void buttonDisable() {
		nextBtn.setText("Next");
		nextBtn.setForeground(Color.GRAY);
		nextBtn.setEnabled(false);
		this.repaint();
	}
	
	public void setMessages(List<String> messages, boolean validation) {
		
		try {
			message1.setText( messages.get( messages.size() - 1 ) );
			message2.setText( messages.get( messages.size() - 2 ) );
			message3.setText( messages.get( messages.size() - 3 ) );
		}
		catch(IndexOutOfBoundsException e) {
			
		}
		
		repaint();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void notify(Observable o) {
		Object lob[] = (Object []) o.get();
		List<String> newMessages = (List<String>) lob[BattleshipConfiguration.objectValues.MESSAGES.getValue()];
		boolean validation = (boolean) lob[BattleshipConfiguration.objectValues.IS_VALID.getValue() ];
		int currentPlayer = (int) lob[BattleshipConfiguration.objectValues.CURRENT_PLAYER.getValue()];
		int humanAttackCount = 0;
		try {
			java.lang.reflect.Field f = rules.CtrlRules.class.getDeclaredField("humanAttackCount");
			f.setAccessible(true);
			humanAttackCount = f.getInt(rules.designPatterns.RulesFacade.getRules().getCtrl());
		} catch(Exception e) {}

		String player2Type = rules.designPatterns.RulesFacade.player2Type;
		boolean isHumanVsHuman = "Human".equals(player2Type);

		if (isHumanVsHuman) {
			if (humanAttackCount < 3) {
				buttonDisable();
				Attack.getAttackFrame().blockCells = false;
			} else {
				buttonEnable();
				Attack.getAttackFrame().blockCells = true;
			}
		} else {
			if(currentPlayer == 1) {
				if(humanAttackCount < 3) {
					buttonDisable();
					Attack.getAttackFrame().blockCells = false;
				} else {
					buttonEnable();
					Attack.getAttackFrame().blockCells = true;
				}
			} else {
				// Bot's turn - don't block cells to allow visualization of bot attacks
				buttonDisable();
				Attack.getAttackFrame().blockCells = false;
			}
		}

		setMessages(newMessages, validation);
		updateSaveButtonVisibility();
	}
	
	// Restituisce l'indice del player di turno (0 o 1)
	public int getCurrentPlayerIndex() {
		int currentPlayer = rules.designPatterns.RulesFacade.getRules().getCurrentPlayer();
		return currentPlayer == 1 ? 0 : 1;
	}

	// Chiamato dopo l'uso di un power
	public void markPowerUsed() {
		int idx = getCurrentPlayerIndex();
		int powerIdx = 0;
		if ("AirAttack".equals(selectedPower)) powerIdx = 0;
		else if ("ScatterBomb".equals(selectedPower)) powerIdx = 1;
		powerUsed[idx][powerIdx] = true;
		selectedPower = null;
		updatePowerButtonsState();
		onTurnOrPowerChange();
		revalidate();
		repaint();
	}

	// Aggiorna abilitazione dei bottoni powers
	public void updatePowerButtonsState() {
		int idx = getCurrentPlayerIndex();
		// 0: AirAttack, 1: ScatterBomb
		airAttackBtn.setEnabled(!powerUsed[idx][0]);
		scatterBombBtn.setEnabled(!powerUsed[idx][1]);
		revalidate();
		repaint();
	}

	private void usePower(String power) {
		selectedPower = power;
		updatePowerButtonsState();
		message1.setText("Select a cell/column for " + power + "!");
	}

	public String getSelectedPower() {
		return selectedPower;
	}
	public void clearSelectedPower() {
		selectedPower = null;
		updatePowerButtonsState();
	}
	
	// Metodo pubblico per impostare un messaggio di errore
	public void setErrorMessage(String message) {
		message1.setText(message);
		repaint();
	}
	
	// Da chiamare ogni volta che cambia il turno o lo stato dei powers
	public void onTurnOrPowerChange() {
		// Reset del potere selezionato quando cambia il turno
		selectedPower = null;
		// Forzo il reset dei bottoni a ogni cambio turno
		updatePowerButtonsState();
		revalidate();
		repaint();
	}

	// Metodo statico per far usare un power al bot (casuale tra quelli disponibili)
	public static void botUsePower(int botIdx) {
		AttackUtilities util = getAttackUtilites();
		List<Integer> available = new ArrayList<>();
		for (int i = 0; i < 2; i++) { // Solo 2 powers ora
			if (!util.powerUsed[botIdx][i]) available.add(i);
		}
		if (available.isEmpty()) return;
		int powerIdx = available.get(new Random().nextInt(available.size()));
		String powerName = powerIdx == 0 ? "AirAttack" : "ScatterBomb";
		util.selectedPower = powerName;
		// Simula la scelta di una cella/colonna casuale
		int gridSize = battleship.main.battleship.BattleshipConfiguration.SQUARE_COUNT;
		int x = new Random().nextInt(gridSize);
		int y = new Random().nextInt(gridSize);
		if (powerIdx == 0) { // AirAttack su colonna x
			for (int row = 0; row < gridSize; row++) {
				rules.designPatterns.RulesFacade.getRules().attack(row, x);
			}
		} else if (powerIdx == 1) { // ScatterBomb su (x, y)
			for (int dx = -1; dx <= 1; dx++) {
				for (int dy = -1; dy <= 1; dy++) {
					int tx = x + dx;
					int ty = y + dy;
					if (tx >= 0 && tx < gridSize && ty >= 0 && ty < gridSize) {
						rules.designPatterns.RulesFacade.getRules().attack(ty, tx);
					}
				}
			}
		}
		util.powerUsed[botIdx][powerIdx] = true;
		util.updatePowerButtonsState();
		util.onTurnOrPowerChange();
	}

	private void updateSaveButtonVisibility() {
		String player2Type = rules.designPatterns.RulesFacade.player2Type;
		boolean isBot = "EasyBot".equals(player2Type) || "HardBot".equals(player2Type);
		saveBtn.setVisible(!isBot);
	}
}



