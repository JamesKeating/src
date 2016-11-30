import java.util.*;

/**
 * Created by siavj on 16/11/2016.
 */
public class AI {

    private ArrayList<Pattern> patterns = new ArrayList<>();
    private int searchDepth;
    private HashMap<Integer, int[]> best = new HashMap<>();
    private HashMap<String, Integer> killer = new HashMap<>();
    private HashMap<String, Integer> historic = new HashMap<>();
    private boolean killerFlag = false;
    private boolean historicFlag = false;

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

    public void makeMove(Position position){
        killer.clear();
        historic.clear();
        best.clear();

        negaMax(position, searchDepth, -Integer.MAX_VALUE, Integer.MAX_VALUE);
        position.getPosition()[best.get(searchDepth)[0]][best.get(searchDepth)[1]].setCellValue(position.getCurrentPlayer().getPlayerSymbol());
        position.getPosition()[best.get(searchDepth)[0]][best.get(searchDepth)[1]].setPlayable(false);

    }

    public int staticEvaluation(Position position){
        int playerWithNextMoveScore = 0;
        int opponentScore = 0;

        for (Pattern p : this.patterns) {
            playerWithNextMoveScore += p.findPattern(position, position.getCurrentPlayer());
            opponentScore += p.findPattern(position, position.getNextPlayer());
           // System.out.println(position.getCurrentPlayer().getPlayerName()+"  "+playerWithNextMoveScore +"  "+ position.getNextPlayer().getPlayerName()+"  "+ opponentScore);
        }
        return playerWithNextMoveScore - opponentScore;
    }

    private int negaMax(Position position, int height, int alpha, int beta) {

        if (height == 0) {
            return staticEvaluation(position);
//            System.out.println("Leaf Node Evaluated: Level = 0\tValue:"+x+ " " +
//                    "\tAlpha:"+ alpha+ " \tBeta:"+ beta);
        }

        int temp;
        ArrayList<int[]> possibleMoves = new ArrayList<>();
        String key;
        best.putIfAbsent(height, new int[]{0,0,-Integer.MAX_VALUE});


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
        if (possibleMoves.size() == 0)
            return staticEvaluation(position);

        if (killerFlag){
            Collections.sort(possibleMoves, (b, a) -> killer.get((String) (a[0] + "-" + a[1] + "-" + height))
                - killer.get((String) (b[0] + "-" + b[1]+"-"+ height)));
//
//            for (int[] move: possibleMoves) {
//                System.out.println(killer.get(move[0] + "-" + move[1] + "-" + position.getPly()));
//            }
//            System.out.println("================");
        }

        else if (historicFlag){
            Collections.sort(possibleMoves, (b, a) -> historic.get((String)(a[0] + "-" + a[1]))
                    - historic.get((String)(b[0] + "-" + b[1])));

//            for (int[] move: possibleMoves) {
//                System.out.println(historic.get(move[0] + "-" + move[1]));
//            }
//            System.out.println("================");
        }

        for (int[] move: possibleMoves) {
//            Position newPosition = new Position(position);
//            newPosition.getPosition()[move[0]][move[1]].setCellValue(position.getCurrentPlayer().getPlayerSymbol());
//            newPosition.getPosition()[move[0]][move[1]].setPlayable(false);
//            newPosition.updatePlayers();
            position.getPosition()[move[0]][move[1]].setCellValue(position.getCurrentPlayer().getPlayerSymbol());
            position.getPosition()[move[0]][move[1]].setPlayable(false);
            position.updatePlayers();

            int terminalCheck = position.isTerminal();
            if (terminalCheck != 0) {
                temp = terminalCheck;
            }
            else
                temp = -negaMax(position, height - 1, -beta, -alpha);

            position.getPosition()[move[0]][move[1]].setCellValue(" ");
            position.getPosition()[move[0]][move[1]].setPlayable(true);
            position.updatePlayers();

            if (temp >= beta) {
//                System.out.println("==========Interior node Calculation: level = " + height + " \tValue:" + temp + "" +
//                        " \talpha:" + alpha + " \tBeta:" + beta);
//                System.out.println("prune is preformed at this node: "+ temp );
                //find move that caused this prune and +1

                if (killerFlag) {
                    if (temp >best.get(height)[2])
                        best.put(height, new int[]{move[0], move[1], temp});
                    else {
                        key = (best.get(height)[0] + "-" + best.get(height)[1] + "-" + height);
//                        System.out.println(key);
                        killer.put(key, killer.get(key) + 1);
                    }
                }
                else if (historicFlag) {
                    if (temp >best.get(height)[2])
                        best.put(height, new int[]{move[0], move[1], temp});
                    else {
                        key = (best.get(height)[0] + "-" + best.get(height)[1]);
//                        System.out.println(key);
//                        System.out.println(historic.get("5-5"));
                        historic.put(key, historic.get(key) + 1);
                    }
                }


                return temp;
            }
            alpha = Math.max(temp, alpha);
            if (alpha > best.get(height)[2]) {
                best.put(height, new int[]{move[0], move[1], alpha});
            }

        }

//        System.out.println("Interior node Calculation: level = "+height+" \tValue:"+alpha+ " " +
//                "\talpha:"+ alpha+ " \tBeta:"+ beta);
        return alpha;
    }
}
