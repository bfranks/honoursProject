package halma.strategies;

import halma.HeuristicValues;

import java.util.ArrayList;
import java.util.HashMap;

import boards.AbstractBoard;

public class MaxnStrategy implements Strategy{
	int depthSearch;
	int heuristicType;
	
	public MaxnStrategy(int depthSearch,int heuristicType){
		this.depthSearch = depthSearch;
		this.heuristicType = heuristicType;
	}
	
	@Override
	public AbstractBoard makeMove(AbstractBoard b) {
		// TODO Auto-generated method stub
		return maxn(b,depthSearch);
	}

	/**
	 * This method assumes that all players will try to maximize their game state.
	 * 
	 * @param b		The current game state
	 * @param depth	The depth in which to search
	 * @return		The best game state as outlined above.
	 */
	public AbstractBoard maxn(AbstractBoard b, int depth){
		HashMap<Integer,AbstractBoard> values = new HashMap<Integer,AbstractBoard>();
		for(AbstractBoard child:b.getChildren(heuristicType)){
			Integer val = maxnValue(child,depth-1,Integer.MIN_VALUE).get(b.turn-1).getHeuristic(heuristicType);
			if(!(values.containsKey(val)&&Math.random()>0.25))
				values.put(val,child);
		}
		//passes
		if (b.children.isEmpty()){
			System.out.println("passing!");
			values.put(maxnValue(b.passes(),depth-1,Integer.MIN_VALUE).get(b.turn-1).getHeuristic(heuristicType),b.passes());
		}
		
		Integer max = Integer.MIN_VALUE;
		for(Integer key:values.keySet()){
			if(key>max){
				max = key;
			}
		}
		System.out.println("max:" + max);
		AbstractBoard ret = values.get(max);
		values.clear();
		return ret;
	}
	
	/**
	 * returns the heuristic vector at a game state
	 * 
	 * @param b		game state to analyze
	 * @param depth	depth to search
	 * @param bound	lower bound
	 * @return		The heuristic vector at the leaf game state
	 */
	private ArrayList<HeuristicValues> maxnValue(AbstractBoard b,int depth,int bound){
		if(b.isWin()){
			b.getHeuristic().get(b.lastMoved-1).isWon(depth);
		}
		if (depth<=0||b.isWin())
			return b.getHeuristic();
		ArrayList<HeuristicValues> profitVector = new ArrayList<HeuristicValues>();
		for (int i=0;i<AbstractBoard.numPlayers;i++)
			profitVector.add(new HeuristicValues().minimize());
		for(AbstractBoard child: b.getChildren(heuristicType)){
			ArrayList<HeuristicValues> values = maxnValue(child,depth-1,profitVector.get(b.turn-1).getHeuristic(heuristicType));
			if(values.get(b.turn-1).getHeuristic(heuristicType)>=profitVector.get(b.turn-1).getHeuristic(heuristicType))
				profitVector = values;
			if (profitVector.get(b.turn-1).getHeuristic(heuristicType)> HeuristicValues.BIGNUMBER*depthSearch-bound)
				return profitVector;
		}
		//passes
		if (b.children.isEmpty()){
			profitVector = maxnValue(b.passes(),depth-1,profitVector.get(b.turn-1).getHeuristic(heuristicType));
		}
		return profitVector;
	}
}
