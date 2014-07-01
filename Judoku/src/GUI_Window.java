import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.MaskFormatter;
import javax.swing.text.PlainDocument;

//Resizable with LayoutManager
public class GUI_Window {

	private JFrame frame;

	private NumberPuzzle puzzle;
	private Controller controller;
	private JFormattedTextField[][] gameField = new JFormattedTextField[9][9];

	// Initialise Buttons
	private JButton btnNewGame;
	private JButton btnRedo;
	private JButton btnUndo;
	private JButton btnReset;
	private JButton btnQuit;
	private JButton btnHint;
	private JButton btnValidate;
	private JComboBox cmbBox;

	/**
	 * Create the application.
	 */
	public GUI_Window(Controller c) {
		this.controller = c;
		initialize();
	}

	public JFrame getJFrame() {
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

		initializeGameField();

		// draw the gamefield


		// TODO: Progress bar? [----> ]

		// Declare Buttons
		btnNewGame = new JButton("New Game");
		btnNewGame.setBounds(370, 11, 100, 40);
		frame.getContentPane().add(btnNewGame);
		btnNewGame.addActionListener(new ButtonLauscher());

		Vector<Difficulty> difficulties = new Vector<>();
		difficulties.add(Difficulty.EASY);
		difficulties.add(Difficulty.MEDIUM);
		difficulties.add(Difficulty.HARD);

		cmbBox = new JComboBox();
		cmbBox.setModel(new DefaultComboBoxModel(difficulties));
		cmbBox.setBounds(370, 50, 100, 40);
		frame.getContentPane().add(cmbBox);
		
		btnReset = new JButton("Reset");
		btnReset.setBounds(370, 89, 100, 40);
		frame.getContentPane().add(btnReset);
		btnReset.addActionListener(new ButtonLauscher());
		btnReset.setEnabled(false);

		btnUndo = new JButton("Undo");
		btnUndo.setBounds(370, 128, 100, 40);
		frame.getContentPane().add(btnUndo);
		btnUndo.addActionListener(new ButtonLauscher());
		btnUndo.setEnabled(false);

		btnRedo = new JButton("Redo");
		btnRedo.setBounds(370, 167, 100, 40);
		frame.getContentPane().add(btnRedo);
		btnRedo.addActionListener(new ButtonLauscher());
		btnRedo.setEnabled(false);

		btnHint = new JButton("Give Hint");
		btnHint.setBounds(370, 206, 100, 40);
		frame.getContentPane().add(btnHint);
		btnHint.addActionListener(new ButtonLauscher());
		btnHint.setEnabled(false);
		
		btnValidate = new JButton("Validate");
		btnValidate.setBounds(370, 245, 100, 40);
		frame.getContentPane().add(btnValidate);
		btnValidate.addActionListener(new ButtonLauscher());
		btnValidate.setEnabled(false);	

		btnQuit = new JButton("Quit Game");
		btnQuit.setBounds(370, 314, 100, 40);
		frame.getContentPane().add(btnQuit);
		btnQuit.addActionListener(new ButtonLauscher());

	}

	private MaskFormatter createFormatter(String s) {
		MaskFormatter formatter = null;
		try {
			formatter = new MaskFormatter(s);

		} catch (java.text.ParseException exc) {
			formatter = new MaskFormatter();
		}
		return formatter;
	}

	public void initializeGameField(){
		int xPosition = 10;
		int yPosition = 10;
		final int width = 37;
		final int height = 37;
		
		Color active = Color.WHITE;
		Color toggle = Color.LIGHT_GRAY;

		for (int i = 0; i < 9; i++) {
			yPosition = 10;
			for (int j = 0; j < 9; j++) {
				if (j % 3 == 0) {
					Color buffer = active;
					active = toggle;
					toggle = buffer;
				}

				// CAUTION: i= Y und J = X
				gameField[i][j] = new JFormattedTextField(createFormatter("#"));
				gameField[i][j].setText("");
				gameField[i][j].setColumns(10);
				// set font size in gameField
				Font font = new Font("Arial", Font.BOLD, 32);
				gameField[i][j].setFont(font);
				gameField[i][j].setHorizontalAlignment(JTextField.CENTER);
				gameField[i][j].setBounds(xPosition, yPosition, width, height);
				gameField[i][j].setBackground(active);
				// gameField[i][j].getDocument().addDocumentListener(myDocumentListener);
				gameField[i][j].addFocusListener(new JudokuFocusListener());
				gameField[i][j].setEnabled(false);

				frame.getContentPane().add(gameField[i][j]);
				yPosition = yPosition + 38;
			}
			if ((i + 1) % 3 != 0) {
				Color buffer = active;
				active = toggle;
				toggle = buffer;
			}
			xPosition = xPosition + 38;
		}		
	}

	public void refreshView() {
		int recentGrid[][] = this.puzzle.getRecentGrid();
		int startGrid[][] = this.puzzle.getStartGrid();

		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (startGrid[i][j] != 0) {
					gameField[i][j].setText(String.valueOf(startGrid[i][j]));
					gameField[i][j].setEnabled(false);
					gameField[i][j].setDisabledTextColor(Color.RED);
				} else if (recentGrid[i][j] != 0) {
					gameField[i][j].setText(String.valueOf(recentGrid[i][j]));
					gameField[i][j].setEnabled(true);
				} else {
					gameField[i][j].setText("");
					gameField[i][j].setEnabled(true);
				}
			}
		}
	}

	public void giveHint() {

	}

	public void displayMistake(int x, int y) {
		this.gameField[y][x].setBackground(Color.RED);
	}

	public NumberPuzzle getNumberPuzzle() {
		return puzzle;
	}

	class JudokuFocusListener implements FocusListener {
		public void focusGained(java.awt.event.FocusEvent evt) {
			JFormattedTextField src = (JFormattedTextField) evt.getSource();
			src.selectAll();
	}

		public void focusLost(FocusEvent e) {
			// nothin so far
		}
	}

	class ButtonLauscher implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btnQuit) {
				frame.dispose();
			} else if (e.getSource() == btnNewGame) {
				puzzle = new SudokuBuilder().newSudoku((Difficulty) cmbBox
						.getModel().getSelectedItem());
				btnHint.setEnabled(true);
				btnUndo.setEnabled(true);
				btnRedo.setEnabled(true);
				btnReset.setEnabled(true);
				refreshView();
				frame.validate();
			} else if (e.getSource() == btnReset) {
				if (puzzle != null) {
					controller.resetPuzzle(puzzle);
					refreshView();
				}
			} else if (e.getSource() == btnUndo) {
				if (puzzle != null) {
					controller.undoPuzzle(puzzle);
					refreshView();
				}
			} else if (e.getSource() == btnRedo) {
				if (puzzle != null) {
					controller.redoPuzzle(puzzle);
					refreshView();
				}
			} else if (e.getSource() == btnHint) {
				if (puzzle != null) {
					controller.giveHintPuzzle(puzzle);
					refreshView();
				}
			}
		}
	}
}
