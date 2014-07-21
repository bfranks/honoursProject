package halma.strategies;

import java.util.HashMap;

import boards.AbstractBoard;

public class ParanoidStrategy implements Strategy{
	int depthSearch;
	int heuristicType;
	int playerNumber;
	
	public ParanoidStrategy(int depthSearch,int heuristicType,int playerNumber){
		this.depthSearch = depthSearch;
		this.heuristicType = heuristicType;
		this.playerNumber = playerNumber;
	}

	@Override
	public AbstractBoard makeMove(AbstractBoard b) {
		// TODO Auto-generated method stub
		return paranoid(b,depthSearch);
	}

	
	/**
	 * This method assumes that the current player will maximize their move
	 * while all opposing players are trying to minimize their gameplay.
	 * 
	 * @param b		The current game state
	 * @param depth	The depth in which to search
	 * @return		The best game state as outlined above.
	 */
	public AbstractBoard paranoid(AbstractBoard b, int depth){
		HashMap<Integer,AbstractBoard> values = new HashMap<Integer,AbstractBoard>();
		for(AbstractBoard child:b.getChildren(heuristicType)){
			Integer val = paranoidAlphaBeta(child,depth-1,Integer.MIN_VALUE,Integer.MAX_VALUE);
			if(!(values.containsKey(val)&&Math.random()>0.25))
				values.put(val,child);
		}
		//passes
		if (b.children.isEmpty()){
			return b.passes();
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
	 * This method uses alpha beta pruning and is called by paranoid
	 * @param b				The predecessor state
	 * @param depth			How much farther to keep looking
	 * @param parentWasNull	
	 * @param alpha
	 * @param beta
	 * @return
	 */
	private int paranoidAlphaBeta(AbstractBoard b,int depth,int alpha,int beta){
		if(b.isWin()){
			b.getHeuristic().get(b.lastMoved-1).isWon(depth);
		}
		if (depth<=0||(b.isWin()&&b.lastMoved==playerNumber))
			return b.getHeuristic(heuristicType,playerNumber);
		for(AbstractBoard child: b.getChildren(heuristicType)){
			//if the paranoid player must go
			if (b.turn==playerNumber){
				alpha = Math.max(alpha, paranoidAlphaBeta(child,depth-1,alpha,beta));
				if(alpha>=beta)
					return alpha;
			}//otherwise minimize
			else{
				beta = Math.min(beta, paranoidAlphaBeta(child,depth-1,alpha,beta));
				if(alpha>=beta)
					return beta;
			}
		}
		//can't move
		if(b.children.isEmpty()){
			if (b.lastMoved==playerNumber){
				alpha = Math.max(alpha, paranoidAlphaBeta(b.passes(),depth-1,alpha,beta));
				if(alpha>=beta)
					return alpha;
			}else{
				beta = Math.min(beta, paranoidAlphaBeta(b.passes(),depth-1,alpha,beta));
				if(alpha>=beta)
					return beta;
			}
		}
		if (b.turn==playerNumber)
			return alpha;
		else
			return beta;
	}
}
