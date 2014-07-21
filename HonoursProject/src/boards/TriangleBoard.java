package boards;

import halma.HeuristicValues;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

public class TriangleBoard extends AbstractBoard {

	static HashMap<Integer, Integer> goalPositions;

	public static void init() {
		TriangleBoard.numPlayers = 3;
		TriangleBoard.startingDistance = 8*3 + 7 * 2 + 6;
		goalPositions = new HashMap<Integer, Integer>();
	}

	public TriangleBoard(int turn, int limitToBestNMoves) {
		TriangleBoard.boardLength = 9;
		TriangleBoard.boardWidth = 9;
		TriangleBoard.limitToBestNMoves = limitToBestNMoves;

		board = new int[boardLength][boardWidth];
		// initialize everything to invalid
		for (int i = 0; i < boardLength; i++) {
			for (int j = 0; j < boardWidth; j++) {
				board[i][j] = -1;
			}
		}
		
		for (int i = 0; i < boardLength; i++) {
			for (int j = (int)(Math.floor(i/2.0)); j < boardWidth - (int)(Math.ceil(i/2.0)); j++) {
				board[i][j] = 0;
			}
		}

		// initializes the player spots
		placePlayers();
		populateHeuristics();
		for (HeuristicValues v : heuristicValues) {
			v.updateHeuristic1(TriangleBoard.startingDistance);
			v.updateHeuristic2(TriangleBoard.startingDistance);
		}
		this.turn = turn;
		lastMoved = 1;
		countHops();
	}

	public TriangleBoard(int[][] board, int turn) {
		this.board = new int[boardLength][boardWidth];
		populateHeuristics();
		for (int i = 0; i < boardLength; i++) {
			for (int j = 0; j < boardWidth; j++) {
				this.board[i][j] = board[i][j];
				// heuristic calculation stuff
				if (this.board[i][j] > 0) {
					int distance = calculateDistance(i, j, this.board[i][j]);
					for (int index = 0; index < heuristicValues.size(); index++) {
						if (this.board[i][j] == index + 1) {
							heuristicValues.get(index).updateHeuristic1(
									distance);
							heuristicValues.get(index).updateHeuristic2(
									distance);
						} else {
							heuristicValues.get(index)
									.updateHeuristic2((TriangleBoard.startingDistance - distance) / 10);
						}
					}
				}
			}
		}
		countHops();
		lastMoved = turn;
		this.turn = turn % TriangleBoard.numPlayers + 1;
		isWin();
	}

	// would be used for passing... support is there I'm just not sure if it can
	// happen
	public TriangleBoard(TriangleBoard b) {
		board = new int[boardLength][boardWidth];
		populateHeuristics();
		heuristicValues = b.heuristicValues;
		for (int i = 0; i < boardLength; i++) {
			for (int j = 0; j < boardWidth; j++) {
				board[i][j] = b.board[i][j];
			}
		}
		lastMoved = b.turn;
		this.turn = b.turn % TriangleBoard.numPlayers + 1;
		isWin();

	}

	private void placePlayers() {
		TriangleBoard.goalPositions.put(1, 3);
		TriangleBoard.goalPositions.put(2, 1);
		TriangleBoard.goalPositions.put(3, 2);
		placePosition1(1);
		placePosition2(2);
		placePosition3(3);
	}

	private void placePosition1(int playerNum) {
		board[0][0] = playerNum;
		board[0][1] = playerNum;
		board[0][2] = playerNum;
		board[1][1] = playerNum;
		board[1][0] = playerNum;
		board[2][1] = playerNum;
	}

	private void placePosition2(int playerNum) {
		board[0][6] = playerNum;
		board[0][7] = playerNum;
		board[0][8] = playerNum;
		board[1][6] = playerNum;
		board[1][7] = playerNum;
		board[2][7] = playerNum;
	}

	private void placePosition3(int playerNum) {
		board[8][4] = playerNum;
		board[7][4] = playerNum;
		board[7][3] = playerNum;
		board[6][3] = playerNum;
		board[6][4] = playerNum;
		board[6][5] = playerNum;
	}
	
	private void countHops(){
		for (int i = 0; i < boardLength; i++) {
			for (int j = 0; j < boardWidth; j++) {
				if(board[i][j]>0){
					if (i%2==0)
						countHopsEven(i,j,new ArrayList<Point>(),board[i][j],calculateDistance(i,j,board[i][j]));
					else
						countHopsOdd(i,j,new ArrayList<Point>(),board[i][j],calculateDistance(i,j,board[i][j]));
				}
			}
		}
	}
	
	private void countHopsEven(int row, int col, ArrayList<Point> visited, int player,int distance) {
		visited.add(new Point(row, col));
		// hop up left
		if (row - 2 >= 0&& col -1 >=0 && board[row - 1][col - 1] > 0
				&& board[row - 2][col - 1] == 0) {
			if (!visited.contains(new Point(row - 2, col - 1))) {
				moveTo(row, col, row - 2, col - 1);
				int dist = calculateDistance(row-2,col-1,player);
				if(dist<distance)
					heuristicValues.get(player-1).updateHeuristic3();
				countHopsEven(row - 2, col - 1, visited, player,distance);
				moveTo(row, col, row - 2, col - 1);
			}
		}
		// hop up right
		if (row - 2 >= 0 && col + 1 <= boardWidth - 1&& board[row - 1][col] > 0
				&& board[row - 2][col + 1] == 0) {
			if (!visited.contains(new Point(row - 2, col + 1))) {
				moveTo(row, col, row - 2, col + 1);
				int dist = calculateDistance(row-2,col+1,player);
				if(dist<distance)
					heuristicValues.get(player-1).updateHeuristic3();
				countHopsEven(row - 2, col + 1, visited, player,distance);
				moveTo(row, col, row - 2, col + 1);
			}
		}
		// hop down left
		if (row + 2 <= boardLength - 1 && col -1 >= 0 && board[row + 1][col - 1] > 0
				&& board[row + 2][col - 1] == 0) {
			if (!visited.contains(new Point(row + 2, col - 1))) {
				moveTo(row, col, row + 2, col - 1);
				int dist = calculateDistance(row+2,col-1,player);
				if(dist<distance)
					heuristicValues.get(player-1).updateHeuristic3();
				countHopsEven(row + 2, col - 1, visited, player,distance);
				moveTo(row, col, row + 2, col - 1);
			}
		}
		// hop down right
		if (row + 2 <= boardLength - 1 && col + 1 <= boardWidth - 1 && board[row + 1][col] > 0
				&& board[row + 2][col + 1] == 0) {
			if (!visited.contains(new Point(row + 2, col + 1))) {
				moveTo(row, col, row + 2, col + 1);
				int dist = calculateDistance(row+2,col+1,player);
				if(dist<distance)
					heuristicValues.get(player-1).updateHeuristic3();
				countHopsEven(row + 2, col+1, visited, player,distance);
				moveTo(row, col, row + 2, col + 1);
			}
		}
		// hop left
		if (col - 2 >= 0 && board[row][col - 1] > 0 && board[row][col - 2] == 0) {
			if (!visited.contains(new Point(row, col - 2))) {
				moveTo(row, col, row, col - 2);
				int dist = calculateDistance(row,col-2,player);
				if(dist<distance)
					heuristicValues.get(player-1).updateHeuristic3();
				countHopsEven(row, col - 2, visited, player,distance);
				moveTo(row, col, row, col - 2);
			}
		}
		// hop right
		if (col + 2 <= boardWidth - 1 && board[row][col + 1] > 0
				&& board[row][col + 2] == 0) {
			if (!visited.contains(new Point(row, col + 2))) {
				moveTo(row, col, row, col + 2);
				int dist = calculateDistance(row,col+2,player);
				if(dist<distance)
					heuristicValues.get(player-1).updateHeuristic3();
				countHopsEven(row, col + 2, visited, player,distance);
				moveTo(row, col, row, col + 2);
			}
		}
	}

	private void countHopsOdd(int row, int col, ArrayList<Point> visited, int player, int distance) {
		visited.add(new Point(row, col));
		// hop up left
		if (row - 2 >= 0 && col - 1>= 0 && board[row - 1][col] > 0
				&& board[row - 2][col - 1] == 0) {
			if (!visited.contains(new Point(row - 2, col - 1))) {
				moveTo(row, col, row - 2, col - 1);
				int dist = calculateDistance(row-2,col-1,player);
				if(dist<distance)
					heuristicValues.get(player-1).updateHeuristic3();
				countHopsOdd(row - 2, col - 1, visited, player,distance);
				moveTo(row, col, row - 2, col - 1);
			}
		}
		// hop up right
		if (row - 2 >= 0 &&col + 1 <= boardWidth - 1 &&board[row - 1][col + 1] > 0
				&& board[row - 2][col + 1] == 0) {
			if (!visited.contains(new Point(row - 2, col + 1))) {
				moveTo(row, col, row - 2, col + 1);
				int dist = calculateDistance(row-2,col+1,player);
				if(dist<distance)
					heuristicValues.get(player-1).updateHeuristic3();
				countHopsOdd(row - 2, col + 1, visited, player,distance);
				moveTo(row, col, row - 2, col + 1);
			}
		}
		// hop down left
		if (row + 2 <= boardLength - 1 && col - 1 >= 0&& board[row + 1][col] > 0
				&& board[row + 2][col - 1] == 0) {
			if (!visited.contains(new Point(row + 2, col - 1))) {
				moveTo(row, col, row + 2, col - 1);
				int dist = calculateDistance(row+2,col-1,player);
				if(dist<distance)
					heuristicValues.get(player-1).updateHeuristic3();
				countHopsOdd(row + 2, col - 1, visited, player,distance);
				moveTo(row, col, row + 2, col - 1);
			}
		}
		// hop down right
		if (row + 2 <= boardLength - 1 && col + 1 <= boardWidth - 1&& board[row + 1][col + 1] > 0
				&& board[row + 2][col + 1] == 0) {
			if (!visited.contains(new Point(row + 2, col + 1))) {
				moveTo(row, col, row + 2, col + 1);
				int dist = calculateDistance(row+2,col+1,player);
				if(dist<distance)
					heuristicValues.get(player-1).updateHeuristic3();
				countHopsOdd(row + 2, col+1, visited, player,distance);
				moveTo(row, col, row + 2, col + 1);
			}
		}
		// hop left
		if (col - 2 >= 0 && board[row][col - 1] > 0 && board[row][col - 2] == 0) {
			if (!visited.contains(new Point(row, col - 2))) {
				moveTo(row, col, row, col - 2);
				int dist = calculateDistance(row,col-2,player);
				if(dist<distance)
					heuristicValues.get(player-1).updateHeuristic3();
				countHopsOdd(row, col - 2, visited, player,distance);
				moveTo(row, col, row, col - 2);
			}
		}
		// hop right
		if (col + 2 <= boardWidth - 1 && board[row][col + 1] > 0
				&& board[row][col + 2] == 0) {
			if (!visited.contains(new Point(row, col + 2))) {
				moveTo(row, col, row, col + 2);
				int dist = calculateDistance(row,col+2,player);
				if(dist<distance)
					heuristicValues.get(player-1).updateHeuristic3();
				countHopsOdd(row, col + 2, visited, player,distance);
				moveTo(row, col, row, col + 2);
			}
		}
	}

	private int calculateDistance(int row, int col, int k) {
		int goalPos = TriangleBoard.goalPositions.get(k);
		if (goalPos == 1) {
			return (int)(Math.floor(row/2.0)) + col;
		} else if (goalPos == 2) {
			return (int)(Math.ceil(row/2.0)) + 8-col;
		} else if (goalPos == 3) {
			return 8-row;
		}else{
			System.out.println("calculate distance is wrong!");
			System.exit(0);
		}
		return 0;
	}

	private void populateHeuristics() {
		for (int i = 0; i < TriangleBoard.numPlayers; i++)
			heuristicValues.add(new HeuristicValues());
	}

	protected void addLegalMovesFor(int row, int col, int heuristicType) {
		if (row % 2 == 0)
			addLegalMovesForEven(row, col, heuristicType);
		else
			addLegalMovesForOdd(row, col, heuristicType);
	}

	// adds legal moves for adjacent squares and then calls the hops method
	private void addLegalMovesForEven(int row, int col, int heuristicType) {
		// moves generated by moving to an adjacent square
		// up left
		if (row - 1 >= 0 && col - 1 >= 0 && board[row - 1][col - 1] == 0) {
			moveTo(row, col, row - 1, col - 1);
			addChild(new TriangleBoard(board, turn), heuristicType);
			moveTo(row, col, row - 1, col - 1);
		}
		// up right
		if (row - 1 >= 0 && board[row - 1][col] == 0) {
			moveTo(row, col, row - 1, col);
			addChild(new TriangleBoard(board, turn), heuristicType);
			moveTo(row, col, row - 1, col);
		}
		// down left
		if (row + 1 <= boardLength - 1 && col - 1 >= 0
				&& board[row + 1][col - 1] == 0) {
			moveTo(row, col, row + 1, col - 1);
			addChild(new TriangleBoard(board, turn), heuristicType);
			moveTo(row, col, row + 1, col - 1);
		}
		// down right
		if (row + 1 <= boardLength - 1 && board[row + 1][col] == 0) {
			moveTo(row, col, row + 1, col);
			addChild(new TriangleBoard(board, turn), heuristicType);
			moveTo(row, col, row + 1, col);
		}
		// left
		if (col - 1 >= 0 && board[row][col - 1] == 0) {
			moveTo(row, col, row, col - 1);
			addChild(new TriangleBoard(board, turn), heuristicType);
			moveTo(row, col, row, col - 1);
		}
		// right
		if (col + 1 <= boardWidth - 1 && board[row][col + 1] == 0) {
			moveTo(row, col, row, col + 1);
			addChild(new TriangleBoard(board, turn), heuristicType);
			moveTo(row, col, row, col + 1);
		}

		// hops
		hopsEven(row, col, new ArrayList<Point>(), heuristicType);
	}

	// adds legal moves for adjacent squares and then calls the hops method
	private void addLegalMovesForOdd(int row, int col, int heuristicType) {
		// moves generated by moving to an adjacent square
		// up left
		if (row - 1 >= 0 && board[row - 1][col] == 0) {
			moveTo(row, col, row - 1, col);
			addChild(new TriangleBoard(board, turn), heuristicType);
			moveTo(row, col, row - 1, col);
		}
		// up right
		if (row - 1 >= 0 &&col + 1 <= boardWidth - 1 && board[row - 1][col+1] == 0) {
			moveTo(row, col, row - 1, col+1);
			addChild(new TriangleBoard(board, turn), heuristicType);
			moveTo(row, col, row - 1, col+1);
		}
		// down left
		if (row + 1 <= boardLength - 1 	&& board[row + 1][col] == 0) {
			moveTo(row, col, row + 1, col);
			addChild(new TriangleBoard(board, turn), heuristicType);
			moveTo(row, col, row + 1, col);
		}
		// down right
		if (row + 1 <= boardLength - 1 && col + 1 <= boardWidth - 1 &&board[row + 1][col+1] == 0) {
			moveTo(row, col, row + 1, col+1);
			addChild(new TriangleBoard(board, turn), heuristicType);
			moveTo(row, col, row + 1, col+1);
		}
		// left
		if (col - 1 >= 0 && board[row][col - 1] == 0) {
			moveTo(row, col, row, col - 1);
			addChild(new TriangleBoard(board, turn), heuristicType);
			moveTo(row, col, row, col - 1);
		}
		// right
		if (col + 1 <= boardWidth - 1 && board[row][col + 1] == 0) {
			moveTo(row, col, row, col + 1);
			addChild(new TriangleBoard(board, turn), heuristicType);
			moveTo(row, col, row, col + 1);
		}

		// hops
		hopsOdd(row, col, new ArrayList<Point>(), heuristicType);
	}

	// recursively discovers children through hops
	private void hopsEven(int row, int col, ArrayList<Point> visited,
			int heuristicType) {
		visited.add(new Point(row, col));
		// hop up left
		if (row - 2 >= 0&& col -1 >=0 && board[row - 1][col - 1] > 0
				&& board[row - 2][col - 1] == 0) {
			if (!visited.contains(new Point(row - 2, col - 1))) {
				moveTo(row, col, row - 2, col - 1);
				addChild(new TriangleBoard(board, turn), heuristicType);
				hopsEven(row - 2, col - 1, visited, heuristicType);
				moveTo(row, col, row - 2, col - 1);
			}
		}
		// hop up right
		if (row - 2 >= 0 && col + 1 <= boardWidth - 1&& board[row - 1][col] > 0
				&& board[row - 2][col + 1] == 0) {
			if (!visited.contains(new Point(row - 2, col + 1))) {
				moveTo(row, col, row - 2, col + 1);
				addChild(new TriangleBoard(board, turn), heuristicType);
				hopsEven(row - 2, col + 1, visited, heuristicType);
				moveTo(row, col, row - 2, col + 1);
			}
		}
		// hop down left
		if (row + 2 <= boardLength - 1 && col -1 >= 0 && board[row + 1][col - 1] > 0
				&& board[row + 2][col - 1] == 0) {
			if (!visited.contains(new Point(row + 2, col - 1))) {
				moveTo(row, col, row + 2, col - 1);
				addChild(new TriangleBoard(board, turn), heuristicType);
				hopsEven(row + 2, col - 1, visited, heuristicType);
				moveTo(row, col, row + 2, col - 1);
			}
		}
		// hop down right
		if (row + 2 <= boardLength - 1 && col + 1 <= boardWidth - 1 && board[row + 1][col] > 0
				&& board[row + 2][col + 1] == 0) {
			if (!visited.contains(new Point(row + 2, col + 1))) {
				moveTo(row, col, row + 2, col + 1);
				addChild(new TriangleBoard(board, turn), heuristicType);
				hopsEven(row + 2, col+1, visited, heuristicType);
				moveTo(row, col, row + 2, col + 1);
			}
		}
		// hop left
		if (col - 2 >= 0 && board[row][col - 1] > 0 && board[row][col - 2] == 0) {
			if (!visited.contains(new Point(row, col - 2))) {
				moveTo(row, col, row, col - 2);
				addChild(new TriangleBoard(board, turn), heuristicType);
				hopsEven(row, col - 2, visited, heuristicType);
				moveTo(row, col, row, col - 2);
			}
		}
		// hop right
		if (col + 2 <= boardWidth - 1 && board[row][col + 1] > 0
				&& board[row][col + 2] == 0) {
			if (!visited.contains(new Point(row, col + 2))) {
				moveTo(row, col, row, col + 2);
				addChild(new TriangleBoard(board, turn), heuristicType);
				hopsEven(row, col + 2, visited, heuristicType);
				moveTo(row, col, row, col + 2);
			}
		}
	}

	private void hopsOdd(int row, int col, ArrayList<Point> visited,
			int heuristicType) {
		visited.add(new Point(row, col));
		// hop up left
		if (row - 2 >= 0 && col - 1>= 0 && board[row - 1][col] > 0
				&& board[row - 2][col - 1] == 0) {
			if (!visited.contains(new Point(row - 2, col - 1))) {
				moveTo(row, col, row - 2, col - 1);
				addChild(new TriangleBoard(board, turn), heuristicType);
				hopsOdd(row - 2, col - 1, visited, heuristicType);
				moveTo(row, col, row - 2, col - 1);
			}
		}
		// hop up right
		if (row - 2 >= 0 &&col + 1 <= boardWidth - 1 &&board[row - 1][col + 1] > 0
				&& board[row - 2][col + 1] == 0) {
			if (!visited.contains(new Point(row - 2, col + 1))) {
				moveTo(row, col, row - 2, col + 1);
				addChild(new TriangleBoard(board, turn), heuristicType);
				hopsOdd(row - 2, col + 1, visited, heuristicType);
				moveTo(row, col, row - 2, col + 1);
			}
		}
		// hop down left
		if (row + 2 <= boardLength - 1 && col - 1 >= 0&& board[row + 1][col] > 0
				&& board[row + 2][col - 1] == 0) {
			if (!visited.contains(new Point(row + 2, col - 1))) {
				moveTo(row, col, row + 2, col - 1);
				addChild(new TriangleBoard(board, turn), heuristicType);
				hopsOdd(row + 2, col - 1, visited, heuristicType);
				moveTo(row, col, row + 2, col - 1);
			}
		}
		// hop down right
		if (row + 2 <= boardLength - 1 && col + 1 <= boardWidth - 1&& board[row + 1][col + 1] > 0
				&& board[row + 2][col + 1] == 0) {
			if (!visited.contains(new Point(row + 2, col + 1))) {
				moveTo(row, col, row + 2, col + 1);
				addChild(new TriangleBoard(board, turn), heuristicType);
				hopsOdd(row + 2, col + 1, visited, heuristicType);
				moveTo(row, col, row + 2, col + 1);
			}
		}
		// hop left
		if (col - 2 >= 0 && board[row][col - 1] > 0 && board[row][col - 2] == 0) {
			if (!visited.contains(new Point(row, col - 2))) {
				moveTo(row, col, row, col - 2);
				addChild(new TriangleBoard(board, turn), heuristicType);
				hopsOdd(row, col - 2, visited, heuristicType);
				moveTo(row, col, row, col - 2);
			}
		}
		// hop right
		if (col + 2 <= boardWidth - 1 && board[row][col + 1] > 0
				&& board[row][col + 2] == 0) {
			if (!visited.contains(new Point(row, col + 2))) {
				moveTo(row, col, row, col + 2);
				addChild(new TriangleBoard(board, turn), heuristicType);
				hopsOdd(row, col + 2, visited, heuristicType);
				moveTo(row, col, row, col + 2);
			}
		}
	}

	@Override
	public boolean isWin() {
		// TODO Auto-generated method stub
		if (TriangleBoard.goalPositions.get(lastMoved) == 1){
			if (checkPosition1(lastMoved)){
				heuristicValues.get(lastMoved-1).isWon();
				return true;
			}
		}else if (TriangleBoard.goalPositions.get(lastMoved) == 2){
			if (checkPosition2(lastMoved)){
				heuristicValues.get(lastMoved-1).isWon();
				return true;
			}
		}else if (TriangleBoard.goalPositions.get(lastMoved) == 3){
			if (checkPosition3(lastMoved)){
				heuristicValues.get(lastMoved-1).isWon();
				return true;
			}
		}
		return false;
	}

	private boolean checkPosition1(int playerNum) {
		return (board[0][0] == playerNum && board[0][1] == playerNum
				&& board[0][2] == playerNum && board[1][1] == playerNum
				&& board[1][0] == playerNum && board[2][1] == playerNum);
	}

	private boolean checkPosition2(int playerNum) {
		return (board[0][7] == playerNum && board[0][8] == playerNum
				&& board[0][6] == playerNum && board[1][7] == playerNum
				&& board[1][6] == playerNum && board[2][7] == playerNum);
	}

	private boolean checkPosition3(int playerNum) {
		return (board[6][3] == playerNum && board[6][4] == playerNum
				&& board[6][5] == playerNum && board[7][4] == playerNum
				&& board[7][3] == playerNum && board[8][4] == playerNum);
	}

	@Override
	public AbstractBoard passes() {
		return new TriangleBoard(this);
	}

	public String toString() {
		String value = "";
		for (int i = 0; i < boardLength; i++) {
			if (i % 2 != 0)
				value += " ";
			for (int j = 0; j < boardWidth; j++) {
				if (board[i][j] > -1)
					value += board[i][j] + " ";
				else
					value += "  ";
			}
			value += "\n";
		}
		return value;
	}
}

