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

    private int swap = 0;
    private Cell[][] position;
    private Player currentPlayer, nextPlayer;

    public Position(Player currentPlayer, Player nextPlayer, String fileName) {
        int boardSize = 11;
        this.position = new Cell[boardSize][boardSize];
        setBorderValues(this.position, fileName);
        this.currentPlayer = currentPlayer;
        this.nextPlayer = nextPlayer;
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
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {//fix with file name
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
            System.out.println("Error reading file check the file name in your gameManager");
            System.exit(0);
        }

    }



    public void makeMove(){
        String move;

        if (currentPlayer.isHuman()) {
            Scanner reader = new Scanner(System.in);  // Reading from System.in
            System.out.println(currentPlayer.getPlayerName() + " please enter your move:  ");
            move = reader.nextLine();
        }

        else {
            System.out.println(currentPlayer.getPlayerName() + " is making his move now");
            this.currentPlayer.getPlayerAI().makeMove(this);
            updatePlayers();
            swap++;
            return;
        }

        Cell x = validateMove(move);
        if (x != null){
            x.setCellValue(currentPlayer.getPlayerSymbol());
            x.setPlayable(false);
            swap++;
            updatePlayers();
        }

        else {
            if (move.equals("X")) {
                if (swap == 1) {
                    int[] opening = takeOpening();
                    position[opening[0]][opening[1]].setCellValue(currentPlayer.getPlayerSymbol());
                    swap++;
                    updatePlayers();
                    return;
                }
                else
                    System.out.println("Swapping moves is not valid this turn.");
            }
            System.out.println("Invlaid move try again.");
            makeMove();
        }


    }

    public int[] takeOpening(){

        for (int row = 1; row < 10; row++ ) {
            for(int col = 1; col < 10; col++){
                if(!position[row][col].toString().equals(" ")){
                    return new int[]{row, col};
                }
            }
        }
        return null;

    }

    private Cell validateMove(String input){
        if (input.length() != 2){
            if (input.length()>2)
                System.out.println("Error too many characters.");
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
        System.out.println("Invalid values entered. \nPlease try in the format: [A-I][1-9]" +
                "\nNote: all [1-9] values are not valid for every row. ");
        return null;

    }

    //if it returns non 0 game is terminal.
    //dosn't return boolean as value is used as game state value
    public int isTerminal(){
        //terminal last player to move won
        if (new Pattern("P-P-P-P", 100).findPattern(this, nextPlayer) > 0) {
            return Integer.MAX_VALUE-2;
        }

        //terminal last player to move lost
        if  (new Pattern("P-P-P", 100).findPattern(this, nextPlayer) > 0){
            return -Integer.MAX_VALUE+1;
        }

        //not terminal
        for (Cell[] row : position){
            for (Cell col : row){
                if (col.getCellBorder() == 7 && col.getPlayable())
                    return 0;
            }
        }

        //terminal draw no valid moves left
        return 1;
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

        return positionAsString;
    }

    private String toStringHelper(int[] val, Cell cell, String s) {
        for (int i : val)
            if (cell.getCellBorder() == i) {
                return s + " ";
            }
        return "  ";
    }

    public void updatePlayers(){
        Player temp = currentPlayer;
        currentPlayer = nextPlayer;
        nextPlayer = temp;
    }

    public int getSwap() {
        return swap;
    }

    public void setSwap(int swap) {
        this.swap = swap;
    }
}
