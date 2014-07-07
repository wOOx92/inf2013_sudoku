import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.MenuBar;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.MaskFormatter;

public class GUI_Window {

	private JFrame frame;
	ImageIcon icon;

	private NumberPuzzle puzzle;
	private Controller controller;
	private JudokuJTextField[][] gameField = new JudokuJTextField[9][9];

	/*
	 * Buttons
	 */
	private JButton btnNewGame;
	private JButton btnRedo;
	private JButton btnUndo;
	private JButton btnReset;
	private JButton btnQuit;
	private JButton btnHint;
	private JButton btnValidate;
	private JButton btnLangDEU;
	private JButton btnLangENG;
	
	/*
	 * GridLayout Components
	 */
	private JMenuBar mnbrTop;
	private JPanel pnlCenter;
	private JPanel pnlSouth;
	
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
		frame.getContentPane().setLayout(new BorderLayout());
		frame.setTitle("Judoku 0.0.0.1");
		
		pnlCenter = new JPanel();	
		pnlCenter.setLayout(new GridLayout(9,9,2,2));
		pnlSouth = new JPanel();
		pnlSouth.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
		
		// positioning and sizing the text fields

		initializeGameField();

		// draw the gamefield
		mnbrTop = new JMenuBar();
		mnbrTop.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
		
		JMenu mnNewGame = new JMenu("New Game");
		
		JButton btnEasy = new JButton("EASY");
		btnEasy.setText("Easy");
		mnNewGame.add(btnEasy);
		JButton btnMedium = new JButton("MEDIUM");
		btnMedium.setText("Medium");
		mnNewGame.add(btnMedium);
		JButton btnHard = new JButton("HARD");
		mnNewGame.add(btnHard);
		btnHard.setText("Hard");
		
		mnbrTop.add(mnNewGame);
		
		btnHint = new JButton("Give Hint");
		mnbrTop.add(btnHint);
		btnHint.addActionListener(new ButtonLauscher());
		btnHint.setEnabled(false);
		
		btnReset = new JButton("Reset");
		mnbrTop.add(btnReset);
		btnReset.addActionListener(new ButtonLauscher());
		btnReset.setEnabled(false);

		btnLangDEU = new JButton("DEU");
		mnbrTop.add(btnLangDEU);
		btnLangDEU.addActionListener(new ButtonLauscher());
		
		btnLangENG = new JButton("ENG");
		btnLangENG.setHorizontalAlignment(SwingConstants.RIGHT);
		mnbrTop.add(btnLangENG);
		btnLangENG.addActionListener(new ButtonLauscher());

		btnUndo = new JButton("Undo");
		pnlSouth.add(btnUndo);
		btnUndo.addActionListener(new ButtonLauscher());
		btnUndo.setEnabled(false);

		btnRedo = new JButton("Redo");
		pnlSouth.add(btnRedo);
		btnRedo.addActionListener(new ButtonLauscher());
		btnRedo.setEnabled(false);

		btnValidate = new JButton("Validate");
		pnlSouth.add(btnValidate);
		btnValidate.addActionListener(new ButtonLauscher());
		btnValidate.setEnabled(false);

		btnQuit = new JButton("Quit Game");
		//frame.getContentPane().add(btnQuit);
		btnQuit.addActionListener(new ButtonLauscher());
		
		frame.getContentPane().add(mnbrTop, BorderLayout.PAGE_START);
		frame.getContentPane().add(pnlCenter, BorderLayout.CENTER);
		frame.getContentPane().add(pnlSouth, BorderLayout.SOUTH);
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
				gameField[y][x] = new JudokuJTextField(x, y) {
					public void setBorder(Border border) {
						// No!
					}
				};
				gameField[y][x].setDocument(new JTextFieldLimit(1));
				// gameField[y][x].setText("");
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
				
				pnlCenter.add(gameField[y][x]);
				//frame.getContentPane().add(gameField[y][x]);
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
			JudokuJTextField currentTextField = (JudokuJTextField) evt
					.getSource();
			currentTextField.selectAll();
		}

		public void focusLost(java.awt.event.FocusEvent evt) {
			JudokuJTextField currentTextField = (JudokuJTextField) evt
					.getSource();
			System.out.println("Was steht in dem Scheiss JTextField? "
					+ currentTextField.getText());
			// this.gameField[y][x].setBackground(Color.RED);

			/** FOR DEBUGGING **/

			if (currentTextField.getText().equals("") || currentTextField.getText().equals(" ") || currentTextField.getText().equals(null)) {
				System.out.println("-------- FOCUS LOST---------\nX-Value: "
						+ currentTextField.X
						+ "\nY-Value: "
						+ currentTextField.Y
						+ "\nCurrent value: "
						+ ""
						+ "\nValue successfully set: "
						+ puzzle.trySetValue(currentTextField.X,
								currentTextField.Y, 0));

			} else {
				System.out.println("-------- FOCUS LOST---------\nX-Value: "
						+ currentTextField.X
						+ "\nY-Value: "
						+ currentTextField.Y
						+ "\nCurrent value: "
						+ ""
						+ "\nValue successfully set: "
						+ puzzle.trySetValue(currentTextField.X,
								currentTextField.Y,
								Integer.parseInt(currentTextField.getText())));

			}

		}
	}

	class ButtonLauscher implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JudokuSwingWorker sWork = null;
			if (e.getSource() == btnQuit) {
				frame.dispose();
			} else if (e.getSource() == btnNewGame) {
				sWork = new JudokuSwingWorker(Difficulty.HARD);
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
