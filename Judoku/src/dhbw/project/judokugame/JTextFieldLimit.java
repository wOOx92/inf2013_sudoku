package dhbw.project.judokugame;

import java.util.HashSet;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

class JTextFieldLimit extends PlainDocument {

	private static final long serialVersionUID = 0l;

	private HashSet<String> allowedValues = new HashSet<>();

	JTextFieldLimit(int limit) {
		super();

		for (int i = 1; i <= limit; i++) {
			allowedValues.add(String.valueOf(i));
		}
	}

	@Override
	public void insertString(int offset, String str, AttributeSet attr)
			throws BadLocationException {
		
		if (str == null)
			return;	

		/*
		 * Do a what-if and see if the resulting value is allowed.
		 */
		StringBuilder textBuilder = new StringBuilder(super.getText(0, getLength()));
		textBuilder.insert(offset, str);
		String text = textBuilder.toString();

		if (allowedValues.contains(text)) {
			super.insertString(offset, str, attr);
		}
	}
}