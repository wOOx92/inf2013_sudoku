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
import dhbw.project.puzzlemodel.Sudoku;

public class GuiWindow {

	private JFrame frame;

	/**
	 * The Sudoku object displayed by this GuiWindow.
	 */
	private Sudoku sudoku;

	/**
	 * The Controller object corresponding to this GuiWindow.
	 */
	private Controller controller;

	/**
	 * The field of textfields in which the Sudoku is displayed.
	 */
	private JudokuJTextField[][] gameField;

	/**
	 * A timer measuring the time needed to solve a Sudoku.
	 */
	private Timer swingTimer;
	private JudokuTimeListener judokuTimeListener;

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
	private JButton btnSolvingMode;

	/*
	 * Other important components
	 */
	private JPanel pnlGameField;
	private JPanel pnlCenter;
	private JProgressBar prgrBar;
	private JTextField txtTime;
	private JTextField txtLostMsg;
	private JTextField txtGameInfo;
	private JTextPane txtWonMsg;
	private CardLayout centerLayout;
	private GuiInfoView infoView;
	private ShortcutKeyDispatcher actKeyDispatcher;

	/*
	 * Variables managing the states of the window.
	 */
	private boolean solvingMode = false;
	private String previousCenterView;
	private String activeCenterView = "gameField";

	/**
	 * Create the application.
	 * 
	 */
	public GuiWindow(Controller c) throws IOException {
		this.controller = c;
		initialize();
	}

	public void setVisible(boolean visibility) {
		frame.setVisible(visibility);
	}

	/**
	 * Initializes the contents of the frame.
	 *
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

		/*
		 * Add a custom key dispatcher listening for shortcuts (like strg+h).
		 */
		actKeyDispatcher = new ShortcutKeyDispatcher();
		KeyboardFocusManager.getCurrentKeyboardFocusManager()
				.addKeyEventDispatcher(actKeyDispatcher);

		JPanel pnlNorth = new JPanel(); // The top panel containing the menu and
										// a button bar.
		pnlNorth.setLayout(new GridLayout(2, 1, 10, 10));

		JMenuBar mnbrTop = new JMenuBar(); // The menu inside the north panel.
		mnbrTop.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
		initializeMenuBar(mnbrTop);
		pnlNorth.add(mnbrTop);

		/*
		 * The panel containing the button bar beneath the menu bar.
		 */
		JPanel pnlNorthBottom = new JPanel();
		pnlNorthBottom.setLayout(new GridLayout(1, 4, 10, 0));
		initializeButtonPanel(pnlNorthBottom);
		pnlNorth.add(pnlNorthBottom);

		pnlCenter = new JPanel(); // The main panel containing cards like the
									// game field.
		centerLayout = new CardLayout();
		pnlCenter.setLayout(centerLayout);

		pnlGameField = new JPanel(); // Panel containing the game field.
		initializeGameField(pnlGameField, 3);
		pnlCenter.add(pnlGameField, "gameField");

		JPanel pnlWon = new JPanel(new BorderLayout()); // Panel shown if the
														// user won.
		initializePanelWon(pnlWon);
		pnlCenter.add(pnlWon, "won");

		JPanel pnlLost = new JPanel(new BorderLayout()); // Panel shown if thhe
															// user lost.
		initializePanelLost(pnlLost);
		pnlCenter.add(pnlLost, "lost");

		infoView = new GuiInfoView(this); // The "help" panel
		pnlCenter.add(infoView.getContentPane(), "info");

		JPanel pnlSouth = new JPanel(); // Bottom panel containing game status
										// information
		pnlSouth.setLayout(new BorderLayout(15, 15));
		initializeGameStatusPanel(pnlSouth);

		/*
		 * After calling initializeGameStatusPanel() the txtTime exit and the
		 * listener can be added.
		 */
		judokuTimeListener = new JudokuTimeListener(txtTime);
		swingTimer = new Timer(1000, judokuTimeListener);

		frame.getContentPane().add(pnlSouth, BorderLayout.SOUTH);
		frame.getContentPane().add(pnlCenter, BorderLayout.CENTER);
		frame.getContentPane().add(pnlNorth, BorderLayout.PAGE_START);

		/*
		 * Additional layouting for the progress bar.
		 */
		UIManager.put("ProgressBar.background", Color.WHITE);
		UIManager.put("ProgressBar.selectionBackground", new Color(0, 0, 0));
		UIManager.put("ProgressBar.selectionForeground", new Color(255, 255,
				255));
		UIManager.put("ProgressBar.font", new Font("DIALOG", Font.PLAIN, 14));
	}

	/**
	 * Writes the buttons to the JMenuBar.
	 */
	private void initializeMenuBar(JMenuBar mnBar) {
		JMenu mnNewGame = new JMenu("New Game");
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

		btnSolvingMode = new JButton("Sudoku Solver");
		btnSolvingMode.setContentAreaFilled(false);
		btnSolvingMode.setBorderPainted(false);
		btnSolvingMode.addActionListener(new JudokuButtonListener());
		btnSolvingMode.setCursor(new Cursor(Cursor.HAND_CURSOR));
		mnNewGame.add(btnSolvingMode);

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
		btnValidate.setToolTipText("Validate");
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
	 * Create a NxN JudokuJTextFields array used as the game field on a panel.
	 * 
	 * @param carreeSize
	 *            Size of one carree (the carree size squared is the size of the
	 *            Sudoku).
	 */
	private void initializeGameField(JPanel pane, int carreeSize) {
		int puzzleSize = carreeSize * carreeSize;
		pane.removeAll(); // Remove the old gameField.

		/*
		 * Set the Layout for the new gameField using the calculated Sudoku
		 * sizes and initialize it.
		 */
		pane.setLayout(new GridLayout(puzzleSize, puzzleSize, 2, 2));
		gameField = new JudokuJTextField[puzzleSize][puzzleSize];

		/*
		 * If it is a 16x16 Sudoku, use a smaller font.
		 */
		int fontSize = 38;
		if (puzzleSize == 16) {
			fontSize = 26;
		}

		Color active = Color.WHITE;
		Color toggle = new Color(195, 220, 255); // light Sudoku-Blue

		/*
		 * Initialize every JudokuJTextField in the gameField.
		 */
		for (int y = 0; y < puzzleSize; y++) {
			for (int x = 0; x < puzzleSize; x++) {
				/*
				 * Toggle the color after carreeSize fields in x direction.
				 */
				if (x % carreeSize == 0) {
					Color buffer = active;
					active = toggle;
					toggle = buffer;
				}

				gameField[y][x] = new JudokuJTextField(x, y);
				gameField[y][x]
						.setDocument(new JudokuPlainDocument(puzzleSize));

				/*
				 * Format the JTextFields
				 */
				gameField[y][x].setFont(new Font("Arial", Font.BOLD, fontSize));
				gameField[y][x].setHorizontalAlignment(JTextField.CENTER);
				gameField[y][x].setInitialColor(active);
				gameField[y][x].setCaretColor(active);
				gameField[y][x].setSelectionColor(new Color(0, 0, 0, 0));
				gameField[y][x].getCaret().setBlinkRate(0);
				gameField[y][x].setCursor(new Cursor(Cursor.HAND_CURSOR));

				/*
				 * Add the required listeners
				 */
				gameField[y][x].addFocusListener(new JudokuFocusListener());
				gameField[y][x].addKeyListener(new GameFieldKeyListener(
						gameField));

				/*
				 * If the game was started in solving mode it has to be enabled.
				 * Otherwise refreshView() will manage enabling / disabling.
				 */
				gameField[y][x].setEnabled(solvingMode);

				pane.add(gameField[y][x]);
			}
			/*
			 * Toggle the background color for carrees in y direction (this is
			 * dependent from the Sudoku Size).
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
		}

		frame.setVisible(true); // Force the frame to update.
	}

	/**
	 * Initializes the panel shown if the user has won.
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

		/*
		 * Formatting the txtWonMsg.
		 */
		txtWonMsg.setFont(new Font("DIALOG", Font.BOLD, 20));
		txtWonMsg.setPreferredSize(new Dimension(200, 60));
		txtWonMsg.setBackground(frame.getBackground());
		txtWonMsg.setEnabled(false);
		txtWonMsg.setAlignmentX(Component.CENTER_ALIGNMENT);
		txtWonMsg.setBackground(frame.getBackground());
		txtWonMsg.setDisabledTextColor(Color.BLACK);
		txtWonMsg.setBorder(BorderFactory.createEmptyBorder());

		pnlWon.add(txtWonMsg, BorderLayout.NORTH); // Add the textPane

		/*
		 * Add winner picture to frame
		 */
		ImageIcon wonImage = new ImageIcon(getClass().getClassLoader()
				.getResource("resources/won.png"));
		JLabel wonLabel = new JLabel("", wonImage, JLabel.CENTER);

		pnlWon.add(wonLabel); // Add the label
	}

	/**
	 * Fills the panel, shown if the user has "lost" (mistakes left).
	 *
	 */
	private void initializePanelLost(JPanel pnlLost) {

		/*
		 * Initialize and format the textField.
		 */
		txtLostMsg = new JTextField();
		txtLostMsg.setFont(new Font("DIALOG", Font.BOLD, 20));
		txtLostMsg.setPreferredSize(new Dimension(200, 60));
		txtLostMsg.setBackground(frame.getBackground());
		txtLostMsg.setEnabled(false);
		txtLostMsg.setHorizontalAlignment(SwingConstants.CENTER);
		txtLostMsg.setBackground(frame.getBackground());
		txtLostMsg.setDisabledTextColor(Color.BLACK);
		txtLostMsg.setBorder(BorderFactory.createEmptyBorder());

		pnlLost.add(txtLostMsg, BorderLayout.NORTH); // Add to panel

		/*
		 * Add looser picture to frame
		 */
		ImageIcon lostImage = new ImageIcon(getClass().getClassLoader()
				.getResource("resources/mistake.png"));
		JLabel lostLabel = new JLabel("", lostImage, JLabel.CENTER);

		pnlLost.add(lostLabel); // Add to panel
	}

	/**
	 * Writes time, progressbar and difficulty to the bottom jpanel.
	 */
	private void initializeGameStatusPanel(JPanel pnlStatus) {
		txtTime = new JTextField(); // textField showing the time.
		txtTime.setEnabled(false);
		txtTime.setText("0:00:00");
		txtTime.setBackground(frame.getBackground());
		txtTime.setDisabledTextColor(Color.BLACK);
		txtTime.setFont(new Font("DIALOG", Font.PLAIN, 14));
		txtTime.setPreferredSize(new Dimension(80, 35));
		txtTime.setHorizontalAlignment(SwingConstants.CENTER);
		pnlStatus.add(txtTime, BorderLayout.WEST);

		txtGameInfo = new JTextField(); // textField showing the game mode.
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
	 * Makes the view show the recent state of the Sudoku object.
	 */
	private void refreshView() {
		int recentGrid[][] = this.sudoku.getRecentGrid();
		int startGrid[][] = this.sudoku.getStartGrid();
		int startFilledFields = 0;
		int userFilledFields = 0;

		/*
		 * Iterate through every cell.
		 */
		for (int y = 0; y < sudoku.getSize(); y++) {
			for (int x = 0; x < sudoku.getSize(); x++) {
				gameField[y][x].setEnabled(true);
				if (startGrid[y][x] != 0) { // If it is in the startGrid, make
											// it non-editable for the user.
					gameField[y][x].setText(String.valueOf(startGrid[y][x]));
					gameField[y][x].setEnabled(false);
					gameField[y][x].setDisabledTextColor(new Color(120, 120,
							120)); // Sudoku-Grey
					startFilledFields++;
				} else if (recentGrid[y][x] != 0) { // Set the content as text
					gameField[y][x].setText(String.valueOf(recentGrid[y][x]));
					userFilledFields++;
				} else { // If the cell is 0, set an empty string as text.
					gameField[y][x].setText("");
				}
			}
		}

		/*
		 * Activate / deactivate the buttonValidate. Is dependent on solvingMode
		 * and whether grid is filled.
		 */
		if (!solvingMode
				&& startFilledFields + userFilledFields == sudoku.getSize()
						* sudoku.getSize()) {
			btnValidate.setEnabled(true);
		} else if (solvingMode
				&& startFilledFields + userFilledFields != sudoku.getSize()
						* sudoku.getSize()) {
			btnValidate.setEnabled(true);
		} else {
			btnValidate.setEnabled(false);
		}

		/*
		 * Disables / enables the undo / redo buttons, if its possible /
		 * impossible.
		 */
		checkUndoRedoButtons();

		/*
		 * Do not update the progress bar if in solving mode (since the progress
		 * bar is (ab)used for displaying texts).
		 */
		if (!solvingMode) {
			prgrBar.setValue(100 * userFilledFields
					/ (sudoku.getSize() * sudoku.getSize() - startFilledFields));
			prgrBar.setString(prgrBar.getValue() + "% Done");
			prgrBar.getRootPane().repaint();
		}
		frame.repaint();
	}

	/**
	 * Marks a mistake red.
	 * 
	 * @param x
	 *            X-value of the mistake in the grid.
	 * @param y
	 *            Y-value of the mistake in the grid.
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
		if (sudoku.redoPossible()) {
			btnRedo.setEnabled(true);
		} else {
			btnRedo.setEnabled(false);
		}

		if (sudoku.undoPossible()) {
			btnUndo.setEnabled(true);
		} else {
			btnUndo.setEnabled(false);
		}
	}

	/**
	 * Focuses the first empty cell in the game field.
	 */
	private void focusFirstCell() {
		outer: for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				/*
				 * If the cell is empty, focus it and break the loop.
				 */
				if (gameField[i][j].getText().length() == 0) {
					gameField[i][j].requestFocusInWindow();
					break outer;
				}
			}
		}
	}

	/**
	 * Toggles the cardLayout in the center back to the previous view.
	 */
	public void toggleCenterViewBack() {
		/*
		 * Only do this actions if panel won and lost are not active.
		 */
		if (!previousCenterView.equals("won")
				&& !previousCenterView.equals("lost") && sudoku != null) {
			if (!solvingMode) {
				btnHint.setEnabled(true);
			}
			btnReset.setEnabled(true);
			swingTimer.start();
			refreshView();
		}

		btnContinue.setEnabled(true);
		switchCenterView(previousCenterView);
	}

	/**
	 * Creates a new Sudoku object in a separate thread.
	 * 
	 * @param carreeSize
	 *            The size of the Sudokus carrees (carreeSize squared equals
	 *            Sudoku size).
	 * @param diff
	 *            Desired difficulty of the Sudoku.
	 * @param gameInfoText
	 *            Text displayed in the game info text field.
	 */
	private void initNewGame(int carreeSize, Difficulty diff,
			String gameInfoText) {
		/*
		 * Start the SwingWorker
		 */
		JudokuSwingWorker sWork = new JudokuSwingWorker(diff, carreeSize);
		sWork.execute();

		solvingMode = false;

		/*
		 * Switch to the game field view
		 */
		switchCenterView("gameField");
		btnHint.setEnabled(true);
		btnReset.setEnabled(true);

		/*
		 * Reset the timers JudokuTimeListener.
		 */
		judokuTimeListener.reset();
		swingTimer.restart();
		txtGameInfo.setText(gameInfoText);
		initializeGameField(pnlGameField, carreeSize);

		/*
		 * Get the Sudoku from the worker thread. If it the thread fails to
		 * complete in appropriate time (2.5s), restart it. This is needed due
		 * to a potential generation time of up to 30 seconds for 16x16 Sudokus.
		 */
		boolean threadSuccess = false;
		int i = 0;
		while (!threadSuccess && i < 3) {
			try {
				sudoku = sWork.get(2500, TimeUnit.MILLISECONDS);
				threadSuccess = true;
			} catch (TimeoutException | InterruptedException
					| ExecutionException ex) {
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
		if (!threadSuccess) {
			try {
				sudoku = sWork.get();
			} catch (InterruptedException | ExecutionException ex) {
				txtGameInfo.setText("ERROR");
			}
		}
	}

	/**
	 * Switches the cardLayout in the center to a specific view.
	 * 
	 * @param cardName
	 *            Name of the card to show.
	 */
	private void switchCenterView(String cardName) {
		/*
		 * The keyDispatcher listening for shortcuts must only be enabled in the
		 * gameField view.
		 */
		if (cardName.equals("gameField")) {
			actKeyDispatcher.setEnabled(true);
		} else {
			actKeyDispatcher.setEnabled(false);
		}
		centerLayout.show(pnlCenter, cardName);
		previousCenterView = activeCenterView;
		activeCenterView = cardName;
	}

	/**
	 * Listens for Shortcuts.
	 */
	private class ShortcutKeyDispatcher implements KeyEventDispatcher {
		private boolean enabled = true;
		private int ctrl = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

		@Override
		public boolean dispatchKeyEvent(KeyEvent e) {
			/*
			 * Only take actions, if enabled and on key_pressed events (and if a Sudoku is existing).
			 * Otherwise directly return false.
			 */
			if (!enabled || sudoku == null || e.getID() != KeyEvent.KEY_PRESSED) {
				return false;
			}
			if (e.getKeyCode() == KeyEvent.VK_Z
					&& (e.getModifiers() & ctrl) != 0) { // Strg + Z
				controller.undoSudoku(sudoku);
				refreshView();
				return true;
			} else if (e.getKeyCode() == KeyEvent.VK_Y
					&& (e.getModifiers() & ctrl) != 0) { // Strg + Y
				controller.redoSudoku(sudoku);
				refreshView();
				return true;
			} else if (e.getKeyCode() == KeyEvent.VK_H // Strg + H (not in
														// solving mode).
					&& (e.getModifiers() & ctrl) != 0 && !solvingMode) {
				controller.giveHintPuzzle(sudoku);
				refreshView();
				return true;
			}
			return false;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}
	}

	/**
	 * Listens to the JTextFields for lost focus within the game field.
	 * 
	 */
	protected class JudokuFocusListener extends FocusAdapter {

		@Override
		public void focusLost(FocusEvent evt) {
			/*
			 * On focus lost, try to transfer the value from the textfield to the Sudoku object.
			 */
			JudokuJTextField currentTextField = (JudokuJTextField) evt
					.getSource();

			controller.trySetValue(currentTextField.X, currentTextField.Y,
					currentTextField.getText(), sudoku);
			refreshView();
		}
	}

	/**
	 * Listens for actions on the buttons.
	 * 
	 */
	private class JudokuButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btnQuit) {
				frame.dispose();
			} else if (e.getSource() == btnEasy) {
				GuiWindow.this.initNewGame(3, Difficulty.EASY, "Easy");
				/*
				 * Close the "drop-down"-Menu.
				 */
				MenuSelectionManager.defaultManager().clearSelectedPath();
				refreshView();
				focusFirstCell();
			} else if (e.getSource() == btnMedium) {
				GuiWindow.this.initNewGame(3, Difficulty.MEDIUM, "Medium");
				MenuSelectionManager.defaultManager().clearSelectedPath();
				refreshView();
				focusFirstCell();
			} else if (e.getSource() == btnHard) {
				GuiWindow.this.initNewGame(3, Difficulty.HARD, "Hard");
				MenuSelectionManager.defaultManager().clearSelectedPath();
				refreshView();
				focusFirstCell();
			} else if (e.getSource() == btnMiniSdk) {
				GuiWindow.this.initNewGame(2, Difficulty.HARD, "Mini 4x4");
				MenuSelectionManager.defaultManager().clearSelectedPath();
				refreshView();
				focusFirstCell();
			} else if (e.getSource() == btnMaxiSdk) {
				GuiWindow.this.initNewGame(4, Difficulty.EASY, "Maxi 16x16");
				MenuSelectionManager.defaultManager().clearSelectedPath();
				refreshView();
				focusFirstCell();
			} else if (e.getSource() == btnSolvingMode) {
				/*
				 * Solving mode needs custom initialization.
				 */
				sudoku = new Sudoku(3);
				switchCenterView("gameField");
				txtGameInfo.setText("Solver");
				btnReset.setEnabled(true);
				btnHint.setEnabled(false);
				/*
				 * (ab)use the progress bar for displaying information.
				 */
				prgrBar.setString("Write your Sudoku in the field");
				prgrBar.setValue(0);
				swingTimer.stop();
				judokuTimeListener.reset();
				solvingMode = true;
				MenuSelectionManager.defaultManager().clearSelectedPath();
				initializeGameField(pnlGameField, 3);
				refreshView();
				focusFirstCell();
			} else if (e.getSource() == btnReset) {
				controller.resetSudoku(sudoku);
				refreshView();
			} else if (e.getSource() == btnUndo) {
				controller.undoSudoku(sudoku);
				refreshView();
			} else if (e.getSource() == btnRedo) {
				controller.redoSudoku(sudoku);
				refreshView();
			} else if (e.getSource() == btnHint) {
				controller.giveHintPuzzle(sudoku);
				refreshView();
			} else if (e.getSource() == btnContinue) {
				switchCenterView("gameField");
				swingTimer.start();
				Container pnlParent = btnContinue.getParent();
				/*
				 * Switch from btnValidate to btnContinue again.
				 */
				pnlParent.add(btnValidate);
				pnlParent.remove(btnContinue);
				btnHint.setEnabled(true);
				btnReset.setEnabled(true);
				refreshView();
			} else if (e.getSource() == btnInfo) {
				switchCenterView("info");
				
				btnHint.setEnabled(false);
				btnReset.setEnabled(false);
				btnUndo.setEnabled(false);
				btnRedo.setEnabled(false);
				btnValidate.setEnabled(false);
				btnContinue.setEnabled(false);
				swingTimer.stop();
				infoView.setScrollBarsToBeginning();
			} else if (e.getSource() == btnValidate && !solvingMode) {
				swingTimer.stop();
				btnHint.setEnabled(false);
				btnReset.setEnabled(false);
				btnUndo.setEnabled(false);
				btnRedo.setEnabled(false);
				int mistakes = controller.validateUserSolution(sudoku);
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
			} else if (e.getSource() == btnValidate && solvingMode) {
				/*
				 * If in solving mode, btnValidate has a different function.
				 */
				int solutions = controller.solveSudoku(sudoku);
				if (solutions == 1) {
					refreshView();
					for (int i = 0; i < 9; i++) {
						for (int j = 0; j < 9; j++) {
							gameField[i][j].setEditable(false);
							gameField[i][j].setFocusable(false);
						}
					}
					btnValidate.setEnabled(false);
					System.out.println("solved");
				} else if (solutions == 0) {
					/*
					 * (ab)use the progress bar for displaying information.
					 */
					prgrBar.setString("This Sudoku has no solution!");
				} else {
					prgrBar.setString("This Sudoku is still missing some clues!");
				}
			}
		}
	}
}
