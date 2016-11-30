import java.util.Scanner;

/**
 * Created by siavj on 11/11/2016.
 */
public class Player {

    private String playerName;
    private String playerSymbol;
    private boolean isHuman;
    private AI playerAI;

    public Player(String name, String playerSymbol){
        this.playerName = name;
        this.playerSymbol = playerSymbol;
        this.isHuman = true;

    }

    public Player(String name, String playerSymbol, AI brain){
        this(name, playerSymbol);
        this.isHuman = false;
        playerAI = brain;
    }

    public AI getPlayerAI() {
        return playerAI;
    }

    public boolean isHuman() {
        return isHuman;
    }

    public String getPlayerSymbol() {
        return playerSymbol;
    }

    public String getPlayerName() {
        return playerName;
    }

}
