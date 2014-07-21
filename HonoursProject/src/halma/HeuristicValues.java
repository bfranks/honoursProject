package halma;

import boards.Board;


public class HeuristicValues {
	private int heuristic1=0;
	private int heuristic2=0;
	private int hopValue=0;
	private int moves = 1;
	public static int BIGNUMBER = 10000;
	public HeuristicValues(){
		heuristic1=Board.startingDistance;
		heuristic2=Board.startingDistance*3;
	}
	
	public void isWon(){
		heuristic1 = BIGNUMBER;
		heuristic2 = BIGNUMBER;
		hopValue = 0;
	}
	
	public void isWon(int moves){
		if (moves==0)
			moves=1;
		this.moves=moves;
		heuristic1 = BIGNUMBER;
		heuristic2 = BIGNUMBER;
		hopValue = 0;
	}
	
	public void updateHeuristic1(int value){
		heuristic1=heuristic1-value;
	}
	public void updateHeuristic2(int value){
		heuristic2-=value;
	}
	public void updateHeuristic3(){
		hopValue++;
	}
	
	public HeuristicValues minimize(){
		heuristic1 = -1;
		heuristic2 = -1;
		return this;
	}
	public int getHeuristic(int heuristicType){
		if(heuristicType==1){
			return getHeuristic1();
		}else if (heuristicType==3){
			if (heuristic2<0){
				heuristic2=0;
			}
			return getHeuristic2();
		}else{
			return getHeuristic3();
		}
	}
	public int getHeuristic1(){
		return heuristic1;
	}
	public int getHeuristic2(){
		if (heuristic2<0){
			heuristic2=0;
		}
		return heuristic2*moves;
	}
	public int getHeuristic3(){
		return Math.max(0, heuristic1 + hopValue)*moves;
	}

	public void setHeuristic1(int heuristic1) {
		this.heuristic1 = heuristic1;
	}
	
	public void setHeuristic2(int heuristic2) {
		this.heuristic2 = heuristic2;
	}

	public void setHeuristic3(int heuristic3) {
		hopValue = heuristic3;
	}
}
