package application;

import java.util.Random;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class Connect4GUI extends Application {
    private static final int NUM_ROWS = 6;
    private static final int NUM_COLS = 7;
    
    private Button[][] buttons;
    private int[][] board;
    private boolean playerTurn;
    
    private Board Board = new Board();
    private int move;
    //Creates the connect4board
    @Override
    public void start(Stage primaryStage) {
		Random rand = new Random();
		int turn = rand.nextInt((1-0) + 1);
        VBox root = new VBox();
        Label label;
        if(turn == 0) {
        	label = new Label("Yellow's turn");
        	playerTurn = false;
        }
        else {
        	label = new Label("Red's turn");
        	playerTurn = true;
        }
        label.setFont(new Font("Arial", 24));
        GridPane grid = new GridPane();
        grid.setStyle("-fx-background-color: blue;");
        buttons = new Button[NUM_ROWS][NUM_COLS];
        board = new int[NUM_ROWS][NUM_COLS];
        
        for (int row = 0; row < NUM_ROWS; row++) {
            for (int col = 0; col < NUM_COLS; col++) {
                buttons[row][col] = new Button();
                buttons[row][col].setShape(new Circle(20));
                buttons[row][col].setMinSize(40, 40);
                buttons[row][col].setMaxSize(40, 40);
                int c = col;
                int r = row;
                //only the buttons on the top row can be clicked to drop a piece
                if (row == 0) {
	                buttons[row][col].setOnAction(e -> handleButtonClick(r, c, label));
                }
	            grid.add(buttons[row][col], col, row);
            }
        }
        root.getChildren().addAll(label, grid);
        Scene scene = new Scene(root, 280, 270);
        primaryStage.setTitle("Connect 4");
        primaryStage.setScene(scene);
        primaryStage.show();
        if(!playerTurn) {
        	aiMove(buttons);
        }

    }
    
    private void handleButtonClick(int row, int col, Label label) {
        if (board[row][col] == 0) {
            int player = 2;
            if(playerTurn) {
            	player = 1;
            }
            //Determines bottom most row to drop
            int insertRow = NUM_ROWS - 1;
            while (insertRow >= 0 && board[insertRow][col] != 0) {
                insertRow--;
            }
            if (insertRow >= 0) {
                board[insertRow][col] = player;
                Button button = buttons[insertRow][col];
                //if AI turn
                if (player == 2) {
					Board.setCol(col);
					Board.setRow(insertRow);
					Board.updateBoard('Y');
                	button.setStyle("-fx-background-color: yellow;");
                    label.setText("Red's Turn");
                    move++;
                }
                //Players turn
                else {
                	Board.setCol(col);
                	Board.setRow(insertRow);
                	Board.updateBoard('R');
                    button.setStyle("-fx-background-color: red;");
                    label.setText("AI's Turn");
                    move++;
            		for (int r = 0; r < NUM_ROWS; r++) {
            		    for (int c = 0; c < NUM_COLS; c++) {
            		        buttons[r][c].setDisable(true);
            		    }
            		}
                }
                boolean winner = false;
                //checks for winner
                if(checkForWin(player)) {
                	winner = true;
                	if(player == 2) {
                		label.setText("AI Wins!!!");
                		for (int r = 0; r < NUM_ROWS; r++) {
                		    for (int c = 0; c < NUM_COLS; c++) {
                		        buttons[r][c].setDisable(true);
                		    }
                		}

                	}
                	else {
                		label.setText("Red Player Wins!!!");
                		for (int r = 0; r < NUM_ROWS; r++) {
                		    for (int c = 0; c < NUM_COLS; c++) {
                		        buttons[r][c].setDisable(true);
                		    }
                		}

                	}
                }
                //checks draw
                if(move == 42) {
                	label.setText("Draw");
            		for (int r = 0; r < NUM_ROWS; r++) {
            		    for (int c = 0; c < NUM_COLS; c++) {
            		        buttons[r][c].setDisable(true);
            		    }
            		}
                }
                playerTurn = !playerTurn;
                //Adds pause for AI
                if(!playerTurn && !winner) {
                	PauseTransition pause = new PauseTransition(Duration.seconds(1));
                	pause.setOnFinished(event ->aiMove(buttons));
                	pause.play();
                }
            }
        }
    }
    //Checks for win
    private boolean checkForWin(int player) {
    	char colour = 'R';
    	if(player == 2) {
    		colour = 'Y';
    	}
    	return Board.checkWinner(Board.getBoard(), colour);	
    }
    //Determines where the AI plays
    private void aiMove(Button[][] buttons) {
    	double[] aiMove = Board.minimax(Board.getBoard(), 6, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, true);
		for (int r = 0; r < NUM_ROWS; r++) {
		    for (int c = 0; c < NUM_COLS; c++) {
		        buttons[r][c].setDisable(false);
		    }
		}
    	buttons[0][(int)aiMove[0] - 1].fire();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}