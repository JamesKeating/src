/**
 * Created by siavj on 11/11/2016.
 */
public class GameManager {

    public void playGame(Player p1, Player p2){
        Position position = new Position(p1, p2, "borderValues.txt");

        System.out.println(position);
        while (position.isTerminal() == 0){
            position.makeMove();
            System.out.println(position);
        }
    }
}
