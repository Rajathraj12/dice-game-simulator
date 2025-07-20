import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MultiplayerServer {
    private int port;
    private ServerSocket serverSocket;
    private List<ClientHandler> clients;
    private GameEngine gameEngine;
    private boolean gameStarted;
    private boolean running;
    public MultiplayerServer(int port) {
        this.port = port;
        this.clients = new CopyOnWriteArrayList<>();
        this.gameStarted = false;
        this.running = false;
    }
    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        running = true;
        System.out.println("Server started on port " + port);
        Thread acceptThread = new Thread(() -> {
            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                    clients.add(clientHandler);
                    clientHandler.start();
                    System.out.println("Client connected. Total clients: " + clients.size());
                    broadcastMessage("PLAYER_COUNT:" + clients.size());
                } catch (IOException e) {
                    if (running) {
                        System.err.println("Error accepting client: " + e.getMessage());
                    }
                }
            }
        });
        acceptThread.start();
    }
    public void stop() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            for (ClientHandler client : clients) {
                client.disconnect();
            }
        } catch (IOException e) {
            System.err.println("Error stopping server: " + e.getMessage());
        }
    }
    public void broadcastMessage(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }
    public void startGame() {
        if (clients.size() < 2) {
            broadcastMessage("ERROR:Need at least 2 players to start");
            return;
        }
        ArrayList<Player> players = new ArrayList<>();
        for (ClientHandler client : clients) {
            players.add(new Player(client.getPlayerName()));
        }
        gameEngine = new GameEngine(players);
        gameStarted = true;
        broadcastMessage("GAME_STARTED");
        broadcastPlayerList();
    }
    public void playRound() {
        if (!gameStarted || gameEngine == null) {
            broadcastMessage("ERROR:Game not started");
            return;
        }
        GameEngine.GameResult result = gameEngine.playRound();
        StringBuilder roundData = new StringBuilder("ROUND_RESULT:");
        roundData.append(result.getRoundNumber()).append(";");
        ArrayList<Player> players = result.getPlayers();
        ArrayList<Integer> rolls = result.getRolls();
        for (int i = 0; i < players.size(); i++) {
            roundData.append(players.get(i).showName())
                    .append(":")
                    .append(rolls.get(i))
                    .append(":")
                    .append(players.get(i).showWins());
            if (i < players.size() - 1) roundData.append(",");
        }
        if (result.getWinner() != null) {
            roundData.append(";WINNER:").append(result.getWinner().showName());
        } else {
            roundData.append(";TIE");
        }
        broadcastMessage(roundData.toString());
    }
    private void broadcastPlayerList() {
        if (gameEngine == null) return;
        StringBuilder playerData = new StringBuilder("PLAYERS:");
        ArrayList<Player> players = gameEngine.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            playerData.append(players.get(i).showName()).append(":").append(players.get(i).showWins());
            if (i < players.size() - 1) playerData.append(",");
        }
        broadcastMessage(playerData.toString());
    }
    public void removeClient(ClientHandler client) {
        clients.remove(client);
        broadcastMessage("PLAYER_COUNT:" + clients.size());
        System.out.println("Client disconnected. Total clients: " + clients.size());
    }
    public boolean isGameStarted() {
        return gameStarted;
    }
    static class ClientHandler extends Thread {
        private Socket socket;
        private MultiplayerServer server;
        private PrintWriter out;
        private BufferedReader in;
        private String playerName;
        public ClientHandler(Socket socket, MultiplayerServer server) {
            this.socket = socket;
            this.server = server;
        }
        @Override
        public void run() {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    processMessage(inputLine);
                }
            } catch (IOException e) {
                System.err.println("Error in client handler: " + e.getMessage());
            } finally {
                disconnect();
                server.removeClient(this);
            }
        }
        private void processMessage(String message) {
            String[] parts = message.split(":", 2);
            String command = parts[0];
            switch (command) {
                case "SET_NAME":
                    if (parts.length > 1) {
                        playerName = parts[1];
                        sendMessage("NAME_SET:" + playerName);
                    }
                    break;
                case "START_GAME":
                    server.startGame();
                    break;
                case "ROLL_DICE":
                    server.playRound();
                    break;
                case "GET_STATUS":
                    sendMessage("GAME_STATUS:" + (server.isGameStarted() ? "STARTED" : "WAITING"));
                    break;
            }
        }
        public void sendMessage(String message) {
            if (out != null) {
                out.println(message);
            }
        }
        public void disconnect() {
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                System.err.println("Error closing client socket: " + e.getMessage());
            }
        }
        public String getPlayerName() {
            return playerName != null ? playerName : "Unknown";
        }
    }
}
