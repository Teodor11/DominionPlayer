/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package put.ai.games.ESZTIKplayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        Map<Move, Integer> moveScores = new HashMap<>();
        for (Move move : moves) {
            Board boardCopy = b.clone();
            boardCopy.doMove(move);

            int playerMoves = boardCopy.getMovesFor(playerColor).size();
            int enemyMoves = boardCopy.getMovesFor(enemyColor).size();
            moveScores.put(move, getScore(boardCopy, playerColor, enemyColor, playerMoves, enemyMoves));
        }

        moves.sort((m1, m2) -> {
            return Integer.compare(moveScores.get(m2), moveScores.get(m1));
        });

        Move bestMove = moves.getFirst();
        int bestScore = Integer.MIN_VALUE;

        int MAX_MOVES = 50;

        for (int i = 0; i < Math.min(moves.size(), MAX_MOVES); i++) {
            Move move = moves.get(i);
            Board boardCopy = b.clone();
            boardCopy.doMove(move);

            int playerMoves = boardCopy.getMovesFor(playerColor).size();
            int enemyMoves = boardCopy.getMovesFor(enemyColor).size();

            int quickScore = getScore(boardCopy, playerColor, enemyColor, playerMoves, enemyMoves);
            if (quickScore < bestScore - 100) { // skip very bad moves
                continue;
            }

            int score = evaluateBoard(boardCopy, playerColor, enemyColor);

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

        for (int i = 1; i <= size - 2; i++) {
            Color field1 = board.getState(i, 0);
            Color field2 = board.getState(i, size-1);
            Color field3 = board.getState(0, i);
            Color field4 = board.getState(size-1, i);

            if (field1 == color) { count++; }
            if (field2 == color) { count++; }
            if (field3 == color) { count++; }
            if (field4 == color) { count++; }
        }

        if (board.getState(0, 0) == color)           { count += 2; }
        if (board.getState(0, size-1) == color)      { count += 2; }
        if (board.getState(size-1, 0) == color)      { count += 2; }
        if (board.getState(size-1, size-1) == color) { count += 2; }

        return count;
    }

    
    /**
     * Funkcja zwraca ocene stanu planszy na podstawie liczby pionków i ich rozmiesszczenia
     */
    private int getScore(Board board, Color playerColor, Color enemyColor, int playerMoves, int enemyMoves){
        int playerStones = ((TypicalBoard) board).countStones(playerColor);
        int enemyStones =  ((TypicalBoard) board).countStones(enemyColor);
        int playerEdgeStones = countEdgeStones(board, playerColor);

        return 100 * playerStones - 95 * enemyStones - 20 * playerEdgeStones
                + 10 * (playerMoves - enemyMoves);
    }

    private int evaluateBoard(Board board, Color playerColor, Color enemyColor) {
//        Board boardCopy = board.clone();
        int playerMovesCount = board.getMovesFor(playerColor).size();
        List<Move> enemyMoves = board.getMovesFor(enemyColor);
        int score = getScore(board, playerColor, enemyColor, playerMovesCount, enemyMoves.size());


        if (enemyMoves.isEmpty()) {
            return score;
        }

        int enemyBestMove = Integer.MAX_VALUE;
        int beta = score;

        for (Move enemyMove : enemyMoves) {
            Board boardAfterEnemyMove = board.clone();
            boardAfterEnemyMove.doMove(enemyMove);

            int playerBestMove = Integer.MIN_VALUE;
            List<Move> playerMoves = boardAfterEnemyMove.getMovesFor(playerColor);

            if (playerMoves.isEmpty()) {
                playerBestMove = getScore(boardAfterEnemyMove, playerColor, enemyColor,
               0, boardAfterEnemyMove.getMovesFor(enemyColor).size());
            }
            else {
                for (Move move : playerMoves) {
                    boardAfterEnemyMove.doMove(move);

                    int playerMovesCount1 = boardAfterEnemyMove.getMovesFor(playerColor).size();
                    int enemyMovesCount1 = boardAfterEnemyMove.getMovesFor(enemyColor).size();

                    int tempScore = getScore(boardAfterEnemyMove, playerColor, enemyColor, playerMovesCount1, enemyMovesCount1);
                    if (tempScore > playerBestMove) {
                        playerBestMove = tempScore;
                    }
                    boardAfterEnemyMove.undoMove(move);
                }   
            }
            
            if (enemyBestMove > playerBestMove) {
                enemyBestMove = playerBestMove;
                beta = Math.min(beta, enemyBestMove);

                if (beta <= score) {
                    break;
                }
            }
        }

       return enemyBestMove;
    }
}
