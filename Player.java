public class Player {
    private String name;
    private int wins;
    public Player(String name) {
        this.name = name;
        this.wins = 0;
    }
    public int showWins() {
        return this.wins;
    }
    public String showName() {
        return this.name;
    }
    public void incrementWin() {
        this.wins++;
    }
    public void resetWins() {
        this.wins = 0;
    }
    @Override
    public String toString() {
        return name + " (Wins: " + wins + ")";
    }
}
