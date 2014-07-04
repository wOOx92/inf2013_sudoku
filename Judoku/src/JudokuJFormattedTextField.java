import java.awt.Color;

import javax.swing.JFormattedTextField;
import javax.swing.text.MaskFormatter;

public class JudokuJFormattedTextField extends JFormattedTextField {

	final public int X;
	final public int Y;
	private Color initialColor;

	public JudokuJFormattedTextField(int x, int y, MaskFormatter mskf) {
		super(mskf);
		this.X = x;
		this.Y = y;
	}
	
	public void setInitialColor (Color thecolor){
		super.setBackground(thecolor);
		this.initialColor =thecolor;
		
		
	}
	public void mark(){
		super.setBackground(Color.RED);
	}
	public void unmmark(){
		super.setBackground(initialColor);
	}

}
