package boards;


import halma.HeuristicValues;

import java.awt.Point;
import java.util.ArrayList;

public class Board extends AbstractBoard{
	public Board(int turn,int boardSize,int limitToBestNMoves) {
		boardLength = boardSize;
		boardWidth = boardSize;
		Board.limitToBestNMoves = limitToBestNMoves;
		populateHeuristics();
		for (HeuristicValues v : heuristicValues){
			v.updateHeuristic1(Board.startingDistance);
			v.updateHeuristic2(Board.startingDistance);
		}
		
		board = new int[boardSize][boardSize];
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				board[i][j] = 0;
			}
		}

		// initializes the player spots
		placePlayer1();
		placePlayer2();
		placePlayer3();
		placePlayer4();

		this.turn = turn;
		hopsCounter();
	}

	public Board(int[][] board, int turn) {
		this.board = new int[boardLength][boardWidth];
		populateHeuristics();
		for (int i = 0; i < boardLength; i++) {
			for (int j = 0; j < boardWidth; j++) {
				this.board[i][j] = board[i][j];
				//heuristic calculation stuff
				if (this.board[i][j]!=0){
					int distance = calculateDistance(i,j,this.board[i][j]);
					for(int index=0;index<heuristicValues.size();index++){
						if (this.board[i][j]==index+1){
							heuristicValues.get(index).updateHeuristic1(distance);
							heuristicValues.get(index).updateHeuristic2(distance);
						}else{
							heuristicValues.get(index).updateHeuristic2((Board.startingDistance-distance)/10);
						}
					}
				}
			}
		}
		lastMoved = turn;
		this.turn = turn % 4 + 1;
		hopsCounter();
		isWin();
	}
	
	//would be used for passing... support is there I'm just not sure if it can happen
	public Board(Board b){
		board = new int[boardLength][boardWidth];
		populateHeuristics();
		for(int i=0;i<4;i++){
			heuristicValues.get(i).setHeuristic1(b.heuristicValues.get(i).getHeuristic1());
			heuristicValues.get(i).setHeuristic2(b.heuristicValues.get(i).getHeuristic2());
			heuristicValues.get(i).setHeuristic3(b.heuristicValues.get(i).getHeuristic3());
		}
		for (int i = 0; i < boardLength; i++) {
			for (int j = 0; j < boardWidth; j++) {
				board[i][j] = b.board[i][j];
			}
		}
		lastMoved = b.turn;
		this.turn = b.turn % 4 + 1;
		isWin();
	}
	
	private void hopsCounter(){
		for (int i = 0; i < boardLength; i++) {
			for (int j = 0; j < boardWidth; j++) {
				if(board[i][j]>0){
					countHops(i,j,new ArrayList<Point>(),board[i][j],calculateDistance(i,j,board[i][j]));
				}
			}
		}
	}
	
	private void countHops(int row, int col,ArrayList<Point> visited,int player,int distance){
		visited.add(new Point(row, col));
		//hop up
		if (row - 1 >= 0 && board[row - 1][col] > 0 && row - 2 >= 0
				&& board[row - 2][col] == 0) {
			if (!visited.contains(new Point(row-2,col))) {
				moveTo(row, col, row - 2, col);
				int dist = calculateDistance(row-2,col,player);
				countHops(row - 2, col, visited,player,distance);
				if(dist<distance)
					heuristicValues.get(player-1).updateHeuristic3();
				moveTo(row, col, row - 2, col);
			}
		}
		//hop down
		if (row + 1 <= boardLength - 1 && board[row + 1][col] > 0
				&& row + 2 <= boardLength - 1 && board[row + 2][col] == 0) {
			if (!visited.contains(new Point(row+2,col))) {
				moveTo(row, col, row + 2, col);
				int dist = calculateDistance(row+2,col,player);
				countHops(row + 2, col, visited,player,distance);
				if(dist<distance)
					heuristicValues.get(player-1).updateHeuristic3();
				moveTo(row, col, row + 2, col);
			}
		}
		//hop left
		if (col - 1 >= 0 && board[row][col - 1] > 0 && col - 2 >= 0
				&& board[row][col - 2] == 0) {
			if (!visited.contains(new Point(row,col-2))) {
				moveTo(row, col, row, col - 2);
				int dist = calculateDistance(row,col-2,player);
				countHops(row, col -2, visited,player,distance);
				if(dist<distance)
					heuristicValues.get(player-1).updateHeuristic3();
				moveTo(row, col, row, col - 2);
			}
		}
		//hop right
		if (col + 1 <= boardWidth - 1 && board[row][col + 1] > 0
				&& col + 2 <= boardWidth - 1 && board[row][col + 2] == 0) {
			if (!visited.contains(new Point(row,col+2))) {
				moveTo(row, col, row, col + 2);
				int dist = calculateDistance(row,col+2,player);
				countHops(row,col+2,visited,player,distance);
				if(dist<distance)
					heuristicValues.get(player-1).updateHeuristic3();
				moveTo(row, col, row, col + 2);
			}
		}
	}
	
	private void populateHeuristics(){
		for (int i=0;i<4;i++)
			heuristicValues.add(new HeuristicValues());
	}
	
	//calculates distance from current positon to goal corner
	private int calculateDistance(int row, int col, int num){
		if (num==1)
			return boardLength-1-row+boardWidth-1-col;
		else if(num==2)
			return boardLength-1-row+col;
		else if (num==3)
			return row+col;
		else if (num==4)
			return row+boardWidth-1-col;
		else{
			System.out.println("invalid number");
			return 0;
		}
	}

	//this also calculates the starting distance from the goal
	private void placePlayer1() {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3 - i; j++) {
				board[i][j] = 1;
				Board.startingDistance+=calculateDistance(i,j,1);
			}
		}
	}

	private void placePlayer2() {
		for (int i = 0; i < 3; i++) {
			for (int j = boardWidth - 1; j >= boardWidth - 3 + i; j--) {
				board[i][j] = 2;
			}
		}
	}

	private void placePlayer3() {
		for (int i = boardLength - 1; i >= boardLength - 4; i--) {
			for (int j = boardWidth - 1; j >= boardWidth - 4 - (i - boardWidth); j--) {
				board[i][j] = 3;
			}
		}
	}

	private void placePlayer4() {
		for (int i = boardLength - 1; i >= boardLength - 4; i--) {
			for (int j = 0; j < i - boardWidth+ 4; j++) {
				board[i][j] = 4;
			}
		}
	}

	/* (non-Javadoc)
	 * @see boards.BoardInterface#isWin()
	 */
	public boolean isWin() {
		if (lastMoved == 1) {
			return player1Won();
		} else if (lastMoved == 2) {
			return player2Won();
		} else if (lastMoved == 3) {
			return player3Won();
		} else if (lastMoved == 4) {
			return player4Won();
		} else {
			return false;
		}
	}

	private boolean player1Won() {
		for (int i = boardLength - 1; i >= boardLength - 4; i--) {
			for (int j = boardWidth - 1; j >= boardWidth - 4 - (i - boardWidth); j--) {
				if (board[i][j] != 1) {
					return false;
				}
			}
		}
		heuristicValues.get(0).isWon();
		return true;
	}

	private boolean player2Won() {
		for (int i = boardLength - 1; i >= boardLength - 4; i--) {
			for (int j = 0; j < i - boardWidth + 4; j++) {
				if (board[i][j] != 2) {
					return false;
				}
			}
		}
		heuristicValues.get(1).isWon();
		return true;
	}

	private boolean player3Won() {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3 - i; j++) {
				if (board[i][j] != 3) {
					return false;
				}
			}
		}
		heuristicValues.get(2).isWon();
		return true;
	}

	private boolean player4Won() {
		for (int i = 0; i < 3; i++) {
			for (int j = boardWidth - 1; j >= boardWidth - 3 + i; j--) {
				if (board[i][j] != 4) {
					return false;
				}
			}
		}
		heuristicValues.get(3).isWon();
		return true;
	}
	
	//adds legal moves for adjacent squares and then calls the hops method
	protected void addLegalMovesFor(int row, int col,int heuristicType) {
		// moves generated by moving to an adjacent square
		//up
		if (row - 1 >= 0 && board[row - 1][col] == 0) {
			moveTo(row, col, row - 1, col);
			addChild(new Board(board, turn),heuristicType);
			moveTo(row, col, row - 1, col);
		}
		//down
		if (row + 1 <= boardLength - 1 && board[row + 1][col] == 0) {
			moveTo(row, col, row + 1, col);
			addChild(new Board(board, turn),heuristicType);
			moveTo(row, col, row + 1, col);
		}
		//left
		if (col - 1 >= 0 && board[row][col - 1] == 0) {
			moveTo(row, col, row, col - 1);
			addChild(new Board(board, turn),heuristicType);
			moveTo(row, col, row, col - 1);
		}
		//right
		if (col + 1 <= boardLength - 1 && board[row][col + 1] == 0) {
			moveTo(row, col, row, col + 1);
			addChild(new Board(board, turn),heuristicType);
			moveTo(row, col, row, col + 1);
		}

		// hops
		hops(row, col, new ArrayList<Point>(),heuristicType);
	}

	//recursively discovers children through hops
	private void hops(int row, int col, ArrayList<Point> visited,int heuristicType) {
		visited.add(new Point(row, col));
		//hop up
		if (row - 1 >= 0 && board[row - 1][col] > 0 && row - 2 >= 0
				&& board[row - 2][col] == 0) {
			if (!visited.contains(new Point(row-2,col))) {
				moveTo(row, col, row - 2, col);
				addChild(new Board(board, turn),heuristicType);
				hops(row - 2, col, visited,heuristicType);
				moveTo(row, col, row - 2, col);
			}
		}
		//hop down
		if (row + 1 <= boardLength - 1 && board[row + 1][col] > 0
				&& row + 2 <= boardLength - 1 && board[row + 2][col] == 0) {
			if (!visited.contains(new Point(row+2,col))) {
				moveTo(row, col, row + 2, col);
				addChild(new Board(board, turn),heuristicType);
				hops(row + 2, col, visited,heuristicType);
				moveTo(row, col, row + 2, col);
			}
		}
		//hop left
		if (col - 1 >= 0 && board[row][col - 1] > 0 && col - 2 >= 0
				&& board[row][col - 2] == 0) {
			if (!visited.contains(new Point(row,col-2))) {
				moveTo(row, col, row, col - 2);
				addChild(new Board(board, turn),heuristicType);
				hops(row, col -2, visited,heuristicType);
				moveTo(row, col, row, col - 2);
			}
		}
		//hop right
		if (col + 1 <= boardWidth - 1 && board[row][col + 1] > 0
				&& col + 2 <= boardWidth - 1 && board[row][col + 2] == 0) {
			if (!visited.contains(new Point(row,col+2))) {
				moveTo(row, col, row, col + 2);
				addChild(new Board(board, turn),heuristicType);
				hops(row,col+2,visited,heuristicType);
				moveTo(row, col, row, col + 2);
			}
		}
	}

	@Override
	public AbstractBoard passes() {
		// TODO Auto-generated method stub
		return new Board(this);
	}
}
