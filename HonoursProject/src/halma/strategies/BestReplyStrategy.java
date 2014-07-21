package halma.strategies;

import java.util.ArrayList;
import java.util.HashMap;

import boards.AbstractBoard;

public class BestReplyStrategy implements Strategy{
	int heuristicType;
	int playerNumber;
	int depthSearch;
	
	public BestReplyStrategy(int depthSearch,int heuristicType,int playerNumber){
		this.depthSearch = depthSearch;
		this.heuristicType = heuristicType;
		this.playerNumber = playerNumber;
	}
	@Override
	public AbstractBoard makeMove(AbstractBoard b) {
		HashMap<Integer,AbstractBoard> values = new HashMap<Integer,AbstractBoard>();
		for(AbstractBoard child:b.getChildren(heuristicType)){
			Integer val = bestReplySearch(child,Integer.MIN_VALUE+1,Integer.MAX_VALUE-1,depthSearch-1,"MIN");
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
	
	public int bestReplySearch(AbstractBoard b, int alpha,int beta, int depth,String turn){
		if(b.isWin()&&b.lastMoved==playerNumber){
			b.getHeuristic().get(b.lastMoved-1).isWon(depth);
		}
		if (depth <= 0|| (b.isWin()&&b.lastMoved==playerNumber)){
			return b.getHeuristic(heuristicType, playerNumber);
		}
		ArrayList<AbstractBoard> moves = new ArrayList<AbstractBoard>();
		if (turn.equals("MAX")){
			while(b.turn!=playerNumber)
				b = b.passes();
			moves.addAll(b.getChildren(heuristicType));
			turn = "MIN";
		}else{
			while(b.turn!=playerNumber){
				moves.addAll(b.getChildren(heuristicType));
				b = b.passes();
			}
			turn = "MAX";
		}
		
		for (AbstractBoard board: moves){
			//MAX but turn has already been flipped
			if (turn.equals("MIN")){
				alpha = Math.max(alpha, bestReplySearch(board,alpha,beta,depth-1,turn));
				if(alpha>=beta)
					return alpha;
			}//otherwise minimize
			else{
				beta = Math.min(beta, bestReplySearch(board,alpha,beta,depth-1,turn));
				if(alpha>=beta)
					return beta;
			}
		}
		
		//passing
		if(moves.isEmpty()){
			//MAX but turn has already been flipped
			if (turn.equals("MIN")){
				alpha = Math.max(alpha, bestReplySearch(b.passes(),alpha,beta,depth-1,turn));
				if(alpha>=beta)
					return alpha;
			}//otherwise minimize
			else{
				beta = Math.min(beta, bestReplySearch(b,alpha,beta,depth-1,turn));
				if(alpha>=beta)
					return beta;
			}
			
		}
		if (turn.equals("MIN"))
			return alpha;
		else
			return beta;
	}

}
