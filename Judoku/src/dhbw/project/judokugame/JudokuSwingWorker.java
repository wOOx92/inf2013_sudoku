package dhbw.project.judokugame;

import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import dhbw.project.puzzlemodel.Difficulty;
import dhbw.project.puzzlemodel.Sudoku;
import dhbw.project.puzzlemodel.SudokuBuilder;

/**
 * The Judoku Swing worker outsources the generating of Sudoku objects to a
 * separate worker thread.
 * 
 * @author Dennis Uteg, Florian Steurer, Markus Wingler, Michael Jauch
 * 
 */
public class JudokuSwingWorker extends SwingWorker<Sudoku, Void> {

	private final Difficulty REQUESTED_DIFFICULTY;
	private final int CARREE_SIZE;
	
	/**
	 * Creates a new JudokuSwingWorker object.
	 * 
	 * @param sudokuDifficulty
	 *            The favored Difficulty of the Sudoku.
	 */
	public JudokuSwingWorker(Difficulty sudokuDifficulty, int carreeSize) {
		this.REQUESTED_DIFFICULTY = sudokuDifficulty;
		this.CARREE_SIZE = carreeSize;
	}

	@Override
	public Sudoku doInBackground() {
		return new SudokuBuilder().newSudoku(REQUESTED_DIFFICULTY, CARREE_SIZE);
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