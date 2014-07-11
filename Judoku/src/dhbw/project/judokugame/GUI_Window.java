package dhbw.project.judokugame;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
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
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.UIManager;

public class GUI_Window {

	private JFrame frame;
	ImageIcon icon;

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
	private JButton btnLangDEU;
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

	private JPanel pnlCenter;
	
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
		frame.setBounds(200, 200, 450, 620);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setMinimumSize(new Dimension(450, 620));
		frame.getContentPane().setLayout(new BorderLayout());
		frame.setTitle("Judoku 0.0.0.1");

		pnlCenter = new JPanel();
		pnlCenter.setLayout(new CardLayout());
		
		JPanel pnlWon = new JPanel();
		JTextArea test = new JTextArea();
		test.setText("afjrgkjehflkehgtluehlrugeuheihgliuerghliu");
		pnlWon.add(test);
		
		JPanel pnlGameField = new JPanel();
		pnlGameField.setLayout(new GridLayout(9, 9, 2, 2));
		
		JPanel pnlSouth = new JPanel();
		pnlSouth.setLayout(new GridLayout(2, 1));
		JPanel pnlSouthTop = new JPanel();
		pnlSouthTop.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
		JPanel pnlSouthBottom = new JPanel();
		pnlSouthBottom.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
		pnlSouth.add(pnlSouthTop);
		pnlSouth.add(pnlSouthBottom);

		// positioning and sizing the text fields
		initializeGameField(pnlGameField);
		
		pnlCenter.add(pnlGameField, "gameField");
		pnlCenter.add(pnlWon, "won");

		// draw the gamefield
		mnbrTop = new JMenuBar();
		mnbrTop.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));

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

		btnReset = new JButton("Reset");
		btnReset.setContentAreaFilled(false);
		btnReset.setBorderPainted(false);
		btnReset.addActionListener(new ButtonLauscher());
		btnReset.setEnabled(false);
		mnbrTop.add(btnReset);

		btnQuit = new JButton("Exit");
		btnQuit.setContentAreaFilled(false);
		btnQuit.addActionListener(new ButtonLauscher());
		btnQuit.setBorderPainted(false);
		mnbrTop.add(btnQuit);

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

		btnHint = new JButton("Give Hint");
		btnHint.setContentAreaFilled(false);
		btnHint.setPreferredSize(new Dimension(90, 35));
		btnHint.addActionListener(new ButtonLauscher());
		btnHint.setEnabled(false);
		pnlSouthTop.add(btnHint);

		btnValidate = new JButton("Validate");
		btnValidate.setContentAreaFilled(false);
		btnValidate.setPreferredSize(new Dimension(90, 35));
		btnValidate.addActionListener(new ButtonLauscher());
		btnValidate.setEnabled(false);
		pnlSouthTop.add(btnValidate);

		txtTime = new JTextField();
		txtTime.setEnabled(false);
		txtTime.setText("0s");
		txtTime.setBackground(Color.WHITE);
		txtTime.setDisabledTextColor(Color.BLACK);
		txtTime.setFont(new Font("DIALOG", Font.PLAIN, 14));
		txtTime.setPreferredSize(new Dimension(80, 25));
		txtTime.setHorizontalAlignment(SwingConstants.RIGHT);
		pnlSouthBottom.add(txtTime);

		JTextField progressTxt = new JTextField();
		progressTxt.setText("Progress");
		progressTxt.setEnabled(false);
		progressTxt.setBackground(frame.getBackground());
		progressTxt.setBorder(BorderFactory.createEmptyBorder());
		progressTxt.setDisabledTextColor(Color.BLACK);
		progressTxt.setFont(new Font("DIALOG", Font.PLAIN, 14));
		pnlSouthBottom.add(progressTxt);

		UIManager.put("ProgressBar.background", Color.WHITE);
		UIManager.put("ProgressBar.selectionBackground", Color.BLACK);
		UIManager.put("ProgressBar.selectionForeground", Color.BLACK);
		UIManager.put("ProgressBar.font", new Font("DIALOG", Font.BOLD, 14));
		prgrBar = new JProgressBar();
		prgrBar.setPreferredSize(new Dimension(200, 25));
		prgrBar.setStringPainted(true);
		prgrBar.setBorderPainted(false);
		prgrBar.setForeground(new Color(255, 200, 200));
		pnlSouthBottom.add(prgrBar);

		frame.getContentPane().add(mnbrTop, BorderLayout.PAGE_START);
		frame.getContentPane().add(pnlCenter, BorderLayout.CENTER);
		frame.getContentPane().add(pnlSouth, BorderLayout.SOUTH);
	}

	public void initializeGameField(JPanel pane) {
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
				gameField[y][x].setBorder(BorderFactory.createEmptyBorder());
				gameField[y][x].setDocument(new JTextFieldLimit(1));

				// Format the JTextFields
				gameField[y][x].setColumns(10);
				gameField[y][x].setFont(new Font("Arial", Font.BOLD, 38));
				gameField[y][x].setHorizontalAlignment(JTextField.CENTER);
				gameField[y][x].setBounds(xPosition, yPosition, width, height);
				gameField[y][x].setInitialColor(active);

				// Add the required listeners
				gameField[y][x].addFocusListener(new JudokuFocusListener());

				gameField[y][x].addMouseListener(new JudokuMouseListener());

				// add key listener, but allow only numeric "strings" from 0 to
				// 9
				gameField[y][x].addKeyListener(new KeyAdapter() {
					public void keyTyped(KeyEvent e) {
						char c = e.getKeyChar();
						if (((c < '0') || (c > '9'))
								&& (c != KeyEvent.VK_BACK_SPACE)) {
							e.consume(); // ignore event
						}
					}
				});

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

	public void refreshView() {
		int recentGrid[][] = this.puzzle.getRecentGrid();
		int startGrid[][] = this.puzzle.getStartGrid();
		int startFilledFields = 0;
		int userFilledFields = 0;
		for (int y = 0; y < 9; y++) {
			for (int x = 0; x < 9; x++) {
				if (startGrid[y][x] != 0) {
					gameField[y][x].setText(String.valueOf(startGrid[y][x]));
					gameField[y][x].setEnabled(false);
					gameField[y][x].setDisabledTextColor(Color.GRAY);
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

		prgrBar.setValue(100 * userFilledFields / (81 - startFilledFields));
		prgrBar.getRootPane().repaint();
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

	public void enableButtons(boolean enabled) {
		btnHint.setEnabled(enabled);
		btnUndo.setEnabled(enabled);
		btnRedo.setEnabled(enabled);
		btnReset.setEnabled(enabled);
	}

	class JudokuMouseListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			JudokuJTextField currentTextField = (JudokuJTextField) e
					.getSource();
			currentTextField.selectAll();
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			JudokuJTextField currentTextField = (JudokuJTextField) e
					.getSource();
			currentTextField.selectAll();
		}

		@Override
		public void mouseExited(MouseEvent e) {
			JudokuJTextField currentTextField = (JudokuJTextField) e
					.getSource();
			currentTextField.selectAll();
		}

		@Override
		public void mousePressed(MouseEvent e) {
			JudokuJTextField currentTextField = (JudokuJTextField) e
					.getSource();
			currentTextField.selectAll();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			JudokuJTextField currentTextField = (JudokuJTextField) e
					.getSource();
			currentTextField.selectAll();
		}

	}

	class JudokuFocusListener implements FocusListener {
		int oldValue;

		public void focusGained(java.awt.event.FocusEvent evt) {
			JudokuJTextField currentTextField = (JudokuJTextField) evt
					.getSource();
			// is the current value numeric?
			try {
				oldValue = Integer.parseInt(currentTextField.getText());
			} catch (NumberFormatException nfe) {
				System.out.println("'" + currentTextField.getText() + "'"
						+ " Is no number.");
			}
			currentTextField.selectAll();
		}

		public void focusLost(java.awt.event.FocusEvent evt) {
			JudokuJTextField currentTextField = (JudokuJTextField) evt
					.getSource();

			if (currentTextField.getText().equals("")
					|| currentTextField.getText().equals(" ")
					|| currentTextField.getText().equals(null)) {
				puzzle.trySetValue(currentTextField.X, currentTextField.Y, 0);
				currentTextField.unmmark();
			} else {
				System.out.println("NewValue: " + currentTextField.getText());
				if (oldValue != Integer.parseInt(currentTextField.getText())) {
					currentTextField.unmmark();
				}

				puzzle.trySetValue(currentTextField.X, currentTextField.Y,
						Integer.parseInt(currentTextField.getText()));

			}
			refreshView();
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

	class ButtonLauscher implements ActionListener {
		@Override
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
				JudokuTimeListener jtl = (JudokuTimeListener) swingTimer
						.getActionListeners()[0];
				jtl.reset();
				swingTimer.restart();
				refreshView();
			} else if (e.getSource() == btnMedium) {
				sWork = new JudokuSwingWorker(Difficulty.MEDIUM);
				sWork.execute();
				puzzle = sWork.easyGet();
				enableButtons(true);
				MenuSelectionManager.defaultManager().clearSelectedPath();
				JudokuTimeListener jtl = (JudokuTimeListener) swingTimer
						.getActionListeners()[0];
				jtl.reset();
				swingTimer.restart();
				refreshView();
			} else if (e.getSource() == btnHard) {
				sWork = new JudokuSwingWorker(Difficulty.HARD);
				sWork.execute();
				puzzle = sWork.easyGet();
				enableButtons(true);
				MenuSelectionManager.defaultManager().clearSelectedPath();
				JudokuTimeListener jtl = (JudokuTimeListener) swingTimer
						.getActionListeners()[0];
				jtl.reset();
				swingTimer.restart();
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
			} else if (e.getSource() == btnValidate) {
				int mistakes = controller.validateUserSolution(puzzle);
				if (mistakes == 0) {
					CardLayout cl = (CardLayout) pnlCenter.getLayout();
					cl.show(pnlCenter, "won");
					
				} else {
					// TODO
					System.out.println(mistakes + " mistakes");
				}
			}
		}
	}

}
