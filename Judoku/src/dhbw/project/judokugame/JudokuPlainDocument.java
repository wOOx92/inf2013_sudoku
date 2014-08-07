package dhbw.project.judokugame;

import java.util.HashSet;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * This custom PlainDocument only allows numeric input ranging from 1 to a
 * certain limit.
 * 
 * @author Dennis Uteg, Florian Steurer, Markus Wingler, Michael Jauch
 * 
 */
public class JudokuPlainDocument extends PlainDocument {

	private static final long serialVersionUID = 0l;

	/**
	 * HashSet containing String values of every possible input from 0 to a
	 * certain limit. Only these Strings will be accepted as content of the
	 * JudokuPlainDocument.
	 */
	private HashSet<String> allowedValues = new HashSet<>();
	
	/**
	 * Returns an instance of JudokuPlainDocument that will only allow values
	 * from 1 to limit (inclusive) as content.
	 */
	JudokuPlainDocument(int limit) {
		super();
		
		/*
		 * Fill the HashSet with allowed values.
		 */
		for (int i = 1; i <= limit; i++) {
			allowedValues.add(String.valueOf(i));
		}
	}

	@Override
	public void insertString(int offset, String str, AttributeSet attr)
			throws BadLocationException {

		/*
		 * Prevent exceptions, do nothing
		 */
		if (str == null)
			return;

		/*
		 * Do a what-if and see if the resulting value is allowed.
		 */
		StringBuilder textBuilder = new StringBuilder(super.getText(0,
				getLength()));
		textBuilder.insert(offset, str);
		String text = textBuilder.toString();

		/*
		 * If it is allowed, call the actual insertString() method.
		 */
		if (allowedValues.contains(text)) {
			super.insertString(offset, str, attr);
		}
	}
}