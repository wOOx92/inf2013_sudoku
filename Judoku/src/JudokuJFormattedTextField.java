import javax.swing.JFormattedTextField;
import javax.swing.text.MaskFormatter;

public class JudokuJFormattedTextField extends JFormattedTextField {

	final int X;
	final int Y;

	public JudokuJFormattedTextField(int x, int y, MaskFormatter mskf) {
		super(mskf);
		this.X = x;
		this.Y = y;
	}

}
