package dhbw.project.judokugame;

import java.awt.Color;
import java.awt.event.FocusEvent;

import javax.swing.BorderFactory;
import javax.swing.JTextField;

/**
 * JudokuJTextFields are JTextFields modified to fit new needs. For the most
 * part JudokuJTextFields act autonomously. They can be marked and unmark
 * themselves if their content is changed and they highlight themselves with a
 * border if they gain focus, and delete this border if they lose it.
 * JudokuJTextFields save their X and Y position inside grids.
 * 
 * @author Dennis Uteg, Florian Steurer, Markus Wingler, Michael Jauch
 * 
 */
public class JudokuJTextField extends JTextField {

	private static final long serialVersionUID = 1l;

	/**
	 * The x-Value of the JudokuJTextField in a grid.
	 */
	final public int X;

	/**
	 * The y-Value of the JudokuJTextField in a grid.
	 */
	final public int Y;

	/**
	 * The text that this JudokuJTextField contained when
	 * {@link JudokuJTextField#mark()} was called.
	 */
	private String contentWhenMarked;

	/**
	 * Its color when it is unmarked.
	 */
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

	@Override
	public void setText(String t) {
		/*
		 * If the text changes or is different from the text when the field was
		 * marked
		 */
		if (!this.getText().equals(t)
				|| !this.getText().equals(contentWhenMarked)) {
			/*
			 * unmark the field.
			 */
			super.setBackground(initialColor);
		}
		super.setText(t);
	}

	/**
	 * Sets the base color of the JudokuJTextField. After calling
	 * {@link JudokuJTextField#mark()}, the JudokuJTextField will switch back to
	 * its initial color when its content changes.
	 */
	public void setInitialColor(Color inColor) {
		super.setBackground(inColor);
		this.initialColor = inColor;
	}

	/**
	 * Marks this JudokuJTextField using a red background color. It will unmark
	 * automatically if its content changes.
	 */
	public void mark() {
		this.contentWhenMarked = this.getText();
		super.setBackground(Color.RED);
	}

	@Override
	protected void processFocusEvent(FocusEvent e) {
		/*
		 * If the focus was gained, set a blue border and select all of the
		 * content (so the user can easily overwrite it).
		 */
		if (e.getID() == FocusEvent.FOCUS_GAINED) {
			this.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3,
					new Color(0, 165, 255))); // Light-Blue

			this.selectAll();
		} else if (e.getID() == FocusEvent.FOCUS_LOST) {
			/*
			 * If the focus was lost, remove the blue border.
			 */
			this.setBorder(BorderFactory.createEmptyBorder());
		}

		super.processFocusEvent(e);
	}
}