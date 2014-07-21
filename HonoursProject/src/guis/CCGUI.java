package guis;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import boards.AbstractBoard;

public class CCGUI extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JPanel buttons=new JPanel();
	JPanel status=new JPanel();
	JButton board [][];
	JLabel currentTurn;
	JLabel message;
	ImageIcon pieces []=new ImageIcon[7];
	int boardLength;
	int boardWidth;
	
	public CCGUI(int boardLength, int boardWidth){
		super("Chinese Checkers");
		this.boardLength= boardLength;
		this.boardWidth= boardWidth;
		board = new JButton[boardLength][boardWidth];
		pieces[0]=new ImageIcon("0.png");
		pieces[1]=new ImageIcon("1.png");
		pieces[2]=new ImageIcon("2.png");
		pieces[3]=new ImageIcon("3.png");
		pieces[4]=new ImageIcon("4.png");
		pieces[5]=new ImageIcon("5.png");
		pieces[6]=new ImageIcon("6.png");
		
	    
	    GridBagLayout layout = new GridBagLayout();
	    GridBagConstraints con = new GridBagConstraints();
	    setLayout(layout);
	    
	    buttons.setLayout(new GridLayout(boardLength,boardWidth,2,2));
	    for(int i=0;i<boardLength;i++){
	    	for (int j=0;j<boardWidth;j++){
	    		board[i][j]=new JButton();
	    		board[i][j].setSize(20, 20);
	    		buttons.add(board[i][j]);
	    	}
	    }
	    con.gridx = 0;
	    con.gridy = 0;
	    con.gridwidth=1;
	    con.gridheight=boardLength;
	    con.weightx = 0;
	    con.weighty = 0;
	    layout.setConstraints(buttons,con);
	    add(buttons);
	    
	    status.setLayout(new GridLayout(2,1,2,2));
	    currentTurn = new JLabel("Player 1's turn");
	    message = new JLabel("Good luck");
	    status.add(currentTurn);
	    status.add(message);
	    con.gridx = 0;
	    con.gridy = 7;
	    con.gridwidth=1;
	    con.gridheight=1;
	    layout.setConstraints(status,con);
	    add(status);
	    
	    setBounds(100,100,450,400);
	    setDefaultCloseOperation(EXIT_ON_CLOSE);
	    setVisible(true);
	}
	
	public void update(AbstractBoard b,boolean isWon){
		for(int i=0;i<boardLength;i++){
	    	for (int j=0;j<boardWidth;j++){
	    		if(b.board[i][j]==-1){
	    			board[i][j].setEnabled(false);
	    			buttons.add(board[i][j]);
	    		}else{
	    			board[i][j].setIcon(pieces[b.board[i][j]]);
	    			buttons.add(board[i][j]);
	    		}
	    	}
	    }
		currentTurn.setText("Player "+b.turn+"'s turn");
		if(isWon){
			message.setText("Player "+b.lastMoved+" won!!!");
		}
	}
}
