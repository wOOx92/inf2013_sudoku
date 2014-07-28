package dhbw.project.judokugame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;

/**
 * Displays time in a given JTextField.
 * 
 */
public class JudokuTimeListener implements ActionListener {
	/**
	 * The JTextField where the time will be written in.
	 */
	private final JTextField txtOutput;

	/**
	 * @param txtOutput
	 *            The listener will write the passed time into this JTextField.
	 */
	JudokuTimeListener(JTextField txtOutput) {
		this.txtOutput = txtOutput;
	}

	private int secondsPassed = 0;

	@Override
	public void actionPerformed(ActionEvent e) {
		/*
		 * Calculates the passed time.
		 */
		secondsPassed++;
		int hrs = secondsPassed / 3600;
		int min = secondsPassed / 60 - hrs*60;
		int sec = secondsPassed - 60*min - hrs*3600;
		
		/*
		 * Formatting the time to a format friendly for human reading.
		 */
		String text = String.format("%d:%02d:%02d", hrs, min, sec);
		txtOutput.setText(text);
	}

	public void reset() {
		secondsPassed = 0;
		txtOutput.setText("0:00:00");
	}
}