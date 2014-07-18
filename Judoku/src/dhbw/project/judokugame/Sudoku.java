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
	private final static int UNDOLIMIT = 5;

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
	 * Checks whether it is possible to place a given value in a certain cell in
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
		for (int i = 0; i < Sudoku.CARREE_SIZE; i++) {
			for (int k = 0; k < Sudoku.CARREE_SIZE; k++) {
				if (val == sudoku[yOffset + i][xOffset + k]) {
					return false;
				}
			}
		}
		return true;
	}

	public void reset() {
		undoStorage.clear();
		redoStorage.clear();

		/*
		 * Set the recentGrid to the startGrid (initial state).
		 */
		this.recentGrid = SudokuBuilder.deepCopy(this.startGrid);

	}

	public void setValue(int x, int y, int val) {
		/*
		 * Saves the recent state to the undo storage (so undo() can be called on
		 * this action).
		 */
		undoStorage.push(SudokuBuilder.deepCopy(recentGrid));
		limitStack(undoStorage);

		this.recentGrid[y][x] = val;

		/*
		 * Redos should only be possible directly after undos, so the stack
		 * needs to be emptied.
		 */
		redoStorage.clear();
	}

	public int[][] getStartGrid() {
		return this.startGrid;
	}

	public int[][] getRecentGrid() {
		return this.recentGrid;
	}

	public int[][] getSolvedGrid() {
		return this.solvedGrid;
	}

	public int[] searchMistake() {
		/*
		 * For each cell in the Sudoku
		 */
		for (int x = 0; x < SIZE; x++) {
			for (int y = 0; y < SIZE; y++) {
				/*
				 * If the cell in the recentGrid is not equal to the
				 * corresponding cell in the solution, this is a mistake.
				 */
				if (this.recentGrid[y][x] != 0
						&& this.recentGrid[y][x] != this.solvedGrid[y][x]) {
					int[] mistake = new int[2];
					mistake[0] = x;
					mistake[1] = y;
					return mistake;
				}
			}
		}

		/*
		 * No mistakes have been found, so an empty array is returned.
		 */
		return new int[0];
	}

	public void giveHint() {
		/*
		 * Get the number of filled cells and check if there are any empty cells
		 * left to give hints in.
		 */
		int clues = SudokuBuilder.getNumberOfClues(recentGrid);
		if (clues == Sudoku.SIZE * Sudoku.SIZE) {
			return;
		}

		/*
		 * Save the recent state so undo() can be called on this action.
		 */
		undoStorage.push(SudokuBuilder.deepCopy(recentGrid));
		limitStack(undoStorage);
		
		/*
		 * Try to find a random empty cell to write the hint in. If for 30
		 * iterations there was no empty cell stop trying at random.
		 */
		Random prng = new Random();
		int i = 0;
		while (i < 40) {
			int x = prng.nextInt(SIZE);
			int y = prng.nextInt(SIZE);

			/*
			 * If the cell is empty, give the hint and end the loop.
			 */
			if (recentGrid[y][x] == 0) {
				recentGrid[y][x] = solvedGrid[y][x];
				i = -1;
				break;
			}
			i++;
		}

		/*
		 * If the first loop did not manage to find an empty cell, go through
		 * the Sudoku once more and give the hint in the first empty cell found.
		 */
		if (i != -1) {
			outer: for (int y = 0; y < Sudoku.SIZE; y++) {
				for (int x = 0; x < Sudoku.SIZE; x++) {
					if (recentGrid[y][x] == 0) {
						recentGrid[y][x] = solvedGrid[y][x];
						break outer;
					}
				}
			}
		}
		
		redoStorage.clear();
	}

	public void undo() {
		if (!undoStorage.empty()) {
			/*
			 * Save the recentState for a redo() call and take latest state on
			 * the undo stack as the new state.
			 */
			redoStorage.push(SudokuBuilder.deepCopy(recentGrid));
			limitStack(redoStorage);
			recentGrid = undoStorage.pop();
		}
	}

	public void redo() {
		if (!redoStorage.empty()) {
			/*
			 * Save the recentState for an undo() call and take the latest state
			 * on the redo stack as the new state.
			 */
			undoStorage.push(SudokuBuilder.deepCopy(recentGrid));
			limitStack(undoStorage);
			recentGrid = redoStorage.pop();
		}
	}

	private void limitStack(Stack<?> undoRedoStack) {
		/*
		 * If the given Stack has reached the defined limit, remove the oldest
		 * element.
		 */
		if (undoRedoStack.size() > UNDOLIMIT) {
			undoRedoStack.remove(0);
		}
	}

	public boolean undoPossible() {
		if (!undoStorage.isEmpty()) {
			return true;
		}
		return false;

	}

	public boolean redoPossible() {
		if (!redoStorage.isEmpty()) {
			return true;
		}
		return false;
	}
}