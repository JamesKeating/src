import java.util.*;

/**
 * Created by siavj on 16/11/2016.
 */
public class AI {

    private ArrayList<Pattern> patterns = new ArrayList<>();
    private int searchDepth;
    private int staticEval = 0;
    private HashMap<Integer, int[]> best = new HashMap<>();
    private HashMap<String, Integer> killer = new HashMap<>();
    private HashMap<String, Integer> historic = new HashMap<>();
    private boolean killerFlag = false;
    private boolean historicFlag = false;
    private int[] swapedCell = {0,0};

    public AI(int searchDepth){

        this.searchDepth = searchDepth;
        patterns.add(new Pattern("E-P-P-E-P", 100));
        patterns.add(new Pattern("E-P-E-E-P", 50));
        patterns.add(new Pattern("E-P-P-E", -150));
        patterns.add(new Pattern("E-P-E-P-E", -400));
    }

    public AI(int searchDepth, String huristic){
        this(searchDepth);
        if (huristic.equals("killer")){
            killerFlag = true;
        }
        else if (huristic.equals("historic")) {
            historicFlag = true;
        }

    }

    public int getStaticEval() {
        return staticEval;
    }

    public void makeMove(Position position){
        killer.clear();
        historic.clear();
        best.clear();

        negaMax(position, searchDepth, -Integer.MAX_VALUE, Integer.MAX_VALUE);
        position.getPosition()[best.get(searchDepth)[0]]
                [best.get(searchDepth)[1]].setCellValue(position.getCurrentPlayer().getPlayerSymbol());
        position.getPosition()[best.get(searchDepth)[0]][best.get(searchDepth)[1]].setPlayable(false);
    }

    //return value of gamestate for player with next move
    public int staticEvaluation(Position position){
        int playerWithNextMoveScore = 0;
        int opponentScore = 0;

        for (Pattern p : this.patterns) {
            playerWithNextMoveScore += p.findPattern(position, position.getCurrentPlayer());
            opponentScore += p.findPattern(position, position.getNextPlayer());
        }
        return playerWithNextMoveScore - opponentScore;
    }

    private int negaMax(Position position, int height, int alpha, int beta) {

        if (height == 0) {
            staticEval++;
            return staticEvaluation(position);
        }

        int temp;
        ArrayList<int[]> possibleMoves = new ArrayList<>();
        String key;
        best.putIfAbsent(height, new int[]{0,0,-Integer.MAX_VALUE});

        //get all possible moves
        for (int row = 1; row < 10; row++) {
            for (int col = 1; col < 10; col++) {
                if (position.getPosition()[row][col].getPlayable()) {
                    possibleMoves.add(new int[]{row, col});

                    if (killerFlag){
                        key = (row + "-" + col + "-" + height);
                        killer.putIfAbsent(key, 0);
                    }
                    else if (historicFlag){
                        key = (row + "-" + col);
                        historic.putIfAbsent(key, 0);
                    }
                }
            }
        }
        //add swap move to possible if can be done
        if (position.getSwap() == 1){
            swapedCell = position.takeOpening();
            possibleMoves.add(swapedCell);

            if (killerFlag){
                key = (swapedCell[0] + "-" + swapedCell[1] + "-" + height);
                killer.putIfAbsent(key, 0);
            }
            else if (historicFlag){
                key = (swapedCell[0] + "-" + swapedCell[1]);
                historic.putIfAbsent(key, 0);
            }
        }

        //if there were no moves
        if (possibleMoves.size() == 0) {
            staticEval++;
            return staticEvaluation(position);
        }

        //sort moves by killer heuristic
        if (killerFlag){
            Collections.sort(possibleMoves, (b, a) -> killer.get((String) (a[0] + "-" + a[1] + "-" + height))
                - killer.get((String) (b[0] + "-" + b[1]+"-"+ height)));

        }

        //sort moves by historic heuristic
        else if (historicFlag){
            Collections.sort(possibleMoves, (b, a) -> historic.get((String)(a[0] + "-" + a[1]))
                    - historic.get((String)(b[0] + "-" + b[1])));

        }

        //negamax for each possible move
        for (int[] move: possibleMoves) {

            //make move
            position.getPosition()[move[0]][move[1]].setCellValue(position.getCurrentPlayer().getPlayerSymbol());
            position.getPosition()[move[0]][move[1]].setPlayable(false);
            position.setSwap(position.getSwap()+1);
            position.updatePlayers();

            //check if its terminal move (isTerminal returns the value of position if its terminal no need for negaMax)
            int terminalCheck = position.isTerminal();
            if (terminalCheck != 0) {
                temp = terminalCheck;
            }
            else
                temp = -negaMax(position, height - 1, -beta, -alpha);

            //unmake move (was initally creating and uncreating positions but this is less expensive)
            if (swapedCell == move){
                position.getPosition()[move[0]][move[1]].setCellValue(position.getCurrentPlayer().getPlayerSymbol());
                position.getPosition()[move[0]][move[1]].setPlayable(false);
            }
            else {
                position.getPosition()[move[0]][move[1]].setCellValue(" ");
                position.getPosition()[move[0]][move[1]].setPlayable(true);
            }
            position.setSwap(position.getSwap()-1);
            position.updatePlayers();


            if (temp >= beta) {

                //if killer increase count for move at this height that caused prune
                if (killerFlag) {
                    if (temp >best.get(height)[2])
                        best.put(height, new int[]{move[0], move[1], temp});
                    else {
                        key = (best.get(height)[0] + "-" + best.get(height)[1] + "-" + height);
                        killer.put(key, killer.get(key) + 1);
                    }
                }

                //if historic increase count for move that caused prune
                else if (historicFlag) {
                    if (temp >best.get(height)[2])
                        best.put(height, new int[]{move[0], move[1], temp});
                    else {
                        key = (best.get(height)[0] + "-" + best.get(height)[1]);
                        historic.put(key, historic.get(key) + 1);
                    }
                }

                return temp;
            }

            //if move is better than current best move update current best move
            alpha = Math.max(temp, alpha);
            if (alpha > best.get(height)[2]) {
                best.put(height, new int[]{move[0], move[1], alpha});
            }
        }
        return alpha;
    }
}
