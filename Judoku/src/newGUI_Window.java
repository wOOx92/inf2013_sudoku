import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.GridLayout;
import javax.swing.JMenuBar;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JProgressBar;


public class newGUI_Window {

	private JFrame frame;
	
	/*
	 * Buttons
	 */
	private JButton btnGiveHint;
	private JButton btnReset;
	private JButton btnRedo;
	private JButton btnNewButton;
	private JButton btnLangDEU; 
	private JButton btnLangENG;
	private JButton btnNewButton_2;
	private JButton btnValidate;
	
	/*
	 * Panels
	 */
	JPanel panel;
	JPanel panel_1;
	JPanel panel_2;
	
	/*
	 * Others
	 */
	JComboBox comboBox;
	JMenuBar menuBar;
	
	/**
	 * Create the application.
	 */
	public newGUI_Window() {
		initialize();
	}

	public JFrame getJFrame(){
		return this.frame;
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 689, 396);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new GridLayout(4, 1, 0, 0));
		
		menuBar = new JMenuBar();
		frame.getContentPane().add(menuBar);
		
		comboBox = new JComboBox();
		menuBar.add(comboBox);
		
		btnGiveHint = new JButton("Give Hint");
		menuBar.add(btnGiveHint);
		
		btnReset = new JButton("Reset");
		menuBar.add(btnReset);
		
		panel = new JPanel();
		menuBar.add(panel);
		
		btnLangDEU = new JButton("DEU");
		btnLangDEU.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		
		panel.add(btnLangDEU);
		
		btnLangENG = new JButton("ENG");
		panel.add(btnLangENG);
		
		panel_2 = new JPanel();
		frame.getContentPane().add(panel_2);
		
		panel_1 = new JPanel();
		frame.getContentPane().add(panel_1);
		
		btnNewButton_2 = new JButton("Undo");
		panel_1.add(btnNewButton_2);
		
		btnRedo = new JButton("Redo");
		panel_1.add(btnRedo);
		
		btnValidate = new JButton("Validate");
		panel_1.add(btnValidate);
		
		JPanel panel_3 = new JPanel();
		frame.getContentPane().add(panel_3);
		
		JLabel lblTime = new JLabel("TIME");
		panel_3.add(lblTime);
		
		JLabel lblCompletedFields = new JLabel("Completed Fields");
		panel_3.add(lblCompletedFields);
		
		JProgressBar progressBar = new JProgressBar();
		panel_3.add(progressBar);
	}
}
