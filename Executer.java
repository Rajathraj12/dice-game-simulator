import java.util.ArrayList;
import java.util.Scanner;
public class Executer {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Give the number of players: ");
        int n = sc.nextInt();
        sc.nextLine();
        ArrayList<Player> list = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            System.out.println("Player " + (i + 1) + " name: ");
            String name = sc.nextLine();
            list.add(new Player(name));
        }
        GameEngine ge = new GameEngine(list);
        while (true) {
            System.out.println("Do you want to play the game? (1/0): ");
            int ip = sc.nextInt();
            if (ip == 1) {
                GameEngine.GameResult result = ge.playRound();
                if (result.getWinner() != null) {
                    System.out.println("The player who won this round is " + result.getWinner().showName() +
                        ", the output we got is " + result.getWinningRoll());
                } else {
                    System.out.println("This round was a tie!");
                }
            } else {
                ge.showResults();
                ge.saveGameToFile("game_results.txt");
                break;
            }
        }
        sc.close();
    }
}
