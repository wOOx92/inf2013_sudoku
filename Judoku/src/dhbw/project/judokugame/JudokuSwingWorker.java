package dhbw.project.judokugame;

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
	 * @param carreeSize
	 *            The size of on carree (the resulting Sudoku will have the
	 *            squared size of on carree).
	 */
	public JudokuSwingWorker(Difficulty sudokuDifficulty, int carreeSize) {
		this.REQUESTED_DIFFICULTY = sudokuDifficulty;
		this.CARREE_SIZE = carreeSize;
	}

	@Override
	public Sudoku doInBackground() {
		return new SudokuBuilder().newSudoku(REQUESTED_DIFFICULTY, CARREE_SIZE);
	}
}