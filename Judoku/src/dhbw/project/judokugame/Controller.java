package dhbw.project.judokugame;

import java.awt.EventQueue;

import dhbw.project.puzzlemodel.NumberPuzzle;

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

	public void resetPuzzle(NumberPuzzle puzzle) {
		puzzle.reset();
	}

	public void undoPuzzle(NumberPuzzle puzzle) {
		puzzle.undo();
	}

	public void redoPuzzle(NumberPuzzle puzzle) {
		puzzle.redo();
	}

	/**
	 * Validates a user input and, if it is valid, sets the value inside the
	 * NumberPuzzle.
	 * 
	 * @param x
	 *            The x-value of the cell in which the value should be set.
	 * @param y
	 *            The y-value of the cell in which the value should be set.
	 * @param strVal
	 *            The input, that is validated and set or denied.
	 * @param puzzle
	 *            The puzzle inside which the value should be set.
	 * @return True, if the value was valid and got set, false otherwise
	 */
	public boolean trySetValue(int x, int y, String strVal, NumberPuzzle puzzle) {
		/*
		 * If the value is an empty String, set a 0 in the NumberPuzzle
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
		if (iVal < 0 || iVal > puzzle.getSize()) {
			return false;
		}

		/*
		 * The x-coordinate must be between 0 and 9.
		 */
		if (x < 0 || x >= puzzle.getSize()) {
			return false;
		}

		/*
		 * The y-coordinate must be between 0 and 9.
		 */
		if (y < 0 || y >= puzzle.getSize()) {
			return false;
		}

		/*
		 * If in the startGrid this cell contains a value, the user is not
		 * allowed to set a value there since this is an essential clue.
		 */
		if (puzzle.getStartGrid()[y][x] != 0) {
			return false;
		}
		
		/*
		 * After validating, set the value in the puzzle.
		 */
		puzzle.setValue(x, y, iVal);
		return true;
	}
	
	/**
	 * Counts the number of mistakes the user made when solving the
	 * NumberPuzzle.
	 * 
	 * @param puzzle
	 *            The user-solved NumberPuzzle
	 * @return The number of mistakes. If it is zero, the user solved the
	 *         NumberPuzzle correctly.
	 */
	public int validateUserSolution(NumberPuzzle puzzle) {
		int mistakes = 0;

		/*
		 * For every field in the NumberPuzzle
		 */
		for (int x = 0; x < puzzle.getSize(); x++) {
			for (int y = 0; y < puzzle.getSize(); y++) {

				/*
				 * Check if the user input varies from the actual solution
				 */
				if (puzzle.getRecentGrid()[y][x] != puzzle.getSolvedGrid()[y][x]) {
					mistakes++;
				}
			}
		}
		return mistakes;
	}

	/**
	 * Checks whether the user already made mistakes filling in the
	 * NumberPuzzle. If so, notifies the GuiWindow to mark the erroneous field,
	 * otherwise calls the NumberPuzzle itself to provide an hint.
	 * 
	 * @param puzzle
	 *            The NumberPuzzle object displayed by the GuiWindow
	 */
	public void giveHintPuzzle(NumberPuzzle puzzle) {
		int[] coords = puzzle.searchMistake();

		/*
		 * If the returned array has length 0, no mistakes have been found.
		 */
		if (coords.length == 0) {
			puzzle.giveHint();
		} else {

			/*
			 * A mistake was found, notify the GUI_Window to display it.
			 */
			linkedGuiWindow.displayMistake(coords[0], coords[1]);
		}
	}
}
