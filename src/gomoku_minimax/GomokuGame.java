package gomoku_minimax;

public class GomokuGame {
    final int boardSize;
    private int[][] board;
    private int currentPlayer;
    private final int maxMoves;
    private int totalMoves;
    private boolean gameOver;
    private int winner;

    // GomokuGame class variable constructor
    public GomokuGame(int boardSize) {
        // Make sure size of board is in range of 5 to 20
        if (boardSize < 5 || boardSize > 20) {
            throw new IllegalArgumentException("Board size should be between 5 and 20");
        }
        this.boardSize = boardSize;
        this.board = new int[boardSize][boardSize];
        this.currentPlayer = 1;
        this.maxMoves = boardSize * boardSize;
        this.totalMoves = 0;
        this.gameOver = false;
        this.winner = 0;
    }

    // Check if win condition is satisfied
    // x is row, and y is column
    public boolean checkWin(int x, int y, int[][] boardRef) {
        // Array consisting of the 4 possible directions of the win conditions that each contains 2 opposite direction of the same slope
        int[][][] directions = {{{0, 1}, {0, -1}}, {{1, 0}, {-1, 0}}, {{1, 1}, {-1, -1}}, {{1, -1}, {-1, 1}}};

        // Iterate through 4 possible directions
        for (int[][] direction : directions) {
            // Count the current x & y position as one of the five condition to satisfy win condition
            int count = 1;

            // Iterate through 2 opposite directions
            for (int[] opposites : direction) {
                // Set the change of x & y position of the given direction
                int dx = opposites[0];
                int dy = opposites[1];

                // Iterate through the x & y positions of the given direction
                for (int i = 1; i < 5; i++) {
                    int newX = x + i * dx;
                    int newY = y + i * dy;

                    if (!isValidPosition(newX, newY) || boardRef[newX][newY] != boardRef[x][y]) {
                        break;
                    }
                    count++;

                    // End the loop when five consecutive condition are detected
                    if (count >= 5) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Decide whether it is allowed to place a circle on the clicked coordinate
    public boolean move(int x, int y) {
        // Do not respond to anything when the game is over
        if (gameOver) { return false;}

        // Check if the position is available inside the board or if the board is already occupied
        if (!isEmpty(x, y)) { return false;}

        // Update the board
        board[x][y] = currentPlayer;
        totalMoves++;

        // Check if game is draw
        if (totalMoves == maxMoves) {
            gameOver = true;
            return true;
        }

        // Check if win condition satisfied
        if (checkWin(x, y, board)) {
            gameOver = true;
            winner = currentPlayer;
        }

        // Switch players
        if (currentPlayer == 1) {
            currentPlayer = 2;
        }
        else {
            currentPlayer = 1;
        }

        return true;
    }

    // Check if position is still in the scope of the board
    public boolean isValidPosition(int x, int y) {
        return x >= 0 && x < boardSize && y >= 0 && y < boardSize;
    }
    public boolean isEmpty(int x, int y) {
        return (isValidPosition(x, y) && board[x][y] == 0);
    }

    // Accessors
    public int[][] getBoardCopy() {
        int[][] boardCopy = new int[boardSize][boardSize];
        for (int i = 0; i < boardSize; i++) {
            System.arraycopy(this.board[i], 0, boardCopy[i], 0, boardSize);
        }
        return boardCopy;
    }

    public int getTotalMoves() {return totalMoves;}
    public int getWinner() {return winner;}
    public int getCurrentPlayer() {return currentPlayer;}
    public boolean isGameOver() {return gameOver;}
}