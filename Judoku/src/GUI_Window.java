import java.awt.EventQueue;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JTextPane;

public class GUI_Window {

	private JFrame frame;

	// the gamefield

	// private JTextField textField;
	// private JTextField textField_1;
	// private JTextField textField_2;
	private JTextField[][] gameField = new JTextField[9][9];

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI_Window window = new GUI_Window();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GUI_Window() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 497, 413);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		// positioning and sizing the text fields
		int xPosition = 10;
		int yPosition = 10;
		final int width = 40;
		final int height = 40;

		//draw the gamefield
		for (int i = 0; i < 9; i++) {
			yPosition = 10;
			for (int j = 0; j < 9; j++) {
				gameField[i][j] = new JTextField();
				gameField[i][j].setColumns(10);
				gameField[i][j].setBounds(xPosition, yPosition, width, height);
				frame.getContentPane().add(gameField[i][j]);
				
				yPosition = yPosition + 38;
			}
			
			xPosition = xPosition + 38;
		}

		JButton btnNewButton = new JButton("New button");
		btnNewButton.setBounds(370, 11, 100, 40);
		frame.getContentPane().add(btnNewButton);

		JButton button = new JButton("New button");
		button.setBounds(370, 50, 100, 40);
		frame.getContentPane().add(button);

		JButton button_1 = new JButton("New button");
		button_1.setBounds(370, 89, 100, 40);
		frame.getContentPane().add(button_1);

		JButton button_2 = new JButton("New button");
		button_2.setBounds(370, 128, 100, 40);
		frame.getContentPane().add(button_2);

		JButton button_3 = new JButton("New button");
		button_3.setBounds(370, 167, 100, 40);
		frame.getContentPane().add(button_3);

		JTextPane txtpnNochVersuch = new JTextPane();
		txtpnNochVersuch.setText("Noch 1 Versuch");
		txtpnNochVersuch.setBounds(370, 245, 100, 40);
		frame.getContentPane().add(txtpnNochVersuch);
	}
}
