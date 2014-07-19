package dhbw.project.judokugame;

import java.util.Arrays;
import java.util.Random;

/**
 * The SudokuBuilder is capable of creating playable, uniquely solvable Sudokus
 * of different difficulties. The generating algorithms originate from
 * https://www
 * .hochschule-trier.de/uploads/tx_rfttheses/Eckart_Sussenburger_-_Loesungs
 * -_und_Generierungsalgorithmen_fuer_Sudoku.pdf
 * 
 * @author Dennis Uteg, Florian Steurer, Markus Wingler, Michael Jauch
 * 
 */
public class SudokuBuilder {

	/**
	 * This is the Solution of the Sudoku that will be generated
	 */
	private int[][] solvedGrid;

	/**
	 * This is the Sudoku grid that will be cut and minimized and will become
	 * the start grid of the new Sudoku
	 */
	private int[][] sudokuGridStorage;

	/**
	 * This is the Random Number Generator that generates every random variable
	 * needed during the generating process.
	 */
	private Random prng;
	
	private int carreeSize;
	
	private int sudokuSize;

	/**
	 * This method sets the carreeSize to the given value and the sudokuSize to carreeSize squared.
	 * @param size
	 */
	protected void setCarreeSize(int size) {
		this.carreeSize = size;
		this.sudokuSize = size*size;
	}
	/**
	 * Builds a Sudoku object of the desired difficulty.
	 * 
	 * @param diff
	 *            The desired difficulty.
	 * @return A playable Sudoku.
	 */
	public Sudoku newSudoku(Difficulty diff, int carreeSize) {
		this.carreeSize = carreeSize;
		this.sudokuSize = carreeSize*carreeSize;	
		/*
		 * Since no seed was specified, a random seed is generated.
		 */
		return newSudoku(new Random().nextLong(), diff);
	}

	/**
	 * Builds a Sudoku object of the desired difficulty using a certain seed.
	 * 
	 * @param seed
	 *            The seed used for the java.util.random generators.
	 * @param diff
	 *            The desired difficulty.
	 * @return A playable Sudoku object.
	 */
	public Sudoku newSudoku(long seed, Difficulty diff) {
		/*
		 * This Random object will be used to generate all random variables
		 * needed so exactly the same Sudoku can be recreated using the same
		 * seed.
		 */
		prng = new Random(seed);

		/*
		 * Step 1: Generate a entirely solved Sudoku grid and copy it for
		 * further manipulation.
		 */
		solvedGrid = generateSolvedGrid();
		sudokuGridStorage = SudokuBuilder.deepCopy(solvedGrid);

		/*
		 * Step 2: Remove 3 elements at random for more randomness in the
		 * generation process (the Sudoku will stay valid).
		 */
		removeRandomElements(3);

		/*
		 * Step 3: Cut one clue from every row / column / carree that is still
		 * complete. This results in more randomness in the generating process.
		 */
		cutCompleteStructures();

		/*
		 * Step 4: Cut all clues that satisfy this condition: If the clue is
		 * removed, the only possible candidate for the resulting empty cell is
		 * said clue.
		 */
		cutDeductively();

		/*
		 * Step 5: Cut all clues using the "Neighbor-rule": If every empty cell
		 * neighboring the clue is neighbored by the value of that cell again,
		 * the clue can be cut out. By definition a cell x is neighbored by a
		 * value if another cell in the row, column or carree of the cell x
		 * contains that value.
		 */
		cutWithNeighborRule();

		/*
		 * Step 6: Try to make the resulting Sudoku irreducible using a
		 * "cut-and-test" method.
		 */
		doRandomCutting(diff);

		/*
		 * If the desired Difficulty is EASY, add additional clues (only needed
		 * for classic 9x9 Sudokus)
		 */
		if (diff == Difficulty.EASY && this.sudokuSize == 9) {
			addRandomClues(this.carreeSize+1);
		}

		return new Sudoku(sudokuGridStorage, solvedGrid, diff);
	}

	/**
	 * Generates a solved Sudoku grid.
	 * 
	 * @param prng
	 *            The (P)RNG used to generate the grid.
	 * @return A completely and correctly filled Sudoku grid.
	 */
	protected int[][] generateSolvedGrid() {
		int[][] solvedGrid = new int[this.sudokuSize][this.sudokuSize];

		/*
		 * Step 1: Place the number 1 to 8 randomly in an empty grid. Placing
		 * the number 9 could possible result in an unsolvable Sudoku.
		 */
		for (int i = 1; i < this.sudokuSize; i++) {
			int x = prng.nextInt(this.sudokuSize);
			int y = prng.nextInt(this.sudokuSize);
			while (solvedGrid[y][x] != 0) {
				x = prng.nextInt(this.sudokuSize);
				y = prng.nextInt(this.sudokuSize);
			}
			solvedGrid[y][x] = i;
		}

		/*
		 * Step 2: Use backtracking to find one (of the many) possible
		 * solutions.
		 */
		solve(0, 0, solvedGrid);

		return solvedGrid;
	}

	/**
	 * Tries to find the first element in all the possible solutions of this
	 * Sudoku per Backtracking.
	 * 
	 * @param x
	 *            The x-value of the cell where possibilities will be applied.
	 * @param y
	 *            The y-value of the cell where possibilities will be applied.
	 * 
	 * @return True if a solution was found, false if not.
	 */
	protected boolean solve(int y, int x, int[][] sudoku) {
		/*
		 * If the algorithm finished recursing through the x-th column, increase
		 * the column index and reset y.
		 */
		if (y == this.sudokuSize) {
			y = 0;
			if (++x == this.sudokuSize) {
				/*
				 * If all columns have been finished (the recursion filled every
				 * field in the Sudoku), a solution has been found
				 */
				return true;
			}
		}

		/*
		 * If the field is already filled, skip it.
		 */
		if (sudoku[y][x] != 0) {
			return solve(y + 1, x, sudoku);
		}

		/*
		 * Try to set every possible value (if it is legal) in the cell and
		 * solve the resulting Sudokus.
		 */
		for (int val = 1; val <= this.sudokuSize; ++val) {
			if (legal(y, x, val, sudoku)) {
				sudoku[y][x] = val;
				if (solve(y + 1, x, sudoku)) {
					/*
					 * If solve(y+1, x, sudoku) was successful, a solution was
					 * found.
					 */
					return true;
				}
			}
		}
		/*
		 * No value was possible, reset on backtrack and return false.
		 */
		sudoku[y][x] = 0;
		return false;
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
	public boolean legal(int y, int x, int val, int[][] sudoku) {
		/*
		 * Check the cells row for violations
		 */
		for (int i = 0; i < this.sudokuSize; ++i) {
			if (val == sudoku[i][x]) {
				return false;
			}
		}

		/*
		 * Check the cells column for violations
		 */
		for (int i = 0; i < this.sudokuSize; ++i) {
			if (val == sudoku[y][i]) {
				return false;
			}
		}

		/*
		 * Calculate the where the carre starts and check the cells carree for
		 * violations.
		 */
		int yOffset = (y / this.carreeSize) * this.carreeSize;
		int xOffset = (x / this.carreeSize) * this.carreeSize;
		for (int i = 0; i < this.carreeSize; i++) {
			for (int k = 0; k < this.carreeSize; k++) {
				if (val == sudoku[yOffset + i][xOffset + k]) {
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Tries to remove a number of random elements from a Sudoku grid. If the
	 * grid has less elements than the number of elements that should be
	 * removed, the method does nothing.
	 * 
	 * @param number
	 *            The number of clues that should be removed.
	 */
	protected void removeRandomElements(int number) {
		if (Sudoku.getNumberOfClues(sudokuGridStorage) < number) {
			return;
		}

		for (int i = 0; i < number; i++) {
			int x = prng.nextInt(this.sudokuSize);
			int y = prng.nextInt(this.sudokuSize);
			if (sudokuGridStorage[y][x] != 0) {
				sudokuGridStorage[y][x] = 0;
				i++;
			}
		}
	}

	/**
	 * Cuts one element from every complete row, column or carree in the Sudoku.
	 */
	protected void cutCompleteStructures() {
		/*
		 * Check the completeness of every row.
		 */
		for (int i = 0; i < this.sudokuSize; i++) {
			boolean lineComplete = true;
			/*
			 * Check the completeness of the i-th row.
			 */
			for (int e = 0; e < this.sudokuSize; e++) {
				if (sudokuGridStorage[i][e] == 0) {
					lineComplete = false;
					break;
				}
			}
			/*
			 * If it was complete, cut a random clue out.
			 */
			if (lineComplete) {
				int x = prng.nextInt(this.sudokuSize);
				sudokuGridStorage[i][x] = 0;
			}
		}

		/*
		 * Check the completeness of every column.
		 */
		for (int i = 0; i < this.sudokuSize; i++) {
			boolean columnComplete = true;
			/*
			 * Check the completeness of the i-th column.
			 */
			for (int e = 0; e < this.sudokuSize; e++) {
				if (sudokuGridStorage[e][i] == 0) {
					columnComplete = false;
					break;
				}
			}
			/*
			 * If the column was still complete, cut a random clue out.
			 */
			if (columnComplete) {
				int y = prng.nextInt(this.sudokuSize);
				sudokuGridStorage[y][i] = 0;
			}
		}

		/*
		 * Check the completeness for every carree in the Sudoku.
		 */
		for (int i = 0; i < this.sudokuSize; i = i + this.carreeSize) {
			for (int e = 0; e < this.sudokuSize; e = e + this.carreeSize) {
				boolean carreeComplete = true;
				/*
				 * For a specific carree check if it is still complete.
				 */
				outer: for (int xos = 0; xos < this.carreeSize; xos++) {
					for (int yos = 0; yos < this.carreeSize; yos++) {
						if (sudokuGridStorage[i + yos][e + xos] == 0) {
							carreeComplete = false;
							break outer;
						}
					}
				}
				/*
				 * If the carree was still complete, cut a random clue out.
				 */
				if (carreeComplete) {
					int x = prng.nextInt(this.carreeSize);
					int y = prng.nextInt(this.carreeSize);
					sudokuGridStorage[i + y][e + x] = 0;
				}
			}
		}
	}

	/**
	 * Tries to cut out clues using the deductive cutting-rule "cut out a cell
	 * if only this value is legal in the resulting empty cell.
	 * 
	 */
	protected void cutDeductively() {
		/*
		 * For each cell in the Sudoku
		 */
		for (int x = 0; x < this.sudokuSize; x++) {
			for (int y = 0; y < this.sudokuSize; y++) {
				/*
				 * Save the cell and cut it
				 */
				int save = sudokuGridStorage[y][x];
				sudokuGridStorage[y][x] = 0;
				/*
				 * Check if any other value than the original value is legal,
				 * the value can not be cut out.
				 */
				for (int i = 1; i <= this.sudokuSize; i++) {
					if (i != save && legal(y, x, i, sudokuGridStorage)) {
						sudokuGridStorage[y][x] = save;
						break;
					}
				}
			}
		}
	}

	/**
	 * Tries to cut out clues using the deductive cutting-rule
	 * "Cut out a number if all neighboring empty cells are neighbored by that number again"
	 * . A cell neighbors a number if that number occurs in the same row, column
	 * or carree as the cell.
	 * 
	 */
	protected void cutWithNeighborRule() {
		/*
		 * Use the row, column and carree neighborRule for each cell in the
		 * Sudoku grid.
		 */
		for (int x = 0; x < this.sudokuSize; x++) {
			for (int y = 0; y < this.sudokuSize; y++) {
				if (sudokuGridStorage[y][x] != 0) {
					cutRowNeighborRule(x, y);
					cutColumnNeighborRule(x, y);
					cutCarreeNeighborRule(x, y);
				}
			}
		}
	}

	/**
	 * Tries to cut out a clue using the neighboring-rule for rows.
	 * 
	 * @param x
	 *            The x-value of the clue in the grid.
	 * @param y
	 *            The y-value of the clue in the grid.
	 */
	private void cutRowNeighborRule(int x, int y) {
		/*
		 * Make a backup of the number and cut it out.
		 */
		int cutCandidate = sudokuGridStorage[y][x];
		sudokuGridStorage[y][x] = 0;

		/*
		 * Check if this violates the "neighbor-rule" for rows, by checking
		 * every other cell in the row.
		 */
		for (int x1 = 0; x1 < this.sudokuSize; x1++) {
			/*
			 * If there is an empty cell && that cell is not the same as the
			 * cell of the cut candidate && that cell is not neighbored by the
			 * cut candidate, this means the cut candidate must not be cut out.
			 */
			if (sudokuGridStorage[y][x1] == 0 && x1 != x
					&& !isNeighboredBy(x1, y, cutCandidate)) {
				/*
				 * Put the candidate back in the cell and return false because
				 * the cell was not cut.
				 */
				sudokuGridStorage[y][x] = cutCandidate;
				return;
			}
		}
	}

	/**
	 * Tries to cut out a clue using the neighboring-rule for columns.
	 * 
	 * @param x
	 *            The x-value of the clue in the grid.
	 * @param y
	 *            The y-value of the clue in the grid.
	 */
	private void cutColumnNeighborRule(int x, int y) {
		/*
		 * Make a backup of the number and cut it out.
		 */
		int cutCandidate = sudokuGridStorage[y][x];
		sudokuGridStorage[y][x] = 0;

		/*
		 * Check if this violates the "neighbor-rule" for columns, by checking
		 * every other cell in the column.
		 */
		for (int y1 = 0; y1 < this.sudokuSize; y1++) {
			/*
			 * If there is an empty cell && that cell is not the same as the
			 * cell of the cut candidate && that cell is not neighbored by the
			 * cut candidate, this means the cut candidate must not be cut out.
			 */
			if (sudokuGridStorage[y1][x] == 0 && y1 != y
					&& !isNeighboredBy(x, y1, cutCandidate)) {
				/*
				 * Put the candidate back in the cell and return false because
				 * the cell was not cut.
				 */
				sudokuGridStorage[y][x] = cutCandidate;
				return;
			}
		}
	}

	/**
	 * Tries to cut out a clue using the neighboring-rule for carrees.
	 * 
	 * @param x
	 *            The x-value of the clue in the grid.
	 * @param y
	 *            The y-value of the clue in the grid.
	 */
	private void cutCarreeNeighborRule(int x, int y) {
		/*
		 * Make a backup of the number and cut it out.
		 */
		int cutCandidate = sudokuGridStorage[y][x];
		sudokuGridStorage[y][x] = 0;
		/*
		 * Calculate the offsets for the carree.
		 */
		int xos = (x / this.carreeSize) * this.carreeSize;
		int yos = (y / this.carreeSize) * this.carreeSize;

		/*
		 * Check if this violates the "neighbor-rule" for carrees, by checking
		 * every other cell in the carree.
		 */
		for (int y1 = 0; y1 < this.carreeSize; y1++) {
			for (int x1 = 0; x1 < this.carreeSize; x1++) {
				/*
				 * If the cell is empty
				 */
				if (sudokuGridStorage[yos + y1][xos + x1] == 0) {
					/*
					 * If the cell is not the same as the cell of the cut
					 * candidate
					 */
					if (xos + x1 != x || yos + y1 != y) {
						/*
						 * If it is not neighbored by the cut candidate again,
						 * it must not be cut out.
						 */
						if (!isNeighboredBy(xos + x1, yos + y1, cutCandidate)) {
							/*
							 * Put the candidate back in the cell and return
							 * false because the cell was not cut.
							 */
							sudokuGridStorage[y][x] = cutCandidate;
							return;
						}
					}
				}
			}
		}
	}

	/**
	 * Shows whether a certain cell in a Sudoku grid has a neighboring cell
	 * containing a given value.
	 * 
	 * @param x
	 *            The x-value of the cell in the Sudoku grid.
	 * @param y
	 *            The y-value of the cell in the Sudoku grid.
	 * @param val
	 *            The value which is searched in the neighboring cells.
	 * @return True if there is a neighbor with the given value, false if not.
	 */
	private boolean isNeighboredBy(int x, int y, int val) {
		for (int i = 0; i < this.sudokuSize; i++) {
			/*
			 * If a cell in the column or in the row contains the value, the
			 * cell is neighbored by that value.
			 */
			if (sudokuGridStorage[i][x] == val
					|| sudokuGridStorage[y][i] == val) {
				return true;
			}
		}

		/*
		 * Calculate the offsets for the carree.
		 */
		int xos = (x / this.carreeSize) * this.carreeSize;
		int yos = (y / this.carreeSize) * this.carreeSize;

		/*
		 * If the value is found within the carree, the cell is neighbored by
		 * that value.
		 */
		for (int x1 = 0; x1 < this.carreeSize; x1++) {
			for (int y1 = 0; y1 < this.carreeSize; y1++) {
				if (sudokuGridStorage[yos + y1][xos + x1] == val) {
					return true;
				}
			}
		}

		/*
		 * If it was neither found in the carres, nor in the rows or columns,
		 * the cell is not neighbored by that value
		 */
		return false;
	}

	/**
	 * Tries to cut out clues which are not cuttable by deduction. Only cuts
	 * clues if the resulting Sudoku still has a unique solution.
	 * 
	 * @param diff
	 *            The difficulty determines the maximum number of clues that get
	 *            cut out by this.
	 */
	protected void doRandomCutting(Difficulty diff) {
		for (int x = 0; x < this.sudokuSize; x++) {
			for (int y = 0; y < this.sudokuSize; y++) {
				if (sudokuGridStorage[y][x] != 0) {
					/*
					 * Save a backup of the value and cut it out.
					 */
					int cutCandidate = sudokuGridStorage[y][x];
					sudokuGridStorage[y][x] = 0;
					/*
					 * Test if the Sudoku still has a unique solution. If it has
					 * not, put the clue back in.
					 */
					if (!hasUniqueSolution(sudokuGridStorage,
							diff.maxRecursionDepth())) {
						sudokuGridStorage[y][x] = cutCandidate;
					}
				}
			}
		}
	}

	/**
	 * Checks whether a given Sudoku is uniquely solvable.
	 * 
	 * @param sudoku
	 *            The Sudoku to be checked.
	 * @param maxRecursionDepth
	 *            The maxRecursionDepth determines how deep the underlying
	 *            backtracking algorithm will search in the solution tree of the
	 *            Sudoku. If this value is 0, this method will always return
	 *            false. If this value gets bigger, it becomes more probable
	 *            that the uniqueness (and thereby the validity) of a Sudoku can
	 *            be proofed.
	 * @return True if only one solution exists, false if there are none or
	 *         multiple solutions.
	 */
	protected boolean hasUniqueSolution(int[][] sudoku,
			int maxRecursionDepth) {
		/*
		 * The grid will get filled, so make a copy and check that copy, so that
		 * the original Sudoku stays untouched.
		 */
		int[][] copy = SudokuBuilder.deepCopy(sudoku);
		return (checkSolutions(0, 0, copy, 0, maxRecursionDepth) == 1);
	}

	/**
	 * Checks whether a given Sudoku has none, one or multiple Solutions per
	 * Backtracking. The Sudoku grid will get modified.
	 * 
	 * @param x
	 *            The x-value of the cell where possibilities will be applied.
	 * @param y
	 *            The y-value of the cell where possibilities will be applied.
	 * @param sudoku
	 *            The Sudoku grid to be checked for solutions.
	 * @param solutionsFound
	 *            The amount of solutions found so far.
	 * @param maxRecursionDepth
	 *            The maxRecursionDepth determines how deep the backtracking
	 *            algorithm will search in the solution tree of the Sudoku. If
	 *            this value is 0, this method will always return 2. If this
	 *            value gets bigger, it becomes more probable that the
	 *            uniqueness (and thereby the validity) of a Sudoku can be
	 *            proofed.
	 * @return 0 if no solutions exist, 1 if the Sudoku has a unique solutions,
	 *         >1 if multiple solutions where found (not necessarily the actual
	 *         amount of solutions).
	 */
	private int checkSolutions(int x, int y, int[][] sudoku,
			int solutionsFound, int maxRecursionDepth) {
		/*
		 * If all columns in a row have been filled increase the number of rows.
		 */
		if (x == this.sudokuSize) {
			x = 0;
			if (++y == this.sudokuSize) {
				/*
				 * If all cells have been filled, another solution has been
				 * found.
				 */
				return 1 + solutionsFound;
			}
		}
		/*
		 * If the maximum depth is reached, return 2 (as is likely to have more
		 * than one solution, or maybe none at all).
		 */
		if (maxRecursionDepth == 0) {
			return 2;
		}

		/*
		 * Skip filled cells by increasing the x value and making a recursive
		 * call
		 */
		if (sudoku[x][y] != 0) {
			return checkSolutions(x + 1, y, sudoku, solutionsFound,
					maxRecursionDepth--);
		}

		/*
		 * Fill every legal value from 1 to 9 in the cell and try to find
		 * solutions recursively. Break the loop if there is already more than 1
		 * solution.
		 */
		for (int val = 1; val <= this.sudokuSize && solutionsFound < 2; ++val) {
			if (legal(x, y, val, sudoku)) {
				sudoku[x][y] = val;
				solutionsFound = checkSolutions(x + 1, y, sudoku,
						solutionsFound, maxRecursionDepth--);
			}
		}
		/*
		 * Finding a solution was not successful, reset on backtrack and return
		 * the number of solutions found so far.
		 */
		sudoku[x][y] = 0;
		return solutionsFound;
	}

	/**
	 * Adds a specified amount of randomly chosen clues to the Sudoku grid. If
	 * there is no place for the amount of clues (because it is already to
	 * full), this will add as much clues as possible.
	 * 
	 * @param add
	 *            The number of clues that should be added.
	 */
	protected void addRandomClues(int add) {
		int clues = Sudoku.getNumberOfClues(sudokuGridStorage);
		/*
		 * If clues + i is equal the SIZE*SIZE, this means there are no empty
		 * cells left and the loop has to stop.
		 */
		for (int i = 0; i < add && clues + i < this.sudokuSize * this.sudokuSize;) {
			int x = prng.nextInt(this.sudokuSize);
			int y = prng.nextInt(this.sudokuSize);
			/*
			 * If the cell has no clue in it, add it and increase the number of
			 * clues added.
			 */
			if (sudokuGridStorage[y][x] == 0) {
				sudokuGridStorage[y][x] = solvedGrid[y][x];
				i++;
			}
		}
	}

	/**
	 * Creates a deep copy of a two-dimensional array of type int.
	 * 
	 * @param template
	 *            The array that gets copied.
	 * @return The (deep) copied array.
	 */
	public static int[][] deepCopy(int[][] template) {
		int[][] copy = new int[template.length][0];
		for (int i = 0; i < template.length; i++) {
			copy[i] = Arrays.copyOf(template[i], template[i].length);
		}
		return copy;
	}
}
