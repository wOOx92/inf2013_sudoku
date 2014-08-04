package dhbw.project.judokugame;

import java.awt.EventQueue;

import dhbw.project.puzzlemodel.Sudoku;

/**
 * The Controller evaluates user inputs from the GUI_Window and initializes the
 * necessary actions inside the model (the NumberPuzzle object).
 * 
 * @author Dennis Uteg, Florian Steurer, Markus Wingler, Michael Jauch
 * 
 */
public class Controller {

	/**
	 * This is the GuiWindow object sending user inputs and requests to this
	 * controller object.
	 */
	private GuiWindow linkedGuiWindow;

	public static void main(String[] args) {
		Controller c = new Controller();
		c.initGui();
	}

	/**
	 * Initializes a GUI_Window that is linked to the Controller object
	 * executing the method.
	 */
	public void initGui() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GuiWindow window = new GuiWindow(Controller.this);
					linkedGuiWindow = window;
					window.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void resetSudoku(Sudoku sdk) {
		sdk.reset();
	}

	public void undoSudoku(Sudoku sdk) {
		sdk.undo();
	}

	public void redoSudoku(Sudoku sdk) {
		sdk.redo();
	}
	
	public int solveSudoku(Sudoku sdk) {
		return sdk.solve();
	}

	/**
	 * Validates a user input and, if it is valid, sets the value inside the
	 * Sudoku.
	 * 
	 * @param x
	 *            The x-value of the cell in which the value should be set.
	 * @param y
	 *            The y-value of the cell in which the value should be set.
	 * @param strVal
	 *            The input, that is validated and set or denied.
	 * @param sdk
	 *            The puzzle inside which the value should be set.
	 * @return True, if the value was valid and got set, false otherwise
	 */
	public boolean trySetValue(int x, int y, String strVal, Sudoku sdk) {
		/*
		 * If the value is an empty String, set a 0 in the Sudoku.
		 */
		int iVal = 0;
		if (!strVal.equals("")) {
			/*
			 * Try and catch is not necessary because JudokuJTextFields only
			 * allow numeric input.
			 */
			iVal = Integer.parseInt(strVal);
		}

		/*
		 * The value must be between 0 and 9.
		 */
		if (iVal < 0 || iVal > sdk.getSize()) {
			return false;
		}

		/*
		 * The x-coordinate must be between 0 and 9.
		 */
		if (x < 0 || x >= sdk.getSize()) {
			return false;
		}

		/*
		 * The y-coordinate must be between 0 and 9.
		 */
		if (y < 0 || y >= sdk.getSize()) {
			return false;
		}

		/*
		 * If in the startGrid this cell contains a value, the user is not
		 * allowed to set a value there since this is an essential clue.
		 */
		if (sdk.getStartGrid()[y][x] != 0) {
			return false;
		}

		/*
		 * After validating, set the value in the Sudoku.
		 */
		sdk.setValue(x, y, iVal);
		return true;
	}

	/**
	 * Counts the number of mistakes the user made when solving the
	 * Sudoku.
	 * 
	 * @param sdk
	 *            The user-solved Sudoku.
	 * @return The number of mistakes. If it is zero, the user solved the
	 *         Sudoku correctly.
	 */
	public int validateUserSolution(Sudoku sdk) {
		int mistakes = 0;

		/*
		 * For every field in the NumberPuzzle
		 */
		for (int x = 0; x < sdk.getSize(); x++) {
			for (int y = 0; y < sdk.getSize(); y++) {

				/*
				 * Check if the user input varies from the actual solution
				 */
				if (sdk.getRecentGrid()[y][x] != sdk.getSolvedGrid()[y][x]) {
					mistakes++;
				}
			}
		}
		return mistakes;
	}

	/**
	 * Checks whether the user already made mistakes filling in the
	 * Sudoku. If so, notifies the GuiWindow to mark the erroneous field,
	 * otherwise calls the Sudoku itself to provide an hint.
	 * 
	 * @param sdk
	 *            The Sudoku object displayed by the GuiWindow
	 */
	public void giveHintPuzzle(Sudoku sdk) {
		int[] coords = sdk.searchMistake();

		/*
		 * If the returned array has length 0, no mistakes have been found.
		 */
		if (coords.length == 0) {
			sdk.giveHint();
		} else {

			/*
			 * A mistake was found, notify the GUI_Window to display it.
			 */
			linkedGuiWindow.displayMistake(coords[0], coords[1]);
		}
	}
}
