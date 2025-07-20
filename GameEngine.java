import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
public class GameEngine {
    private ArrayList<Player> players;
    private Dice dice;
    private List<String> gameLog;
    private int roundNumber;
    public GameEngine(ArrayList<Player> players) {
        this.players = players;
        this.dice = new Dice();
        this.gameLog = new ArrayList<>();
        this.roundNumber = 0;
    }
    public GameResult playRound() {
        roundNumber++;
        int highestRoll = 0;
        Player winner = null;
        ArrayList<Player> tiedPlayers = new ArrayList<>();
        ArrayList<Integer> playerRolls = new ArrayList<>();
        StringBuilder roundLog = new StringBuilder();
        roundLog.append("Round ").append(roundNumber).append(": ");
        for (Player player : players) {
            int roll = dice.roll();
            playerRolls.add(roll);
            roundLog.append(player.showName()).append(" rolled ").append(roll).append(", ");
            if (roll > highestRoll) {
                highestRoll = roll;
                winner = player;
                tiedPlayers.clear();
                tiedPlayers.add(player);
            } else if (roll == highestRoll) {
                if (!tiedPlayers.contains(player)) {
                    if (winner != null && !tiedPlayers.contains(winner)) {
                        tiedPlayers.add(winner);
                    }
                    tiedPlayers.add(player);
                }
                winner = null; // Tie situation
            }
        }
        if (winner != null) {
            winner.incrementWin();
            roundLog.append(" Winner: ").append(winner.showName());
        } else {
            roundLog.append(" Tie between: ");
            for (int i = 0; i < tiedPlayers.size(); i++) {
                roundLog.append(tiedPlayers.get(i).showName());
                if (i < tiedPlayers.size() - 1) roundLog.append(", ");
            }
        }
        gameLog.add(roundLog.toString());
        return new GameResult(roundNumber, players, playerRolls, winner, highestRoll);
    }
    public void showResults() {
        for (Player player : players) {
            System.out.println(player.showName() + " has won " + player.showWins() + " rounds");
        }
    }
    public ArrayList<Player> getPlayers() {
        return players;
    }
    public void saveGameToFile(String filename) {
        try (FileWriter writer = new FileWriter(filename, true)) {
            writer.write("=== Dice Game Results ===\n");
            writer.write("Date: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n");
            writer.write("Players: " + players.size() + "\n\n");
            for (String log : gameLog) {
                writer.write(log + "\n");
            }
            writer.write("\nFinal Results:\n");
            for (Player player : players) {
                writer.write(player.showName() + ": " + player.showWins() + " wins\n");
            }
            writer.write("\n" + "=".repeat(50) + "\n\n");
        } catch (IOException e) {
            System.err.println("Error saving game to file: " + e.getMessage());
        }
    }
    public void resetGame() {
        for (Player player : players) {
            player.resetWins();
        }
        gameLog.clear();
        roundNumber = 0;
    }
    public List<String> getGameLog() {
        return gameLog;
    }
    public int getRoundNumber() {
        return roundNumber;
    }
    public static class GameResult {
        private int roundNumber;
        private ArrayList<Player> players;
        private ArrayList<Integer> rolls;
        private Player winner;
        private int winningRoll;
        public GameResult(int roundNumber, ArrayList<Player> players, ArrayList<Integer> rolls, Player winner, int winningRoll) {
            this.roundNumber = roundNumber;
            this.players = new ArrayList<>(players);
            this.rolls = new ArrayList<>(rolls);
            this.winner = winner;
            this.winningRoll = winningRoll;
        }
        public int getRoundNumber() { return roundNumber; }
        public ArrayList<Player> getPlayers() { return players; }
        public ArrayList<Integer> getRolls() { return rolls; }
        public Player getWinner() { return winner; }
        public int getWinningRoll() { return winningRoll; }
    }
}
