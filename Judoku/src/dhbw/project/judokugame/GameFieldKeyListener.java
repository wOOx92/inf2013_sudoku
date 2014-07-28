package dhbw.project.judokugame;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Listens to KeyInputs within a game field array and takes corresponding actions.
 */
public class GameFieldKeyListener extends KeyAdapter {

	/**
	 * The game field where this listener is applied.
	 */
	private final JudokuJTextField[][] gameField;
	
	/**
	 * Returns a new instance of GameFieldKeyListener.
	 * @param gameField The game field where this listener will apply actions.
	 */
	public GameFieldKeyListener(JudokuJTextField[][] gameField) {
		this.gameField = gameField;
	}
	
	@Override 
	public void keyPressed(KeyEvent e) {
		/*
		 * The listener should be applied on the game field elements, which are
		 * JudokuJTextFields.
		 */
		JudokuJTextField currentTextField = (JudokuJTextField) e
				.getSource();
		
		/*
		 * Get the pressed key and the y and x coordinates of the field that
		 * dispatched this event.
		 */
		int key = e.getKeyCode();
		int x = currentTextField.X;
		int y = currentTextField.Y;
		
		/*
		 * If the user pressed arrow up, down, left or right, focus the next
		 * game field in this direction.
		 */
		if(key == KeyEvent.VK_UP) {
			focusNextTextFieldY(x, y, false);
		} else if(key == KeyEvent.VK_DOWN) {
			focusNextTextFieldY(x, y, true);
		} else if(key == KeyEvent.VK_LEFT) {
			focusNextTextFieldX(x, y, false);
		} else if(key == KeyEvent.VK_RIGHT) {
			focusNextTextFieldX(x, y, true);
		}
	}
	
	@Override
	public void keyTyped(KeyEvent e) {			
		JudokuJTextField currentTextField = (JudokuJTextField) e
				.getSource();
		
		char c = e.getKeyChar();
		
		/*
		 * If the input is unexpected or a backspace, end this method and do not
		 * selectAll().
		 */
		if (!Character.isDigit(c) && (c != KeyEvent.VK_BACK_SPACE)) {
			return;
		}
		
		/*
		 * If the Sudoku is 16x16 and the JudokuJTextField contains a "1", then
		 * this method does nothing, because if it would selectAll(), the user
		 * were not able to put 10-16 in the boxes because it would select and
		 * then overwrite the "1".
		 */
		if(currentTextField.getText().equals("1") && this.gameField.length == 16) {
			return;
		}
		
		/*
		 * Automatically selecting the content so the caret can be disabled.
		 */
		currentTextField.selectAll();
	}
	
	/**
	 * Tries to find the next focusable JudokuJTextField in this listeners
	 * gameField in y direction.
	 * 
	 * @param x
	 *            The column index.
	 * @param y
	 *            The row index.
	 * @param positiveDirection
	 *            If true, searches every index greater than y and smaller
	 *            than the Sudoku size (increasing y), otherwise searches
	 *            every index smaller than y and greater equal 0 (decreasing
	 *            y).
	 */
	private void focusNextTextFieldY(int x, int y, boolean positiveDirection) {
		int size = gameField.length;
		do {
			if (positiveDirection) {
				y++;
			} else {
				y--;
			}
		} while (y < size && y >= 0 && !gameField[y][x].isEnabled());

		if (0 <= y && y < size) {
			gameField[y][x].requestFocusInWindow();
		}
	}

	/**
	 * Tries to find the next focusable JudokuJTextField in this listeners
	 * gameField in x direction.
	 * 
	 * @param x
	 *            The column index.
	 * @param y
	 *            The row index.
	 * @param positiveDirection
	 *            If true, searches every index greater than x and smaller
	 *            than the Sudoku size (increasing x), otherwise searches
	 *            every index smaller than x and greater equal 0 (decreasing
	 *            x).
	 */
	private void focusNextTextFieldX(int x, int y, boolean positiveDirection) {
		int size = gameField.length;
		
		do {
			if (positiveDirection) {
				x++;
			} else {
				x--;
			}
		} while (x < size && x >= 0 && !gameField[y][x].isEnabled());

		if (0 <= x && x < size) {
			gameField[y][x].requestFocusInWindow();
		}
	}
}