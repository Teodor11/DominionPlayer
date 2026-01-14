/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package put.ai.games.ESZTIKplayer;

import java.util.List;
import java.util.Random;

import put.ai.games.game.Board;
import put.ai.games.game.Move;
import put.ai.games.game.Player;

public class ESZTIKplayer extends Player {

    private Random random = new Random(0xdeadbeef);


    @Override
    public String getName() {
        return "Eryk Zinkowski 160114 Teodor Kunze 160231";
    }


    @Override
    public Move nextMove(Board b) {
        Color playerColor = getColor();
        Color enemyColor = Player.getOpponent(getColor());
        List<Move> moves = b.getMovesFor(playerColor);
        Move bestMove = null;
        int bestScore = Integer.MIN_VALUE;

        for (Move move : moves) {
            Board copy = ((Board) b).clone();
            copy.doMove(move);

            int score = evaluateBoard(copy, playerColor, enemyColor);

            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }

        if (bestScore == Integer.MIN_VALUE) {
            bestMove = moves.get(random.nextInt(moves.size())); //z orginalnego gracza naiwnego
        }

        return bestMove;
    }

    /**
     * Funkcja oblicza aktualną liczbę wszystkich pionków danego koloru na planszy
     */
    private int calculateStones(Board board, Color color) {


        return 0;
    }


    /**
     * Funkcja oblicza aktualną liczbę pionków przy krawędziach / w narożnikach danego koloru na planszy.
     * Pionki przy krawędziach są liczone jako 1, a w narożnikach jako 2.
     */
    private int calculateEdgeStones(Board board, Color color) {


        return 0;
    }

    private int getscore(Board board, Color color){
        return calculateEdgeStones(board, color)+calculateStones(board, color);
    }

    private int evaluateBoard(Board board, Color playerColor, Color enemyColor) {
        int score = getscore(board, playerColor);

        //if(playerColor == getWinner(playerColor)) return Integer.MAX_VALUE;

        List<Move> enemyMoves = board.getMovesFor(enemyColor);
        for (Move enemyMove : enemyMoves){
            board.doMove(enemyMove);
            score -= getscore(board, enemyColor);
            
            List<Move> moves = board.getMovesFor(playerColor);
            for (Move move : moves){
                board.doMove(move);
                score += getscore(board, playerColor);
                board.undoMove(move);
            }

            board.undoMove(enemyMove);
        }
        return score;
    }
}
