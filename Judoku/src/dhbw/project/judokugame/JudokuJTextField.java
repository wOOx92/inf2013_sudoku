package dhbw.project.judokugame;

import java.awt.Color;

import javax.swing.JTextField;

/**
 * JudokuJTextFields a JTextFields modified to fit new needs. JudokuJTextFields
 * are easy to mark and unmark and can save their X and Y position in a grid.
 * 
 * @author DH10STF
 * 
 */
public class JudokuJTextField extends JTextField {

	/**
	 * The x-Value of the JudokuJTextField in the game field grid.
	 */
	final public int X;

	/**
	 * The y-Value of the JudokuJTextField in the game field grid.
	 */
	final public int Y;

	private Color initialColor;

	/**
	 * Creates a new JudokuJTextField.
	 * 
	 * @param x
	 *            The x-position of the JudokuJTextField in the game field.
	 * @param y
	 *            The y-position of the JudokuJTextField in the game field.
	 */
	public JudokuJTextField(int x, int y) {
		this.X = x;
		this.Y = y;
	}

	/**
	 * Sets the base color of the JudokuJTextField. After calling
	 * {@link JudokuJTextField#unmark()}, the JudokuJTextField will switch back
	 * to its initial color.
	 */
	public void setInitialColor(Color inColor) {
		super.setBackground(inColor);
		this.initialColor = inColor;
	}

	/**
	 * Switches this JudokuJTextFields color to red.
	 */
	public void mark() {
		super.setBackground(Color.RED);
	}

	/**
	 * Switches this JudokuJTextFields to its initial color.
	 */
	public void unmark() {
		super.setBackground(initialColor);
	}
}