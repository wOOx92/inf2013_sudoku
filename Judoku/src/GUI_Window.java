import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JTextPane;
//Resizable with LayoutManager
public class GUI_Window {

	private JFrame frame;

	private INumberPuzzle puzzle;
	private JTextField[][] gameField = new JTextField[9][9];
	
	//Initialise Buttons
	private JButton btnNewGame;
	private JButton btnRedo;
	private JButton btnUndo;
	private JButton btnReset;
	private JButton btnQuit;
	
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
		for (int i = 0; i < 9; i++) {
			yPosition = 10;
			for (int j = 0; j < 9; j++) {
				gameField[i][j] = new JTextField();
				gameField[i][j].setColumns(10);
				//set font size in gameField
				Font font = new Font("Arial", Font.BOLD, 32);
				gameField[i][j].setFont(font);
				gameField[i][j].setHorizontalAlignment(JTextField.CENTER);
				gameField[i][j].setBounds(xPosition, yPosition, width, height);
				frame.getContentPane().add(gameField[i][j]);
				
				yPosition = yPosition + 38;
			}
			
			xPosition = xPosition + 38;
		}

		
		//ToDo: Progress bar?   [---->     ]

		
		//Declare Buttons
		btnNewGame = new JButton("New Game");
		btnNewGame.setBounds(370, 11, 100, 40);
		frame.getContentPane().add(btnNewGame);
		btnNewGame.addActionListener(new ButtonLauscher()); 
		
		btnReset = new JButton("Reset");
		btnReset.setBounds(370, 50, 100, 40);
		frame.getContentPane().add(btnReset);

		btnUndo = new JButton("Undo");
		btnUndo.setBounds(370, 89, 100, 40);
		frame.getContentPane().add( btnUndo);

		btnRedo = new JButton("Redo");
		btnRedo.setBounds(370, 128, 100, 40);
		frame.getContentPane().add(btnRedo);

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
	
	class ButtonLauscher implements ActionListener { 
        public void actionPerformed(ActionEvent e) { 
            System.out.println("Button clicked");
        	if(e.getSource() == btnQuit){ 
                frame.dispose();
            } else if(e.getSource() == btnNewGame){
            	puzzle = new Sudoku(Difficulty.EASY);
            	refreshView();
            }
            /*
             *  else if(e.getSource() == nächster Buttonname){
            	//hier action rein
            	
            }
             */
        } 
    } 

}


