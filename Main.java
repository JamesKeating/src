/**
 * Created by siavj on 10/11/2016.
 */
public class Main {

    public static void main(String args[]){

        AI normal = new AI(4);
        AI killer = new AI(4, "killer");
        AI historic = new AI(4, "historic");

        Player human1 = new Player("James", "X");
        Player human2 = new Player("Alan", "O");

        Player NormalRobot = new Player("NormalRobot", "Y", normal);
        Player KillerRobot = new Player("KillerRobot", "X", killer);
        Player HistoricRobot = new Player("HistoricRobot", "O", historic);


        GameManager game = new GameManager();
        game.playGame(KillerRobot, HistoricRobot);

    }
}

