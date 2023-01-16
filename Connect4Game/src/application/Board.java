package application;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Board {

	private char[][] board;
	private int row;
	private int col;
	//creates the board
	public Board() {
		board = new char[6][7];	
		for(int i = 0; i < board.length; i++) {
			for(int j = 0; j < board[0].length; j++) {
				board[i][j] = ' ';
			}
		}		
		row = 0;
		col = 0;
	}
	public char[][] getBoard(){
		return board;
	}
	public int getCol() {
		return col;
	}
	
	public int getRow() {
		return row;
	}
	public void displayBoard() {
		for(int i = 0; i < board.length; i++) {
			for(int j = 0; j < board[0].length; j++) {
				System.out.print("|"+ board[i][j]);
			}
			System.out.print("|");
			System.out.println();
		}
		System.out.println(" 1 2 3 4 5 6 7");
	}
	
	public void setRow(int input) {
		row = input;
	}
	
	public void setCol(int input) {
		col = input;
	}
	//Determines if the column is full
	private boolean isValid(int col) throws ArrayIndexOutOfBoundsException{
		try {
			if(board[0][col] == ' ') {
				row = 5;
				while(board[row][col] != ' ') {
					row--;
				}
				return true;
			}
			return false;
		}
		catch(ArrayIndexOutOfBoundsException e) {
			System.out.println("Invalid Column!\n");
			return false;
		}
	}
	//returns list of valid columns
	private List<Integer> getValidLocation() {
		List<Integer> validLocations = new ArrayList<Integer>();
		for(int i = 0; i<7; i++) {
			if(isValid(i)) {
				validLocations.add(i);
			}
		}
		return validLocations;
	}
	//updates the real board
	public void updateBoard(char player) {
		board[row][col] = player;
		displayBoard();
	}
	//updates the temp board
	private char[][] updateBoard(char[][] tempBoard,char player, int col) {
		if(row >= 0) {
			tempBoard[row][col] = player;
		}
		return tempBoard;
	}
	
	private int getOpenRow(char[][] tempBoard, int col) {
		for(int i = 5; i >= 0; i--) {
			if(tempBoard[i][col] == ' ') {
				return i;
			}
		}
		return -1;
	}
	
	public boolean checkWinner(char[][] board, char player) {
		if(col < 0) {
			return false;
		}
		//checks vertical for win
		int count = 0;
		for(int i = 0; i < 6; i++) {
			if(board[i][col] == player) {
				count++;
				if(count == 4) {
					return true;
				}
			}
			else {
				count = 0;
			}
		}
		//checks horizontal for win
		count = 0;
		for(int i = 0; i < 7; i++) {
			if(board[row][i] == player) {
				count++;
				if(count == 4) {
					return true;
				}
			}
			else {
				count = 0;
			}
		}
		//checks for positively sloped diagonals
		count = 0;
		int tempRow = row;
		int tempCol = col;
		String diagonal = "";
		while(tempRow >= 0 && tempCol <= 6) {
			diagonal = diagonal + board[tempRow][tempCol];
			tempRow--;
			tempCol++;
		}
		tempRow = row + 1;
		tempCol = col - 1;
		while(tempRow <= 5 && tempCol >= 0) {
			diagonal = board[tempRow][tempCol] + diagonal;
			tempRow++;
			tempCol--;
		}
		for(int i = 0; i < diagonal.length(); i++) {
			if(diagonal.charAt(i) == player) {
				count++;
				if(count == 4) {
					return true;
				}
			}
			else {
				count = 0;
			}
		}
		//checks for negatively sloped diagonal
		count = 0;
		diagonal = "";
		tempRow = row;
		tempCol = col;
		while(tempRow >= 0 && tempCol >= 0) {
			diagonal = diagonal + board[tempRow][tempCol];
			tempRow--;
			tempCol--;
		}
		tempRow = row + 1;
		tempCol = col + 1;
		while(tempRow <= 5 && tempCol <= 6) {
			diagonal = board[tempRow][tempCol] + diagonal;
			tempRow++;
			tempCol++;
		}
		for(int i = 0; i < diagonal.length(); i++) {
			if(diagonal.charAt(i) == player) {
				count++;
				if(count == 4) {
					return true;
				}
			}
			else {
				count = 0;
			}
		}
		
		return false;
	}
	//Determines points awarded for a move using 4 slots at a time
	private int evaluateWindow(List<Character> window, char player) {
		int score = 0;
		char oppPiece = 'R';
		if(player == 'R') {
			oppPiece = 'Y';
		}
		int countOpp = 0;
		int countPlayer = 0;
		int countEmpty = 0;
		for(int i = 0; i < window.size(); i++) {
			if(window.get(i) == oppPiece) {
				countOpp +=1;
			}
			else if(window.get(i) == player) {
				countPlayer +=1;
			}
			else {
				countEmpty +=1;
			}
		}
		if(countPlayer == 4) {
			score+=1000000000;
		}
		else if(countPlayer == 3 && countEmpty == 1) {
			score += 50;
		}
		else if(countPlayer == 2 && countEmpty == 2) {
			score += 10;
		}
		if(countOpp == 3 && countEmpty == 1) {
			score -=50;
		}
		else if(countOpp == 2 && countEmpty == 2) {
			score -= 10;
		}
		
		return score;
	}
	//Determines score of a move
	private int scorePosition(char[][] tempBoard, char player) {
		int score = 0;
		// center column
		int centerCount = 0;
		for(int i = 0; i < 6; i++) {
			if(tempBoard[i][3] == player) {
				centerCount += 1;
			}
		}
		score += centerCount*3;
		
		List<Character> window = new ArrayList<Character>();
		//horizontal
		for(int i = 0; i < 6; i++) {
			char[] rowArr = tempBoard[i];
			for(int j = 0; j < 4; j++) {
				for(int k = j; k < j+4; k++) {
					window.add(rowArr[k]);
				}
				score += evaluateWindow(window, player);
				window.clear();
			}
		}
		
		//vertical
		char[] colArr = new char[6];
		for(int i = 0; i < 5; i++) {
			for(int j = 0; j < 6; j++) {
				colArr[j] = tempBoard[j][i];
			}
			for(int j = 0; j < 3; j++) {
				for(int k = j; k < j+4; k++) {
					window.add(colArr[k]);
				}
				score += evaluateWindow(window, player);
				window.clear();
			}
			
		}
		//negative diagonal
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 4; j++) {
				for(int k = 0; k < 4; k++) {
					window.add(tempBoard[i+k][j+k]);
				}
				score += evaluateWindow(window, player);
				window.clear();
			}
		}
		//positive diagonal
		for(int i = 5; i > 3; i--) {
			for(int j = 0; j < 4; j++) {
				for(int k = 0; k < 4; k++) {
					window.add(tempBoard[i-k][j+k]);
				}
				score += evaluateWindow(window, player);
				window.clear();
			}
		}
		
		return score;
	}
	
	//Does the move end the game
	private boolean isTerminal(char[][]tempBoard) {
		return checkWinner(tempBoard, 'R') || checkWinner(tempBoard, 'Y') || (getValidLocation().size() == 0);
	}
	//Algorithm for AI
	public double[] minimax(char[][] tempBoard, int depth, double alpha, double beta, boolean maximizingPlayer) {
		List<Integer> validLocations = getValidLocation();
		double[] returnArr;
		boolean terminal = isTerminal(tempBoard);
		if(depth == 0 || terminal) {
			if(terminal) {
				if(checkWinner(tempBoard, 'R')) {
					returnArr = new double[] {-1, -1000000000};
					return returnArr;
				}
				else if(checkWinner(tempBoard, 'Y')) {
					returnArr = new double[] {-1, 1000000000};
					return returnArr;
				}
				else {
					returnArr = new double[] {-1, 0};
					return returnArr;
				}
			}
			else {
				returnArr = new double[] {-1, scorePosition(tempBoard, 'Y')};
				return returnArr;
			}
		}
		if(maximizingPlayer) {
			double val = Double.NEGATIVE_INFINITY;
			boolean cont = false;
			int col = 0;
			while(!cont) {
				Random rand = new Random();
				col = validLocations.get(rand.nextInt(validLocations.size()));
				if(validLocations.contains(col)) {
					cont = true;
				}
			}
			for(int i = 0; i < validLocations.size(); i++) {
				char[][] tBoard = new char[6][7];
				for(int j = 0; j < tBoard.length; j++) {
					for(int k = 0; k < tBoard[0].length; k++) {
						tBoard[j][k] = tempBoard[j][k];
					}
				}
				setRow(getOpenRow(tBoard, validLocations.get(i)));
				tBoard = updateBoard(tBoard, 'Y', validLocations.get(i));
				double newScore = minimax(tBoard, depth-1, alpha, beta, false)[1];
				if(newScore > val) {	
					val = newScore;
					col = validLocations.get(i);
				}
				alpha = Math.max(alpha, val);
				if(alpha >= beta) {	
					break;
				}
			}
			returnArr = new double[] {col + 1, val};
			return returnArr;
		}
		else {
			double val = Double.POSITIVE_INFINITY;
			boolean cont = false;
			int col = 0;
			while(!cont) {
				Random rand = new Random();
				col = validLocations.get(rand.nextInt(validLocations.size()));
				if(validLocations.contains(col)) {
					cont = true;
				}
			}
			for(int i = 0; i < validLocations.size(); i++) {
				char[][] tBoard = new char[6][7];
				for(int j = 0; j < tBoard.length; j++) {
					for(int k = 0; k < tBoard[0].length; k++) {
						tBoard[j][k] = tempBoard[j][k];
					}
				}
				setRow(getOpenRow(tBoard, validLocations.get(i)));
				tBoard = updateBoard(tBoard, 'R', validLocations.get(i));
				double newScore = minimax(tBoard, depth-1, alpha, beta, true)[1];
				if(newScore < val) {
					val = newScore;
					col = validLocations.get(i);
				}
				beta = Math.min(beta, val);
				if(alpha >= beta) {
					break;
				}
			}
			returnArr = new double[] {col + 1, val};
			return returnArr;			
		}
		
	}
}