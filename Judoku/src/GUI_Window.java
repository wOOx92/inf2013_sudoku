import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.MenuSelectionManager;
import javax.swing.border.Border;
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
	private JButton btnRedo;
	private JButton btnUndo;
	private JButton btnReset;
	private JButton btnQuit;
	private JButton btnHint;
	private JButton btnValidate;
	private JButton btnLangDEU;
	private JButton btnLangENG;
	private JButton btnEasy;
	private JButton btnMedium;
	private JButton btnHard;
	
	/*
	 * GridLayout Components
	 */
	private JMenuBar mnbrTop;
	private JMenu mnNewGame;
	private JPanel pnlCenter;
	private JPanel pnlSouth;
	private JPanel pnlSouthTop;
	private JPanel pnlSouthBottom;

	private JProgressBar prgrBar; 
	private JTextArea txtTime;
	
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
		pnlSouth.setLayout(new GridLayout(2,1));
		pnlSouthTop = new JPanel();
		pnlSouthTop.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
		pnlSouthBottom = new JPanel();
		pnlSouthBottom.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
		pnlSouth.add(pnlSouthTop);
		pnlSouth.add(pnlSouthBottom);
		
		// positioning and sizing the text fields
		initializeGameField();

		// draw the gamefield
		mnbrTop = new JMenuBar();
		//mnbrTop.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
		
		mnNewGame = new JMenu("New Game");
		
		btnEasy = new JButton("EASY");
		btnEasy.setText("Easy");
		btnEasy.setContentAreaFilled(false);
		btnEasy.setBorderPainted(false);
		btnEasy.addActionListener(new ButtonLauscher());
		mnNewGame.add(btnEasy);
		
		btnMedium = new JButton("MEDIUM");
		btnMedium.setText("Medium");
		btnMedium.setContentAreaFilled(false);
		btnMedium.setBorderPainted(false);
		btnMedium.addActionListener(new ButtonLauscher());
		mnNewGame.add(btnMedium);
		
		btnHard = new JButton("HARD");
		btnHard.setContentAreaFilled(false);
		btnHard.setBorderPainted(false);
		btnHard.setText("Hard");
		btnHard.addActionListener(new ButtonLauscher());
		mnNewGame.add(btnHard);
		
		mnbrTop.add(mnNewGame);
		
		btnHint = new JButton("Give Hint");
		btnHint.setContentAreaFilled(false);
		btnHint.setBorderPainted(false);
		btnHint.addActionListener(new ButtonLauscher());
		btnHint.setEnabled(false);
		mnbrTop.add(btnHint);
		
		btnReset = new JButton("Reset");
		btnReset.setContentAreaFilled(false);
		btnReset.setBorderPainted(false);
		btnReset.addActionListener(new ButtonLauscher());
		btnReset.setEnabled(false);
		mnbrTop.add(btnReset);
		
		btnLangDEU = new JButton("DEU");
		btnLangDEU.setContentAreaFilled(false);
		btnLangDEU.setBorderPainted(false);
		btnLangDEU.addActionListener(new ButtonLauscher());
		mnbrTop.add(btnLangDEU);
		
		btnLangENG = new JButton("ENG");
		btnLangENG.setContentAreaFilled(false);
		btnLangENG.setBorderPainted(false);
		btnLangENG.addActionListener(new ButtonLauscher());
		mnbrTop.add(btnLangENG);
		
		btnUndo = new JButton("Undo");
		btnUndo.setContentAreaFilled(false);
		btnUndo.setPreferredSize(new Dimension(90, 35));
		btnUndo.addActionListener(new ButtonLauscher());
		btnUndo.setEnabled(false);
		pnlSouthTop.add(btnUndo);
		
		btnRedo = new JButton("Redo");
		btnRedo.setContentAreaFilled(false);
		btnRedo.setPreferredSize(new Dimension(90, 35));
		btnRedo.addActionListener(new ButtonLauscher());
		btnRedo.setEnabled(false);
		pnlSouthTop.add(btnRedo);

		btnValidate = new JButton("Validate");
		btnValidate.setContentAreaFilled(false);
		btnValidate.setPreferredSize(new Dimension(90, 35));
		btnValidate.addActionListener(new ButtonLauscher());
		btnValidate.setEnabled(false);
		pnlSouthTop.add(btnValidate);

		btnQuit = new JButton("Exit");
		btnQuit.setContentAreaFilled(false);
		btnQuit.setPreferredSize(new Dimension(90, 35));
		pnlSouthTop.add(btnQuit);
		btnQuit.addActionListener(new ButtonLauscher());

		txtTime = new JTextArea();
		txtTime.setEnabled(false);
		txtTime.setText("19 Sekunden");
		pnlSouthBottom.add(txtTime);
		
		JTextArea progressTxt = new JTextArea();
		progressTxt.setText("Progress");
		progressTxt.setEnabled(false);
		pnlSouthBottom.add(progressTxt);
		
		prgrBar = new JProgressBar();
		prgrBar.setPreferredSize(new Dimension(200, 20));
		prgrBar.setValue(33);
		pnlSouthBottom.add(prgrBar);
		
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
				gameField[y][x] = new JudokuJTextField(x, y);
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

	public void enableButtons(boolean enabled){
		btnHint.setEnabled(enabled);
		btnUndo.setEnabled(enabled);
		btnRedo.setEnabled(enabled);
		btnReset.setEnabled(enabled);		
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
			} else if (e.getSource() == btnEasy) {
				sWork = new JudokuSwingWorker(Difficulty.EASY);
				sWork.execute();
				puzzle = sWork.easyGet();
				enableButtons(true);
				MenuSelectionManager.defaultManager().clearSelectedPath();
				refreshView();
			} else if (e.getSource() == btnMedium){
				sWork = new JudokuSwingWorker(Difficulty.MEDIUM);
				sWork.execute();
				puzzle = sWork.easyGet();
				enableButtons(true);
				MenuSelectionManager.defaultManager().clearSelectedPath();
				refreshView();
			} else if (e.getSource() == btnHard){
				sWork = new JudokuSwingWorker(Difficulty.HARD);
				sWork.execute();
				puzzle = sWork.easyGet();
				enableButtons(true);
				MenuSelectionManager.defaultManager().clearSelectedPath();
				refreshView();
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
