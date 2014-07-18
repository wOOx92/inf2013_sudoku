package dhbw.project.judokugame;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.UIManager;

public class GUI_Window {

	private JFrame frame;

	private NumberPuzzle puzzle;
	private Controller controller;
	private JudokuJTextField[][] gameField = new JudokuJTextField[9][9];
	private Timer swingTimer = new Timer(1000, new JudokuTimeListener());;

	/*
	 * Buttons
	 */
	private JButton btnRedo;
	private JButton btnUndo;
	private JButton btnReset;
	private JButton btnQuit;
	private JButton btnHint;
	private JButton btnValidate;
	private JButton btnContinue;
	private JButton btnInfo;
	private JButton btnLangENG;
	private JButton btnEasy;
	private JButton btnMedium;
	private JButton btnHard;

	/*
	 * Other Components
	 */
	private JMenuBar mnbrTop;
	private JMenu mnNewGame;
	private JProgressBar prgrBar;
	private JTextField txtTime;
	private JTextField txtLostMsg;
	private JTextArea txtWonMsg;
	private JTextField txtDifficulty;

	private JPanel pnlCenter;

	/**
	 * Create the application.
	 * 
	 * @throws IOException
	 */
	public GUI_Window(Controller c) throws IOException {
		this.controller = c;
		initialize();
	}

	public void setVisible(boolean visibility) {
		frame.setVisible(visibility);
	}

	/**
	 * Initialize the contents of the frame.
	 * 
	 * @throws IOException
	 */
	private void initialize() throws IOException {
		frame = new JFrame();
		frame.setBounds(150, 150, 585, 800);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setMinimumSize(new Dimension(465, 635));
		frame.setLayout(new BorderLayout(10, 10));
		frame.setTitle("Judoku");
		ImageIcon windowIcon = new ImageIcon(getClass().getClassLoader()
				.getResource("resources/judoku_icon.png"));
		frame.setIconImage(windowIcon.getImage());

		JPanel pnlNorth = new JPanel();
		pnlNorth.setLayout(new GridLayout(2, 1, 10, 10));
		
		mnbrTop = new JMenuBar();
		mnbrTop.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
		initializeMenuBar(mnbrTop);
		pnlNorth.add(mnbrTop);
		
		JPanel pnlNorthBottom = new JPanel();
		pnlNorthBottom.setLayout(new GridLayout(1, 4, 10, 0));
		initializeButtonPanel(pnlNorthBottom);
		pnlNorth.add(pnlNorthBottom);
		
		pnlCenter = new JPanel();
		pnlCenter.setLayout(new CardLayout());

		JPanel pnlGameField = new JPanel();
		pnlGameField.setLayout(new GridLayout(9, 9, 2, 2));
		initializeGameField(pnlGameField);
		pnlCenter.add(pnlGameField, "gameField");
		
		JPanel pnlWon = new JPanel(new BorderLayout());
		initializePanelWon(pnlWon);
		pnlCenter.add(pnlWon, "won");
		
		JPanel pnlLost = new JPanel(new BorderLayout());
		initializePanelLost(pnlLost);
		pnlCenter.add(pnlLost, "lost");
		
		JPanel pnlSouth = new JPanel();
		pnlSouth.setLayout(new BorderLayout(15, 15));
		initializeGameStatusPanel(pnlSouth);
		
		frame.getContentPane().add(pnlSouth, BorderLayout.SOUTH);
		frame.getContentPane().add(pnlCenter, BorderLayout.CENTER);
		frame.getContentPane().add(pnlNorth, BorderLayout.PAGE_START);
		
		UIManager.put("ProgressBar.background", Color.WHITE);
		UIManager.put("ProgressBar.selectionBackground", new Color(0, 0, 0));
		UIManager.put("ProgressBar.selectionForeground", new Color(255, 255,
				255));
		UIManager.put("ProgressBar.font", new Font("DIALOG", Font.PLAIN, 14));
	}
	
	private void initializeMenuBar(JMenuBar mnBar) {
		mnNewGame = new JMenu("New Game");
		mnNewGame.setCursor(new Cursor(Cursor.HAND_CURSOR));
		mnNewGame.setToolTipText("Start a new Judoku");
		mnNewGame.setPreferredSize(new Dimension(80, 25));

		btnEasy = new JButton("EASY");
		btnEasy.setText("Easy");
		btnEasy.setContentAreaFilled(false);
		btnEasy.setBorderPainted(false);
		btnEasy.addActionListener(new JudokuButtonListener());
		btnEasy.setCursor(new Cursor(Cursor.HAND_CURSOR));
		mnNewGame.add(btnEasy);

		btnMedium = new JButton("MEDIUM");
		btnMedium.setText("Medium");
		btnMedium.setContentAreaFilled(false);
		btnMedium.setBorderPainted(false);
		btnMedium.addActionListener(new JudokuButtonListener());
		btnMedium.setCursor(new Cursor(Cursor.HAND_CURSOR));

		mnNewGame.add(btnMedium);

		btnHard = new JButton("HARD");
		btnHard.setContentAreaFilled(false);
		btnHard.setBorderPainted(false);
		btnHard.setText("Hard");
		btnHard.addActionListener(new JudokuButtonListener());
		btnHard.setCursor(new Cursor(Cursor.HAND_CURSOR));
		mnNewGame.add(btnHard);

		mnBar.add(mnNewGame);

		btnReset = new JButton("Reset");
		btnReset.setContentAreaFilled(false);
		btnReset.setBorderPainted(false);
		btnReset.addActionListener(new JudokuButtonListener());
		btnReset.setEnabled(false);
		btnReset.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnReset.setToolTipText("Reset Judoku");
		btnReset.setPreferredSize(new Dimension(80, 25));
		mnBar.add(btnReset);

		btnQuit = new JButton("Exit");
		btnQuit.setContentAreaFilled(false);
		btnQuit.addActionListener(new JudokuButtonListener());
		btnQuit.setBorderPainted(false);
		btnQuit.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnQuit.setToolTipText("Quit Judoku");
		btnQuit.setPreferredSize(new Dimension(80, 25));
		mnBar.add(btnQuit);

		btnInfo = new JButton("Info");
		btnInfo.setContentAreaFilled(false);
		btnInfo.setBorderPainted(false);
		btnInfo.addActionListener(new JudokuButtonListener());
		btnInfo.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnInfo.setPreferredSize(new Dimension(80, 25));
		mnBar.add(btnInfo);

		btnLangENG = new JButton("ENG");
		btnLangENG.setContentAreaFilled(false);
		btnLangENG.setBorderPainted(false);
		btnLangENG.addActionListener(new JudokuButtonListener());
		btnLangENG.setCursor(new Cursor(Cursor.HAND_CURSOR));
		mnBar.add(btnLangENG);
	}
	
	private void initializeButtonPanel(JPanel btnPnl) {
		btnUndo = new JButton();
		btnUndo.setContentAreaFilled(false);
		btnUndo.addActionListener(new JudokuButtonListener());
		btnUndo.setEnabled(false);
		btnUndo.setCursor(new Cursor(Cursor.HAND_CURSOR));
		ImageIcon undoImage = new ImageIcon(getClass().getClassLoader()
				.getResource("resources/undo.png"));
		btnUndo.setIcon(undoImage);
		btnUndo.setToolTipText("Undo last change");
		btnPnl.add(btnUndo);

		btnRedo = new JButton();
		btnRedo.setContentAreaFilled(false);
		btnRedo.addActionListener(new JudokuButtonListener());
		btnRedo.setEnabled(false);
		btnRedo.setCursor(new Cursor(Cursor.HAND_CURSOR));
		ImageIcon redoImage = new ImageIcon(getClass().getClassLoader()
				.getResource("resources/redo.png"));
		btnRedo.setIcon(redoImage);
		btnRedo.setToolTipText("Redo last change");
		btnPnl.add(btnRedo);

		btnHint = new JButton();
		btnHint.setContentAreaFilled(false);
		btnHint.addActionListener(new JudokuButtonListener());
		btnHint.setEnabled(false);
		btnHint.setCursor(new Cursor(Cursor.HAND_CURSOR));
		ImageIcon hintImage = new ImageIcon(getClass().getClassLoader()
				.getResource("resources/hint.png"));
		btnHint.setIcon(hintImage);
		btnHint.setToolTipText("Give a hint");
		btnPnl.add(btnHint);

		btnValidate = new JButton();
		btnValidate.setContentAreaFilled(false);
		btnValidate.addActionListener(new JudokuButtonListener());
		btnValidate.setEnabled(false);
		btnValidate.setCursor(new Cursor(Cursor.HAND_CURSOR));
		ImageIcon validateImage = new ImageIcon(getClass().getClassLoader()
				.getResource("resources/validate.png"));
		btnValidate.setIcon(validateImage);
		btnValidate.setToolTipText("Validate my solution");
		btnPnl.add(btnValidate);
		
		btnContinue = new JButton();
		btnContinue.setContentAreaFilled(false);
		btnContinue.addActionListener(new JudokuButtonListener());
		btnContinue.setCursor(new Cursor(Cursor.HAND_CURSOR));
		ImageIcon continueImage = new ImageIcon(getClass().getClassLoader().getResource(
						"resources/correct.png"));
		btnContinue.setIcon(continueImage);
		btnContinue.setToolTipText("Correct my mistake(s)");
	}

	private void initializeGameField(JPanel pane) {
		int xPosition = 10;
		int yPosition = 10;
		final int width = 37;
		final int height = 37;

		Color active = Color.WHITE;
		Color toggle = new Color(195, 220, 255); // light Sudoku-Blue

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
				gameField[y][x].setBorder(BorderFactory.createEmptyBorder());
				gameField[y][x].setDocument(new JTextFieldLimit(1));

				// Format the JTextFields
				gameField[y][x].setColumns(10);
				gameField[y][x].setFont(new Font("Arial", Font.BOLD, 38));
				gameField[y][x].setHorizontalAlignment(JTextField.CENTER);
				gameField[y][x].setBounds(xPosition, yPosition, width, height);
				gameField[y][x].setInitialColor(active);
				gameField[y][x].setSelectionColor(new Color(0, 0, 0, 0));
				gameField[y][x].getCaret().setBlinkRate(0);
				gameField[y][x].setCursor(new Cursor(Cursor.HAND_CURSOR));

				// Add the required listeners
				gameField[y][x].addFocusListener(new JudokuFocusListener());
				//gameField[y][x].addMouseListener(new JudokuMouseListener());
				gameField[y][x].addKeyListener(new JudokuKeyListener());

				gameField[y][x].setEnabled(false);

				pane.add(gameField[y][x]);
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
	
	private void initializePanelWon(JPanel pnlWon) {
		txtWonMsg = new JTextArea();
		txtWonMsg.setFont(new Font("DIALOG", Font.BOLD, 20));
		txtWonMsg.setPreferredSize(new Dimension(200, 60));
		txtWonMsg.setBackground(frame.getBackground());
		txtWonMsg.setEnabled(false);
		txtWonMsg.setAlignmentX(Component.CENTER_ALIGNMENT);
		txtWonMsg.setBackground(frame.getBackground());
		txtWonMsg.setDisabledTextColor(Color.BLACK);
		txtWonMsg.setBorder(BorderFactory.createEmptyBorder());
		pnlWon.add(txtWonMsg, BorderLayout.NORTH);

		// Add winner picture to frame
		ImageIcon wonImage = new ImageIcon(getClass().getClassLoader()
				.getResource("resources/won.png"));
		JLabel wonLabel = new JLabel("", wonImage, JLabel.CENTER);
		pnlWon.add(wonLabel);		
	}
	
	private void initializePanelLost(JPanel pnlLost) {
		txtLostMsg = new JTextField();
		txtLostMsg.setFont(new Font("DIALOG", Font.BOLD, 20));
		txtLostMsg.setPreferredSize(new Dimension(200, 60));
		txtLostMsg.setBackground(frame.getBackground());
		txtLostMsg.setEnabled(false);
		txtLostMsg.setHorizontalAlignment(SwingConstants.CENTER);
		txtLostMsg.setBackground(frame.getBackground());
		txtLostMsg.setDisabledTextColor(Color.BLACK);
		txtLostMsg.setBorder(BorderFactory.createEmptyBorder());
		pnlLost.add(txtLostMsg, BorderLayout.NORTH);
		
		// Add looser picture to frame
		ImageIcon lostImage = new ImageIcon(getClass().getClassLoader()
				.getResource("resources/mistake.png"));
		JLabel lostLabel = new JLabel("", lostImage, JLabel.CENTER);
		pnlLost.add(lostLabel);	
	}

	private void initializeGameStatusPanel(JPanel pnlStatus) {
		txtTime = new JTextField();
		txtTime.setEnabled(false);
		txtTime.setText("0s");
		txtTime.setBackground(frame.getBackground());
		txtTime.setDisabledTextColor(Color.BLACK);
		txtTime.setFont(new Font("DIALOG", Font.PLAIN, 14));
		txtTime.setPreferredSize(new Dimension(80, 35));
		txtTime.setHorizontalAlignment(SwingConstants.CENTER);
		pnlStatus.add(txtTime, BorderLayout.WEST);

		txtDifficulty = new JTextField();
		txtDifficulty.setEnabled(false);
		txtDifficulty.setBackground(frame.getBackground());
		txtDifficulty.setDisabledTextColor(Color.BLACK);
		txtDifficulty.setFont(new Font("DIALOG", Font.PLAIN, 14));
		txtDifficulty.setPreferredSize(new Dimension(80, 25));
		txtDifficulty.setHorizontalAlignment(SwingConstants.CENTER);
		pnlStatus.add(txtDifficulty, BorderLayout.EAST);

		prgrBar = new JProgressBar();
		prgrBar.setPreferredSize(new Dimension(210, 25));
		prgrBar.setString("0% Done");
		prgrBar.setStringPainted(true);
		prgrBar.setBorderPainted(false);
		prgrBar.setForeground(new Color(0, 165, 255)); // Sudoku-Blue

		pnlStatus.add(prgrBar, BorderLayout.CENTER);
	}
	
	private void refreshView() {
		int recentGrid[][] = this.puzzle.getRecentGrid();
		int startGrid[][] = this.puzzle.getStartGrid();
		int startFilledFields = 0;
		int userFilledFields = 0;
		for (int y = 0; y < 9; y++) {
			for (int x = 0; x < 9; x++) {
				if (startGrid[y][x] != 0) {
					gameField[y][x].setText(String.valueOf(startGrid[y][x]));
					gameField[y][x].setEnabled(false);
					gameField[y][x].setDisabledTextColor(new Color(120, 120,
							120)); // Sudoku-Grey
					startFilledFields++;
				} else if (recentGrid[y][x] != 0) {
					gameField[y][x].setText(String.valueOf(recentGrid[y][x]));
					gameField[y][x].setEnabled(true);
					userFilledFields++;
				} else {
					gameField[y][x].setText("");
					gameField[y][x].setEnabled(true);
				}

			}
		}
		if (startFilledFields + userFilledFields == 81) {
			btnValidate.setEnabled(true);
		} else {
			btnValidate.setEnabled(false);
		}

		checkUndoRedoButtons();
		
		prgrBar.setValue(100 * userFilledFields / (81 - startFilledFields));
		prgrBar.setString(prgrBar.getValue() + "% Done");
		prgrBar.getRootPane().repaint();
	}

	public void displayMistake(int x, int y) {
		this.gameField[y][x].mark();
		gameField[y][x].setCaretColor(gameField[y][x].getBackground());
	}

	public NumberPuzzle getNumberPuzzle() {
		return puzzle;
	}

	public void setNumberPuzzle(NumberPuzzle np) {
		this.puzzle = np;
		refreshView();
	}

	public void enableButtons(boolean enabled) {
		btnHint.setEnabled(enabled);
		btnReset.setEnabled(enabled);
	}

	public void checkUndoRedoButtons() {
		if (puzzle.redoPossible()) {
			btnRedo.setEnabled(true);
		} else {
			btnRedo.setEnabled(false);
		}

		if (puzzle.undoPossible()) {
			btnUndo.setEnabled(true);
		} else {
			btnUndo.setEnabled(false);
		}
	}

/*	class JudokuMouseListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			JudokuJTextField currentTextField = (JudokuJTextField) e
					.getSource();
			currentTextField.selectAll();
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			//JudokuJTextField currentTextField = (JudokuJTextField) e
			//		.getSource();
			//currentTextField.selectAll();
		}

		@Override
		public void mouseExited(MouseEvent e) {

		}

		@Override
		public void mousePressed(MouseEvent e) {
			//JudokuJTextField currentTextField = (JudokuJTextField) e
			//		.getSource();
			//currentTextField.selectAll();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			//JudokuJTextField currentTextField = (JudokuJTextField) e
			//		.getSource();
			//currentTextField.selectAll();
		}

	}*/

	class JudokuFocusListener implements FocusListener {
		int oldValue;

		public void focusGained(java.awt.event.FocusEvent evt) {
			JudokuJTextField currentTextField = (JudokuJTextField) evt
					.getSource();

			// set cursorcolor "invisible"
			currentTextField.setCaretColor(currentTextField.getBackground());
			currentTextField.setBorder(BorderFactory.createMatteBorder(2, 2, 2,
					2, new Color(0, 165, 255)));

			// Only allow numeric input from 0 to 9
			char input = '?';
			if (currentTextField.getText().length() == 1) {
				input = currentTextField.getText().charAt(0);
			}
			if (Character.isDigit(input)) {
				oldValue = Integer.parseInt(currentTextField.getText());
			}

			currentTextField.selectAll();
		}

		public void focusLost(java.awt.event.FocusEvent evt) {
			JudokuJTextField currentTextField = (JudokuJTextField) evt
					.getSource();

			if (currentTextField.getText().equals("")
					|| currentTextField.getText().equals(null)) {
				if (controller.trySetValue(currentTextField.X,
						currentTextField.Y, 0, puzzle)) {
				}
				;
			} else {
				if (controller.trySetValue(currentTextField.X,
						currentTextField.Y,
						Integer.parseInt(currentTextField.getText()), puzzle)) {
				}
				;
			}

			//checkUndoRedoButtons();
			refreshView();
			currentTextField.setBorder(BorderFactory.createEmptyBorder());

		}
	}

	class JudokuKeyListener implements KeyListener {

		@Override
		public void keyPressed(KeyEvent e) {
			// do nothing

		}

		@Override
		public void keyReleased(KeyEvent e) {
			// do nothing

		}

		@Override
		public void keyTyped(KeyEvent e) {
			JudokuJTextField currentTextField = (JudokuJTextField) e
					.getSource();

			char c = e.getKeyChar();
			if (((c < '0') || (c > '9')) && (c != KeyEvent.VK_BACK_SPACE)) {
				e.consume(); // ignore event
			}
			currentTextField.selectAll();
		}

	}

	class JudokuTimeListener implements ActionListener {
		private int secondsPassed = 0;

		@Override
		public void actionPerformed(ActionEvent e) {
			secondsPassed++;
			int hours = secondsPassed / 3600;
			int minutes = secondsPassed / 60 - hours * 60;
			int seconds = secondsPassed - 60 * minutes - hours * 3600;

			if (hours == 0 && minutes == 0) {
				txtTime.setText(seconds + "s");
			} else if (hours == 0) {
				txtTime.setText(minutes + "m " + seconds + "s");
			} else {
				txtTime.setText(hours + "h " + minutes + "m " + seconds + "s");
			}
			txtTime.getRootPane().repaint();
		}

		public void reset() {
			secondsPassed = 0;
			txtTime.setText(secondsPassed + "s");
		}
	}

	class JudokuButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			JudokuSwingWorker sWork = null;
			if (e.getSource() == btnQuit) {
				frame.dispose();
			} else if (e.getSource() == btnEasy) {
				sWork = new JudokuSwingWorker(Difficulty.EASY);
				sWork.execute();
				CardLayout cl = (CardLayout) pnlCenter.getLayout();
				cl.show(pnlCenter, "gameField");
				enableButtons(true);
				MenuSelectionManager.defaultManager().clearSelectedPath();
				JudokuTimeListener jtl = (JudokuTimeListener) swingTimer
						.getActionListeners()[0];
				jtl.reset();
				swingTimer.restart();
				txtDifficulty.setText("Easy");
				puzzle = sWork.easyGet();
				refreshView();
			} else if (e.getSource() == btnMedium) {
				sWork = new JudokuSwingWorker(Difficulty.MEDIUM);
				sWork.execute();
				CardLayout cl = (CardLayout) pnlCenter.getLayout();
				cl.show(pnlCenter, "gameField");
				enableButtons(true);
				MenuSelectionManager.defaultManager().clearSelectedPath();
				JudokuTimeListener jtl = (JudokuTimeListener) swingTimer
						.getActionListeners()[0];
				jtl.reset();
				swingTimer.restart();
				txtDifficulty.setText("Medium");
				puzzle = sWork.easyGet();
				refreshView();
			} else if (e.getSource() == btnHard) {
				sWork = new JudokuSwingWorker(Difficulty.HARD);
				sWork.execute();
				CardLayout cl = (CardLayout) pnlCenter.getLayout();
				cl.show(pnlCenter, "gameField");
				enableButtons(true);
				MenuSelectionManager.defaultManager().clearSelectedPath();
				JudokuTimeListener jtl = (JudokuTimeListener) swingTimer
						.getActionListeners()[0];
				jtl.reset();
				swingTimer.restart();
				txtDifficulty.setText("Hard");
				puzzle = sWork.easyGet();
				refreshView();
			} else if (e.getSource() == btnReset) {
				controller.resetPuzzle(puzzle);
				refreshView();
			} else if (e.getSource() == btnUndo) {
				controller.undoPuzzle(puzzle);
				refreshView();
			} else if (e.getSource() == btnRedo) {
				controller.redoPuzzle(puzzle);
				refreshView();
			} else if (e.getSource() == btnHint) {
				controller.giveHintPuzzle(puzzle);
				refreshView();
			}  else if (e.getSource() == btnContinue) {
				CardLayout cl = (CardLayout) pnlCenter.getLayout();
				cl.show(pnlCenter, "gameField");
				btnContinue.getParent().add(btnValidate);
				btnContinue.getParent().remove(btnContinue);
				enableButtons(true);
			} else if (e.getSource() == btnValidate) {
				CardLayout cl = (CardLayout) pnlCenter.getLayout();
				int mistakes = controller.validateUserSolution(puzzle);
				if (mistakes == 0) {
					cl.show(pnlCenter, "won");
					swingTimer.stop();
					btnValidate.setEnabled(false);
					txtWonMsg
							.setText("Congratulations, you won! \n Your Time: "
									+ txtTime.getText());

				} else {
					if (mistakes == 1) {
						txtLostMsg.setText("There is " + mistakes + " mistake left");
					} else {
						txtLostMsg.setText("There are " + mistakes + " mistakes left");
					}
					cl.show(pnlCenter, "lost");
					btnValidate.getParent().add(btnContinue);
					btnValidate.getParent().remove(btnValidate);
				}
				enableButtons(false);
				btnUndo.setEnabled(false);
				btnRedo.setEnabled(false);
			} else if (e.getSource() == btnInfo) {
				/*
				 * 
				 */
			}
			else if (e.getSource() == btnLangENG) {
				for (int i = 0; i < 81; i++) {
					puzzle.giveHint();
				}
				refreshView();
			}
		}
	}

}
