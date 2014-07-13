package dhbw.project.judokugame;

import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;

/**
 * The Judoku Swing worker outsources the generating of Sudoku object to a
 * separate worker thread.
 * 
 * @author Dennis Uteg, Florian Steurer, Markus Wingler, Michael Jauch
 * 
 */
public class JudokuSwingWorker extends SwingWorker<Sudoku, Void> {

	private final Difficulty requestedDifficulty;

	/**
	 * Creates a new JudokuSwingWorker object.
	 * 
	 * @param sudokuDifficulty
	 *            The favored Difficulty of the Sudoku.
	 */
	public JudokuSwingWorker(Difficulty sudokuDifficulty) {
		this.requestedDifficulty = sudokuDifficulty;
	}

	@Override
	public Sudoku doInBackground() {
		return new SudokuBuilder().newSudoku(requestedDifficulty);
	}

	/**
	 * Provides an easy way (without using try and catch in the calling method)
	 * to get the result of the worker thread.
	 * 
	 * @see SwingWorker#get()
	 * @return The Sudoku object, if exceptions occurred, returns null
	 */
	public Sudoku easyGet() {
		try {
			return this.get();
		} catch (InterruptedException | ExecutionException ex) {
			return null;
		}
	}
}