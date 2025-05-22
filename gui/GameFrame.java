package gui;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GameFrame extends JFrame {
    private final boolean vsBot;
    private final boolean modern;
    private final Difficulty difficulty;
    private final GameController controller = new GameController();
    private final BoardPanel playerBoard;
    private final BoardPanel enemyBoard;
    private final BotPlayer bot;
    private final Board botBoard;
    private final Board playerLogicBoard = new Board();

    private boolean placingShips = true;
    private int placingIndex = 0;
    private final int[] shipSizes = {4, 3, 3, 2, 2, 2, 1, 1, 1, 1};
    private boolean horizontalPlacement = true;

    private boolean usedScatter = false;
    private boolean usedScan = false;
    private boolean usedAir = false;
    private boolean usedPowerThisTurn = false;

    public GameFrame(boolean vsBot, boolean modern, Difficulty difficulty) {
        this.vsBot = vsBot;
        this.modern = modern;
        this.difficulty = difficulty;
        this.bot = vsBot ? new BotPlayer(difficulty) : null;
        this.botBoard = vsBot ? new Board() : null;

        setTitle("Battleship - Game");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        playerBoard = new BoardPanel("Your Fleet", true, this::placeShipAt);
        enemyBoard = new BoardPanel("Opponent", false, (row, col) -> handleAttack(row, col));

        JPanel boardsPanel = new JPanel(new GridLayout(1, 2));
        boardsPanel.add(playerBoard);
        boardsPanel.add(enemyBoard);

        add(boardsPanel, BorderLayout.CENTER);
        setupUI();

        if (vsBot) {
            List<Ship> botShips = bot.placeShipsClassic();
            for (Ship ship : botShips) {
                controller.addShip(false, ship);
                botBoard.placeShip(ship);
            }
        }

        setVisible(true);
        JOptionPane.showMessageDialog(this, "Place your ships on the left grid. Toggle orientation with the button.");
    }

    private void placeShipAt(int row, int col) {
        if (!placingShips) return;
        if (placingIndex >= shipSizes.length) return;

        int size = shipSizes[placingIndex];
        Ship ship = new Ship(size, row, col, horizontalPlacement);
        if (playerLogicBoard.placeShip(ship)) {
            controller.addShip(true, ship);
            for (int i = 0; i < size; i++) {
                int r = horizontalPlacement ? row : row + i;
                int c = horizontalPlacement ? col + i : col;
                playerBoard.updateCell(r, c, true);
            }
            placingIndex++;
            if (placingIndex == shipSizes.length) {
                placingShips = false;
                JOptionPane.showMessageDialog(this, "All ships placed. Start attacking on the right grid.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid placement.");
        }
    }

    private void handleAttack(int row, int col) {
        if (placingShips || usedPowerThisTurn) return;

        boolean hit = controller.attack(row, col, true);
        enemyBoard.updateCell(row, col, hit);

        if (controller.checkWin(true)) {
            JOptionPane.showMessageDialog(this, "You won!");
            dispose();
            new GameModeSelector();
            return;
        }

        controller.switchTurn();
        usedPowerThisTurn = false;

        if (vsBot) {
            int[] move = bot.generateMove();
            boolean botHit = controller.attack(move[0], move[1], false);
            playerBoard.updateCell(move[0], move[1], botHit);

            if (controller.checkWin(false)) {
                JOptionPane.showMessageDialog(this, "Bot won!");
                dispose();
                new GameModeSelector();
                return;
            }

            controller.switchTurn();
        }
    }

    private void usePower(List<int[]> cells, boolean damaging) {
        for (int[] pos : cells) {
            int row = pos[0];
            int col = pos[1];
            boolean hit = damaging ? controller.attack(row, col, true) : botBoard.getCell(row, col).hasShip();
            enemyBoard.updateCell(row, col, hit);
        }
        usedPowerThisTurn = true;
        controller.switchTurn();
    }

    private void setupUI() {
        JPanel bottomPanel = new JPanel();

        JButton scatterBtn = new JButton("Scatter Bomb");
        JButton scanBtn = new JButton("Scanner");
        JButton airBtn = new JButton("Air Strike");
        JButton surrenderBtn = new JButton("Surrender");
        JButton orientationBtn = new JButton("Toggle Orientation");

        scatterBtn.addActionListener(e -> {
            if (!modern || usedScatter || usedPowerThisTurn) return;
            String input = JOptionPane.showInputDialog("Enter center (e.g., 3,4):");
            String[] parts = input.split(",");
            List<int[]> cells = Powers.scatterBomb(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
            usePower(cells, true);
            usedScatter = true;
        });

        scanBtn.addActionListener(e -> {
            if (!modern || usedScan || usedPowerThisTurn) return;
            String input = JOptionPane.showInputDialog("Enter center (e.g., 3,4):");
            String[] parts = input.split(",");
            List<int[]> cells = Powers.scanner(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
            usePower(cells, false);
            usedScan = true;
        });

        airBtn.addActionListener(e -> {
            if (!modern || usedAir || usedPowerThisTurn) return;
            String input = JOptionPane.showInputDialog("Enter column (0-9):");
            List<int[]> cells = Powers.attaccoAereo(Integer.parseInt(input));
            usePower(cells, true);
            usedAir = true;
        });

        surrenderBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "You surrendered!");
            dispose();
            new GameModeSelector();
        });

        orientationBtn.addActionListener(e -> {
            horizontalPlacement = !horizontalPlacement;
            orientationBtn.setText("Orientation: " + (horizontalPlacement ? "Horizontal" : "Vertical"));
        });

        if (modern) {
            bottomPanel.add(scatterBtn);
            bottomPanel.add(scanBtn);
            bottomPanel.add(airBtn);
        }

        bottomPanel.add(orientationBtn);
        bottomPanel.add(surrenderBtn);
        add(bottomPanel, BorderLayout.SOUTH);
    }
}
