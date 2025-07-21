import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.border.EmptyBorder;
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
    private JLabel statusLabel;
    private JPanel statusPanel;
    
    // Modern color scheme
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private final Color ACCENT_COLOR = new Color(231, 76, 60);
    private final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private final Color WARNING_COLOR = new Color(243, 156, 18);
    private final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private final Color PANEL_COLOR = new Color(255, 255, 255);
    private final Color TEXT_COLOR = new Color(44, 62, 80);
    public DiceGameGUI() {
        initializeUI();
    }
    private void initializeUI() {
        setTitle("🎲 Modern Dice Arena - Elite Gaming Experience");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setResizable(true);
        
        // Create main container with custom layout
        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));
        mainContainer.setBackground(new Color(18, 18, 18)); // Dark theme
        
        createModernMenuBar();
        createHeaderSection();
        createMainGameArea();
        createBottomSection();
        
        mainContainer.add(createHeaderSection());
        mainContainer.add(Box.createVerticalStrut(10));
        mainContainer.add(createMainGameArea());
        mainContainer.add(Box.createVerticalStrut(10));
        mainContainer.add(createBottomSection());
        
        setContentPane(mainContainer);
        SwingUtilities.invokeLater(this::showNewGameDialog);
    }
    private void createModernMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(30, 30, 30));
        menuBar.setBorder(new EmptyBorder(5, 10, 5, 10));
        
        JMenu gameMenu = createStyledMenu("🎮 Game", Color.WHITE);
        JMenu multiplayerMenu = createStyledMenu("🌐 Multiplayer", Color.WHITE);
        
        JMenuItem newGameItem = createStyledMenuItem("🆕 New Game");
        JMenuItem saveGameItem = createStyledMenuItem("💾 Save Game");  
        JMenuItem exitItem = createStyledMenuItem("❌ Exit");
        
        newGameItem.addActionListener(e -> showNewGameDialog());
        saveGameItem.addActionListener(e -> saveGame());
        exitItem.addActionListener(e -> System.exit(0));
        
        gameMenu.add(newGameItem);
        gameMenu.add(saveGameItem);
        gameMenu.addSeparator();
        gameMenu.add(exitItem);
        
        JMenuItem hostGameItem = createStyledMenuItem("🖥️ Host Game");
        JMenuItem joinGameItem = createStyledMenuItem("🔗 Join Game");
        
        hostGameItem.addActionListener(e -> hostMultiplayerGame());
        joinGameItem.addActionListener(e -> joinMultiplayerGame());
        
        multiplayerMenu.add(hostGameItem);
        multiplayerMenu.add(joinGameItem);
        
        menuBar.add(gameMenu);
        menuBar.add(multiplayerMenu);
        setJMenuBar(menuBar);
    }
    
    private JMenu createStyledMenu(String text, Color color) {
        JMenu menu = new JMenu(text);
        menu.setForeground(color);
        menu.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return menu;
    }
    
    private JMenuItem createStyledMenuItem(String text) {
        JMenuItem item = new JMenuItem(text);
        item.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        item.setBackground(new Color(40, 40, 40));
        item.setForeground(Color.WHITE);
        return item;
    }
    
    private JPanel createHeaderSection() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(new Color(25, 25, 35));
        headerPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        
        // Main title with glow effect
        JLabel titleLabel = new JLabel("DICE ARENA ELITE", JLabel.CENTER);
        titleLabel.setFont(new Font("Impact", Font.BOLD, 36));
        titleLabel.setForeground(new Color(255, 215, 0)); // Gold
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("⚡ The Ultimate Dice Gaming Experience ⚡", JLabel.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        subtitleLabel.setForeground(new Color(180, 180, 180));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Status bar
        statusLabel = new JLabel("🎲 Ready to Roll! Start a new game to begin your journey.", JLabel.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusLabel.setForeground(new Color(100, 200, 255));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(subtitleLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(statusLabel);
        
        return headerPanel;
    }
    
    private JPanel createMainGameArea() {
        JPanel mainArea = new JPanel(new BorderLayout());
        mainArea.setBackground(new Color(18, 18, 18));
        mainArea.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        // Control panel with modern circular buttons
        JPanel controlArea = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 15));
        controlArea.setBackground(new Color(35, 35, 45));
        controlArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 120), 1),
            new EmptyBorder(15, 20, 15, 20)
        ));
        
        rollDiceButton = createCircularButton("🎲", "ROLL", new Color(220, 50, 50));
        newGameButton = createCircularButton("🆕", "NEW GAME", new Color(50, 200, 50));
        saveGameButton = createCircularButton("💾", "SAVE", new Color(255, 165, 0));
        
        rollDiceButton.addActionListener(new RollDiceListener());
        newGameButton.addActionListener(e -> showNewGameDialog());
        saveGameButton.addActionListener(e -> saveGame());
        
        rollDiceButton.setEnabled(false);
        saveGameButton.setEnabled(false);
        
        controlArea.add(rollDiceButton);
        controlArea.add(newGameButton);
        controlArea.add(saveGameButton);
        
        // Players area with hexagonal layout
        playersPanel = new JPanel();
        playersPanel.setBackground(new Color(18, 18, 18));
        
        mainArea.add(controlArea, BorderLayout.NORTH);
        mainArea.add(playersPanel, BorderLayout.CENTER);
        
        return mainArea;
    }
    
    private JButton createCircularButton(String emoji, String text, Color color) {
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int size = Math.min(getWidth(), getHeight());
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;
                
                // Shadow
                g2d.setColor(new Color(0, 0, 0, 50));
                g2d.fillOval(x + 3, y + 3, size - 6, size - 6);
                
                // Main circle
                if (getModel().isPressed() && isEnabled()) {
                    g2d.setColor(color.darker());
                } else if (getModel().isRollover() && isEnabled()) {
                    g2d.setColor(color.brighter());
                } else {
                    g2d.setColor(isEnabled() ? color : color.darker().darker());
                }
                g2d.fillOval(x, y, size - 3, size - 3);
                
                // Border
                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawOval(x + 1, y + 1, size - 5, size - 5);
                
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        
        button.setLayout(new BorderLayout());
        
        JLabel emojiLabel = new JLabel(emoji, JLabel.CENTER);
        emojiLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        
        JLabel textLabel = new JLabel(text, JLabel.CENTER);
        textLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        textLabel.setForeground(Color.WHITE);
        
        button.add(emojiLabel, BorderLayout.CENTER);
        button.add(textLabel, BorderLayout.SOUTH);
        
        button.setPreferredSize(new Dimension(80, 80));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    private JPanel createBottomSection() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(18, 18, 18));
        bottomPanel.setBorder(new EmptyBorder(10, 20, 20, 20));
        
        // Modern game log with terminal-like appearance
        gameLogArea = new JTextArea(6, 50);
        gameLogArea.setEditable(false);
        gameLogArea.setFont(new Font("Courier New", Font.PLAIN, 11));
        gameLogArea.setBackground(new Color(12, 12, 12));
        gameLogArea.setForeground(new Color(0, 255, 127)); // Matrix green
        gameLogArea.setBorder(new EmptyBorder(15, 20, 15, 20));
        gameLogArea.setText(">>> DICE ARENA TERMINAL READY <<<\n>>> Waiting for game initialization...\n");
        
        logScrollPane = new JScrollPane(gameLogArea);
        logScrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0, 255, 127), 2), 
                "🖥️ GAME TERMINAL", 
                0, 
                0, 
                new Font("Segoe UI", Font.BOLD, 14), 
                new Color(0, 255, 127)),
            new EmptyBorder(5, 5, 5, 5)
        ));
        logScrollPane.setPreferredSize(new Dimension(0, 180));
        logScrollPane.getViewport().setBackground(new Color(12, 12, 12));
        
        bottomPanel.add(logScrollPane, BorderLayout.CENTER);
        return bottomPanel;
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
        
        // Update status
        statusLabel.setText("🎮 BATTLE STATIONS ACTIVE - " + players.size() + " warriors ready!");
        
        gameLogArea.setText(">>> GAME INITIALIZATION COMPLETE <<<\n");
        gameLogArea.append(">>> " + players.size() + " PLAYERS ENTERED THE ARENA <<<\n");
        gameLogArea.append("═══════════════════════════════════════════════════════════\n");
        for (Player player : players) {
            gameLogArea.append(">>> WARRIOR: " + player.showName() + " [READY]\n");
        }
        gameLogArea.append("═══════════════════════════════════════════════════════════\n");
        gameLogArea.append(">>> PRESS ROLL TO BEGIN COMBAT! <<<\n\n");
    }
    private void setupPlayersPanel(ArrayList<Player> players) {
        playersPanel.removeAll();
        playersPanel.setLayout(new GridLayout(1, players.size(), 15, 15));
        playersPanel.setBackground(new Color(18, 18, 18));
        
        playerLabels = new ArrayList<>();
        diceLabels = new ArrayList<>();
        winsLabels = new ArrayList<>();
        
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            JPanel playerCard = createModernPlayerCard(player, i);
            playersPanel.add(playerCard);
        }
        
        playersPanel.revalidate();
        playersPanel.repaint();
    }
    
    private JPanel createModernPlayerCard(Player player, int index) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Outer glow
                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.fillRoundRect(5, 5, getWidth()-10, getHeight()-10, 25, 25);
                
                // Main card with gradient
                Color[] neonColors = {
                    new Color(255, 20, 147),  // Deep pink
                    new Color(0, 191, 255),   // Deep sky blue
                    new Color(50, 205, 50),   // Lime green
                    new Color(255, 165, 0),   // Orange
                    new Color(148, 0, 211),   // Dark violet
                    new Color(255, 215, 0)    // Gold
                };
                Color cardColor = neonColors[index % neonColors.length];
                
                // Card background
                g2d.setColor(new Color(30, 30, 40));
                g2d.fillRoundRect(2, 2, getWidth()-4, getHeight()-4, 20, 20);
                
                // Neon border
                g2d.setColor(cardColor);
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRoundRect(3, 3, getWidth()-6, getHeight()-6, 20, 20);
                
                // Inner glow
                g2d.setColor(new Color(cardColor.getRed(), cardColor.getGreen(), cardColor.getBlue(), 30));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(8, 8, getWidth()-16, getHeight()-16, 15, 15);
                
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        
        card.setLayout(new BorderLayout(10, 10));
        card.setBorder(new EmptyBorder(25, 20, 25, 20));
        card.setOpaque(false);
        
        // Player name with warrior emojis
        String[] warriors = {"⚔️", "🛡️", "🗡️", "�", "🔥", "⚡"};
        JLabel nameLabel = new JLabel(warriors[index % warriors.length] + " " + player.showName(), JLabel.CENTER);
        nameLabel.setFont(new Font("Orbitron", Font.BOLD, 16));
        nameLabel.setForeground(Color.WHITE);
        
        // Wins label with cyberpunk styling  
        JLabel winsLabel = new JLabel("VICTORIES: " + player.showWins(), JLabel.CENTER);
        winsLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        winsLabel.setForeground(new Color(0, 255, 127));
        
        // Extra large dice display
        JLabel diceLabel = new JLabel("🎲", JLabel.CENTER);
        diceLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 72));
        
        // Stats panel
        JPanel statsPanel = new JPanel(new GridLayout(2, 1, 8, 8));
        statsPanel.setOpaque(false);
        statsPanel.add(nameLabel);
        statsPanel.add(winsLabel);
        
        card.add(statsPanel, BorderLayout.NORTH);
        card.add(diceLabel, BorderLayout.CENTER);
        
        playerLabels.add(nameLabel);
        diceLabels.add(diceLabel);
        winsLabels.add(winsLabel);
        
        return card;
    }
    private void updatePlayerDisplay() {
        ArrayList<Player> players = gameEngine.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            winsLabels.get(i).setText("VICTORIES: " + player.showWins());
        }
    }
    private void animateDiceRoll(ArrayList<Integer> rolls) {
        rollDiceButton.setEnabled(false);
        statusLabel.setText("🎲 Rolling dice... Get ready for the results!");
        
        Timer animationTimer = new Timer(120, null);
        final int[] animationCount = {0};
        final int maxAnimations = 15; // Longer animation for better effect
        
        animationTimer.addActionListener(e -> {
            for (int i = 0; i < diceLabels.size(); i++) {
                int randomRoll = (int)(Math.random() * 6) + 1;
                diceLabels.get(i).setText(getDiceFace(randomRoll));
                
                // Add shake effect by slightly changing the font size
                int fontSize = 64 + (int)(Math.random() * 8) - 4;
                diceLabels.get(i).setFont(new Font("Segoe UI Emoji", Font.PLAIN, fontSize));
            }
            
            animationCount[0]++;
            if (animationCount[0] >= maxAnimations) {
                // Final results with celebration
                for (int i = 0; i < rolls.size(); i++) {
                    diceLabels.get(i).setText(getDiceFace(rolls.get(i)));
                    diceLabels.get(i).setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
                }
                
                animationTimer.stop();
                rollDiceButton.setEnabled(true);
                statusLabel.setText("🎯 Round complete! Check the results below.");
                
                // Brief celebration effect
                Timer celebrationTimer = new Timer(200, null);
                final int[] celebCount = {0};
                celebrationTimer.addActionListener(celebEvent -> {
                    for (JLabel dice : diceLabels) {
                        int size = celebCount[0] % 2 == 0 ? 70 : 64;
                        dice.setFont(new Font("Segoe UI Emoji", Font.PLAIN, size));
                    }
                    celebCount[0]++;
                    if (celebCount[0] >= 4) {
                        celebrationTimer.stop();
                        for (JLabel dice : diceLabels) {
                            dice.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
                        }
                    }
                });
                celebrationTimer.start();
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
