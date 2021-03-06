package dhbw.project.puzzlemodel;

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
public class Sudoku {

	/**
	 * This field is the side-length of a carree in a Sudoku.
	 */
	public final int CARREE_SIZE;

	/**
	 * The side-length of a Sudoku grid.
	 */
	public final int SIZE;
			
	/**
	 * The maximum number of steps a user can use {@link Sudoku#undo()} and
	 * {@link Sudoku#undo()}.
	 */
	private final static int UNDOLIMIT = 7;

	/**
	 * The solution of the Sudoku.
	 */
	private int[][] solvedGrid;

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
	 */
	public Sudoku(int[][] sudokuGrid, int[][] solvedGrid) {
		SIZE = solvedGrid.length;
		CARREE_SIZE = (int)Math.sqrt(solvedGrid.length);
	
		/*
		 * Never assign one of the grid fields to another one without making a
		 * deep copy. Without making a deep copy each grid will adapt changes in
		 * the other one!
		 */
		this.startGrid = SudokuBuilder.deepCopy(sudokuGrid);
		this.recentGrid = SudokuBuilder.deepCopy(startGrid);
		this.solvedGrid = SudokuBuilder.deepCopy(solvedGrid);
	}

	/**
	 * Creates a empty Sudoku.
	 */
	public Sudoku(int carreeSize) {
		CARREE_SIZE = carreeSize;
		SIZE = carreeSize*carreeSize;
		this.startGrid = new int[SIZE][SIZE];
		this.recentGrid = new int[SIZE][SIZE];
		this.solvedGrid = new int[SIZE][SIZE];
	}
	
	/**
	 * Calculates the number of clues in a Sudoku grid.
	 * 
	 * @param sudoku
	 *            The Sudoku grid.
	 * @return The amount of given clues.
	 */
	public static int getNumberOfClues(int[][] sudoku) {
		int clues = 0;
		for (int x = 0; x < sudoku.length; x++) {
			for (int y = 0; y < sudoku.length; y++) {
				if (sudoku[y][x] != 0) {
					clues++;
				}
			}
		}
		return clues;
	}
	
	/**
	 * Resets the Sudoku to the initial state.
	 */
	public void reset() {
		undoStorage.clear();
		redoStorage.clear();

		/*
		 * Set the recentGrid to the startGrid (initial state).
		 */
		this.recentGrid = SudokuBuilder.deepCopy(this.startGrid);

	}

	/**
	 * Sets a value at a certain position in the recentGrid of this Sudoku.
	 * @param x X-coordinate of the cell.
	 * @param y Y-coordinate of the cell.
	 * @param val Value to be set.
	 */
	public void setValue(int x, int y, int val) {
		/*
		 * If the value is already set at this place, do not set it again
		 * (otherwise undo() will recognize this as an undoable action even
		 * though nothing actually happened.
		 */
		if (recentGrid[y][x] == val) {
			return;
		}
		
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
	
	/**
	 * @return Size of this Sudoku.
	 */
	public int getSize() {
		return this.SIZE;
	}
	
	/**
	 * @return Carree size of this Sudoku.
	 */
	public int getCarreeSize() {
		return this.CARREE_SIZE;
	}
	
	/**
	 * @return The initial state of this Sudoku (before values have been set with {@link Sudoku#setValue(int, int, int)}.
	 */
	public int[][] getStartGrid() {
		return this.startGrid;
	}

	/**
	 * @return The recent state of this Sudoku.
	 */
	public int[][] getRecentGrid() {
		return this.recentGrid;
	}

	/**
	 * @return The unique solution of this Sudoku.
	 */
	public int[][] getSolvedGrid() {
		return this.solvedGrid;
	}

	/**
	 * Searches for a mistake (differences between the recentGrid and the
	 * solvedGrid).
	 * 
	 * @return An array of length 0 if no mistakes were found, an array of
	 *         length 2 (the first entry beeing the x, the second the y
	 *         coordinate of the first mistake) if mistakes have been found.
	 */
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

	/**
	 * Writes a hint in the recentGrid of the Sudoku.
	 */
	public void giveHint() {
		/*
		 * Get the number of filled cells and check if there are any empty cells
		 * left to give hints in.
		 */
		int clues = Sudoku.getNumberOfClues(recentGrid);
		if (clues == this.SIZE * this.SIZE) {
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
			outer: for (int y = 0; y < this.SIZE; y++) {
				for (int x = 0; x < this.SIZE; x++) {
					if (recentGrid[y][x] == 0) {
						recentGrid[y][x] = solvedGrid[y][x];
						break outer;
					}
				}
			}
		}
		
		redoStorage.clear();
	}

	/**
	 * Returns to a previous state.
	 */
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

	/**
	 * Returns to the state before an {@link Sudoku#undo()} call.
	 */
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

	/**
	 * Limits a stack to a defined limit {@link Sudoku#UNDOLIMIT}.
	 */
	private void limitStack(Stack<?> undoRedoStack) {
		/*
		 * If the given Stack has reached the defined limit, remove the oldest
		 * element.
		 */
		if (undoRedoStack.size() > UNDOLIMIT) {
			undoRedoStack.remove(0);
		}
	}

	/**
	 * Checks if an {@link Sudoku#undo()} would result in a change in the state.
	 * @return True if undo possible, false otherwise.
	 */
	public boolean undoPossible() {
		if (!undoStorage.isEmpty()) {
			return true;
		}
		return false;

	}
	
	/**
	 * Checks if an {@link Sudoku#redo()} would result in a change in the state.
	 * @return True if redo possible, false otherwise.
	 */
	public boolean redoPossible() {
		if (!redoStorage.isEmpty()) {
			return true;
		}
		return false;
	}
	
	/**
	 * Tries to solve this Sudoku using the recentGrid of the Sudoku. This is not undoable.
	 * @return 0 if not solvable, 1 if uniquely solvable, > 1 if more than one solution (not necessarily the actual number of solutions).
	 */
	public int solve() {
		SudokuBuilder sb = new SudokuBuilder();
		/*
		 * Make a copy and use the SudokuBuilder algorithms to determine the number of solutions.
		 */
		int[][] copy = SudokuBuilder.deepCopy(recentGrid);
		sb.setCarreeSize(CARREE_SIZE);
		int solutions = sb.checkSolutions(0, 0, copy, 0, 34);
		if(solutions == 1) {
			/*
			 * If it is valid, generate the actual solution and manage the grids.
			 */
			this.startGrid = SudokuBuilder.deepCopy(recentGrid);
			sb.solve(0, 0, recentGrid);
			this.solvedGrid = SudokuBuilder.deepCopy(recentGrid);
			this.undoStorage.clear();
			this.redoStorage.clear();
		}
		return solutions;
	}
}