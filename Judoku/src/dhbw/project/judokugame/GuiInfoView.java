package dhbw.project.judokugame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;

public class GuiInfoView {
	private GuiWindow parentWindow;

	/*
	 * Declaring buttons, panels and textpane
	 */
	private JPanel contentPane;
	private JButton btnBack;
	

	/**
	 * Creates a new instance of GUIInfoView
	 * 
	 * @param parentWindow
	 */
	public GuiInfoView(GuiWindow parentWindow) {
		this.parentWindow = parentWindow;

		btnBack = new JButton("Back");
		btnBack.addActionListener(new InfoViewBtnListener());
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(5, 5));
		
		JTabbedPane tabbedPane = new JTabbedPane();
		
		JTextPane tpSudokuRules = new JTextPane();
		tpSudokuRules.setEditable(false);
		tpSudokuRules.setFocusable(false);
		JScrollPane jspSudokuRules = new JScrollPane(tpSudokuRules);
		tpSudokuRules.setContentType("text/html");
		tpSudokuRules.setText("<html><div align='justify'>"
				+ "<p>- Sudoku is played over a 4x4, 9x9 or 16x16 grid, divided to 2x2, 3x3 or 4x4 sub grids called 'regions':</p> "
				+ "<img src='recources//redo.png'></img>"
				+ "<p>- Sudoku begins with some of the grid cells already filled with numbers:</p>"
				+ "<p>- The object of Sudoku is to fill the other empty cells with numbers between 1 and 4, 1 and 9 or 1 and 16(1 number only in each cell) according the following guidelines: </p>"
				+ "<p>1. A number can appear only once on each row:</p>"
				+ "<p>Allowed:</p>"
				+ "<img src='recources//redo.png'></img>"
				+ "<p>Not allowed:</p> "
				+ "<img src='recources//redo.png'></img>"
				+ "<p>2. A number can appear only once on each column:</p>"
				+ "<p>Allowed:</p>"
				+ "<img src='recources//redo.png'></img>"
				+ "<p>Not allowed:</p>"
				+ "<img src='recources//redo.png'></img>"
				+ "<p>3. A number can appear only once on each region:</p>"
				+ "<p>Allowed:</p>"
				+ "<img src='recources//redo.png'></img>"
				+ "<p>Not allowed:</p>"
				+ "<img src='recources//redo.png'></img>"
				+ "<p>- A summary of these guidelines would be, that a number should appear only once on each row, column and a region.</p>"
				+ "</div></html>");
		tpSudokuRules.setBorder(new EmptyBorder(10, 10, 10, 10));
		tpSudokuRules.setBackground(new Color(247,247,247));
		tabbedPane.addTab("SudokuRules", jspSudokuRules);
		
		JTextPane tpFunctions = new JTextPane();
		JScrollPane jspFunctions = new JScrollPane(tpFunctions);
		tpFunctions.setEditable(false);
		tpFunctions.setFocusable(false);
		tpFunctions.setContentType("text/html");
		tpFunctions.setText("<html><div align='justify'>"
				+ "<b><p>New Game:</p></b> Shows which difficulties you can choose (easy/ medium/ hard). Also you can choose betwenn the different game modes (Mini 4x4, Maxi 16x16, Sudoku Solver)."
				+ "<b><p>Reset:</p></b> Resets the whole gamefield. After resetting all cells will be empty." 
				+ "<b><p>Exit:</p></b> Closes the Game." 
				+ "<b><p>Info:</p></b> Opens the info dialogue."
				+ "<b><p>Redo [ctrl + y]:</p></b> Redoes the last step." 
				+ "<b><p>Undo [ctrl + z]:</p></b> Undoes the last redo." 
				+ "<b><p>Hint [ctrl + h]:</p></b> Gives a hint. There are two possibilties: "
				+ "<br>1. Markes a wrong number red.</br>"
				+ "<br>2. If all numbers are correct, a new number appears.</br>"
				+ "<b><p>Validate:</p></b> Is only enabled if all cells are filled in. Validates whether the Sudoku was filled in correctly. If in solving-mode this tries to solve the Sudoku. " 
				+ "<b><p>Statuspanel:</p></b> Shows the infos: Time (how long you are playing), progressbar(percentage of filled in fields), difficulty(shows the current game mode)"
				+ "<b><p>Solving-Mode:</p></b> In this mode you can enter a Sudoku to solve."
				+ "</div></html>");
		tpFunctions.setBorder(new EmptyBorder(10, 10, 10, 10));
		tpFunctions.setBackground(new Color(247,247,247));
		tabbedPane.addTab("Functions", jspFunctions);

		JTextPane tpCredits = new JTextPane();
		JScrollPane jspCredits = new JScrollPane(tpCredits);
		tpCredits.setEditable(false);
		tpCredits.setFocusable(false);
		tpCredits.setContentType("text/html");
		tpCredits.setText("<html><div align='center'>" 
				+ "<p></p>"
				+ "<h2>Sudoku game programmed by students of DHBW-Horb</h2>"
				+ "<h2>Programmed by</h2>" 
				+ "<p></p>"
				+ "<p></p>"
				+ "<h3>Florian Steurer</h3>" 
				+ "<h3>Markus Wingler</h3>"
				+ "<h3>Dennis Uteg</h3>" 
				+ "<h3>Michael Jauch</h3>"
				+ "</div></html>");
		tpCredits.setBorder(new EmptyBorder(10, 10, 10, 10));
		tpCredits.setBackground(new Color(247,247,247));
		tabbedPane.addTab("Credits", jspCredits);

		contentPane.add(tabbedPane, BorderLayout.CENTER);

		JPanel pnlSouth = new JPanel(new FlowLayout(FlowLayout.CENTER));
		btnBack.setPreferredSize(new Dimension(100,50));
		pnlSouth.setPreferredSize(new Dimension(100,70));
		pnlSouth.add(btnBack);
		btnBack.setContentAreaFilled(false);
		contentPane.add(pnlSouth, BorderLayout.SOUTH);
	}

	public JPanel getContentPane() {
		return contentPane;
	}
	
	private class InfoViewBtnListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {

			if(e.getSource() == btnBack) {
				GuiInfoView.this.parentWindow.toggleCenterViewBack();
			}

			
		}
	}

}
