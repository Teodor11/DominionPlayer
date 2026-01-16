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
        Color enemyColor = Player.getOpponent(playerColor);
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

        moves.sort((m1, m2) -> Integer.compare(moveScores.get(m2), moveScores.get(m1)));

        Move bestMove = moves.getFirst();
        int bestScore = Integer.MIN_VALUE;

        int MAX_MOVES = 50;
        int depth = 2;

        int totalStones = ((TypicalBoard) b).countStones(playerColor) + ((TypicalBoard) b).countStones(enemyColor);
        int MAX_DEPTH = (totalStones < 0.6 * Math.pow(b.getSize(), 2)) ? 4 : 7;

        while (depth < MAX_DEPTH) {
            for (int i = 0; i < Math.min(moves.size(), MAX_MOVES); i++) {
                Move move = moves.get(i);
                Board boardCopy = b.clone();
                boardCopy.doMove(move);

                int score = minimax(boardCopy, depth - 1, false,
                        playerColor, enemyColor, Integer.MIN_VALUE, Integer.MAX_VALUE);

                if (score > bestScore) {
                    bestScore = score;
                    bestMove = move;
                }
            }
            depth++;
        }

        return bestMove;
    }

    /**
     * Funkcja oblicza aktualną liczbę pionków przy krawędziach / w narożnikach danego koloru na planszy.
     * Pionki przy krawędziach są liczone jako 1, a w narożnikach jako 30.
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

        if (board.getState(0, 0) == color)           { count += 30; }
        if (board.getState(0, size-1) == color)      { count += 30; }
        if (board.getState(size-1, 0) == color)      { count += 30; }
        if (board.getState(size-1, size-1) == color) { count += 30; }

        int[][] adj = {
                {1, 0}, {0, 1}, {1, 1},
                {1, size - 1}, {0, size - 2}, {1, size - 2},
                {size - 2, 0}, {size - 1, 1}, {size - 2, 1},
                {size - 2, size - 1}, {size - 1, size - 2}, {size - 2, size - 2}
        };
        for (int[] p : adj) {
            if (board.getState(p[0], p[1]) == color) count -= 20;
        }

        return count;
    }


    private int minimax(Board board, int depth, boolean isMaxPlayer, Color playerColor,
            Color enemyColor, int alpha, int beta) {

        List<Move> playerMoves = board.getMovesFor(playerColor);
        List<Move> enemyMoves = board.getMovesFor(enemyColor);

        if (depth == 0 || (playerMoves.isEmpty() && enemyMoves.isEmpty())) {
            return getScore(board, playerColor, enemyColor, playerMoves.size(), enemyMoves.size());
        }

        List<Move> moves = isMaxPlayer ? playerMoves : enemyMoves;

        if (moves.isEmpty()) {
            return minimax(board, depth-1, !isMaxPlayer, playerColor, enemyColor, alpha, beta);
        }

        if (isMaxPlayer) {
            int localMax = Integer.MIN_VALUE;
            for (Move m: moves) {
                Board boardCopy = board.clone();
                boardCopy.doMove(m);
                int score = minimax(boardCopy, depth-1, false, playerColor, enemyColor, alpha, beta);

                localMax = Math.max(localMax, score);
                alpha = Math.max(alpha, score);
                if (alpha >= beta) break;
            }
            return localMax;
        }
        else {
            int localMin = Integer.MAX_VALUE;
            for (Move m: moves) {
                Board boardCopy = board.clone();
                boardCopy.doMove(m);
                int score = minimax(boardCopy, depth-1, true, playerColor, enemyColor, alpha, beta);

                localMin = Math.min(localMin, score);
                beta = Math.min(beta, score);
                if (alpha >= beta) break;
            }
            return localMin;
        }
    }


    /**
     * Funkcja zwraca ocenę stanu planszy na podstawie liczby pionków i ich rozmiesszczenia
     */
    private int getScore(Board board, Color playerColor, Color enemyColor, int playerMoves, int enemyMoves) {
        int playerStones = ((TypicalBoard) board).countStones(playerColor);
        int enemyStones =  ((TypicalBoard) board).countStones(enemyColor);
        int stonesDiff = playerStones - enemyStones;

        int movesDiff = playerMoves - enemyMoves;
        int edgesDiff = countEdgeStones(board, playerColor) - countEdgeStones(board, enemyColor);

        int filled = playerStones + enemyStones;
        int size = (int) Math.pow(board.getSize(), 2);

        if (filled < 0.3 * size) {
            return 50 * movesDiff + 15 * edgesDiff;
        }
        else if (filled < 0.55 * size) {
            return 30 * movesDiff + 20 * edgesDiff + 10 * stonesDiff;
        }
        else {
            return 100 * stonesDiff + 50 * edgesDiff;
        }
    }
}
