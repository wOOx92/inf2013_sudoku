package dhbw.project.judokugame;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.MenuSelectionManager;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;

import dhbw.project.judokugame.GUI_Window.JudokuButtonListener;
import dhbw.project.judokugame.GUI_Window.JudokuTimeListener;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WindowHelp extends JFrame {

	private JPanel contentPane;
    private JButton btnInfo;
    private JButton btnSudokuRules;
    private JButton btnFunctions;
	private JButton btnCredits;
	private JTextArea txtCenterText;
	private JPanel pnlTextArea;
	private JPanel pnlButtons;
	/**
	 * Create the frame.
	 */
	public WindowHelp() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(5, 5));
		setContentPane(contentPane);
		setSize(500, 300);
		setResizable(false);
		
		JPanel pnlButtons = new JPanel();
		pnlButtons.setLayout(new GridLayout(4,1,10,10));
		
		contentPane.add(pnlButtons, BorderLayout.WEST);
		JButton btnInfo = new JButton("Info");
		btnInfo.addActionListener(new WindowHelpButtonListener());
		JButton btnSudokuRules = new JButton("Sudoku Rules");
		btnSudokuRules.addActionListener(new WindowHelpButtonListener());
		JButton btnFunctions = new JButton("Functions");
		btnFunctions.addActionListener(new WindowHelpButtonListener());
		JButton btnCredits = new JButton("Credits");
		btnCredits.addActionListener(new WindowHelpButtonListener());
		pnlButtons.add(btnInfo);
		pnlButtons.add(btnSudokuRules);
		pnlButtons.add(btnFunctions);
		pnlButtons.add(btnCredits);
	
		JPanel pnlTextArea = new JPanel();
		contentPane.add(pnlTextArea, BorderLayout.CENTER);
		JTextArea txtCenterText = new JTextArea(100, 100);
		
		pnlTextArea.add(txtCenterText);
		
		
	}
	
	class WindowHelpButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btnInfo) {
			txtCenterText.setText("Here comes the Info");
			pnlTextArea.repaint();
			} 
			else if (e.getSource() == btnSudokuRules) {
				txtCenterText.setText("Rulez");
				pnlTextArea.repaint();
				} 
			    else if (e.getSource() == btnFunctions) {
			    	txtCenterText.setText("Functionz");
			    	pnlTextArea.repaint();
					} 
			    	else if (e.getSource() == btnCredits) {
			    		txtCenterText.setText("Credits");
			    		pnlTextArea.repaint();
						} 
			
		}
	}

}
