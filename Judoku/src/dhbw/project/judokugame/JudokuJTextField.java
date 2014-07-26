package dhbw.project.judokugame;

import java.awt.Color;
import java.awt.event.FocusEvent;

import javax.swing.BorderFactory;
import javax.swing.JTextField;

/**
 * JudokuJTextFields a JTextFields modified to fit new needs. JudokuJTextFields
 * are easy to mark and unmark and can save their X and Y position in a grid.
 * 
 * @author DH10STF
 * 
 */
public class JudokuJTextField extends JTextField {

	
	private static final long serialVersionUID = 1l;
	
	/**
	 * The x-Value of the JudokuJTextField in the game field grid.
	 */
	final public int X;

	/**
	 * The y-Value of the JudokuJTextField in the game field grid.
	 */
	final public int Y;

	/**
	 * The text that this JudokuJTextField contained when {@link JudokuJTextField#mark()} was called.
	 */
	private String contentWhenMarked;
	
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
	public void setText(String t){
		if(!this.getText().equals(t) || !this.getText().equals(contentWhenMarked)){
			this.unmark();
		}
		super.setText(t);
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
		this.contentWhenMarked = this.getText();
		super.setBackground(Color.RED);
	}

	/**
	 * Switches this JudokuJTextFields to its initial color.
	 */
	public void unmark() {
		super.setBackground(initialColor);
	}
	
	@Override
	protected void processFocusEvent(FocusEvent e) {
		if(e.getID() == FocusEvent.FOCUS_GAINED) {
			this.setBorder(BorderFactory.createMatteBorder(3, 3, 3,
					3, new Color(0, 165, 255)));

			this.selectAll();
		} else if(e.getID() == FocusEvent.FOCUS_LOST) {
			this.setBorder(BorderFactory.createEmptyBorder());		
		}
		
		super.processFocusEvent(e);
	}
	
}