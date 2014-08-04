package dhbw.project.judokugame;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;

public class GuiInfoView {
	private GuiWindow parentWindow;
	
	private JPanel contentPane;
    private JButton btnInfo;
    private JButton btnSudokuRules;
    private JButton btnFunctions;
	private JButton btnCredits;
	private JButton btnReturn;
	private JTextPane txtCenterText;
	private JPanel pnlButtons;
	
	/**
	 * Create the frame.
	 */
	public GuiInfoView(GuiWindow parentWindow) {
		this.parentWindow = parentWindow;
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(5, 5));
		
		pnlButtons = new JPanel();
		pnlButtons.setLayout(new GridLayout(5,1,30,30));
		
		contentPane.add(pnlButtons, BorderLayout.WEST);
		btnInfo = new JButton("Info");
		btnInfo.setContentAreaFilled(false);
		btnInfo.addActionListener(new WindowHelpButtonListener());
		
		btnSudokuRules = new JButton("Sudoku Rules");
		btnSudokuRules.addActionListener(new WindowHelpButtonListener());
		btnSudokuRules.setContentAreaFilled(false);
		
		btnFunctions = new JButton("Functions");
		btnFunctions.addActionListener(new WindowHelpButtonListener());
		btnFunctions.setContentAreaFilled(false);
		
		btnCredits = new JButton("Credits");
		btnCredits.addActionListener(new WindowHelpButtonListener());
		btnCredits.setContentAreaFilled(false);
		
		btnReturn = new JButton("Go back");
		btnReturn.addActionListener(new WindowHelpButtonListener());
		btnReturn.setContentAreaFilled(false);
		
		pnlButtons.add(btnInfo);
		pnlButtons.add(btnSudokuRules);
		pnlButtons.add(btnFunctions);
		pnlButtons.add(btnCredits);
		pnlButtons.add(btnReturn);
		
		
		txtCenterText = new JTextPane();
		txtCenterText.setContentType("text/html");
		txtCenterText.setBackground(contentPane.getBackground());
		contentPane.add(txtCenterText, BorderLayout.CENTER);			
	}
	
	public JPanel getContentPane() {
		return contentPane;
	}
	
	class WindowHelpButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btnInfo) {
			txtCenterText.setText("<html><font face = 'arial'>This game is a programming-project.</font face></html>");
		
			} 
			else if (e.getSource() == btnSudokuRules) {
				txtCenterText.setText("<html><font face = 'arial'>Completed puzzles are always a type of Latin square with an additional constraint on the contents of individual regions. \n"
						+ "For example, the same single integer may not appear twice in the same 9×9 playing board row or column or in any of the nine 3×3 \n"
						+ "subregions of the 9×9 playing board.</font face></html>");
			
				} 
			    else if (e.getSource() == btnFunctions) {
			    	txtCenterText.setText("<html> <font face = 'arial'>"
			    			+ "<b>New Game</b>: You can chose between tree different levels (easy/medium/hard). By selecting one of this levels, a new sudoku puzzle will be created.\n"
			    			+ "<br>"
			    			+ "<b>Hint Button</b>: Gives a hint. If there exist a false integer it will be underlined in red. If all integers are correct, a new one will appear.\n"
			    			+ "<br>"
			    			+ "<b>Undo Button</b>: The last integer will dissapear, but you can push this button only 5times in a row.\n"
			    			+ "<br>"
			    			+ "<b>Redo Button</b>: Recreates the last integer which was deleted by the undo function. "
			    			+ "<br>"
			    			+ "<b>Validate Button</b>: Validates the Puzzle. An info will appear if there are mistakes left. If there are no mistakes left the game ends\n"
			    			+ "<br>"
			    			+ "<b>Help</b>: Opens the window you see right now\n"
			    			+ "<br>"
			    			+ "<b>Reset</b>: Resets the sudoku-gamefield. A new game will be created.\n"
			    			+ "<br>"
			    			+ "<b>Exit</b>: Closes the Game.</font face></html>");
			    	
					} 
			    	else if (e.getSource() == btnCredits) {
			    		txtCenterText.setText("<html><font face = 'arial'><b>This game is proudly presented by:</b>"
			    				+ "<p></p>"
			    				+ "Florian Steurer"
			    				+ "<br>"
			    				+ "Markus Wingler"
			    				+ "<br>"
			    				+ "Dennis Uteg"
			    				+ "<br>"
			    				+ "Michael Jauch</font face></html>");				
			    	} 
			    	else if(e.getSource() == btnReturn){
			    		parentWindow.toggleCenterViewBack();
			    	}
		}
	}

}
