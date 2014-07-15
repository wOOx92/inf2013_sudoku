package dhbw.project.judokugame;

import java.util.Random;
import java.util.Stack;

/**
 * This class represents Sudokus. It implements NumberPuzzle. It contains the
 * logic to administer its state, but the logic to create playable Sudokus is
 * located in a external builder class.
 * 
 * @see SudokuBuilder
 * @author Dennis Uteg, Florian Steurer, Markus Wingler, Michael Jauch
 * 
 */
public class Sudoku implements NumberPuzzle {

	/**
	 * This field is the side-length of a carree in a Sudoku.
	 */
	public final static int CARREE_SIZE = 3;

	/**
	 * The side-length of a Sudoku grid.
	 */
	public final static int SIZE = 9;

	/**
	 * The maximum number of steps a user can use {@link Sudoku#undo()} and
	 * {@link Sudoku#undo()}.
	 */
	public final static int UNDOLIMIT = 5;

	/**
	 * The difficulty rating of the Sudoku.
	 */
	public final Difficulty DIFFICULTY;

	/**
	 * The solution of the Sudoku.
	 */
	private final int[][] solvedGrid;

	/**
	 * The initial state of the Sudoku.
	 */
	private int[][] startGrid;

	/**
	 * This array saves the recent state of the Sudoku.
	 */
	private int[][] recentGrid;

	/**
	 * Stores the previous states of this Sudoku
	 */
	private Stack<int[][]> undoStorage = new Stack<int[][]>();

	/**
	 * Stores the undone states of the Sudoku for redoing.
	 */
	private Stack<int[][]> redoStorage = new Stack<int[][]>();

	/**
	 * Creates an instance of the Sudoku class.
	 * 
	 * @param sudokuGrid
	 *            The 9x9 grid of the Sudoku.
	 * @param The
	 *            complete solution in a 9x9 grid.
	 * @param diff
	 *            The estimated difficulty.
	 */
	public Sudoku(int[][] sudokuGrid, int[][] solvedGrid, Difficulty diff) {
		this.DIFFICULTY = diff;
		this.startGrid = sudokuGrid;
		/*
		 * Never assign one of the grid fields to another one without making a
		 * deep copy. Without making a deep copy each grid will adapt changes in
		 * the other one!
		 */
		this.recentGrid = SudokuBuilder.deepCopy(startGrid);
		this.solvedGrid = solvedGrid;
	}

	/**
	 * Checks whether it is possible to place a given value in a ceratin cell in
	 * an Sudoku grid, or if this value already exists in the cells row, column
	 * or carree.
	 * 
	 * @param y
	 *            The y-Value of the cell.
	 * @param x
	 *            The x-Value of the cell.
	 * @param val
	 *            The integer value to be placed
	 * @param sudoku
	 *            The 9x9 array grid representing the Sudoku.
	 * @return true, if it is possible to place the value in the cell, false
	 *         otherwise.
	 */
	public static boolean legal(int y, int x, int val, int[][] sudoku) {
		/*
		 * Check the cells row for violations
		 */
		for (int i = 0; i < Sudoku.SIZE; ++i) {
			if (val == sudoku[i][x]) {
				return false;
			}
		}

		/*
		 * Check the cells column for violations
		 */
		for (int i = 0; i < Sudoku.SIZE; ++i) {
			if (val == sudoku[y][i]) {
				return false;
			}
		}

		/*
		 * Calculate the where the carre starts and check the cells carree for
		 * violations.
		 */
		int yOffset = (y / Sudoku.CARREE_SIZE) * Sudoku.CARREE_SIZE;
		int xOffset = (x / Sudoku.CARREE_SIZE) * Sudoku.CARREE_SIZE;
		for (int i = 0; i < Sudoku.CARREE_SIZE; ++i) {
			for (int k = 0; k < Sudoku.CARREE_SIZE; ++k) {
				if (val == sudoku[yOffset + i][xOffset + k]) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Resets the Sudoku to its initial state. Calling {@link Sudoku#undo()}
	 * after a reset is possible.
	 */
	public void reset() {
		undoStorage.clear();
		redoStorage.clear();

		/*
		 * Set the recentGrid to the startGrid (initial state).
		 */
		this.recentGrid = SudokuBuilder.deepCopy(this.startGrid);

	}

	public void setValue(int x, int y, int val) {
		undoStorage.push(SudokuBuilder.deepCopy(recentGrid));
		limitStack(undoStorage);
		this.recentGrid[y][x] = val;
		redoStorage.clear();
	}

	/**
	 * @return The initial grid of the Sudoku.
	 */
	public int[][] getStartGrid() {
		return this.startGrid;
	}

	/**
	 * @return The recent grid of the Sudoku.
	 */
	public int[][] getRecentGrid() {
		return this.recentGrid;
	}

	/**
	 * @return The solution of the Sudoku.
	 */
	public int[][] getSolvedGird() {
		return this.solvedGrid;
	}

	public int[] searchMistake() {
		for (int x = 0; x < SIZE; x++) {
			for (int y = 0; y < SIZE; y++) {
				if (this.recentGrid[y][x] != 0
						&& this.recentGrid[y][x] != this.solvedGrid[y][x]) {
					int[] mistake = new int[2];
					mistake[0] = x;
					mistake[1] = y;
					return mistake;
				}
			}
		}

		// No mistake found
		return new int[0];
	}

	public void giveHint() {
		undoStorage.push(SudokuBuilder.deepCopy(recentGrid));
		Random prng = new Random();
		int iter = 0;
		int clues = SudokuBuilder.getNumberOfClues(recentGrid);
		while (clues < Sudoku.SIZE * Sudoku.SIZE && iter < 30) {
			int x = prng.nextInt(SIZE);
			int y = prng.nextInt(SIZE);
			if (recentGrid[y][x] == 0) {
				recentGrid[y][x] = solvedGrid[y][x];
				iter = 99;
			}
			iter++;
		}
		if (iter != 100 && clues < 81) {
			outer: for (int y = 0; y < Sudoku.SIZE; y++) {
				for (int x = 0; x < Sudoku.SIZE; x++) {
					if (recentGrid[y][x] == 0) {
						recentGrid[y][x] = solvedGrid[y][x];
						break outer;
					}
				}
			}
		}
		limitStack(undoStorage);
	}

	public void undo() {
		if (!undoStorage.empty()) {
			redoStorage.push(SudokuBuilder.deepCopy(recentGrid));
			limitStack(redoStorage);
			recentGrid = undoStorage.pop();
		}
	}

	public void redo() {
		if (!redoStorage.empty()) {
			undoStorage.push(SudokuBuilder.deepCopy(recentGrid));
			limitStack(undoStorage);
			recentGrid = redoStorage.pop();
		}
	}

	private void limitStack(Stack<?> undoRedoStack) {
		if (undoRedoStack.size() > UNDOLIMIT) {

			undoRedoStack.remove(0);
		}
	}

	public boolean undoPossible() {
		if(!undoStorage.isEmpty()){
			return true;
		}
		return false;
		
	}

	public boolean redoPossible() {
		if(!redoStorage.isEmpty()){
			return true;
		}
		return false;
	}
}