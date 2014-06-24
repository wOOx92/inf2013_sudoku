import java.awt.EventQueue;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JTextPane;
//Resizable with LayoutManager
public class GUI_Window {

	private JFrame frame;

	// the gamefield

	// private JTextField textField;
	// private JTextField textField_1;
	// private JTextField textField_2;
	private JTextField[][] gameField = new JTextField[9][9];

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
				gameField[i][j].setBounds(xPosition, yPosition, width, height);
				frame.getContentPane().add(gameField[i][j]);
				
				yPosition = yPosition + 38;
			}
			
			xPosition = xPosition + 38;
		}

		JButton btnNewGame = new JButton("New Game");
		btnNewGame.setBounds(370, 11, 100, 40);
		frame.getContentPane().add(btnNewGame);

		JButton btnReset = new JButton("Reset");
		btnReset.setBounds(370, 50, 100, 40);
		frame.getContentPane().add(btnReset);

		JButton btnUndo = new JButton("Undo");
		 btnUndo.setBounds(370, 89, 100, 40);
		frame.getContentPane().add( btnUndo);

		JButton btnRedo = new JButton("Redo");
		btnRedo.setBounds(370, 128, 100, 40);
		frame.getContentPane().add(btnRedo);

		JButton btnQuit = new JButton("Quit Game");
		btnQuit.setBounds(370, 314, 100, 40);
		frame.getContentPane().add(btnQuit);

		JTextPane txtpnNochVersuch = new JTextPane();
		txtpnNochVersuch.setText("Noch 1 Versuch");
		txtpnNochVersuch.setBounds(370, 245, 100, 40);
		frame.getContentPane().add(txtpnNochVersuch);
	}
}
