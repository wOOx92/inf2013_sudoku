package dhbw.project.judokugame;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Listens for keyinput from user.
 */
public class JudokuKeyListener extends KeyAdapter {

	/**
	 * 
	 */
	private final JudokuJTextField[][] gameField;
	
	public JudokuKeyListener(JudokuJTextField[][] gameField) {
		this.gameField = gameField;
	}
	
	@Override 
	public void keyPressed(KeyEvent e) {
		JudokuJTextField currentTextField = (JudokuJTextField) e
				.getSource();
		
		int key = e.getKeyCode();
		int x = currentTextField.X;
		int y = currentTextField.Y;
		
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
		if (!Character.isDigit(c) && (c != KeyEvent.VK_BACK_SPACE)) {
			return;
		}
		
		if(currentTextField.getText().equals("1") && this.gameField.length == 16) {
			return;
		}
		
		currentTextField.selectAll();
	}
	
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