import java.awt.Color;

import javax.swing.JTextField;
import javax.swing.border.Border;

public class JudokuJTextField extends JTextField {

	final public int X;
	final public int Y;
	private Color initialColor;
	
	public JudokuJTextField(int x, int y) {
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
