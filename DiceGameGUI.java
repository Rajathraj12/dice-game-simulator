import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.awt.event.ActionListener;
import java.util.ArrayList;
public class DiceGameGUI extends JFrame {
    private GameEngine gameEngine;
    private JPanel playersPanel;
    private JPanel gamePanel;
    private JPanel controlPanel;
    private JButton rollDiceButton;
    private JButton newGameButton;
    private JButton saveGameButton;
    private JTextArea gameLogArea;
    private JScrollPane logScrollPane;
    private ArrayList<JLabel> playerLabels;
    private ArrayList<JLabel> diceLabels;
    private ArrayList<JLabel> winsLabels;
    public DiceGameGUI() {
        initializeUI();
    }
    private void initializeUI() {
        setTitle("Dice Game Simulator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        createMenuBar();
        createControlPanel();
        createGamePanel();
        createLogPanel();
        add(controlPanel, BorderLayout.NORTH);
        add(gamePanel, BorderLayout.CENTER);
        add(logScrollPane, BorderLayout.SOUTH);
        SwingUtilities.invokeLater(this::showNewGameDialog);
    }
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Game");
        JMenuItem newGameItem = new JMenuItem("New Game");
        JMenuItem saveGameItem = new JMenuItem("Save Game");
        JMenuItem exitItem = new JMenuItem("Exit");
        newGameItem.addActionListener(e -> showNewGameDialog());
        saveGameItem.addActionListener(e -> saveGame());
        exitItem.addActionListener(e -> System.exit(0));
        gameMenu.add(newGameItem);
        gameMenu.add(saveGameItem);
        gameMenu.addSeparator();
        gameMenu.add(exitItem);
        JMenu multiplayerMenu = new JMenu("Multiplayer");
        JMenuItem hostGameItem = new JMenuItem("Host Game");
        JMenuItem joinGameItem = new JMenuItem("Join Game");
        hostGameItem.addActionListener(e -> hostMultiplayerGame());
        joinGameItem.addActionListener(e -> joinMultiplayerGame());
        multiplayerMenu.add(hostGameItem);
        multiplayerMenu.add(joinGameItem);
        menuBar.add(gameMenu);
        menuBar.add(multiplayerMenu);
        setJMenuBar(menuBar);
    }
    private void createControlPanel() {
        controlPanel = new JPanel(new FlowLayout());
        rollDiceButton = new JButton("Roll Dice");
        newGameButton = new JButton("New Game");
        saveGameButton = new JButton("Save Game");
        rollDiceButton.addActionListener(new RollDiceListener());
        newGameButton.addActionListener(e -> showNewGameDialog());
        saveGameButton.addActionListener(e -> saveGame());
        rollDiceButton.setEnabled(false);
        saveGameButton.setEnabled(false);
        controlPanel.add(rollDiceButton);
        controlPanel.add(newGameButton);
        controlPanel.add(saveGameButton);
    }
    private void createGamePanel() {
        gamePanel = new JPanel(new BorderLayout());
        playersPanel = new JPanel();
        gamePanel.add(playersPanel, BorderLayout.CENTER);
        JLabel titleLabel = new JLabel("Dice Game Simulator", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gamePanel.add(titleLabel, BorderLayout.NORTH);
    }
    private void createLogPanel() {
        gameLogArea = new JTextArea(8, 50);
        gameLogArea.setEditable(false);
        gameLogArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        logScrollPane = new JScrollPane(gameLogArea);
        logScrollPane.setBorder(BorderFactory.createTitledBorder("Game Log"));
    }
    private void showNewGameDialog() {
        String input = JOptionPane.showInputDialog(this, "Enter number of players (2-6):", "New Game", JOptionPane.QUESTION_MESSAGE);
        if (input == null) return; // User cancelled
        try {
            int numPlayers = Integer.parseInt(input);
            if (numPlayers < 2 || numPlayers > 6) {
                JOptionPane.showMessageDialog(this, "Number of players must be between 2 and 6!", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return;
            }
            ArrayList<Player> players = new ArrayList<>();
            for (int i = 0; i < numPlayers; i++) {
                String name = JOptionPane.showInputDialog(this, "Enter name for Player " + (i + 1) + ":", "Player Name", JOptionPane.QUESTION_MESSAGE);
                if (name == null || name.trim().isEmpty()) {
                    name = "Player " + (i + 1);
                }
                players.add(new Player(name.trim()));
            }
            startNewGame(players);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number!", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void startNewGame(ArrayList<Player> players) {
        gameEngine = new GameEngine(players);
        setupPlayersPanel(players);
        rollDiceButton.setEnabled(true);
        saveGameButton.setEnabled(true);
        gameLogArea.setText("New game started with " + players.size() + " players!\n");
        for (Player player : players) {
            gameLogArea.append("- " + player.showName() + "\n");
        }
        gameLogArea.append("\nClick 'Roll Dice' to start playing!\n\n");
    }
    private void setupPlayersPanel(ArrayList<Player> players) {
        playersPanel.removeAll();
        playersPanel.setLayout(new GridLayout(2, players.size(), 10, 10));
        playerLabels = new ArrayList<>();
        diceLabels = new ArrayList<>();
        winsLabels = new ArrayList<>();
        for (Player player : players) {
            JPanel playerPanel = new JPanel(new BorderLayout());
            playerPanel.setBorder(BorderFactory.createTitledBorder(player.showName()));
            JLabel nameLabel = new JLabel(player.showName(), JLabel.CENTER);
            nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
            JLabel winsLabel = new JLabel("Wins: " + player.showWins(), JLabel.CENTER);
            winsLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            JLabel diceLabel = new JLabel("🎲", JLabel.CENTER);
            diceLabel.setFont(new Font("Arial", Font.PLAIN, 48));
            playerPanel.add(nameLabel, BorderLayout.NORTH);
            playerPanel.add(diceLabel, BorderLayout.CENTER);
            playerPanel.add(winsLabel, BorderLayout.SOUTH);
            playersPanel.add(playerPanel);
            playerLabels.add(nameLabel);
            diceLabels.add(diceLabel);
            winsLabels.add(winsLabel);
        }
        playersPanel.revalidate();
        playersPanel.repaint();
    }
    private void updatePlayerDisplay() {
        ArrayList<Player> players = gameEngine.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            winsLabels.get(i).setText("Wins: " + player.showWins());
        }
    }
    private void animateDiceRoll(ArrayList<Integer> rolls) {
        rollDiceButton.setEnabled(false);
        Timer animationTimer = new Timer(100, null);
        final int[] animationCount = {0};
        final int maxAnimations = 10;
        animationTimer.addActionListener(e -> {
            for (int i = 0; i < diceLabels.size(); i++) {
                int randomRoll = (int)(Math.random() * 6) + 1;
                diceLabels.get(i).setText(getDiceFace(randomRoll));
            }
            animationCount[0]++;
            if (animationCount[0] >= maxAnimations) {
                for (int i = 0; i < rolls.size(); i++) {
                    diceLabels.get(i).setText(getDiceFace(rolls.get(i)));
                }
                animationTimer.stop();
                rollDiceButton.setEnabled(true);
            }
        });
        animationTimer.start();
    }
    private String getDiceFace(int roll) {
        String[] diceFaces = {"⚀", "⚁", "⚂", "⚃", "⚄", "⚅"};
        return diceFaces[roll - 1];
    }
    private void saveGame() {
        if (gameEngine == null) {
            JOptionPane.showMessageDialog(this, "No game to save!", "Save Game", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String filename = JOptionPane.showInputDialog(this, "Enter filename to save:", "Save Game", JOptionPane.QUESTION_MESSAGE);
        if (filename == null || filename.trim().isEmpty()) {
            filename = "dice_game_results.txt";
        }
        if (!filename.endsWith(".txt")) {
            filename += ".txt";
        }
        gameEngine.saveGameToFile(filename);
        JOptionPane.showMessageDialog(this, "Game saved to " + filename, "Save Game", JOptionPane.INFORMATION_MESSAGE);
    }
    private void hostMultiplayerGame() {
        new MultiplayerHostDialog(this).setVisible(true);
    }
    private void joinMultiplayerGame() {
        new MultiplayerClientDialog(this).setVisible(true);
    }
    private class RollDiceListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (gameEngine == null) return;
            GameEngine.GameResult result = gameEngine.playRound();
            animateDiceRoll(result.getRolls());
            updatePlayerDisplay();
            StringBuilder logEntry = new StringBuilder();
            logEntry.append("Round ").append(result.getRoundNumber()).append(": ");
            ArrayList<Player> players = result.getPlayers();
            ArrayList<Integer> rolls = result.getRolls();
            for (int i = 0; i < players.size(); i++) {
                logEntry.append(players.get(i).showName())
                       .append(" rolled ")
                       .append(rolls.get(i));
                if (i < players.size() - 1) logEntry.append(", ");
            }
            if (result.getWinner() != null) {
                logEntry.append(" - Winner: ").append(result.getWinner().showName());
            } else {
                logEntry.append(" - Tie!");
            }
            gameLogArea.append(logEntry.toString() + "\n");
            gameLogArea.setCaretPosition(gameLogArea.getDocument().getLength());
        }
    }
    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
        }
        SwingUtilities.invokeLater(() -> {
            new DiceGameGUI().setVisible(true);
        });
    }
}
