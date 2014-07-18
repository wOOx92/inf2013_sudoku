package dhbw.project.judokugame;

import java.util.Arrays;
import java.util.Random;

/**
 * SudokuBuilder's are capable of creating playable, uniquely solvable Sudokus
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

	/**
	 * Builds a Sudoku object of the desired difficulty.
	 * 
	 * @param diff
	 *            The desired difficulty.
	 * @return A playable Sudoku.
	 */
	public Sudoku newSudoku(Difficulty diff) {
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
		 * If the desired Difficulty is EASY, add 5 additional clues.
		 */
		if (diff == Difficulty.EASY) {
			addRandomClues(5);
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
	private int[][] generateSolvedGrid() {
		int[][] solvedGrid = new int[Sudoku.SIZE][Sudoku.SIZE];

		/*
		 * Step 1: Place the number 1 to 8 randomly in an empty grid.
		 */
		for (int i = 1; i <= 8; i++) {
			int x = prng.nextInt(Sudoku.SIZE);
			int y = prng.nextInt(Sudoku.SIZE);
			while (solvedGrid[y][x] != 0) {
				x = prng.nextInt(Sudoku.SIZE);
				y = prng.nextInt(Sudoku.SIZE);
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
	 * @param sudoku
	 *            The Sudoku grid to be checked for solutions.
	 * @return True if a solution was found, false if not.
	 */
	private static boolean solve(int y, int x, int[][] sudoku) {
		/*
		 * If the algorithm finished recursing through the x-th column, increase
		 * the column index and reset y.
		 */
		if (y == 9) {
			y = 0;
			if (++x == 9) {
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
		for (int val = 1; val <= 9; ++val) {
			if (Sudoku.legal(y, x, val, sudoku)) {
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
	 * Tries to remove a number of random elements from a Sudoku grid. If the
	 * grid has less elements than the number of elements that should be
	 * removed, the method does nothing.
	 * 
	 * @param sudoku
	 *            The Sudoku grid which gets modified.
	 * @param prng
	 *            The Random Number Generator used to generate the random
	 *            variables.
	 * @param number
	 *            The number of clues that should be removed.
	 */
	private void removeRandomElements(int number) {
		if (SudokuBuilder.getNumberOfClues(sudokuGridStorage) < number) {
			return;
		}

		for (int i = 0; i < number; i++) {
			int x = prng.nextInt(Sudoku.SIZE);
			int y = prng.nextInt(Sudoku.SIZE);
			if (sudokuGridStorage[y][x] != 0) {
				sudokuGridStorage[y][x] = 0;
				i++;
			}
		}
	}

	/**
	 * Cuts one element from every complete row, column or carre in the Sudoku.
	 * 
	 * @param sudoku
	 *            The Sudoku which gets modified.
	 * @param prng
	 *            The Random Number Generator used to generate the random
	 *            variables.
	 */
	private void cutCompleteStructures() {
		/*
		 * Check the completeness of every row.
		 */
		for (int i = 0; i < Sudoku.SIZE; i++) {
			boolean lineComplete = true;
			/*
			 * Check the completeness of the i-th row.
			 */
			for (int e = 0; e < Sudoku.SIZE; e++) {
				if (sudokuGridStorage[i][e] == 0) {
					lineComplete = false;
					break;
				}
			}
			/*
			 * If it was complete, cut a random clue out.
			 */
			if (lineComplete) {
				int x = prng.nextInt(Sudoku.SIZE);
				sudokuGridStorage[i][x] = 0;
			}
		}

		/*
		 * Check the completeness of every column.
		 */
		for (int i = 0; i < Sudoku.SIZE; i++) {
			boolean columnComplete = true;
			/*
			 * Check the completeness of the i-th column.
			 */
			for (int e = 0; e < Sudoku.SIZE; e++) {
				if (sudokuGridStorage[e][i] == 0) {
					columnComplete = false;
					break;
				}
			}
			/*
			 * If the column was still complete, cut a random clue out.
			 */
			if (columnComplete) {
				int y = prng.nextInt(Sudoku.SIZE);
				sudokuGridStorage[y][i] = 0;
			}
		}

		/*
		 * Check the completeness for every carree in the Sudoku.
		 */
		for (int i = 0; i < Sudoku.SIZE; i = i + Sudoku.CARREE_SIZE) {
			for (int e = 0; e < Sudoku.SIZE; e = e + Sudoku.CARREE_SIZE) {
				boolean carreeComplete = true;
				/*
				 * For a specific carre check if it is still complete.
				 */
				outer: for (int xos = 0; xos < Sudoku.CARREE_SIZE; xos++) {
					for (int yos = 0; yos < Sudoku.CARREE_SIZE; yos++) {
						if (sudokuGridStorage[i + yos][e + xos] == 0) {
							carreeComplete = false;
							break outer;
						}
					}
				}
				/*
				 * If the carre was still complete, cut a random clue out.
				 */
				if (carreeComplete) {
					int x = prng.nextInt(Sudoku.CARREE_SIZE);
					int y = prng.nextInt(Sudoku.CARREE_SIZE);
					sudokuGridStorage[i + y][e + x] = 0;
				}
			}
		}
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
		for (int x = 0; x < 9; x++) {
			for (int y = 0; y < 9; y++) {
				if (sudoku[y][x] != 0) {
					clues++;
				}
			}
		}
		return clues;
	}

	/**
	 * Tries to cut out clues using the deductive cutting-rule
	 * "cut out a cell if only this value is legal in the resulting empty cell"
	 * 
	 * @param sudoku
	 *            The Sudoku grid to be cut.
	 */
	private void cutDeductively() {
		/*
		 * For each cell in the Sudoku
		 */
		for (int x = 0; x < Sudoku.SIZE; x++) {
			for (int y = 0; y < Sudoku.SIZE; y++) {
				/*
				 * Save the cell and cut it
				 */
				int save = sudokuGridStorage[y][x];
				sudokuGridStorage[y][x] = 0;
				/*
				 * Check if any other value than the original value is legal,
				 * the value can not be cut out.
				 */
				for (int i = 1; i <= Sudoku.SIZE; i++) {
					if (i != save && Sudoku.legal(y, x, i, sudokuGridStorage)) {
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
	 * or carre as the cell.
	 * 
	 * @param sudoku
	 *            The Sudoku grid to be cut.
	 */
	private void cutWithNeighborRule() {
		/*
		 * Use the row, column and carree neighborRule for each cell in the
		 * Sudoku grid.
		 */
		for (int x = 0; x < Sudoku.SIZE; x++) {
			for (int y = 0; y < Sudoku.SIZE; y++) {
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
	 * @param sudoku
	 *            The Sudoku grid.
	 * @return True if the clue has been cut, false if not.
	 */
	private boolean cutRowNeighborRule(int x, int y) {
		/*
		 * Make a backup of the number and cut it out.
		 */
		int cutCandidate = sudokuGridStorage[y][x];
		sudokuGridStorage[y][x] = 0;

		/*
		 * Check if this violates the "neighbor-rule" for rows, by checking
		 * every other cell in the row.
		 */
		for (int x1 = 0; x1 < Sudoku.SIZE; x1++) {
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
				return false;
			}
		}
		return true;
	}

	/**
	 * Tries to cut out a clue using the neighboring-rule for columns.
	 * 
	 * @param x
	 *            The x-value of the clue in the grid.
	 * @param y
	 *            The y-value of the clue in the grid.
	 * @param sudoku
	 *            The Sudoku grid.
	 * @return True if the clue has been cut, false if not.
	 */
	private boolean cutColumnNeighborRule(int x, int y) {
		/*
		 * Make a backup of the number and cut it out.
		 */
		int cutCandidate = sudokuGridStorage[y][x];
		sudokuGridStorage[y][x] = 0;

		/*
		 * Check if this violates the "neighbor-rule" for columns, by checking
		 * every other cell in the column.
		 */
		for (int y1 = 0; y1 < Sudoku.SIZE; y1++) {
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
				return false;
			}
		}
		return true;
	}

	/**
	 * Tries to cut out a clue using the neighboring-rule for carrees.
	 * 
	 * @param x
	 *            The x-value of the clue in the grid.
	 * @param y
	 *            The y-value of the clue in the grid.
	 * @param sudoku
	 *            The Sudoku grid.
	 * @return True if the clue has been cut, false if not.
	 */
	private boolean cutCarreeNeighborRule(int x, int y) {
		/*
		 * Make a backup of the number and cut it out.
		 */
		int cutCandidate = sudokuGridStorage[y][x];
		sudokuGridStorage[y][x] = 0;
		/*
		 * Calculate the offsets for the carree.
		 */
		int xos = (x / Sudoku.CARREE_SIZE) * Sudoku.CARREE_SIZE;
		int yos = (y / Sudoku.CARREE_SIZE) * Sudoku.CARREE_SIZE;

		/*
		 * Check if this violates the "neighbor-rule" for carrees, by checking
		 * every other cell in the carree.
		 */
		for (int y1 = 0; y1 < Sudoku.CARREE_SIZE; y1++) {
			for (int x1 = 0; x1 < Sudoku.CARREE_SIZE; x1++) {
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
							return false;
						}
					}
				}
			}
		}
		return true;
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
	 * @param sudoku
	 *            The Sudoku grid.
	 * @return True if there is a neighbor with the given value, false if not.
	 */
	private boolean isNeighboredBy(int x, int y, int val) {
		for (int i = 0; i < Sudoku.SIZE; i++) {
			/*
			 * If a cell in the column or in the row contains the value, the
			 * cell is neighbored by that value.
			 */
			if (sudokuGridStorage[i][x] == val || sudokuGridStorage[y][i] == val) {
				return true;
			}
		}

		/*
		 * Calculate the offsets for the carree.
		 */
		int xos = (x / Sudoku.CARREE_SIZE) * Sudoku.CARREE_SIZE;
		int yos = (y / Sudoku.CARREE_SIZE) * Sudoku.CARREE_SIZE;

		/*
		 * If the value is found within the carree, the cell is neighbored by
		 * that value.
		 */
		for (int x1 = 0; x1 < Sudoku.CARREE_SIZE; x1++) {
			for (int y1 = 0; y1 < Sudoku.CARREE_SIZE; y1++) {
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
	 * @param sudoku
	 *            The Sudoku grid to be cut.
	 * @param diff
	 *            The difficulty determines the maximum number of clues that get
	 *            cut out by this.
	 */
	private void doRandomCutting(Difficulty diff) {
		for (int x = 0; x < Sudoku.SIZE; x++) {
			for (int y = 0; y < Sudoku.SIZE; y++) {
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
					if (!hasUniqueSolution(sudokuGridStorage, diff.maxRecursionDepth())) {
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
	private static boolean hasUniqueSolution(int[][] sudoku,
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
	private static int checkSolutions(int x, int y, int[][] sudoku,
			int solutionsFound, int maxRecursionDepth) {
		/*
		 * If all columns in a row have been filled increase the number of rows.
		 */
		if (x == Sudoku.SIZE) {
			x = 0;
			if (++y == Sudoku.SIZE) {
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
		if (sudoku[x][y] != 0)
			return checkSolutions(x + 1, y, sudoku, solutionsFound,
					maxRecursionDepth--);

		/*
		 * Fill every legal value from 1 to 9 in the cell and try to find
		 * solutions recursively. Break the loop if there is already more than 1
		 * solution.
		 */
		for (int val = 1; val <= Sudoku.SIZE && solutionsFound < 2; ++val) {
			if (Sudoku.legal(x, y, val, sudoku)) {
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
	 * @param sudoku
	 *            The Sudoku grid where the clues will be added.
	 * @param solutions
	 *            The solution to the Sudoku grid (the clues will be taken from
	 *            here).
	 * @param prng
	 *            The Random Number Generator to generate the random variables.
	 * @param add
	 *            The number of clues that should be added.
	 */
	private void addRandomClues(int add) {
		int clues = getNumberOfClues(sudokuGridStorage);
		/*
		 * If clues + i is equal the SIZE*SIZE, this means there are no empty
		 * cells left and the loop has to stop.
		 */
		for (int i = 0; i < add && clues + i < Sudoku.SIZE * Sudoku.SIZE;) {
			int x = prng.nextInt(9);
			int y = prng.nextInt(9);
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
