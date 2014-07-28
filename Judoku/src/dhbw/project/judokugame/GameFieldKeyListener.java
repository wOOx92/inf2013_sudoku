package dhbw.project.judokugame;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Listens to KeyInputs within a game field array and takes corresponding
 * actions.
 */
public class GameFieldKeyListener extends KeyAdapter {

	/**
	 * The game field where this listener is applied.
	 */
	private final JudokuJTextField[][] gameField;

	/**
	 * Returns a new instance of GameFieldKeyListener.
	 * 
	 * @param gameField
	 *            The game field where this listener will apply actions.
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
		JudokuJTextField currentTextField = (JudokuJTextField) e.getSource();

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
		if (key == KeyEvent.VK_UP) {
			focusNextTextField(x, y, false, true);
		} else if (key == KeyEvent.VK_DOWN) {
			focusNextTextField(x, y, true, true);
		} else if (key == KeyEvent.VK_LEFT) {
			focusNextTextField(x, y, false, false);
		} else if (key == KeyEvent.VK_RIGHT) {
			focusNextTextField(x, y, true, false);
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		JudokuJTextField currentTextField = (JudokuJTextField) e.getSource();

		char keyChar = e.getKeyChar();

		/*
		 * If the input is unexpected or a backspace, end this method and do not
		 * selectAll().
		 */
		if (!Character.isDigit(keyChar) && (keyChar != KeyEvent.VK_BACK_SPACE)) {
			return;
		}

		/*
		 * If the Sudoku is 16x16 and the JudokuJTextField contains a "1", then
		 * this method does nothing, because if it would selectAll(), the user
		 * were not able to put 10-16 in the boxes because it would select and
		 * then overwrite the "1".
		 */
		if (currentTextField.getText().equals("1")
				&& this.gameField.length == 16) {
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
	 *            If true, searches every index greater than x and smaller than
	 *            the Sudoku size (increasing x), otherwise searches every index
	 *            smaller than x and greater equal 0 (decreasing x).
	 * @param searchInY
	 *            Searches the game field in y direction instead of x direction.
	 */
	private void focusNextTextField(int x, int y, boolean positiveDirection,
			boolean searchInY) {
		int size = gameField.length;
		/*
		 * Increase (Decrease) the x (y) coordinate until an enabled field is
		 * found or the maximum (minimum) is reached. Do this at least once.
		 */
		do {
			if (positiveDirection && searchInY) {
				y++;
			} else if (positiveDirection && !searchInY) {
				x++;
			} else if (!positiveDirection && searchInY) {
				y--;
			} else if (!positiveDirection && !searchInY) {
				x--;
			}
		} while (y < size && y >= 0 && 0 <= x && x < size
				&& !gameField[y][x].isEnabled());

		/*
		 * If the values have not been increased (decreased) over the borders of
		 * the array, request focus for the cell.
		 */
		if (0 <= y && y < size && 0 <= x && x < size) {
			gameField[y][x].requestFocusInWindow();
		}
	}
}