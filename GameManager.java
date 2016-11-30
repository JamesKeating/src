/**
 * Created by siavj on 11/11/2016.
 */
public class GameManager {

    public void playGame(Player p1, Player p2){

        //may need to change fileName depending on its location.
        Position position = new Position(p1, p2, "borderValues");
        int gameState= 0;

        System.out.println(position);
        while (gameState == 0){
            position.makeMove();
            System.out.println(position);
            gameState = position.isTerminal();
        }

        switch (gameState){
            case 1:
                System.out.println("Game was a draw there are no valid moves remaining.");
                break;

            case Integer.MAX_VALUE-2:
                System.out.println("Game over "+ position.getNextPlayer().getPlayerName()+ " has won the game");
                break;

            case -Integer.MAX_VALUE + 1:
                System.out.println("Game over "+ position.getNextPlayer().getPlayerName()+ " has lost the game");
                break;
        }

        if (!p1.isHuman())
            System.out.println(p1.getPlayerName()+ " made "+p1.getPlayerAI().getStaticEval()
                    + " static evaluations this game");

        if (!p2.isHuman())
            System.out.println(p2.getPlayerName()+ " made "+p2.getPlayerAI().getStaticEval()
                    + " static evaluations this game");

    }
}
