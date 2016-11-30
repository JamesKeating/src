import java.util.ArrayList;

/**
 * Created by siavj on 15/11/2016.
 */
public class Pattern {

    private String[] pattern;
    private int score;
    private boolean symetric = false;

    public Pattern(String pat, int score){
        if (new StringBuilder(pat).reverse().toString().equals(pat))
            symetric = true;

        this.pattern = pat.split("-");
        for (String part : this.pattern){
            if (!part.equals("P") &&  !part.equals("E") && !part.equals("O")){
                System.out.println("Error invalid Pattern " + part);
                System.exit(0);
            }
        }
        this.score = score;
    }

    public int findPattern(Position position, Player p){

        int[][] orientations = {{0, 1},{1, -1}, {1, 0} , {0, -1}, {-1, 1}, {-1, 0}};
        int cap = 6;
        if(symetric)
            cap = 3;

        int score = 0;
        for(int row = 1; row < 10; row++){
            for (int col = 1; col < 10; col++){
                for (int i = 0; i < cap; i++)
                    score += checkOrientation(position.getPosition(), orientations[i], row, col, p);
            }
        }
        return score;
    }

    private int checkOrientation(Cell[][] board, int[] orientation, int row, int col, Player p){
        String matchCheck;
        for (String pat : pattern) {
            if (pat.equals("E")){
                if (!board[row][col].getPlayable()) {
                    return 0;
                }
                matchCheck = " ";
            }

            else
                matchCheck = p.getPlayerSymbol();

            if (board[row][col].toString().equals(matchCheck)){
                row += orientation[0];
                col += orientation[1];
            }

            else
                return 0;
        }
        return this.score;
    }
}
