import com.sun.org.apache.regexp.internal.RE;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

import static com.sun.org.apache.xml.internal.serialize.OutputFormat.Defaults.Indent;

/**
 * Created by siavj on 10/11/2016.
 */
public class Position {

    private boolean swap = true;
    private Cell[][] position;
    private Player currentPlayer, nextPlayer;


    public Position(Player currentPlayer, Player nextPlayer, String fileName) {
        int boardSize = 11;
        this.position = new Cell[boardSize][boardSize];
        setBorderValues(this.position, fileName);
        this.currentPlayer = currentPlayer;
        this.nextPlayer = nextPlayer;

    }

    public Position(Position another) {
        int boardSize = 11;
        this.position = new Cell[boardSize][boardSize];
        for (int row = 0; row < 11; row++){
            for (int col = 0; col < 11; col++){
                this.position[row][col] = new Cell(another.getPosition()[row][col]);
            }
        }
        this.swap = another.swap;
        if (!another.getCurrentPlayer().isHuman()){
            this.currentPlayer = new Player(another.getCurrentPlayer().getPlayerName(),
                another.getCurrentPlayer().getPlayerSymbol(), another.getCurrentPlayer().getPlayerAI());
        }
        else
            this.currentPlayer = new Player(another.getCurrentPlayer().getPlayerName(),
                    another.getCurrentPlayer().getPlayerSymbol());

        if(!another.getNextPlayer().isHuman()){
            this.nextPlayer = new Player(another.getNextPlayer().getPlayerName(),
                another.getNextPlayer().getPlayerSymbol(), another.getNextPlayer().getPlayerAI());
        }
        else
            this.nextPlayer = new Player(another.getNextPlayer().getPlayerName(),
                    another.getNextPlayer().getPlayerSymbol());
    }

    public Cell[][] getPosition() {
        return position;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Player getNextPlayer() {
        return nextPlayer;
    }

    private void setBorderValues(Cell[][] cells, String filename) {
        int row = 0, col = 0;
        try (BufferedReader br = new BufferedReader(new FileReader("D:\\AI2\\src\\borderValues"))) {//fix with file name
            String line;
            while ((line = br.readLine()) != null) {
                for (String value : line.split(" ")) {
                    cells[row][col] = new Cell();
                    cells[row][col].setCellBorder(Integer.valueOf(value));
                    if (cells[row][col].getCellBorder() == 7)
                        cells[row][col].setPlayable(true);
                    col++;
                }
                row++;
                col = 0;
            }
        } catch (Exception e) {
            System.out.println("Error reading file");
        }

    }

    public String toString() {

        String indent = "  ", positionAsString = "", newLine = "\n";
        int[] possibleBorderValues1 = {4, 5, 7};
        int[] possibleBorderValues2 = {2, 3, 7};
        int[] possibleBorderValues3 = {1, 3, 5, 7};

        for (Cell[] row : position) {
            positionAsString += indent + " ";
            for (Cell cell : row) {
                positionAsString += cell.toString() + " ";
                positionAsString += toStringHelper(possibleBorderValues1, cell, "|");
            }

            positionAsString += newLine + indent;
            for (Cell cell : row) {
                positionAsString += toStringHelper(possibleBorderValues2, cell, "\\");
                positionAsString += toStringHelper(possibleBorderValues3, cell, "/");
            }
            positionAsString += newLine;
            indent += "  ";
        }
        //positionAsString += getGameStatusMessage();
        return positionAsString;
    }

    private String toStringHelper(int[] val, Cell cell, String s) {
        for (int i : val)
            if (cell.getCellBorder() == i) {
                return s + " ";
            }
        return "  ";
    }

    public void makeMove(){
        String move;
        //System.out.println(currentPlayer.getPlayerName() +"=========" + currentPlayer.isHuman());
        if (currentPlayer.isHuman()) {
            Scanner reader = new Scanner(System.in);  // Reading from System.in
            System.out.println(currentPlayer.getPlayerName() + " please enter your move:  ");
            move = reader.nextLine();
        }

        else {
            this.currentPlayer.getPlayerAI().makeMove(this);
            updatePlayers();
            return;
        }



        Cell x = validateMove(move);
        if (x != null){
            x.setCellValue(currentPlayer.getPlayerSymbol());
            x.setPlayable(false);
            updatePlayers();
        }

        else {
            if (move.equals("X") && swap) {
                if (takeOpening(currentPlayer))
                    return;
            }
            System.out.println("Invlaid move try again");
            makeMove();
        }


    }

    private boolean takeOpening(Player player){
        Cell opening = null;
        int startCol , endCol = 10;
        for (Cell[] row : position) {
            for(Cell cell : row){
                if(cell.toString().equals(" ")){
                    if (opening != null)
                        return false;
                    opening = cell;
                }
            }
        }
        if (opening != null){
            opening.setCellValue(player.getPlayerSymbol());
            swap = false;
            return true;
        }
        return false;
    }

    private Cell validateMove(String input){
        if (input.length() != 2){
            System.out.println("Error too many characters");
            return null;
        }

        int row = (int)input.charAt(0)- 64;
        int col = Character.getNumericValue(input.charAt(1));

        if (row < 6)
            col += 5 - row;

        if (row >= 1 && row <= 10 && col < 10){
            if (this.position[row][col].getPlayable()) {
                return this.position[row][col];
            }
        }
        System.out.println("invalid values");
        return null;

    }

    public int isTerminal(){

        if  (new Pattern("O-P-P-P-O", 100).findPattern(this, nextPlayer) > 0){
            return -Integer.MAX_VALUE+1;
        }

        if (new Pattern("P-P-P-P", 100).findPattern(this, nextPlayer) > 0) {
            return Integer.MAX_VALUE;
        }

        return 0;
    }

    public void updatePlayers(){
        Player temp = currentPlayer;
        currentPlayer = nextPlayer;
        nextPlayer = temp;
    }

}
