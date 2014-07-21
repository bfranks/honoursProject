package boards;

import halma.HeuristicValues;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

public class StarBoard extends AbstractBoard {

	static HashMap<Integer, Integer> goalPositions;

	public static void init(int numPlayers) {
		StarBoard.numPlayers = numPlayers;
		StarBoard.startingDistance = 12 + 11 * 2 + 10 * 3;
		goalPositions = new HashMap<Integer, Integer>();
	}

	public StarBoard(int turn, int limitToBestNMoves) {
		StarBoard.boardLength = 13;
		StarBoard.boardWidth = 10;
		StarBoard.limitToBestNMoves = limitToBestNMoves;

		board = new int[boardLength][boardWidth];
		// initialize everything to invalid
		for (int i = 0; i < boardLength; i++) {
			for (int j = 0; j < boardWidth; j++) {
				board[i][j] = -1;
			}
		}
		// player 1 spot
		board[0][5] = 0;
		for (int i = 4; i <= 5; i++)
			board[1][i] = 0;
		for (int i = 4; i <= 6; i++)
			board[2][i] = 0;
		// rows 3,4,5
		for (int i = 0; i <= 9; i++)
			board[3][i] = 0;
		for (int i = 1; i <= 9; i++)
			board[4][i] = 0;
		for (int i = 1; i <= 8; i++)
			board[5][i] = 0;
		// row 6
		for (int i = 2; i <= 8; i++)
			board[6][i] = 0;
		// row 7,8,9
		for (int i = 1; i <= 8; i++)
			board[7][i] = 0;
		for (int i = 1; i <= 9; i++)
			board[8][i] = 0;
		for (int i = 0; i <= 9; i++)
			board[9][i] = 0;
		// row 10,11,12 player 4 spot
		for (int i = 4; i <= 6; i++)
			board[10][i] = 0;
		for (int i = 4; i <= 5; i++)
			board[11][i] = 0;
		board[12][5] = 0;

		// initializes the player spots
		placePlayers();
		populateHeuristics();
		for (HeuristicValues v : heuristicValues) {
			v.updateHeuristic1(StarBoard.startingDistance);
			v.updateHeuristic2(StarBoard.startingDistance);
		}
		this.turn = turn;
		lastMoved = 1;
		countHops();
	}

	public StarBoard(int[][] board, int turn) {
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
									.updateHeuristic2((StarBoard.startingDistance - distance) / 10);
						}
					}
				}
			}
		}
		countHops();
		lastMoved = turn;
		this.turn = turn % StarBoard.numPlayers + 1;
		isWin();
	}

	// would be used for passing... support is there I'm just not sure if it can
	// happen
	public StarBoard(StarBoard b) {
		board = new int[boardLength][boardWidth];
		populateHeuristics();
		heuristicValues = b.heuristicValues;
		for (int i = 0; i < boardLength; i++) {
			for (int j = 0; j < boardWidth; j++) {
				board[i][j] = b.board[i][j];
			}
		}
		lastMoved = b.turn;
		this.turn = b.turn % StarBoard.numPlayers + 1;
		isWin();

	}

	private void placePlayers() {
		if (StarBoard.numPlayers == 2) {
			StarBoard.goalPositions.put(1, 4);
			StarBoard.goalPositions.put(2, 1);
			placePosition1(1);
			placePosition4(2);
		} else if (StarBoard.numPlayers == 3) {
			StarBoard.goalPositions.put(1, 4);
			StarBoard.goalPositions.put(2, 6);
			StarBoard.goalPositions.put(3, 2);
			placePosition1(1);
			placePosition3(2);
			placePosition5(3);
		} else if (StarBoard.numPlayers == 4) {
			StarBoard.goalPositions.put(1, 4);
			StarBoard.goalPositions.put(2, 5);
			StarBoard.goalPositions.put(3, 1);
			StarBoard.goalPositions.put(4, 2);
			placePosition1(1);
			placePosition2(2);
			placePosition4(3);
			placePosition5(4);
		} else if (StarBoard.numPlayers == 6) {
			StarBoard.goalPositions.put(1, 4);
			StarBoard.goalPositions.put(2, 5);
			StarBoard.goalPositions.put(3, 6);
			StarBoard.goalPositions.put(4, 1);
			StarBoard.goalPositions.put(5, 2);
			StarBoard.goalPositions.put(6, 3);
			placePosition1(1);
			placePosition2(2);
			placePosition3(3);
			placePosition4(4);
			placePosition5(5);
			placePosition6(6);
		}
	}

	private void placePosition1(int playerNum) {
		board[0][5] = playerNum;
		board[1][4] = playerNum;
		board[1][5] = playerNum;
		board[2][4] = playerNum;
		board[2][5] = playerNum;
		board[2][6] = playerNum;
	}

	private void placePosition2(int playerNum) {
		board[3][7] = playerNum;
		board[3][8] = playerNum;
		board[3][9] = playerNum;
		board[4][8] = playerNum;
		board[4][9] = playerNum;
		board[5][8] = playerNum;
	}

	private void placePosition3(int playerNum) {
		board[7][8] = playerNum;
		board[8][8] = playerNum;
		board[8][9] = playerNum;
		board[9][7] = playerNum;
		board[9][8] = playerNum;
		board[9][9] = playerNum;
	}

	private void placePosition4(int playerNum) {
		board[12][5] = playerNum;
		board[11][4] = playerNum;
		board[11][5] = playerNum;
		board[10][4] = playerNum;
		board[10][5] = playerNum;
		board[10][6] = playerNum;
	}

	private void placePosition5(int playerNum) {
		board[7][1] = playerNum;
		board[8][1] = playerNum;
		board[8][2] = playerNum;
		board[9][0] = playerNum;
		board[9][1] = playerNum;
		board[9][2] = playerNum;
	}

	private void placePosition6(int playerNum) {
		board[3][0] = playerNum;
		board[3][1] = playerNum;
		board[3][2] = playerNum;
		board[4][1] = playerNum;
		board[4][2] = playerNum;
		board[5][1] = playerNum;
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
		int goalPos = StarBoard.goalPositions.get(k);
		if (goalPos == 1) {
			return Math.max(row, Math.abs(col - 5));
		} else if (goalPos == 2) {
			return Math.max((int)(Math.floor(Math.abs(row - 3)/2.0)) + Math.abs(col - 9),Math.abs(row - 3));
		} else if (goalPos == 3) {
			return Math.max((int)(Math.floor(Math.abs(row - 9)/2.0)) + Math.abs(col - 9),Math.abs(row - 9));
		} else if (goalPos == 4) {
			return Math.max(Math.abs(row - 12), Math.abs(col - 5));
		} else if (goalPos == 5) {
			return Math.max((int)(Math.ceil(Math.abs(row - 9)/2.0)) + col,Math.abs(row - 9));
		} else if (goalPos == 6) {
			return Math.max((int)(Math.ceil(Math.abs(row - 3)/2.0)) + col,Math.abs(row - 3));
		}else{
			System.out.println("calculate distance is wrong!");
			System.exit(0);
		}
		return 0;
	}

	private void populateHeuristics() {
		for (int i = 0; i < StarBoard.numPlayers; i++)
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
			addChild(new StarBoard(board, turn), heuristicType);
			moveTo(row, col, row - 1, col - 1);
		}
		// up right
		if (row - 1 >= 0 && board[row - 1][col] == 0) {
			moveTo(row, col, row - 1, col);
			addChild(new StarBoard(board, turn), heuristicType);
			moveTo(row, col, row - 1, col);
		}
		// down left
		if (row + 1 <= boardLength - 1 && col - 1 >= 0
				&& board[row + 1][col - 1] == 0) {
			moveTo(row, col, row + 1, col - 1);
			addChild(new StarBoard(board, turn), heuristicType);
			moveTo(row, col, row + 1, col - 1);
		}
		// down right
		if (row + 1 <= boardLength - 1 && board[row + 1][col] == 0) {
			moveTo(row, col, row + 1, col);
			addChild(new StarBoard(board, turn), heuristicType);
			moveTo(row, col, row + 1, col);
		}
		// left
		if (col - 1 >= 0 && board[row][col - 1] == 0) {
			moveTo(row, col, row, col - 1);
			addChild(new StarBoard(board, turn), heuristicType);
			moveTo(row, col, row, col - 1);
		}
		// right
		if (col + 1 <= boardWidth - 1 && board[row][col + 1] == 0) {
			moveTo(row, col, row, col + 1);
			addChild(new StarBoard(board, turn), heuristicType);
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
			addChild(new StarBoard(board, turn), heuristicType);
			moveTo(row, col, row - 1, col);
		}
		// up right
		if (row - 1 >= 0 &&col + 1 <= boardWidth - 1 && board[row - 1][col+1] == 0) {
			moveTo(row, col, row - 1, col+1);
			addChild(new StarBoard(board, turn), heuristicType);
			moveTo(row, col, row - 1, col+1);
		}
		// down left
		if (row + 1 <= boardLength - 1 	&& board[row + 1][col] == 0) {
			moveTo(row, col, row + 1, col);
			addChild(new StarBoard(board, turn), heuristicType);
			moveTo(row, col, row + 1, col);
		}
		// down right
		if (row + 1 <= boardLength - 1 && col + 1 <= boardWidth - 1 &&board[row + 1][col+1] == 0) {
			moveTo(row, col, row + 1, col+1);
			addChild(new StarBoard(board, turn), heuristicType);
			moveTo(row, col, row + 1, col+1);
		}
		// left
		if (col - 1 >= 0 && board[row][col - 1] == 0) {
			moveTo(row, col, row, col - 1);
			addChild(new StarBoard(board, turn), heuristicType);
			moveTo(row, col, row, col - 1);
		}
		// right
		if (col + 1 <= boardWidth - 1 && board[row][col + 1] == 0) {
			moveTo(row, col, row, col + 1);
			addChild(new StarBoard(board, turn), heuristicType);
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
				addChild(new StarBoard(board, turn), heuristicType);
				hopsEven(row - 2, col - 1, visited, heuristicType);
				moveTo(row, col, row - 2, col - 1);
			}
		}
		// hop up right
		if (row - 2 >= 0 && col + 1 <= boardWidth - 1&& board[row - 1][col] > 0
				&& board[row - 2][col + 1] == 0) {
			if (!visited.contains(new Point(row - 2, col + 1))) {
				moveTo(row, col, row - 2, col + 1);
				addChild(new StarBoard(board, turn), heuristicType);
				hopsEven(row - 2, col + 1, visited, heuristicType);
				moveTo(row, col, row - 2, col + 1);
			}
		}
		// hop down left
		if (row + 2 <= boardLength - 1 && col -1 >= 0 && board[row + 1][col - 1] > 0
				&& board[row + 2][col - 1] == 0) {
			if (!visited.contains(new Point(row + 2, col - 1))) {
				moveTo(row, col, row + 2, col - 1);
				addChild(new StarBoard(board, turn), heuristicType);
				hopsEven(row + 2, col - 1, visited, heuristicType);
				moveTo(row, col, row + 2, col - 1);
			}
		}
		// hop down right
		if (row + 2 <= boardLength - 1 && col + 1 <= boardWidth - 1 && board[row + 1][col] > 0
				&& board[row + 2][col + 1] == 0) {
			if (!visited.contains(new Point(row + 2, col + 1))) {
				moveTo(row, col, row + 2, col + 1);
				addChild(new StarBoard(board, turn), heuristicType);
				hopsEven(row + 2, col+1, visited, heuristicType);
				moveTo(row, col, row + 2, col + 1);
			}
		}
		// hop left
		if (col - 2 >= 0 && board[row][col - 1] > 0 && board[row][col - 2] == 0) {
			if (!visited.contains(new Point(row, col - 2))) {
				moveTo(row, col, row, col - 2);
				addChild(new StarBoard(board, turn), heuristicType);
				hopsEven(row, col - 2, visited, heuristicType);
				moveTo(row, col, row, col - 2);
			}
		}
		// hop right
		if (col + 2 <= boardWidth - 1 && board[row][col + 1] > 0
				&& board[row][col + 2] == 0) {
			if (!visited.contains(new Point(row, col + 2))) {
				moveTo(row, col, row, col + 2);
				addChild(new StarBoard(board, turn), heuristicType);
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
				addChild(new StarBoard(board, turn), heuristicType);
				hopsOdd(row - 2, col - 1, visited, heuristicType);
				moveTo(row, col, row - 2, col - 1);
			}
		}
		// hop up right
		if (row - 2 >= 0 &&col + 1 <= boardWidth - 1 &&board[row - 1][col + 1] > 0
				&& board[row - 2][col + 1] == 0) {
			if (!visited.contains(new Point(row - 2, col + 1))) {
				moveTo(row, col, row - 2, col + 1);
				addChild(new StarBoard(board, turn), heuristicType);
				hopsOdd(row - 2, col + 1, visited, heuristicType);
				moveTo(row, col, row - 2, col + 1);
			}
		}
		// hop down left
		if (row + 2 <= boardLength - 1 && col - 1 >= 0&& board[row + 1][col] > 0
				&& board[row + 2][col - 1] == 0) {
			if (!visited.contains(new Point(row + 2, col - 1))) {
				moveTo(row, col, row + 2, col - 1);
				addChild(new StarBoard(board, turn), heuristicType);
				hopsOdd(row + 2, col - 1, visited, heuristicType);
				moveTo(row, col, row + 2, col - 1);
			}
		}
		// hop down right
		if (row + 2 <= boardLength - 1 && col + 1 <= boardWidth - 1&& board[row + 1][col + 1] > 0
				&& board[row + 2][col + 1] == 0) {
			if (!visited.contains(new Point(row + 2, col + 1))) {
				moveTo(row, col, row + 2, col + 1);
				addChild(new StarBoard(board, turn), heuristicType);
				hopsOdd(row + 2, col + 1, visited, heuristicType);
				moveTo(row, col, row + 2, col + 1);
			}
		}
		// hop left
		if (col - 2 >= 0 && board[row][col - 1] > 0 && board[row][col - 2] == 0) {
			if (!visited.contains(new Point(row, col - 2))) {
				moveTo(row, col, row, col - 2);
				addChild(new StarBoard(board, turn), heuristicType);
				hopsOdd(row, col - 2, visited, heuristicType);
				moveTo(row, col, row, col - 2);
			}
		}
		// hop right
		if (col + 2 <= boardWidth - 1 && board[row][col + 1] > 0
				&& board[row][col + 2] == 0) {
			if (!visited.contains(new Point(row, col + 2))) {
				moveTo(row, col, row, col + 2);
				addChild(new StarBoard(board, turn), heuristicType);
				hopsOdd(row, col + 2, visited, heuristicType);
				moveTo(row, col, row, col + 2);
			}
		}
	}

	@Override
	public boolean isWin() {
		// TODO Auto-generated method stub
		if (StarBoard.goalPositions.get(lastMoved) == 1){
			if (checkPosition1(lastMoved)){
				heuristicValues.get(lastMoved-1).isWon();
				return true;
			}
		}else if (StarBoard.goalPositions.get(lastMoved) == 2){
			if (checkPosition2(lastMoved)){
				heuristicValues.get(lastMoved-1).isWon();
				return true;
			}
		}else if (StarBoard.goalPositions.get(lastMoved) == 3){
			if (checkPosition3(lastMoved)){
				heuristicValues.get(lastMoved-1).isWon();
				return true;
			}
		}else if (StarBoard.goalPositions.get(lastMoved) == 4){
			if (checkPosition4(lastMoved)){
				heuristicValues.get(lastMoved-1).isWon();
				return true;
			}
		}else if (StarBoard.goalPositions.get(lastMoved) == 5){
			if (checkPosition5(lastMoved)){
				heuristicValues.get(lastMoved-1).isWon();
				return true;
			}
		}else if (StarBoard.goalPositions.get(lastMoved) == 6){
			if (checkPosition6(lastMoved)){
				heuristicValues.get(lastMoved-1).isWon();
				return true;
			}
		}
		return false;
	}

	private boolean checkPosition1(int playerNum) {
		return (board[0][5] == playerNum && board[1][4] == playerNum
				&& board[1][5] == playerNum && board[2][4] == playerNum
				&& board[2][5] == playerNum && board[2][6] == playerNum);
	}

	private boolean checkPosition2(int playerNum) {
		return (board[3][7] == playerNum && board[3][8] == playerNum
				&& board[3][9] == playerNum && board[4][8] == playerNum
				&& board[4][9] == playerNum && board[5][8] == playerNum);
	}

	private boolean checkPosition3(int playerNum) {
		return (board[7][8] == playerNum && board[8][8] == playerNum
				&& board[8][9] == playerNum && board[9][7] == playerNum
				&& board[9][8] == playerNum && board[9][9] == playerNum);
	}

	private boolean checkPosition4(int playerNum) {
		return (board[12][5] == playerNum && board[11][4] == playerNum
				&& board[11][5] == playerNum && board[10][4] == playerNum
				&& board[10][5] == playerNum && board[10][6] == playerNum);
	}

	private boolean checkPosition5(int playerNum) {
		return (board[7][1] == playerNum && board[8][1] == playerNum
				&& board[8][2] == playerNum && board[9][0] == playerNum
				&& board[9][1] == playerNum && board[9][2] == playerNum);
	}

	private boolean checkPosition6(int playerNum) {
		// TODO Auto-generated method stub
		return (board[3][0] == playerNum && board[3][1] == playerNum
				&& board[3][2] == playerNum && board[4][1] == playerNum
				&& board[4][2] == playerNum && board[5][1] == playerNum);
	}

	@Override
	public AbstractBoard passes() {
		return new StarBoard(this);
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
