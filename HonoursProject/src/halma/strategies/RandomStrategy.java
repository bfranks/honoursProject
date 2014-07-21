package halma.strategies;

import java.util.ArrayList;

import boards.AbstractBoard;

public class RandomStrategy implements Strategy{

	int heuristicType;
	
	public RandomStrategy(int heuristicType){
		this.heuristicType = heuristicType;
	}
	
	@Override
	/**
	 * This method selects a random move
	 * @param b The current game state
	 * @return	the game state of a randomMove
	 */
	public AbstractBoard makeMove(AbstractBoard b){
		ArrayList<AbstractBoard> possibleMoves = b.getChildren(heuristicType);
		if (possibleMoves.size() == 0){
			return b.passes();
		}
		int index = (int)(Math.random()*possibleMoves.size());
		return possibleMoves.get(index);
	}

}
