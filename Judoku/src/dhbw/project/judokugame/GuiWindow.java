package dhbw.project.judokugame;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import dhbw.project.puzzlemodel.Difficulty;
import dhbw.project.puzzlemodel.NumberPuzzle;

public class GuiWindow {

	private JFrame frame;

	NumberPuzzle puzzle;
	private Controller controller;
	private JudokuJTextField[][] gameField;
	private Timer swingTimer;

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
	private JButton btnEasy;
	private JButton btnMedium;
	private JButton btnHard;
	private JButton btnMiniSdk;
	private JButton btnMaxiSdk;

	/*
	 * Other Components
	 */
	private JPanel pnlGameField;
	private JMenuBar mnbrTop;
	private JMenu mnNewGame;
	private JProgressBar prgrBar;
	private JTextField txtTime;
	private JTextField txtLostMsg;
	private JTextPane txtWonMsg;
	private JTextField txtGameInfo;

	private JPanel pnlCenter;
	private CardLayout centerLayout;
	private String activeCenterView = "gameField";

	/**
	 * Create the application.
	 * 
	 * @throws IOException
	 */
	public GuiWindow(Controller c) throws IOException {
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
		frame.setMinimumSize(new Dimension(520, 685));
		frame.setLayout(new BorderLayout(10, 10));
		frame.setTitle("Judoku");
		ImageIcon windowIcon = new ImageIcon(getClass().getClassLoader()
				.getResource("resources/judoku_icon.png"));
		frame.setIconImage(windowIcon.getImage());
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new ShortcutKeyDispatcher());
		
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
		centerLayout = new CardLayout();
		pnlCenter.setLayout(centerLayout);

		pnlGameField = new JPanel();
		initializeGameField(pnlGameField, 3);
		pnlCenter.add(pnlGameField, "gameField");

		JPanel pnlWon = new JPanel(new BorderLayout());
		initializePanelWon(pnlWon);
		pnlCenter.add(pnlWon, "won");

		JPanel pnlLost = new JPanel(new BorderLayout());
		initializePanelLost(pnlLost);
		pnlCenter.add(pnlLost, "lost");

		GuiInfoView viewHelp = new GuiInfoView(this);
		pnlCenter.add(viewHelp.getContentPane(), "help");

		JPanel pnlSouth = new JPanel();
		pnlSouth.setLayout(new BorderLayout(15, 15));
		initializeGameStatusPanel(pnlSouth);

		/*
		 * Now that txtTime exists, the Timer can be instantiated.
		 */
		swingTimer = new Timer(1000, new JudokuTimeListener(txtTime));
		
		frame.getContentPane().add(pnlSouth, BorderLayout.SOUTH);
		frame.getContentPane().add(pnlCenter, BorderLayout.CENTER);
		frame.getContentPane().add(pnlNorth, BorderLayout.PAGE_START);

		UIManager.put("ProgressBar.background", Color.WHITE);
		UIManager.put("ProgressBar.selectionBackground", new Color(0, 0, 0));
		UIManager.put("ProgressBar.selectionForeground", new Color(255, 255,
				255));
		UIManager.put("ProgressBar.font", new Font("DIALOG", Font.PLAIN, 14));
	}

	/**
	 * Writes the buttons to the JMenuBar.
	 * 
	 * @param mnBar
	 */
	private void initializeMenuBar(JMenuBar mnBar) {
		mnNewGame = new JMenu("New Game");
		mnNewGame.setCursor(new Cursor(Cursor.HAND_CURSOR));
		mnNewGame.setToolTipText("Start a new Judoku");
		mnNewGame.setPreferredSize(new Dimension(80, 25));

		btnEasy = new JButton("Easy Classic");
		btnEasy.setContentAreaFilled(false);
		btnEasy.setBorderPainted(false);
		btnEasy.addActionListener(new JudokuButtonListener());
		btnEasy.setCursor(new Cursor(Cursor.HAND_CURSOR));
		mnNewGame.add(btnEasy);

		btnMedium = new JButton("Medium Classic");
		btnMedium.setContentAreaFilled(false);
		btnMedium.setBorderPainted(false);
		btnMedium.addActionListener(new JudokuButtonListener());
		btnMedium.setCursor(new Cursor(Cursor.HAND_CURSOR));
		mnNewGame.add(btnMedium);

		btnHard = new JButton("Hard Classic");
		btnHard.setContentAreaFilled(false);
		btnHard.setBorderPainted(false);
		btnHard.addActionListener(new JudokuButtonListener());
		btnHard.setCursor(new Cursor(Cursor.HAND_CURSOR));
		mnNewGame.add(btnHard);

		btnMiniSdk = new JButton("Mini 4x4 ");
		btnMiniSdk.setContentAreaFilled(false);
		btnMiniSdk.setBorderPainted(false);
		btnMiniSdk.addActionListener(new JudokuButtonListener());
		btnMiniSdk.setCursor(new Cursor(Cursor.HAND_CURSOR));
		mnNewGame.add(btnMiniSdk);

		btnMaxiSdk = new JButton("Maxi 16x16");
		btnMaxiSdk.setContentAreaFilled(false);
		btnMaxiSdk.setBorderPainted(false);
		btnMaxiSdk.addActionListener(new JudokuButtonListener());
		btnMaxiSdk.setCursor(new Cursor(Cursor.HAND_CURSOR));
		mnNewGame.add(btnMaxiSdk);

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
	}

	/**
	 * Initialize Panel with undo, redo and hint and validate buttons.
	 * 
	 * @param btnPnl
	 */
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
		ImageIcon continueImage = new ImageIcon(getClass().getClassLoader()
				.getResource("resources/correct.png"));
		btnContinue.setIcon(continueImage);
		btnContinue.setToolTipText("Correct my mistake(s)");
	}

	/**
	 * Create a NxN JudokuJTextFields array used as the game field.
	 * 
	 * @param pane
	 */
	private void initializeGameField(JPanel pane, int carreeSize) {
		int puzzleSize = carreeSize * carreeSize;
		pane.removeAll();
		pane.setLayout(new GridLayout(puzzleSize, puzzleSize, 2, 2));
		gameField = new JudokuJTextField[puzzleSize][puzzleSize];

		int fontSize = 38;
		if (puzzleSize == 16) {
			fontSize = 26;
		}

		int xPosition = 10;
		int yPosition = 10;
		final int width = 37;
		final int height = 37;

		Color active = Color.WHITE;
		Color toggle = new Color(195, 220, 255); // light Sudoku-Blue

		for (int y = 0; y < puzzleSize; y++) {
			yPosition = 10;
			for (int x = 0; x < puzzleSize; x++) {
				if (x % carreeSize == 0) {
					Color buffer = active;
					active = toggle;
					toggle = buffer;
				}

				gameField[y][x] = new JudokuJTextField(x, y);
				gameField[y][x].setBorder(BorderFactory.createEmptyBorder());
				gameField[y][x].setDocument(new JudokuPlainDocument(puzzleSize));

				/*
				 * Format the JTextFields
				 */
				gameField[y][x].setColumns(10);
				gameField[y][x].setFont(new Font("Arial", Font.BOLD, fontSize));
				gameField[y][x].setHorizontalAlignment(JTextField.CENTER);
				gameField[y][x].setBounds(xPosition, yPosition, width, height);
				gameField[y][x].setInitialColor(active);
				gameField[y][x].setCaretColor(active);
				gameField[y][x].setSelectionColor(new Color(0, 0, 0, 0));
				gameField[y][x].getCaret().setBlinkRate(0);
				gameField[y][x].setCursor(new Cursor(Cursor.HAND_CURSOR));

				/*
				 * Add the required listeners
				 */
				gameField[y][x].addFocusListener(new JudokuFocusListener());
				gameField[y][x].addKeyListener(new GameFieldKeyListener(gameField));
				gameField[y][x].setEnabled(false);

				pane.add(gameField[y][x]);
				yPosition = yPosition + 38;
			}
			/*
			 * Toggle background color for neighbored carrees (white /
			 * light-blue).
			 */
			if (puzzleSize == 4) {
				if (y % carreeSize != 0) {
					Color buffer = active;
					active = toggle;
					toggle = buffer;
				}
			} else if (puzzleSize == 9) {
				if ((y + 1) % carreeSize != 0) {
					Color buffer = active;
					active = toggle;
					toggle = buffer;
				}
			} else if (puzzleSize == 16) {
				if ((y) % carreeSize > 2) {
					Color buffer = active;
					active = toggle;
					toggle = buffer;
				}
			}
			xPosition = xPosition + 38;
		}
		frame.setVisible(true);
	}

	/**
	 * Fills the panel, shown if the user has won (no mistakes).
	 * 
	 * @param pnlWon
	 */
	private void initializePanelWon(JPanel pnlWon) {
		txtWonMsg = new JTextPane();
		
		/*
		 * Centering the text in the Message
		 */
		StyledDocument doc = txtWonMsg.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);
		
		txtWonMsg.setFont(new Font("DIALOG", Font.BOLD, 20));
		txtWonMsg.setPreferredSize(new Dimension(200, 60));
		txtWonMsg.setBackground(frame.getBackground());
		txtWonMsg.setEnabled(false);
		txtWonMsg.setAlignmentX(Component.CENTER_ALIGNMENT);
		txtWonMsg.setBackground(frame.getBackground());
		txtWonMsg.setDisabledTextColor(Color.BLACK);
		txtWonMsg.setBorder(BorderFactory.createEmptyBorder());
		pnlWon.add(txtWonMsg, BorderLayout.NORTH);

		/*
		 * Add winner picture to frame
		 */
		ImageIcon wonImage = new ImageIcon(getClass().getClassLoader()
				.getResource("resources/won.png"));
		JLabel wonLabel = new JLabel("", wonImage, JLabel.CENTER);
		pnlWon.add(wonLabel);
	}

	/**
	 * Fills the panel, shown if the user has "lost" (mistakes left).
	 * 
	 * @param pnlLost
	 */
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

	/**
	 * Writes time, progressbar and difficulty to the bottom jpanel.
	 * 
	 * @param pnlStatus
	 */
	private void initializeGameStatusPanel(JPanel pnlStatus) {
		txtTime = new JTextField();
		txtTime.setEnabled(false);
		txtTime.setText("0:00:00");
		txtTime.setBackground(frame.getBackground());
		txtTime.setDisabledTextColor(Color.BLACK);
		txtTime.setFont(new Font("DIALOG", Font.PLAIN, 14));
		txtTime.setPreferredSize(new Dimension(80, 35));
		txtTime.setHorizontalAlignment(SwingConstants.CENTER);
		pnlStatus.add(txtTime, BorderLayout.WEST);
		
		txtGameInfo = new JTextField();
		txtGameInfo.setEnabled(false);
		txtGameInfo.setBackground(frame.getBackground());
		txtGameInfo.setDisabledTextColor(Color.BLACK);
		txtGameInfo.setFont(new Font("DIALOG", Font.PLAIN, 14));
		txtGameInfo.setPreferredSize(new Dimension(80, 25));
		txtGameInfo.setHorizontalAlignment(SwingConstants.CENTER);
		pnlStatus.add(txtGameInfo, BorderLayout.EAST);

		prgrBar = new JProgressBar();
		prgrBar.setPreferredSize(new Dimension(210, 25));
		prgrBar.setString("0% Done");
		prgrBar.setStringPainted(true);
		prgrBar.setBorderPainted(false);
		prgrBar.setForeground(new Color(0, 165, 255)); // Judoku-Blue

		pnlStatus.add(prgrBar, BorderLayout.CENTER);
	}

	/**
	 * Makes the view show the recent status of the numberpuzzle object.
	 */
	private void refreshView() {
		int recentGrid[][] = this.puzzle.getRecentGrid();
		int startGrid[][] = this.puzzle.getStartGrid();
		int startFilledFields = 0;
		int userFilledFields = 0;
		for (int y = 0; y < puzzle.getSize(); y++) {
			for (int x = 0; x < puzzle.getSize(); x++) {
				gameField[y][x].setEnabled(true);
				if (startGrid[y][x] != 0) {
					gameField[y][x].setText(String.valueOf(startGrid[y][x]));
					gameField[y][x].setEnabled(false);
					gameField[y][x].setDisabledTextColor(new Color(120, 120,
							120)); // Sudoku-Grey
					startFilledFields++;
				} else if (recentGrid[y][x] != 0) {
					gameField[y][x].setText(String.valueOf(recentGrid[y][x]));
					userFilledFields++;
				} else {
					gameField[y][x].setText("");
				}
			}
		}
		if (startFilledFields + userFilledFields == puzzle.getSize()
				* puzzle.getSize()) {
			btnValidate.setEnabled(true);
		} else {
			btnValidate.setEnabled(false);
		}

		/*
		 * disables / enables the undo / redo buttons, if its possible /
		 * impossible.
		 */
		checkUndoRedoButtons();

		prgrBar.setValue(100 * userFilledFields
				/ (puzzle.getSize() * puzzle.getSize() - startFilledFields));
		prgrBar.setString(prgrBar.getValue() + "% Done");
		prgrBar.getRootPane().repaint();
		
		frame.repaint();
	}

	/**
	 * Marks a mistake red.
	 * 
	 * @param x
	 * @param y
	 */
	public void displayMistake(int x, int y) {
		this.gameField[y][x].mark();
		gameField[y][x].setCaretColor(gameField[y][x].getBackground());
	}

	/**
	 * Check, if undo and /or redo is possible and enables / disables the
	 * corresponding buttons.
	 */
	private void checkUndoRedoButtons() {
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

	public void toggleInfoViewBack() {
		btnContinue.setEnabled(true);
		switchCenterView(activeCenterView);

		if (!activeCenterView.equals("won") && !activeCenterView.equals("lost")
				&& puzzle != null) {
			btnHint.setEnabled(true);
			btnReset.setEnabled(true);
			swingTimer.start();
			refreshView();
		}

	}

	private void initNewGame(int carreeSize, Difficulty diff,
			String gameInfoText) {
		/*
		 * Start the SwingWorker
		 */
		JudokuSwingWorker sWork = new JudokuSwingWorker(diff, carreeSize);
		sWork.execute();

		/*
		 * Switch to the game field view
		 */
		switchCenterView("gameField");
		btnHint.setEnabled(true);
		btnReset.setEnabled(true);

		/*
		 * Close the "drop-down"-Menu.
		 */
		MenuSelectionManager.defaultManager().clearSelectedPath();
		/*
		 * Reset the timers JudokuTimeListener.
		 */
		JudokuTimeListener jtl = (JudokuTimeListener) swingTimer
				.getActionListeners()[0];
		jtl.reset();
		swingTimer.restart();
		txtGameInfo.setText(gameInfoText);
		initializeGameField(pnlGameField, carreeSize);
		pnlGameField.repaint();

		/*
		 * Get the Sudoku from the worker thread. If it the thread fails to
		 * complete in appropriate time (2.5s), restart it. This is needed due
		 * to a potential generation time of up to 30 seconds for 16x16 Sudokus.
		 */
		boolean threadSuccess = false;
		int i = 0;
		while(!threadSuccess && i < 3) {
			try {
				puzzle = sWork.get(2500, TimeUnit.MILLISECONDS);
				threadSuccess = true;
			} catch(TimeoutException | InterruptedException | ExecutionException ex) {
				/*
				 * Create a new worker thread and start it.
				 */
				sWork = new JudokuSwingWorker(diff, carreeSize);
				sWork.execute();
			}
			i++;
		}
		/*
		 * Fallback: If the thread failed to complete 3 times in a row try to
		 * get it without timing it out.
		 */
		if(!threadSuccess) {
			try {
				puzzle = sWork.get();
			} catch(InterruptedException | ExecutionException ex) {
				txtGameInfo.setText("ERROR");
			}
		}
	}

	private void switchCenterView(String cardName) {
		centerLayout.show(pnlCenter, cardName);
		activeCenterView = cardName;
	}

	protected class ShortcutKeyDispatcher implements KeyEventDispatcher {
		private int ctrl = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();	
		
		@Override
		public boolean dispatchKeyEvent(KeyEvent e) {
			if(e.getID() != KeyEvent.KEY_PRESSED) {
				return false;
			}
			if(e.getKeyCode() == KeyEvent.VK_Z && (e.getModifiers() & ctrl) != 0) {
				controller.undoPuzzle(puzzle);
				refreshView();
			} else if(e.getKeyCode() == KeyEvent.VK_Y && (e.getModifiers() & ctrl) != 0) {
				controller.redoPuzzle(puzzle);
				refreshView();
			} else if(e.getKeyCode() == KeyEvent.VK_H && (e.getModifiers() & ctrl) != 0) {
				controller.giveHintPuzzle(puzzle);
				refreshView();
			}
			return false;
		}
	}
	
	/**
	 * Listens to the JTextFields for gained / lost focus within the gamefield.
	 * 
	 */
	protected class JudokuFocusListener extends FocusAdapter {
		
		@Override
		public void focusLost(FocusEvent evt) {
			JudokuJTextField currentTextField = (JudokuJTextField) evt
					.getSource();
			
			controller.trySetValue(currentTextField.X, currentTextField.Y,
					currentTextField.getText(), puzzle);
			refreshView();
		}
	}

	/**
	 * Listens for actions on the buttons.
	 * 
	 */
	protected class JudokuButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btnQuit) {
				frame.dispose();
			} else if (e.getSource() == btnEasy) {
				GuiWindow.this.initNewGame(3, Difficulty.EASY, "Easy");
				refreshView();
			} else if (e.getSource() == btnMedium) {
				GuiWindow.this.initNewGame(3, Difficulty.MEDIUM, "Medium");
				refreshView();
			} else if (e.getSource() == btnHard) {
				GuiWindow.this.initNewGame(3, Difficulty.HARD, "Hard");
				refreshView();
			} else if (e.getSource() == btnMiniSdk) {
				GuiWindow.this.initNewGame(2, Difficulty.HARD, "Mini 4x4");
				refreshView();
			} else if (e.getSource() == btnMaxiSdk) {
				GuiWindow.this.initNewGame(4, Difficulty.EASY, "Maxi 16x16");
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
			} else if (e.getSource() == btnContinue) {
				switchCenterView("gameField");
				swingTimer.start();
				Container pnlParent = btnContinue.getParent();
				pnlParent.add(btnValidate);
				pnlParent.remove(btnContinue);
				btnHint.setEnabled(true);
				btnReset.setEnabled(true);
				refreshView();
			} else if (e.getSource() == btnInfo) {
				centerLayout.show(pnlCenter, "help");
				btnHint.setEnabled(false);
				btnReset.setEnabled(false);
				btnUndo.setEnabled(false);
				btnRedo.setEnabled(false);
				btnValidate.setEnabled(false);
				btnContinue.setEnabled(false);
				swingTimer.stop();
			} else if (e.getSource() == btnValidate) {
				swingTimer.stop();
				btnHint.setEnabled(false);
				btnReset.setEnabled(false);;
				btnUndo.setEnabled(false);
				btnRedo.setEnabled(false);
				int mistakes = controller.validateUserSolution(puzzle);
				/*
				 * User has won (no mistakes).
				 */
				if (mistakes == 0) {
					switchCenterView("won");
					btnValidate.setEnabled(false);
					txtWonMsg
							.setText("Congratulations, you won! \n Your Time: "
									+ txtTime.getText());
					return;

				}
				/*
				 * User has "lost".
				 */
				switchCenterView("lost");
				btnValidate.getParent().add(btnContinue);
				btnValidate.getParent().remove(btnValidate);
				if (mistakes == 1) {
					txtLostMsg
							.setText("There is " + mistakes + " mistake left");
				} else {
					txtLostMsg.setText("There are " + mistakes
							+ " mistakes left");
				}
			}
		}
	}
}
