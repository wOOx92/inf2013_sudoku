import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.text.MaskFormatter;

//Resizable with LayoutManager
public class GUI_Window {

	private JFrame frame;
	ImageIcon icon;

	private NumberPuzzle puzzle;
	private Controller controller;
	private JudokuJFormattedTextField[][] gameField = new JudokuJFormattedTextField[9][9];

	// Initialize Buttons
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
		icon = new ImageIcon("judku_icon.png");
		// frame.setIconImage(icon.getImage());
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

	public void initializeGameField() {
		int xPosition = 10;
		int yPosition = 10;
		final int width = 37;
		final int height = 37;

		Color active = Color.WHITE;
		Color toggle = new Color(255, 200, 200);

		for (int y = 0; y < 9; y++) {
			yPosition = 10;
			for (int x = 0; x < 9; x++) {
				if (x % 3 == 0) {
					Color buffer = active;
					active = toggle;
					toggle = buffer;
				}

				// CAUTION: i= Y und J = X
				gameField[y][x] = new JudokuJFormattedTextField(x, y,
						createFormatter("#")) {
					@Override
					public void setBorder(Border border) {
						// No!
					}
				};
				gameField[y][x].setText("");
				gameField[y][x].setColumns(10);
				// set font size in gameField
				Font font = new Font("Arial", Font.BOLD, 32);
				gameField[y][x].setFont(font);
				gameField[y][x].setHorizontalAlignment(JTextField.CENTER);
				gameField[y][x].setBounds(xPosition, yPosition, width, height);

				// gameField[y][x].setBackground(active);
				// set Color
				gameField[y][x].setInitialColor(active);
				// gameField[i][j].getDocument().addDocumentListener(myDocumentListener);
				gameField[y][x].addFocusListener(new JudokuFocusListener());
				gameField[y][x].setEnabled(false);

				frame.getContentPane().add(gameField[y][x]);
				yPosition = yPosition + 38;
			}
			if ((y + 1) % 3 != 0) {
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

		for (int y = 0; y < 9; y++) {
			for (int x = 0; x < 9; x++) {

				if (startGrid[y][x] != 0) {
					// New Game / Give Hint / Undo / Redo
					// gameField[y][x].setBackground(Color.RED);
					gameField[y][x].setText(String.valueOf(startGrid[y][x]));
					gameField[y][x].setEnabled(false);
					gameField[y][x].setDisabledTextColor(Color.GRAY);
				} else if (recentGrid[y][x] != 0) {
					gameField[y][x].setText(String.valueOf(recentGrid[y][x]));
					gameField[y][x].setEnabled(true);
				} else {
					gameField[y][x].setText("");
					gameField[y][x].setEnabled(true);
				}
			}
		}
	}

	public void displayMistake(int x, int y) {
		this.gameField[y][x].mark();
	}

	public NumberPuzzle getNumberPuzzle() {
		return puzzle;
	}

	public void setNumberPuzzle(NumberPuzzle np) {
		this.puzzle = np;
		refreshView();
	}

	class JudokuFocusListener implements FocusListener {
		public void focusGained(java.awt.event.FocusEvent evt) {

			System.out.println("-------- FOCUS GAINED---------\n");
			JudokuJFormattedTextField currentTextField = (JudokuJFormattedTextField) evt
					.getSource();
			currentTextField.selectAll();
		}

		public void focusLost(java.awt.event.FocusEvent evt) {
			JudokuJFormattedTextField currentTextField = (JudokuJFormattedTextField) evt
					.getSource();

			// this.gameField[y][x].setBackground(Color.RED);
			if (currentTextField.getText().equals(" ")
					|| currentTextField.getText().equals("")) {
				// If value was deleted or still no value inserted

				/** FOR DEBUGGING **/
				System.out.println("-------- FOCUS LOST---------\nX-Value: "
						+ currentTextField.X
						+ "\nY-Value: "
						+ currentTextField.Y
						+ "\nCurrent value: "
						+ "NULL"
						+ "\nValue successfully set: "
						+ puzzle.trySetValue(currentTextField.X,
								currentTextField.Y, 0));

			} else {

				if (currentTextField.getText().equals("0")) {
					// if 0 was inserted, remove immediately
					currentTextField.setText("");

				} else {

					/** Remove String outputs for final Version **/

					currentTextField.unmmark();
					;
					System.out
							.println("-------- FOCUS LOST---------\nX-Value: "
									+ currentTextField.X
									+ "\nY-Value: "
									+ currentTextField.Y
									+ "\nCurrent value: "
									+ "NULL"
									+ "\nValue successfully set: "
									+ puzzle.trySetValue(currentTextField.X,
											currentTextField.Y, Integer
													.parseInt(currentTextField
															.getText())));

				}

			}

		}
	}

	class ButtonLauscher implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JudokuSwingWorker sWork = null;
			if (e.getSource() == btnQuit) {
				frame.dispose();
			} else if (e.getSource() == btnNewGame) {
				sWork = new JudokuSwingWorker((Difficulty) cmbBox.getModel()
						.getSelectedItem(), GUI_Window.this);
				sWork.execute();
				btnHint.setEnabled(true);
				btnUndo.setEnabled(true);
				btnRedo.setEnabled(true);
				btnReset.setEnabled(true);
			} else if (e.getSource() == btnReset) {
				if (puzzle != null) {
					controller.resetPuzzle(puzzle);
				}
			} else if (e.getSource() == btnUndo) {
				if (puzzle != null) {
					controller.undoPuzzle(puzzle);
				}
			} else if (e.getSource() == btnRedo) {
				if (puzzle != null) {
					controller.redoPuzzle(puzzle);
				}
			} else if (e.getSource() == btnHint) {
				if (puzzle != null) {
					controller.giveHintPuzzle(puzzle);
				}
			}

			if (sWork != null) {
				try {
					puzzle = sWork.get();
				} catch (Exception exceptio) {

				}
			}
			refreshView();
		}
	}
}
