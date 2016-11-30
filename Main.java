/**
 * Created by siavj on 10/11/2016.
 */
public class Main {

    public static void main(String args[]){

        //Args: Search Depth, Type
        AI normal = new AI(4);
        AI killer = new AI(4, "killer");
        AI historic = new AI(4, "historic");

        //Args: Name, Player Symbol
        Player human1 = new Player("Arthur", "Z");
        Player human2 = new Player("James", "P");

        //Args: Name, Player Symbol, AI
        Player NormalRobot = new Player("NormalRobot", "Y", normal);
        Player KillerRobot = new Player("KillerRobot", "X", killer);
        Player HistoricRobot = new Player("HistoricRobot", "O", historic);

        GameManager game = new GameManager();

        //uncomment game type you want to play or make your own :)

        //game.playGame(KillerRobot, HistoricRobot);
        game.playGame(human1, KillerRobot);
        //game.playGame(human1, HistoricRobot);
        //game.playGame(human1, NormalRobot);
        //game.playGame(human1, human2);



    }
}

