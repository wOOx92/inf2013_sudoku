import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
//Resizable with LayoutManager
public class GUI_Window {

	private JFrame frame;

	private NumberPuzzle puzzle;
	private JTextField[][] gameField = new JTextField[9][9];
	
	//Initialise Buttons
	private JButton btnNewGame;
	private JButton btnRedo;
	private JButton btnUndo;
	private JButton btnReset;
	private JButton btnQuit;
	private JButton btnHint;
	
	/**
	 * Create the application.
	 */
	public GUI_Window() {
		initialize();
	}

	public JFrame getJFrame(){
		return this.frame;
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 497, 413);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setTitle("Judoku 0.0.0.1");

		// positioning and sizing the text fields
		int xPosition = 10;
		int yPosition = 10;
		final int width = 40;
		final int height = 40;

		//draw the gamefield
		
		Color active = Color.WHITE;
		Color toggle = Color.LIGHT_GRAY;
		
		for (int i = 0; i < 9; i++) {
			yPosition = 10;
			for (int j = 0; j < 9; j++) {
				if(j%3 == 0){
					Color buffer = active;
					active = toggle;
					toggle = buffer;
				}
				
				gameField[i][j] = new JTextField();
				gameField[i][j].setColumns(10);
				//set font size in gameField
				Font font = new Font("Arial", Font.BOLD, 32);
				gameField[i][j].setFont(font);
				gameField[i][j].setHorizontalAlignment(JTextField.CENTER);
				gameField[i][j].setBounds(xPosition, yPosition, width, height);
				gameField[i][j].setBackground(active);
				frame.getContentPane().add(gameField[i][j]);
				
				yPosition = yPosition + 38;
			}
			if((i+1)%3 != 0){
				Color buffer = active;
				active = toggle;
				toggle = buffer;
			}
			xPosition = xPosition + 38;
		}
		
		//TODO: Progress bar?   [---->     ]

		
		//Declare Buttons
		btnNewGame = new JButton("New Game");
		btnNewGame.setBounds(370, 11, 100, 40);
		frame.getContentPane().add(btnNewGame);
		btnNewGame.addActionListener(new ButtonLauscher()); 
		
		btnReset = new JButton("Reset");
		btnReset.setBounds(370, 50, 100, 40);
		frame.getContentPane().add(btnReset);
		btnReset.addActionListener(new ButtonLauscher());
		btnReset.setEnabled(false);
		
		btnUndo = new JButton("Undo");
		btnUndo.setBounds(370, 89, 100, 40);
		frame.getContentPane().add( btnUndo);
		btnUndo.addActionListener(new ButtonLauscher());
		btnUndo.setEnabled(false);
		
		btnRedo = new JButton("Redo");
		btnRedo.setBounds(370, 128, 100, 40);
		frame.getContentPane().add(btnRedo);
		btnRedo.addActionListener(new ButtonLauscher());
		btnRedo.setEnabled(false); 
		
		btnHint = new JButton("Give Hint");
		btnHint.setBounds(370, 167, 100, 40);
		frame.getContentPane().add(btnHint);
		btnHint.addActionListener(new ButtonLauscher()); 
		btnHint.setEnabled(false);
		
		btnQuit = new JButton("Quit Game");
		btnQuit.setBounds(370, 314, 100, 40);
		frame.getContentPane().add(btnQuit);
		btnQuit.addActionListener(new ButtonLauscher()); 
		
		JTextPane txtpnNochVersuch = new JTextPane();
		txtpnNochVersuch.setText("Noch 1 Versuch");
		txtpnNochVersuch.setBounds(370, 245, 100, 40);
		frame.getContentPane().add(txtpnNochVersuch);
	}
	
	public void refreshView() {
		int recentGrid[][] = this.puzzle.getRecentGrid();
		
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {	
				if(recentGrid[i][j] != 0){
					gameField[i][j].setText(String.valueOf(recentGrid[i][j])); 
				}
				else{
					gameField[i][j].setText("");
				}
			}
		}
	}
	
	public void giveHint(){
		
	}
	
	class ButtonLauscher implements ActionListener { 
        public void actionPerformed(ActionEvent e) { 
        	if(e.getSource() == btnQuit){ 
                frame.dispose();
            } else if(e.getSource() == btnNewGame){
            		puzzle = new Sudoku(Difficulty.HARD);
            		btnHint.setEnabled(true);
            		btnUndo.setEnabled(true);
            		btnRedo.setEnabled(true);
            		btnReset.setEnabled(true);
            		
            		refreshView();
            } else if(e.getSource() == btnReset){
            	if(puzzle != null){
            		puzzle.reset();
            		refreshView();
            	}
            } else if(e.getSource() == btnUndo){
            	if(puzzle != null){
            		puzzle.undo();
            		refreshView();
            	}
            } else if(e.getSource() == btnRedo){
            	if(puzzle != null){
            		puzzle.redo();
            		refreshView();
            	}
            } else if(e.getSource() == btnHint){
            	if(puzzle != null){
            		int[] coords = puzzle.searchMistake();
            		if(coords.length == 0){
            			puzzle.giveHint();
            		}
            		else{
            			gameField[coords[1]][coords[0]].setBackground(Color.RED);
            			refreshView();
            		}
            	}
            }
        } 
    } 
}


