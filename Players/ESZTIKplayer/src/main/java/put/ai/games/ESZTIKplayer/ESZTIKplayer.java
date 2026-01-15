/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package put.ai.games.ESZTIKplayer;

import java.util.List;

import put.ai.games.game.Board;
import put.ai.games.game.Move;
import put.ai.games.game.Player;
import put.ai.games.game.TypicalBoard;

public class ESZTIKplayer extends Player {

    @Override
    public String getName() {
        return "Eryk Zinkowski 160114 Teodor Kunze 160231";
    }

    @Override
    public Move nextMove(Board b) {
        Color playerColor = getColor();
        Color enemyColor = Player.getOpponent(getColor());
        List<Move> moves = b.getMovesFor(playerColor);

        if (moves == null || moves.isEmpty()) {
            return null;
        }

        Move bestMove = moves.get(0);
        int bestScore = Integer.MIN_VALUE;

        for (Move move : moves) {
            Board copy = b.clone();
            copy.doMove(move);

           int score = evaluateBoard(copy, playerColor, enemyColor);

            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }

        return bestMove;
    }

    /**
     * Funkcja oblicza aktualną liczbę pionków przy krawędziach / w narożnikach danego koloru na planszy.
     * Pionki przy krawędziach są liczone jako 1, a w narożnikach jako 2.
     */
    private int countEdgeStones(Board board, Color color) {
        int count = 0;
        int size = board.getSize();

        for (int i = 0; i < size; i++) {
            Color field1 = board.getState(i, 0);
            Color field2 = board.getState(i, size-1);
            Color field3 = board.getState(0, i);
            Color field4 = board.getState(size-1, i);

            if (field1 == color) { count++; }
            if (field2 == color) { count++; }
            if (field3 == color) { count++; }
            if (field4 == color) { count++; }
        }
        return count;
    }

    /**
     * Funkcja zwraca ocene stanu planszy na podstawie liczby pionków i ich rozmiesszczenia
     */
    private int getScore(Board board, Color playerColor, Color enemyColor){
        int playerStones = ((TypicalBoard) board).countStones(playerColor);
        int enemyStones =  ((TypicalBoard) board).countStones(enemyColor);
        int playerEdgeStones = countEdgeStones(board, playerColor);
        return 100 * playerStones - 85 * enemyStones - 20 * playerEdgeStones;
    }

    private int evaluateBoard(Board board, Color playerColor, Color enemyColor) {
        Board boardCopy = board.clone();
        int score = getScore(boardCopy, playerColor, enemyColor);

        List<Move> enemyMoves = boardCopy.getMovesFor(enemyColor);
        if(enemyMoves.isEmpty()) return score;

        int enemyBestMove = Integer.MAX_VALUE;
        for (Move enemyMove : enemyMoves) {
            Board boardAfterEnemyMove = boardCopy.clone();
            boardAfterEnemyMove.doMove(enemyMove);

            int playerBestMove = Integer.MIN_VALUE;
            List<Move> playerMoves = boardAfterEnemyMove.getMovesFor(playerColor);

            if (playerMoves.isEmpty()) {
                playerBestMove = getScore(boardAfterEnemyMove, playerColor, enemyColor);
            }
            else {
                for (Move move : playerMoves) {
                    Board boardAfterPlayerMove = boardAfterEnemyMove.clone();
                    boardAfterPlayerMove.doMove(move);
                    int tempScore = getScore(boardAfterPlayerMove, playerColor, enemyColor);
                    if (tempScore > playerBestMove) {
                        playerBestMove = tempScore;
                    }
                }   
            }
            
            if (enemyBestMove > playerBestMove) {
                enemyBestMove = playerBestMove;
            }
        }
        // return enemyBestMove;
       return score + enemyBestMove;
    }
}
