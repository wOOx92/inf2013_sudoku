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
	/**
	 * The GuiWindow in which this GuiInfoView is shown.
	 */
	private GuiWindow parentWindow;

	/*
	 * Declaring the content.
	 */
	private JPanel contentPane;
	private JButton btnBack;
	private JScrollPane jspSudokuRules;
	private JScrollPane jspFunctions;

	/**
	 * Creates a new instance of GUIInfoView
	 * 
	 * @param parentWindow
	 *            GuiWindow containing this GuiInfoView.
	 */
	public GuiInfoView(GuiWindow parentWindow) {	
		this.parentWindow = parentWindow;
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(5, 5));

		/*
		 * Initialize the panel and button in the south of the border layout.
		 */
		btnBack = new JButton("Back");
		btnBack.addActionListener(new InfoViewBtnListener());
		btnBack.setPreferredSize(new Dimension(100,50));
		btnBack.setContentAreaFilled(false);
		
		JPanel pnlSouth = new JPanel(new FlowLayout(FlowLayout.CENTER));
		pnlSouth.add(btnBack);
		contentPane.add(pnlSouth, BorderLayout.SOUTH);
		
		/*
		 * Prepare Image loading
		 */
		ClassLoader contextCl = Thread.currentThread().getContextClassLoader();
		
		/*
		 * Initialize all components for the tab "Sudoku Rules"
		 */
		JTabbedPane tabbedPane = new JTabbedPane();
		JTextPane tpSudokuRules = new JTextPane();
		jspSudokuRules = new JScrollPane(tpSudokuRules);
		tpSudokuRules.setEditable(false);
		tpSudokuRules.setFocusable(false);
		tpSudokuRules.setBorder(new EmptyBorder(10, 10, 10, 10));
		tpSudokuRules.setBackground(new Color(247, 247, 247)); // light grey
		tabbedPane.addTab("Sudoku Rules", jspSudokuRules); // Add the pane
		tpSudokuRules.setContentType("text/html");
		tpSudokuRules.setText("<html><div align='justify'>"
				+ "<p>- Sudoku is played over a 4x4, 9x9 or 16x16 grid, divided to 2x2, 3x3 or 4x4 sub grids called 'regions':</p> "
				+ "<p></p>"
				+ "<div align='center'><img src=\"" + contextCl.getResource("resources/sudokufield_empty.png") + "\" border = '1'/></img></div>"
				+ "<p>- Sudoku begins with some of the grid cells already filled with numbers:</p>"
				+ "<p></p>"
				+ "<div align='center'><img src=\"" + contextCl.getResource("resources/sudokufield_standard.png") + "\" border = '1'/></img></div>"
				+ "<p>- The object of Sudoku is to fill the other empty cells with numbers between 1 and 4, 1 and 9 or 1 and 16(1 number only in each cell) according the following guidelines: </p>"
				+ "<p>1. A number can appear only once on each row:</p>"
				+ "<p>Allowed:</p>"
				+ "<div align='center'><img src=\"" + contextCl.getResource("resources/sudokufield_row_correct.png") + "\" border = '1'/></img></div>"
				+ "<p>Not allowed:</p> "
				+ "<div align='center'><img src=\"" + contextCl.getResource("resources/sudokufield_row_false.png") + "\" border = '1'/></img></div>"
				+ "<p>2. A number can appear only once on each column:</p>"
				+ "<p>Allowed:</p>"
				+ "<div align='center'><img src=\"" + contextCl.getResource("resources/sudokufield_column_correct.png") + "\" border = '1'/></img></div>"
				+ "<p>Not allowed:</p>"
				+ "<div align='center'><img src=\"" + contextCl.getResource("resources/sudokufield_column_false.png") + "\" border = '1'/></img></div>"
				+ "<p>3. A number can appear only once on each region:</p>"
				+ "<p>Allowed:</p>"
				+ "<div align='center'><img src=\"" + contextCl.getResource("resources/sudokufield_region_correct.png") + "\" border = '1'/></img></div>"
				+ "<p>Not allowed:</p>"
				+ "<div align='center'><img src=\"" + contextCl.getResource("resources/sudokufield_region_false.png") + "\" border = '1'/></img></div>"
				+ "<p>- A summary of these guidelines would be, that a number should appear only once on each row, column and a region.</p>"
				+ "<p></p>"
				+ "- The Sudoku Game is finished successful, if all cells are filled in without breaking one of the above-mentioned rules."
				+ "</div></html>");
		
		/*
		 * Initialize all components for the tab "Functions".
		 */
		JTextPane tpFunctions = new JTextPane();
		jspFunctions = new JScrollPane(tpFunctions);
		tpFunctions.setEditable(false);
		tpSudokuRules.setFocusable(false);
		tpFunctions.setBorder(new EmptyBorder(10, 10, 10, 10));
		tpFunctions.setBackground(new Color(247, 247, 247));
		tabbedPane.addTab("Functions", jspFunctions);
		tpFunctions.setContentType("text/html");
		
		tpFunctions.setText("<html><div align='justify'>"
				+ "<b><p>New Game:</p></b> Shows which difficulties you can choose (easy/ medium/ hard). Also you can choose betwenn the different game modes (Mini 4x4, Maxi 16x16, Sudoku Solver)."
				+ "<b><p>Navigation:</p></b> When the game starts, the focus is automatically set on the first free cell. "
				+ "<br />"
				+ "There are two ways to navigate:."
				+ "<br />"
				+ "- You can click into the cell you want to mark, by using the <b>mouse</b>"
				+ "<br />"
				+ "- Or you can navigate through the cells, by using the <b>arrow keys</b>"
				+ "<b><p>Reset:</p></b> Resets the whole gamefield. After resetting all cells will be empty." 
				+ "<b><p>Info:</p></b> Opens the info dialogue."
				+ "<b><p>Redo [ctrl + y]:</p></b> "
				+ "<div align='left'><img src=\"" + contextCl.getResource("resources/redo.png") + "\" border = '1'/></img></div>"
				+ "Redoes the last step." 
				+ "<b><p>Undo [ctrl + z]:"
				+ "<div align='left'><img src=\"" + contextCl.getResource("resources/undo.png") + "\" border = '1'/></img></div>"
				+ "</p></b> Undoes the last redo." 
				+ "<b><p>Hint [ctrl + h]:</p></b> "
				+ "<div align='left'><img src=\"" + contextCl.getResource("resources/hint.png") + "\" border = '1'/></img></div>"
				+ "Gives a hint. There are two possibilties: "
				+ "<br>1. Markes a wrong number red.</br>"
				+ "<br>2. If all numbers are correct, a new number appears.</br>"
				+ "<b><p>Validate:</p></b> "
				+ "<div align='left'><img src=\"" + contextCl.getResource("resources/validate.png") + "\" border = '1'/></img></div>"
				+ "Is only enabled if all cells are filled in. Validates whether the Sudoku was filled in correctly. If in solving-mode this tries to solve the Sudoku. " 
				+ "<b><p>Statuspanel:</p></b> Shows the infos: Time (how long you are playing), progressbar(percentage of filled in fields), difficulty(shows the current game mode)"
				+ "<b><p>Solving-Mode:</p></b> In this mode you can enter a Sudoku to solve."
				+ "<b><p>Exit:</p></b> Closes the Game." 
				+ "</div></html>");
		
		/*
		 * Initialize all components for the tab "Credits"
		 */
		JTextPane tpCredits = new JTextPane();
		tpCredits.setEditable(false);
		tpSudokuRules.setFocusable(false);
		tpCredits.setBorder(new EmptyBorder(10, 10, 10, 10));
		tpCredits.setBackground(new Color(247, 247, 247));
		tabbedPane.addTab("Credits", tpCredits);
		tpCredits.setContentType("text/html");
		tpCredits.setText("<html><div align='center'>" 
				+ "<p></p>"
				+ "<h2>Sudoku game programmed by students of DHBW-Horb</h2>"
				+ "<p></p>"
				+ "<h2>Authors:</h2>" 
				+ "<p></p>"
				+ "<h3>Florian Steurer</h3>" 
				+ "<h3>Markus Wingler</h3>"
				+ "<h3>Dennis Uteg</h3>" 
				+ "<h3>Michael Jauch</h3>"
				+ "</div></html>");

		/*
		 * Add the tabbedPane to the main JPanel.
		 */
		contentPane.add(tabbedPane, BorderLayout.CENTER);
	}

	/**
	 * @return The content pane of this view.
	 */
	public JPanel getContentPane() {
		return contentPane;
	}

	/**
	 * Sets all scroll bars to the beginning. This is needed because the scroll
	 * bars change their value when the text in the pane is rendered.
	 */
	public void setScrollBarsToBeginning() {
		jspSudokuRules.getVerticalScrollBar().setValue(
				jspSudokuRules.getVerticalScrollBar().getMinimum());
		jspSudokuRules.getParent().repaint();
		jspFunctions.getVerticalScrollBar().setValue(
				jspFunctions.getVerticalScrollBar().getMinimum());
		jspFunctions.getParent().repaint();
	}

	/**
	 * Listens for buttons within this HelpInfoView.
	 */
	private class InfoViewBtnListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {

			if (e.getSource() == btnBack) {
				/*
				 * Make the GuiInfoView toggle back.
				 */
				GuiInfoView.this.parentWindow.toggleCenterViewBack();
			}

		}
	}

}
