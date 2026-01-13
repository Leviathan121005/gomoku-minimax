package gomoku_minimax;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class GomokuGameFX extends Application {
    private static final int boxSize = 40;
    private static final int boardSize = 16;
    private static final int boardLength = 16 * 40;

    private GomokuGame game;
    private GomokuGameAI agent;

    // For readability
    private static StackPane createBlock() {
        StackPane block = new StackPane();
        block.setPrefSize(boxSize, boxSize);
        block.setStyle("-fx-background-color: #FFF2D7;" + "-fx-border-color: black;" + "-fx-border-width: 1.5;");
        return block;
    }

    private static StackPane createSection() {
        StackPane section = new StackPane();
        section.setPrefSize(200, 106);
        return section;
    }

    public void applyMove(Circle[][] circles, int row, int column, int currentPlayer,
                          Label gameWinner, Label gameRound, Label gameCurrentPlayer) {
        circles[row][column].setVisible(true);

        // Set colors to players
        if (currentPlayer == 1) {
            circles[row][column].setFill(Color.BLACK);
            circles[row][column].setStroke(Color.WHITE);
        }
        else if (currentPlayer == 2) {
            circles[row][column].setStroke(Color.BLACK);
            circles[row][column].setFill(Color.WHITE);
        }

        if (game.isGameOver()) {
            if (game.getWinner() == 0) {
                gameWinner.setText("Game over! It's a draw!");
            }
            else {
                gameWinner.setText("Game over! The winner is Player " + game.getWinner() + "!");
            }
        }

        // Increment the turn number and switch to the next player
        if (!game.isGameOver()) {
            gameRound.setText("Round : " + ((game.getTotalMoves() / 2) + 1));
            gameCurrentPlayer.setText("Player " + game.getCurrentPlayer() + "'s " + "Turn");
        }
    }

    @Override
    public void start(Stage stage) {
        // The main grid divided into left (large one for the game grid) and right (smaller one for labels & buttons)
        GridPane containerGrid = new GridPane();
        containerGrid.getColumnConstraints().add(new ColumnConstraints(640));
        containerGrid.getColumnConstraints().add(new ColumnConstraints(200));

        // Create two grids for the game and for the labels & buttons
        StackPane gameGrid = new StackPane();
        GridPane labelsButtonsGrid = new GridPane();

        // Make the outer and inner border width of the game grid similar
        gameGrid.setStyle("-fx-border-width: 1.5;" + "-fx-border-color: black;");

        // Add the game grid and label & buttons grid into the container grid
        containerGrid.add(gameGrid, 0, 0);
        containerGrid.add(labelsButtonsGrid, 1, 0);

        // Game grid setup
        // -------------------------------------------------------------------------------------------------------------
        // Create two types of grid for block grid and circle grid
        GridPane blockGrid = new GridPane();
        GridPane circleGrid = new GridPane();

        // Create a circle 2D array to store and control the circles
        Circle[][] circles = new Circle[boardSize - 1][boardSize - 1];

        // Fill the block and circle grids
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                // Fill the block grids
                StackPane block = createBlock();
                blockGrid.add(block, j, i);

                // Fill the circle grids and assign the circles into the "circles" array
                if (i > 0 && j > 0) {
                    StackPane invisibleBlock = new StackPane();
                    invisibleBlock.setPrefSize(boxSize, boxSize);
                    Circle circle = new Circle(0.4 * boxSize);

                    // All circles are initially invisible
                    circle.setVisible(false);
                    invisibleBlock.getChildren().add(circle);
                    circles[i - 1][j - 1] = circle;
                    circleGrid.add(invisibleBlock, j - 1, i - 1);
                }
            }
        }

        // Place block and circle grids into the game grid
        gameGrid.getChildren().add(blockGrid);
        gameGrid.getChildren().add(circleGrid);

        // Translate the circle grid to position the circles be on the blocks' intersections
        circleGrid.setTranslateX(17);
        circleGrid.setTranslateY(17);
        // -------------------------------------------------------------------------------------------------------------

        // Labels & buttons grid setup
        // -------------------------------------------------------------------------------------------------------------
        // Labels & buttons variables
        Label gameBoardSize = new Label("Board Size : " + (boardSize - 1) + " x " + (boardSize - 1));
        Label gameCurrentPlayer = new Label();
        Label gameRound = new Label("");
        Label gameWinner = new Label();
        Button AIMove = new Button("AI Move");
        Button startNewGame = new Button("Start a New Game");
        Button exit = new Button("Exit");

        // Set font for the labels' & buttons' text
        Font font = new Font("Brawler", 18);
        gameBoardSize.setFont(font);
        gameRound.setFont(font);
        gameCurrentPlayer.setFont(font);
        gameWinner.setFont(font);
        AIMove.setFont(font);
        startNewGame.setFont(font);
        exit.setFont(font);

        // Modifications for the "gameWinner" label
        gameWinner.setWrapText(true);
        gameWinner.setPadding(new Insets(12));
        gameWinner.setTextAlignment(TextAlignment.CENTER);
        gameWinner.setStyle("-fx-font-weight: bold");

        AIMove.setDisable(true);
        AIMove.setVisible(false);

        // Place the labels & buttons into a stack pane for clean layout
        StackPane[] sections = new StackPane[7];
        for (int i = 0; i < 7; i++) {
            sections[i] = createSection();
        }
        sections[0].getChildren().add(gameBoardSize);
        sections[1].getChildren().add(gameCurrentPlayer);
        sections[2].getChildren().add(gameRound);
        sections[3].getChildren().add(gameWinner);
        sections[4].getChildren().add(AIMove);
        sections[5].getChildren().add(startNewGame);
        sections[6].getChildren().add(exit);

        // Place the sections into the labels & buttons grid
        for (int i = 0; i < 7; i++) {
            labelsButtonsGrid.add(sections[i], 0,  i);
        }
        // -------------------------------------------------------------------------------------------------------------

        startNewGame.setOnAction(event -> {
            game = new GomokuGame(boardSize - 1);
            agent = new GomokuGameAI(game, 3);

            gameRound.setText("Round : 1");
            gameCurrentPlayer.setText("Player " + game.getCurrentPlayer() + "'s " + "turn");
            gameWinner.setText("");
            AIMove.setDisable(false);
            AIMove.setVisible(true);

            for (int i = 0; i < boardSize - 1; i++) {
                for (int j = 0; j < boardSize - 1; j++) {
                    circles[i][j].setVisible(false);
                }
            }

            circleGrid.setOnMouseClicked(e -> {
                // Get the StackPane position of the clicked coordinates
                int column = (int) (e.getX() / boxSize);
                int row = (int) (e.getY() / boxSize);

                // Find the distance of the clicked coordinates from the center of a corresponding circle in the StackPane
                double xc = (e.getX() % boxSize) - ((double) boxSize / 2);
                double yc = (e.getY() % boxSize) - ((double) boxSize / 2);

                // Move only if the clicked coordinates corresponds to the circle region in the StackPane
                if (xc * xc + yc * yc <= 0.16 * boxSize * boxSize && (row < 15 && column < 15)) {
                    // Store the game's current player as it will change after move is called
                    int currentPlayer = game.getCurrentPlayer();
                    if (game.move(row, column)) {
                        applyMove(circles, row, column, currentPlayer, gameWinner, gameRound, gameCurrentPlayer);
                    }
                }
                else {
                    System.out.println("Invalid move!");
                }
            });

            AIMove.setOnMouseClicked(e -> {
                int[] bestMove = agent.findBestMove();
                int currentPlayer = game.getCurrentPlayer();
                if (game.move(bestMove[0], bestMove[1])) {
                    applyMove(circles, bestMove[0], bestMove[1], currentPlayer, gameWinner, gameRound, gameCurrentPlayer);
                }
            });
        });

        exit.setOnAction(e -> System.exit(0));

        Scene scene = new Scene(containerGrid, boardLength + 200, boardLength);
        stage.setTitle("Gomoku Game");
        stage.setScene(scene);
        stage.show();
    }
}