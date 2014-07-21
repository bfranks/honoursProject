package halma;

import guis.CCGUI;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Properties;

import boards.AbstractBoard;
import boards.Board;
import boards.StarBoard;
import boards.TriangleBoard;

public class Driver {
	LinkedList<AbstractBoard> moveHistory = new LinkedList<AbstractBoard>();
	int turn = 1;
	int numPlayers=4;
	int limitMoves=-1;
	CCGUI gui;
	ArrayList<Player> playerList;
	int boardSize;
	BufferedReader inputReader;
	FileWriter fw;
	int playNTimes;
	
	private enum BoardTypes {
		square,
		star,
		triangle,
		other
	}
	BoardTypes boardType;
	
	public Driver(){
		inputReader=new BufferedReader(new InputStreamReader(System.in));
		configureWriter();
		playNTimes =getNumberOfGames();
		getBoardType();
		limitMoves=getLimitMoves();
		playerList = new ArrayList<Player>();
		for (int i=0;i<numPlayers;i++){
			configurePlayer(i);
		}
	}
	
	public Driver(String config){
		if(!PropertyTable.initialize(config)){
			System.out.println("config file is missing or invalid");
		}
		Properties props = PropertyTable.getInstance();
		inputReader=new BufferedReader(new InputStreamReader(System.in));
		try {
			fw = new FileWriter(props.getProperty("stats.file"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			playNTimes = Integer.parseInt(props.getProperty("stats.runNTimes"));
		}catch (NumberFormatException e){
			System.out.println("Input error, please enter just 1 number");
		}
		//getBoardType();
		if(props.getProperty("board.type").equals("square")){
			boardType = BoardTypes.square;
			numPlayers = 4;
			boardSize=Integer.parseInt(props.getProperty("board.size"));
		}else if(props.getProperty("board.type").equals("star")){
			boardType = BoardTypes.star;
			try{
				numPlayers = Integer.parseInt(props.getProperty("board.numPlayers"));
			}catch (NumberFormatException e){
				System.err.println("Config error, board.numPlayers must be an integer");
				System.exit(0);
			}
		}else if(props.getProperty("board.type").equals("triangle")){
			boardType = BoardTypes.triangle;
			numPlayers = 3;
		}else{
			System.err.println("Config error, board.type must be star or square");
			System.exit(0);
		}
		limitMoves=Integer.parseInt(props.getProperty("board.limitToMoves"));
		playerList = new ArrayList<Player>();
		for (int i=0;i<numPlayers;i++){
			int heuristic=0;
			int lookAhead=0;
			try{
				heuristic = Integer.parseInt(props.getProperty("player."+i+".heuristic"));
			}catch (NumberFormatException e){
				System.err.println("Config error, player."+i+".heuristic must be an integer");
				System.exit(0);
			}
			try{
				lookAhead = Integer.parseInt(props.getProperty("player."+i+".lookahead"));
			}catch (NumberFormatException e){
				System.err.println("Config error, player."+i+".lookahead must be an integer");
				System.exit(0);
			}
			PlayerTypes playerType;
			if(props.getProperty("player."+i+".playerType").equals("maxn"))
				playerType = PlayerTypes.maxn;
			else if(props.getProperty("player."+i+".playerType").equals("paranoid"))
				playerType =  PlayerTypes.paranoid;
			else if(props.getProperty("player."+i+".playerType").equals("bestReply"))
				playerType =  PlayerTypes.bestReply;
			else{
				System.out.println("RANDOM!!!");
				System.exit(0);
				playerType =  PlayerTypes.random;
			}
			playerList.add(i, new Player(heuristic,playerType,lookAhead,i+1));
		}
	}
	
	private void configureWriter(){
		try {
			System.out.println("enter file name to write stats to");
			String data = inputReader.readLine();
			fw = new FileWriter(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private int getNumberOfGames(){
		int value=0;
		boolean acceptedBoardSize = false;
		while(!acceptedBoardSize){
			try {
				System.out.println("How many games would you like to run");
				String data= inputReader.readLine();
				value = Integer.parseInt(data);
				if(value>0)
					acceptedBoardSize=true;
				else
					System.out.println("There must be more than 0 games");
			} catch (IOException e) {
				System.out.println("Input error, please enter just 1 number");
			}catch (NumberFormatException e){
				System.out.println("Input error, please enter just 1 number");
			}
		}
		return value;
	}
	
	public void configurePlayer(int playerNum){
		boolean isSuccess = false;
		while(!isSuccess){
			try {
				System.out.println("Configuring player " + (playerNum+1));
				int heuristic = getHeuristic();
				int lookAhead = getLookAhead();
				PlayerTypes playerType = getPlayerType();
				isSuccess=true;
				playerList.add(playerNum, new Player(heuristic,playerType,lookAhead,playerNum+1));
			} catch (IOException e) {
				System.out.println("Input error, retrying");
			}catch (NumberFormatException e){
				System.out.println("Input error, please enter just 1 number");
			}
		}
	}
	
	private void getBoardType(){
		while(true){
			try{
				System.out.println("Which Board would you like to use?"+
						"\n\t1. Square"+
						"\n\t2. Star"+
						"\n\t3. Triangle"+
						"\n\t4. other");
				int data = Integer.parseInt(inputReader.readLine());
				if(data==1){
					boardType = BoardTypes.square;
					boardSize=getBoardSize();
				}else if(data==2){
					boardType = BoardTypes.star;
					numPlayers = getNumberOfPlayers();
				}else if(data==3){
					boardType = BoardTypes.triangle;
					numPlayers = 3;
				}else
					boardType = BoardTypes.other;
				break;
			}catch (IOException e){
				
			}catch (NumberFormatException e){
				
			}
		}
	}
	
	public int getNumberOfPlayers(){
		int value=0;
		boolean acceptedBoardSize = false;
		while(!acceptedBoardSize){
			try {
				System.out.println("How many players (2,3,4,6)");
				String data= inputReader.readLine();
				value = Integer.parseInt(data);
				if(value!=2||value!=3||value!=4||value!=6)
					acceptedBoardSize=true;
				else
					System.out.println("There must be 2,3,4, or players");
			} catch (IOException e) {
				System.out.println("Input error, please enter just 1 number");
			}catch (NumberFormatException e){
				System.out.println("Input error, please enter just 1 number");
			}
		}
		return value;
	}
	
	public int getBoardSize(){
		int value=0;
		boolean acceptedBoardSize = false;
		while(!acceptedBoardSize){
			try {
				System.out.println("What board size would you like to play on (board is nxn please enter n)");
				String data= inputReader.readLine();
				value = Integer.parseInt(data);
				if(value>=6)
					acceptedBoardSize=true;
				else
					System.out.println("Size must be at least 6, please try again.");
			} catch (IOException e) {
				System.out.println("Input error, please enter just 1 number");
			}catch (NumberFormatException e){
				System.out.println("Input error, please enter just 1 number");
			}
		}
		return value;
	}
	public int getLimitMoves(){
		int value=0;
		boolean acceptedBoardSize = false;
		while(!acceptedBoardSize){
			try {
				System.out.println("Limit to the best n moves (-1) to allow all moves");
				String data= inputReader.readLine();
				value = Integer.parseInt(data);
				if(value>0||value==-1)
					acceptedBoardSize=true;
				else
					System.out.println("Must be greater than 0 or -1");
			} catch (IOException e) {
				System.out.println("Input error, please enter just 1 number");
			}catch (NumberFormatException e){
				System.out.println("Input error, please enter just 1 number");
			}
		}
		return value;
	}
	
	public int getHeuristic()throws IOException{
		System.out.println("Which Heuristic would you like to use?"+
				"\n\t1. Own pieces"+
				"\n\t2. All pieces"+
				"\n\t3. Hops");
		String data = inputReader.readLine();
		return Integer.parseInt(data);
	}
	public int getLookAhead()throws IOException{
		System.out.println("How many moves would you like to look ahead?");
		String data = inputReader.readLine();
		return Integer.parseInt(data);
	}
	public PlayerTypes getPlayerType()throws IOException{
		System.out.println("Which Algorithm would you like this player to use?"+
				"\n\t1. maxn"+
				"\n\t2. paranoid"+
				"\n\t3. best reply"+
				"\n\t4. random selection");
		String data = inputReader.readLine();
		int selected = Integer.parseInt(data);
		if(selected==1)
			return PlayerTypes.maxn;
		else if(selected==2)
			return PlayerTypes.paranoid;
		else if(selected==3)
			return PlayerTypes.bestReply;
		else if(selected==4)
			return PlayerTypes.random;
		else
			return PlayerTypes.other;
	}
	
	public void play() throws IOException{
		AbstractBoard init;
		if (boardType == BoardTypes.square)
			init = new Board(turn,boardSize,limitMoves);
		else if(boardType == BoardTypes.star){
			StarBoard.init(numPlayers);
			init = new StarBoard(turn,limitMoves);
		}else if(boardType == BoardTypes.triangle){
			TriangleBoard.init();
			init = new TriangleBoard(turn,limitMoves);
		}else 
			init = new Board(turn,boardSize,limitMoves);
		gui = new CCGUI(AbstractBoard.boardLength,AbstractBoard.boardWidth);
		gui.update(init,false);
		System.out.println(init.toString());
		moveHistory.addLast(init);
		while(true){
			AbstractBoard move;
			System.out.println("Player " + turn+" turn");
			move = playerList.get(turn-1).makeMove(moveHistory.getLast());
			moveHistory.getLast().children.clear();
			System.out.println(move.toString());
			moveHistory.addLast(move);
			if(move.isWin()){
				gui.update(move,true);
				fw.write(move.lastMoved+"\n");
				System.out.println("They Win!");
				return;
			}else{
				gui.update(move,false);
			}
			System.out.println("*******************");
			turn=turn%numPlayers+1;
		}
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try{
			Driver driver;
			if (args.length==1)
				driver = new Driver(args[0]);
			else
				driver = new Driver();
			//driver.testPossibleMoves();
			for (int i=0;i<driver.playNTimes;i++){
				driver.play();
			}
			driver.fw.close();
			System.exit(0);
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	catch(OutOfMemoryError e){
			System.out.println("OutOfMemoryError: try expanding the runtime memory, limiting to a smaller number of moves, looking less far ahead, or using a smaller board.");
		}
	}

}
