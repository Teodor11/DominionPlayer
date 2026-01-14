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

import static java.lang.Math.sqrt;

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
    private int countStones(Board board, Color color) {
        int count = 0;
        int size = board.getSize();
        int edge = (int) Math.sqrt(size);
        for (int i = 0; i < edge; i++) {
            for (int j = 0; j < edge; j++) {
                Color field = board.getState(i, j);
                if (field == color) {
                    count++;
                }
            }
        }
        return count;
    }


    /**
     * Funkcja oblicza aktualną liczbę pionków przy krawędziach / w narożnikach danego koloru na planszy.
     * Pionki przy krawędziach są liczone jako 1, a w narożnikach jako 2.
     */
    private int countEdgeStones(Board board, Color color) {
        int count = 0;
        int size = board.getSize();
        int edge = (int) Math.sqrt(size);

        for (int i = 0; i < edge; i++) {
            Color field1 = board.getState(i, 0);
            Color field2 = board.getState(i, edge-1);
            Color field3 = board.getState(0, i);
            Color field4 = board.getState(edge-1, i);

            if (field1 == color) { count++; }
            if (field2 == color) { count++; }
            if (field3 == color) { count++; }
            if (field4 == color) { count++; }
        }

        return count;
    }

    private int getScore(Board board, Color color){
        return countEdgeStones(board, color) + countStones(board, color);
    }

    private int evaluateBoard(Board board, Color playerColor, Color enemyColor) {
        int score = getScore(board, playerColor);

        //if(playerColor == getWinner(playerColor)) return Integer.MAX_VALUE;

        List<Move> enemyMoves = board.getMovesFor(enemyColor);
        for (Move enemyMove : enemyMoves){
            board.doMove(enemyMove);
            score -= getScore(board, enemyColor);
            
            List<Move> moves = board.getMovesFor(playerColor);
            for (Move move : moves){
                board.doMove(move);
                score += getScore(board, playerColor);
                board.undoMove(move);
            }

            board.undoMove(enemyMove);
        }
        return score;
    }
}
