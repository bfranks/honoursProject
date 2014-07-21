package boards;

import halma.HeuristicValues;

import java.util.ArrayList;

public abstract class AbstractBoard{
	static int limitToBestNMoves=-1;
	private int minCutOff=Integer.MAX_VALUE;
	public static int boardLength;
	public static int boardWidth;
	public static int numPlayers =4;
	public int[][] board;
	public int turn;
	public int lastMoved;
	public ArrayList<AbstractBoard> children = new ArrayList<AbstractBoard>();
	public static int startingDistance;
	ArrayList<HeuristicValues> heuristicValues = new ArrayList<HeuristicValues>();
	
	
	
	/**
	 * 
	 * @return whether or not the last move won the game
	 */
	public abstract boolean isWin();
	protected abstract void addLegalMovesFor(int i, int j, int heuristicType);
	public abstract AbstractBoard passes();

	public void printBoard() {
		for (int i = 0; i < boardLength; i++) {
			String row = "";
			for (int j = 0; j < boardWidth; j++) {
				row += board[i][j] + " ";
			}
			System.out.println(row);
		}
	}

	public String toString() {
		String value = "";
		for (int i = 0; i < boardLength; i++) {
			for (int j = 0; j < boardWidth; j++) {
				value += board[i][j] + " ";
			}
			value += "\n";
		}
		return value;
	}
	
	/**
	 * Used by the maxn algorithm
	 * @return an ArrayList of heuristic values
	 */
	public ArrayList<HeuristicValues> getHeuristic(){
		return heuristicValues;
	}
	
	//returns a heuristic int (used by paranoid)
	public int getHeuristic(int heuristicType,int index) {
		if(heuristicType==1){
			return heuristicValues.get(index-1).getHeuristic1();
		}else if(heuristicType==2){
			return heuristicValues.get(index-1).getHeuristic2();
		}else if(heuristicType==3){
			return heuristicValues.get(index-1).getHeuristic3();
		}
		return 0;
	}
	
	/**
	 * Generates and returns the children.
	 * @param heuristicType	heuristicType on which to compare the children
	 * @return children of that game state (may be reduced to comply with configured limits)
	 */
	public ArrayList<AbstractBoard> getChildren(int heuristicType) {
		children.clear();
		for (int i = 0; i < boardLength; i++) {
			for (int j = 0; j < boardWidth; j++) {
				if (board[i][j] == turn) {
					addLegalMovesFor(i, j,heuristicType);
				}
			}
		}

		return children;
	}

	//used to swap to pieces on the board
	protected void moveTo(int row, int col, int row2, int col2) {
		int temp = board[row][col];
		board[row][col] = board[row2][col2];
		board[row2][col2] = temp;
	}
	
	/**
	 * This method will add children to the child list according to how it limited
	 * 
	 * @param b				child to be added
	 * @param heuristicType heuristic type to compare the value
	 */
	protected void addChild(AbstractBoard b,int heuristicType){
		if (limitToBestNMoves==-1){
			children.add(b);
		}else{
			if (children.size()<limitToBestNMoves){
				children.add(b);
				minCutOff = Math.min(b.getHeuristic(heuristicType, turn),minCutOff);
			}else if(b.getHeuristic(heuristicType, turn)>minCutOff){
				int index=0;
				for (int i=0; i<children.size();i++){
					if(children.get(i).getHeuristic(heuristicType, turn)==minCutOff){
						index=i;
						break;
					}
				}
				children.remove(index);
				children.add(b);
				minCutOff = Integer.MAX_VALUE;
				for (AbstractBoard c:children){
					if(c.getHeuristic(heuristicType, turn)<=minCutOff)
						minCutOff = c.getHeuristic(heuristicType, turn);
				}
			}else{
				b=null;
			}
		}
	}
}
