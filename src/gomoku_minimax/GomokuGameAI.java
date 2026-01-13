package gomoku_minimax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;

public class GomokuGameAI {
    public static class HeuristicCounter {
        int five = 0;
        int openFour = 0;
        int closedFour = 0;
        int openThree = 0;
        int closedThree = 0;
        int openTwo = 0;
        int closedTwo = 0;
    }

    private final int maxDepth;
    GomokuGame game;
    Map<Integer, HeuristicCounter> counters = new HashMap<>();

    public GomokuGameAI(GomokuGame game, int maxDepth) {
        this.game = game;
        this.maxDepth = maxDepth;
        this.counters.put(1, new HeuristicCounter());
        this.counters.put(2, new HeuristicCounter());
    }

    public void simulateMove(int[][] board, int x, int y, int player) {
        updateBoardHeuristicsCount(board, x, y, 1, counters.get(1), true);
        updateBoardHeuristicsCount(board, x, y, 2, counters.get(2), true);
        board[x][y] = player;
        updateBoardHeuristicsCount(board, x, y, 1, counters.get(1), false);
        updateBoardHeuristicsCount(board, x, y, 2, counters.get(2), false);
    }

    public void rebuildHeuristics() {
        counters.put(1, new HeuristicCounter());
        counters.put(2, new HeuristicCounter());
        int[][] boardCopy = game.getBoardCopy();
        int[][] tempBoard = new int[game.boardSize][game.boardSize];
        for (int i = 0; i < game.boardSize; i++) {
            for (int j = 0; j < game.boardSize; j++) {
                if (boardCopy[i][j] != 0) {
                    int player = boardCopy[i][j];
                    simulateMove(tempBoard, i, j, player);
                }
            }
        }
    }

    public void updateBoardHeuristicsCount(int[][] board, int x, int y, int player, HeuristicCounter counter, boolean subtract) {
        // Horizontal
        checkLinePatterns(board[x], player, counter, subtract);

        // Vertical
        int[] column = new int[game.boardSize];
        for (int i = 0; i < game.boardSize; i++) {
            column[i] = board[i][y];
        }
        checkLinePatterns(column, player, counter, subtract);

        // Diagonal down
        int difference = abs(y - x);
        if (difference <= 10) {
            int diagonalLength = game.boardSize - difference;
            int[] diagonalDown = new int[diagonalLength];
            if (y >= x) {
                for (int i = 0; i < diagonalLength; i++) {
                    diagonalDown[i] = board[i][difference + i];
                }
            }
            else {
                for (int i = 0; i < diagonalLength; i++) {
                    diagonalDown[i] = board[difference + i][i];
                }
            }
            checkLinePatterns(diagonalDown, player, counter, subtract);
        }

        // Diagonal Up
        int sum = y + x;
        if (sum + 1 >= 5 && 2 * game.boardSize - 1 - sum >= 5) {
            if (sum >= game.boardSize) {
                int diagonalLength = 2 * game.boardSize - 1 - sum;
                int[] diagonalUp = new int[diagonalLength];
                for (int i = 0; i < diagonalLength; i++) {
                    diagonalUp[i] = board[game.boardSize - 1 - i][game.boardSize - diagonalLength + i];
                }
                checkLinePatterns(diagonalUp, player, counter, subtract);
            }
            else {
                int diagonalLength = sum + 1;
                int[] diagonalUp = new int[diagonalLength];
                for (int i = 0; i < diagonalLength; i++) {
                    diagonalUp[i] = board[diagonalLength - 1 - i][i];
                }
                checkLinePatterns(diagonalUp, player, counter, subtract);
            }
        }
    }

    // Not exact
    public void checkLinePatterns(int[] line, int player, HeuristicCounter counter, boolean subtract) {
        // Check for open four
        if (line.length >= 6) {
            for (int i = 0; i < line.length - 5; i++) {
                int count = 0;
                for (int j = i; j < i + 6; j++) {
                    if (line[j] == player) {
                        count++;
                    }
                }
                if (count == 4 && (line[i] == 0 && line[i + 5] == 0)) {
                    counter.openFour += subtract ? -1 : 1;
                }
            }
        }

        // Check for other patterns
        for (int i = 0; i < line.length - 4; i++) {
            int countPlayer = 0;
            int countEmpty = 0;
            for (int j = i; j < i + 5; j++) {
                if (line[j] == player) {
                    countPlayer++;
                } else if (line[j] == 0) {
                    countEmpty++;
                }
            }
            if ((countEmpty + countPlayer) == 5 && countPlayer >= 2) {
                if (countPlayer == 5) {
                    counter.five += subtract ? -1 : 1;
                }
                else if (countPlayer == 4) {
                    counter.closedFour += subtract ? -1 : 1;
                }
                else if (countPlayer == 3) {
                    if (line[i] == 0 && line[i + 4] == 0) {
                        counter.openThree += subtract ? -1 : 1;
                    }
                    else {
                        counter.closedThree += subtract ? -1 : 1;
                    }
                }
                else if (countPlayer == 2) {
                    if (line[i] + line[i + 3] + line[i + 4] == 0 || line[i] + line[i + 1] + line[i + 4] == 0) {
                        counter.openTwo += subtract ? -1 : 1;
                    }
                    else {
                        counter.closedTwo += subtract ? -1 : 1;
                    }
                }
            }
        }
    }

    public int calculateHeuristicsScore(int maxPlayer, int minPlayer, boolean maxTurn) {
        HeuristicCounter maxCounter = counters.get(maxPlayer);
        HeuristicCounter minCounter = counters.get(minPlayer);

        if (maxCounter.five > 0) return 1000000;
        if (minCounter.five > 0) return -1000000;

        if (maxTurn && maxCounter.openFour > 0) return 500000;
        if (!maxTurn && minCounter.openFour > 0) return -500000;

        int score = 0;
        score += maxCounter.openFour * 50000;
        score += minCounter.openFour * -50000;

        score += maxCounter.closedFour * 10000;
        score += minCounter.closedFour * -10000;

        score += maxCounter.openThree * 1000;
        score += minCounter.openThree * -1000;

        score += maxCounter.closedThree * 300;
        score += minCounter.closedThree * -300;

        score += maxCounter.openTwo * 50;
        score += minCounter.openTwo * -50;

        return score;
    }

    public ArrayList<int[]> getActions(int[][] board) {
        int[][] tempBoard = new int[game.boardSize][game.boardSize];
        ArrayList<int[]> actions = new ArrayList<>();
        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}, {1, 1}, {-1, -1}, {1, -1}, {-1, 1}};
        for(int i = 0; i < game.boardSize; i++) {
            for (int j = 0; j < game.boardSize; j++) {
                // Empty cells around non-empty cells are considered as possible actions
                if (board[i][j] != 0) {
                    for (int[] direction : directions) {
                        if (game.isValidPosition(i + direction[0], j + direction[1]) && board[i + direction[0]][j + direction[1]] == 0) {
                            tempBoard[i + direction[0]][j + direction[1]] = 1;
                        }
                        // Up to 2 tiles on all 8 directions
                        if (game.isValidPosition(i + 2 * direction[0], j + 2 * direction[1]) && board[i + 2 * direction[0]][j + 2 * direction[1]] == 0) {
                            if (Math.random() < 0.5) {
                                tempBoard[i + 2 * direction[0]][j + 2 * direction[1]] = 1;
                            }
                        }
                    }
                }
            }
        }
        for (int i = 0; i < game.boardSize; i++) {
            for (int j = 0; j < game.boardSize; j++) {
                if (tempBoard[i][j] == 1) {
                    actions.add(new int[] {i, j});
                }
            }
        }
        return actions;
    }

    public ArrayList<int[]> sortActions(int[][] board, int maxPlayer, int minPlayer, boolean maxTurn, ArrayList<int[]> actions) {
        int n = actions.size();

        int[] scores = new int[n];
        for (int i = 0; i < n; i++) {
            int[] action = actions.get(i);
            simulateMove(board, action[0], action[1], maxPlayer);
            scores[i] = calculateHeuristicsScore(maxPlayer, minPlayer, maxTurn);
            simulateMove(board, action[0], action[1], 0);
        }

        Integer[] indices = new Integer[n];
        for (int i = 0; i < n; i++) {
            indices[i] = i;
        }
        Arrays.sort(indices, (i, j) -> maxTurn ? Integer.compare(scores[j], scores[i]) : Integer.compare(scores[i], scores[j]));

        ArrayList<int[]> sortedActions = new ArrayList<>();
        for (int index : indices) {
            sortedActions.add(actions.get(index));
        }

        return sortedActions;
    }

    public int minimax(int[][] board, int maxPlayer, int minPlayer, boolean maxTurn, int[] prevAction, int depth, int alpha, int beta) {
        HeuristicCounter maxCounter = counters.get(maxPlayer);
        HeuristicCounter minCounter = counters.get(minPlayer);

        boolean terminal = game.checkWin(prevAction[0], prevAction[1], board);
        if (terminal) {
            return !maxTurn ? 1000000 : -1000000;
        }
        if (depth == maxDepth) {
            return calculateHeuristicsScore(maxPlayer, minPlayer, maxTurn);
        }

        if (maxTurn) {
            int maxScore = -10000000;
            ArrayList<int[]> actions = sortActions(board, maxPlayer, minPlayer, maxTurn, getActions(board));
            for (int[] action : actions) {
                simulateMove(board, action[0], action[1], maxPlayer);
                maxScore = max(maxScore, minimax(board, maxPlayer, minPlayer, false, action, depth + 1, alpha, beta));
                simulateMove(board, action[0], action[1], 0);

                // If I find anything larger or equal to the known smallest, I should stop as any larger value found will not be accepted by parent min
                alpha = max(alpha, maxScore);
                if (alpha >= beta) {
                    break;
                }
            }
            return maxScore;
        }
        else {
            int minScore = 10000000;
            ArrayList<int[]> actions = sortActions(board, maxPlayer, minPlayer, false, getActions(board));
            for (int[] action : actions) {
                simulateMove(board, action[0], action[1], minPlayer);
                minScore = min(minScore, minimax(board, maxPlayer, minPlayer, true, action, depth + 1, alpha, beta));
                simulateMove(board, action[0], action[1], 0);

                // If I find anything smaller or equal to known largest, I should stop as any smaller value found will not be accepted by parent max
                beta = min(beta, minScore);
                if (beta <= alpha) {
                    break;
                }
            }
            return minScore;
        }
    }

    public int[] findBestMove() {
        rebuildHeuristics();

        int[][] boardCopy = game.getBoardCopy();
        int currentPlayer = game.getCurrentPlayer();
        int nextPlayer = (currentPlayer == 1) ? 2 : 1;

        int maxScore = -10000000;
        int[] bestMove = new int[2];

        int alpha = -10000000;
        int beta = 10000000;

        ArrayList<int[]> actions = sortActions(boardCopy, currentPlayer, nextPlayer, true, getActions(boardCopy));
        for (int[] action : actions) {
            simulateMove(boardCopy, action[0], action[1], currentPlayer);
            int newScore = minimax(boardCopy, currentPlayer, nextPlayer, false, action,1, alpha, beta);
            System.out.println("Action: " + action[0] + " " + action[1] + " Score: " + newScore);
            if (maxScore < newScore) {
                maxScore = newScore;
                bestMove = new int[]{action[0], action[1]};
            }
            simulateMove(boardCopy, action[0], action[1], 0);

            alpha = max(alpha, maxScore);
        }
        System.out.println();
        return bestMove;
    }
}
