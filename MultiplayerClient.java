import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Map;
public class MultiplayerClient extends JFrame {
    private String host;
    private int port;
    private String playerName;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean connected;
    private JPanel playersPanel;
    private JButton rollDiceButton;
    private JButton startGameButton;
    private JTextArea gameLogArea;
    private JScrollPane logScrollPane;
    private JLabel statusLabel;
    private Map<String, JLabel> playerLabels;
    private Map<String, JLabel> diceLabels;
    private Map<String, JLabel> winsLabels;
    public MultiplayerClient(String host, int port, String playerName) {
        this.host = host;
        this.port = port;
        this.playerName = playerName;
        this.connected = false;
        this.playerLabels = new HashMap<>();
        this.diceLabels = new HashMap<>();
        this.winsLabels = new HashMap<>();
        initializeUI();
    }
    private void initializeUI() {
        setTitle("Dice Game - Multiplayer Client (" + playerName + ")");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        createControlPanel();
        createGamePanel();
        createLogPanel();
        add(createControlPanel(), BorderLayout.NORTH);
        add(playersPanel, BorderLayout.CENTER);
        add(logScrollPane, BorderLayout.SOUTH);
    }
    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout());
        statusLabel = new JLabel("Disconnected");
        statusLabel.setForeground(Color.RED);
        startGameButton = new JButton("Start Game");
        rollDiceButton = new JButton("Roll Dice");
        JButton disconnectButton = new JButton("Disconnect");
        startGameButton.setEnabled(false);
        rollDiceButton.setEnabled(false);
        startGameButton.addActionListener(e -> sendMessage("START_GAME"));
        rollDiceButton.addActionListener(e -> sendMessage("ROLL_DICE"));
        disconnectButton.addActionListener(e -> disconnect());
        controlPanel.add(statusLabel);
        controlPanel.add(startGameButton);
        controlPanel.add(rollDiceButton);
        controlPanel.add(disconnectButton);
        return controlPanel;
    }
    private void createGamePanel() {
        playersPanel = new JPanel();
        playersPanel.setLayout(new GridLayout(1, 1));
        playersPanel.add(new JLabel("Waiting for players...", JLabel.CENTER));
    }
    private void createLogPanel() {
        gameLogArea = new JTextArea(8, 50);
        gameLogArea.setEditable(false);
        gameLogArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        logScrollPane = new JScrollPane(gameLogArea);
        logScrollPane.setBorder(BorderFactory.createTitledBorder("Game Log"));
    }
    public void connect() throws IOException {
        socket = new Socket(host, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        connected = true;
        statusLabel.setText("Connected to " + host + ":" + port);
        statusLabel.setForeground(Color.GREEN);
        sendMessage("SET_NAME:" + playerName);
        Thread messageListener = new Thread(this::listenForMessages);
        messageListener.start();
        gameLogArea.append("Connected to server as " + playerName + "\n");
        setVisible(true);
    }
    private void listenForMessages() {
        try {
            String message;
            while (connected && (message = in.readLine()) != null) {
                processServerMessage(message);
            }
        } catch (IOException e) {
            if (connected) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Connection lost: " + e.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
                    disconnect();
                });
            }
        }
    }
    private void processServerMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            String[] parts = message.split(":", 2);
            String command = parts[0];
            switch (command) {
                case "NAME_SET":
                    gameLogArea.append("Player name set to: " + parts[1] + "\n");
                    break;
                case "PLAYER_COUNT":
                    int count = Integer.parseInt(parts[1]);
                    gameLogArea.append("Players connected: " + count + "\n");
                    startGameButton.setEnabled(count >= 2);
                    break;
                case "GAME_STARTED":
                    gameLogArea.append("Game started!\n");
                    startGameButton.setEnabled(false);
                    rollDiceButton.setEnabled(true);
                    break;
                case "PLAYERS":
                    updatePlayersDisplay(parts[1]);
                    break;
                case "ROUND_RESULT":
                    processRoundResult(parts[1]);
                    break;
                case "ERROR":
                    gameLogArea.append("Error: " + parts[1] + "\n");
                    break;
            }
            gameLogArea.setCaretPosition(gameLogArea.getDocument().getLength());
        });
    }
    private void updatePlayersDisplay(String playerData) {
        playersPanel.removeAll();
        playerLabels.clear();
        diceLabels.clear();
        winsLabels.clear();
        String[] players = playerData.split(",");
        playersPanel.setLayout(new GridLayout(1, players.length, 10, 10));
        for (String playerInfo : players) {
            String[] info = playerInfo.split(":");
            String name = info[0];
            int wins = info.length > 1 ? Integer.parseInt(info[1]) : 0;
            JPanel playerPanel = new JPanel(new BorderLayout());
            playerPanel.setBorder(BorderFactory.createTitledBorder(name));
            JLabel nameLabel = new JLabel(name, JLabel.CENTER);
            nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
            JLabel winsLabel = new JLabel("Wins: " + wins, JLabel.CENTER);
            winsLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            JLabel diceLabel = new JLabel("🎲", JLabel.CENTER);
            diceLabel.setFont(new Font("Arial", Font.PLAIN, 48));
            playerPanel.add(nameLabel, BorderLayout.NORTH);
            playerPanel.add(diceLabel, BorderLayout.CENTER);
            playerPanel.add(winsLabel, BorderLayout.SOUTH);
            playersPanel.add(playerPanel);
            playerLabels.put(name, nameLabel);
            diceLabels.put(name, diceLabel);
            winsLabels.put(name, winsLabel);
        }
        playersPanel.revalidate();
        playersPanel.repaint();
    }
    private void processRoundResult(String resultData) {
        String[] parts = resultData.split(";");
        int roundNumber = Integer.parseInt(parts[0]);
        String[] playerResults = parts[1].split(",");
        StringBuilder logEntry = new StringBuilder();
        logEntry.append("Round ").append(roundNumber).append(": ");
        for (String result : playerResults) {
            String[] info = result.split(":");
            String name = info[0];
            int roll = Integer.parseInt(info[1]);
            int wins = Integer.parseInt(info[2]);
            if (diceLabels.containsKey(name)) {
                diceLabels.get(name).setText(getDiceFace(roll));
                winsLabels.get(name).setText("Wins: " + wins);
            }
            logEntry.append(name).append(" rolled ").append(roll).append(", ");
        }
        if (parts.length > 2) {
            if (parts[2].equals("TIE")) {
                logEntry.append(" - Tie!");
            } else if (parts[2].startsWith("WINNER:")) {
                String winner = parts[2].substring(7); // Remove "WINNER:" prefix
                logEntry.append(" - Winner: ").append(winner);
            }
        }
        gameLogArea.append(logEntry.toString() + "\n");
    }
    private String getDiceFace(int roll) {
        String[] diceFaces = {"⚀", "⚁", "⚂", "⚃", "⚄", "⚅"};
        return diceFaces[roll - 1];
    }
    private void sendMessage(String message) {
        if (out != null && connected) {
            out.println(message);
        }
    }
    public void disconnect() {
        connected = false;
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Error disconnecting: " + e.getMessage());
        }
        statusLabel.setText("Disconnected");
        statusLabel.setForeground(Color.RED);
        startGameButton.setEnabled(false);
        rollDiceButton.setEnabled(false);
        gameLogArea.append("Disconnected from server.\n");
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception e) {
            }
            String host = "localhost";
            int port = 12345;
            String playerName = "TestPlayer";
            MultiplayerClient client = new MultiplayerClient(host, port, playerName);
            try {
                client.connect();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Failed to connect: " + e.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
