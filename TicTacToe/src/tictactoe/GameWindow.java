/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
// TO BETTER UNDERSTAND ALGORITHM CHANGE O TO X AND X TO O;
package tictactoe;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 *
 * @author ASHU
 */
public class GameWindow implements ActionListener,Runnable{
    JFrame frame;
    JButton gameboard[][] = new JButton[3][3];
    int board[][]=new int[3][3];
    int turn;
    List<Move> nextMoves;
    List<Integer> nextScores;
    private Thread threadObject;
    boolean player;
    JButton close;
    JLabel title;
  
    class Move{
        int i;
        int j;
        Move(int i,int j)
        {
            this.i=i;
            this.j=j;
        }
    }
    GameWindow()
    {
            frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().setBackground(Color.LIGHT_GRAY );
            frame.setLayout(null);
            frame.setSize(400,550);
          //  frame.setUndecorated(true);
            
            frame.setVisible(true);
            frame.setTitle("Tic Tac Toe");
            frame.setLocationRelativeTo(null);
            turn = 0; // Turn of O player
            
            createGUI();
            player = false;
            threadObject = new Thread(this);
            threadObject.start();

    }
    void createGUI()
    {
        for(int i=0;i<3;i++)
        {
            for(int j=0;j<3;j++)
            {
                gameboard[i][j] = new JButton(" ");
                gameboard[i][j].setBounds(j*120+10,i*120+50,120,120);
                gameboard[i][j].setActionCommand(""+(i*3+j));
                gameboard[i][j].addActionListener(this);
                gameboard[i][j].setFocusable(false);
                gameboard[i][j].setBackground(Color.WHITE);
                Font font = new Font("Century Gothic", Font.BOLD,80);
                gameboard[i][j].setFont(font);
        
                frame.add(gameboard[i][j]);
                board[i][j]=-1; // All board positions are empty
            }
        }
        close = new JButton("Close");
        close.setBounds(90,430,200,50);
        close.setBackground(Color.WHITE);
        Font font = new Font("Century Gothic", Font.BOLD,30);
        close.setFont(font);
        close.setActionCommand("close");
        close.addActionListener(this);
        frame.add(close);
         
               
        title = new JLabel("Tic Tac Toe(minimax)");
        title.setBounds(50,0,350,50);
        title.setBackground(Color.WHITE);
        font = new Font("Century Gothic", Font.BOLD,30);
        title.setFont(font);
        frame.add(title);
        
        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
       String command = e.getActionCommand();
       if(command!=null)
       {
           if(command.equals("close"))
               System.exit(0);
           int pos = Integer.parseInt(command);
           int x=pos/3;
           int y=pos%3;
           player = true;
           boolean set=false;
           if(turn == 1)
           {
               if(board[x][y]==-1){
                   set=true;
                   board[x][y]=1;
                   gameboard[x][y].setText("O");
                   //turn = 0;
                   
               }
           }
           else if(turn == 0)
           {
               if(board[x][y]==-1){
                   set=true;
                   board[x][y]=0;
                   gameboard[x][y].setText("X");
                  // turn = 1;
               }
           }
           if(set)
           {
                // Resume
                synchronized(threadObject)
                {
                    threadObject.notify();
                }
           }
       }
    }

    boolean isGameOver()
    {
        if(hasXWon()||hasOWon())
            return true;
        if(plausibleMoveGenerator().isEmpty())
            return true;
        return false;
    }
    boolean hasXWon()
    {
        if((board[0][0]==board[1][1]&&board[0][0]==board[2][2]&&board[0][0]==1)||
           (board[2][0]==board[1][1]&&board[2][0]==board[0][2]&&board[2][0]==1))//Diagonal Win
            return true;
        
        for(int i=0;i<3;i++)
        {
            if((board[0][i]==board[1][i]&&board[0][i]==board[2][i]&&board[0][i]==1)||
               (board[i][0]==board[i][1]&&board[i][0]==board[i][2]&&board[i][0]==1))
                return true;
        }
        return false;
    }
    boolean hasOWon()
    {
        if((board[0][0]==board[1][1]&&board[0][0]==board[2][2]&&board[0][0]==0)||
           (board[2][0]==board[1][1]&&board[2][0]==board[0][2]&&board[2][0]==0))//Diagonal Win
            return true;
        
        for(int i=0;i<3;i++)
        {
            if((board[0][i]==board[1][i]&&board[0][i]==board[2][i]&&board[0][i]==0)||
               (board[i][0]==board[i][1]&&board[i][0]==board[i][2]&&board[i][0]==0))
                return true;
        }
        return false;
    }
    int minimax(int depth,int turn)
    {
        if(hasXWon())
            return 10-depth; // Modify to 10 - depth
        else if(hasOWon())
            return depth-10; // Modify to depth - 10
        
        List<Move> availableMoves = plausibleMoveGenerator();
        List<Integer> scores = new ArrayList<Integer>(); 
        if(availableMoves.isEmpty())
            return 0;
        
        for(int i=0;i<availableMoves.size();i++)
        {
            Move move = availableMoves.get(i);
            if(turn == 1)// X's Turn
            {//Maximizer is here
                 board[move.i][move.j]=1;// Place the move (done by algo so it is needed to be reset afterwards)
                 int currentScore = minimax(depth+1,0);
                 scores.add(currentScore);// Add the current score to the list of all possible Scores
                 if(depth == 0)
                 {
                     nextMoves.add(move);
                     nextScores.add(currentScore);
                 }
            }
            else if(turn == 0) // O's Turn
            {//Minimizer is here
                board[move.i][move.j]=0;// Place the move
                int currentScore = minimax(depth+1,1);
                scores.add(currentScore);// Add the current score to the list of all possible Scores
            }
            board[move.i][move.j]=-1;// Reset board position
            
        }
        if(turn == 1)
            return getMax(scores);
        else if(turn == 0)
            return getMin(scores);
        
        return 0;
    }
    int getMax(List<Integer> scores)
    {
        int max=Integer.MIN_VALUE; // As scores can be negative
        for(int i=0;i<scores.size();i++)
        {
            if(scores.get(i)>max)
                max=scores.get(i);
        }
        return max;
    }
    int getMin(List<Integer> scores)
    {
        int min=Integer.MAX_VALUE; // As scores can be negative
        for(int i=0;i<scores.size();i++)
        {
            if(scores.get(i)<min)
                min=scores.get(i);
        }
        return min;
    }
    
    List<Move> plausibleMoveGenerator()
    {
        List<Move> plausibleMoves = new ArrayList<Move>();
        for(int i=0;i<3;i++)
        {
            for(int j=0;j<3;j++)
            {
                if(board[i][j]==-1) // board position is empty
                {
                    plausibleMoves.add(new Move(i,j));
                }
            }
        }
        return plausibleMoves;
    }

    void AlgorithmMove()
    {
        nextMoves = new ArrayList<Move>();
        nextScores = new ArrayList<Integer>();
        minimax(0,1);
        Move move = selectBestMove();
        board[move.i][move.j]=1;        
        gameboard[move.i][move.j].setText("O");
    }
    Move selectBestMove()
    {
        int max = Integer.MIN_VALUE;
        int index=-1;
        for(int i=0;i<nextMoves.size();i++)
        {
            if(max<nextScores.get(i))
            {
                max = nextScores.get(i);
                index = i;
            }
        }
        return nextMoves.get(index);
    }
    
    @Override
    public void run() 
    {
        while(!isGameOver())
        {
            synchronized(threadObject)
            {
                // Pause
                try 
                {
                    threadObject.wait();
                } 
                catch (InterruptedException e) 
                {
                }
            }
            if(isGameOver())
                break;
            /*for(int i=0;i<3;i++)
            {
                for(int j=0;j<3;j++)
                    System.out.print(board[i][j]+"  ");
                System.out.println();
            }*/
            
            AlgorithmMove();
            
            if(isGameOver())
               break;
        }
        // GAME OVER
        for(int i=0;i<3;i++)
        {
            for(int j=0;j<3;j++)
                gameboard[i][j].removeActionListener(this);//.disable();
        }
        
        String st;
        if(hasXWon())
            st="CPU Wins";
        else if(hasOWon())
            st="Player Wins";
        else
            st="Draw";
        
        JLabel label = new JLabel(st);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        JOptionPane.showMessageDialog(frame, label,"Game Over",JOptionPane.PLAIN_MESSAGE);
    }
    
}
