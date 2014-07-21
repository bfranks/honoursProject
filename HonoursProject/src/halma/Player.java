package halma;

import halma.strategies.BestReplyStrategy;
import halma.strategies.MaxnStrategy;
import halma.strategies.ParanoidStrategy;
import halma.strategies.RandomStrategy;
import halma.strategies.Strategy;

import java.util.ArrayList;
import java.util.HashMap;

import boards.AbstractBoard;
import boards.Board;

public class Player {
	int heuristicType = 1;
	PlayerTypes typeOfPlayer;
	int depthSearch;
	int playerNumber;
	Strategy strat;
	
	/**
	 * 
	 * @param heuristicType	1=only own pieces, 2=all pieces
	 * @param typeOfPlayer	What algorithm that player will use
	 * @param depthSearch	The depth for that player to search
	 * @param playerNumber	The players number (1-4)
	 */
	public Player(int heuristicType, PlayerTypes typeOfPlayer,int depthSearch,int playerNumber){
		this.heuristicType=heuristicType;
		this.typeOfPlayer=typeOfPlayer;
		this.depthSearch = depthSearch;
		this.playerNumber = playerNumber;
		if(typeOfPlayer==PlayerTypes.maxn){
			strat = new MaxnStrategy(depthSearch,heuristicType);
		}else if(typeOfPlayer==PlayerTypes.paranoid){
			strat = new ParanoidStrategy(depthSearch,heuristicType,playerNumber);
		}else if(typeOfPlayer==PlayerTypes.bestReply){
			strat = new BestReplyStrategy(depthSearch,heuristicType,playerNumber);
		}else{
			strat = new RandomStrategy(heuristicType);
		}
	}
	
	/**
	 * The player will make a move based upon their player type
	 * 
	 * @param 	b	the game state for them to make a move in.
	 * @return	the game state once a move has been made.
	 */
	public AbstractBoard makeMove(AbstractBoard b){
		return strat.makeMove(b);
	}
	

	

}
