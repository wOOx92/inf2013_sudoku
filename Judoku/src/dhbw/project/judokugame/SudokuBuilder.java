package dhbw.project.judokugame;
import java.util.Arrays;
import java.util.Random;

public class SudokuBuilder {
	
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
	public Sudoku newSudoku(long seed, Difficulty diff){
		/*
		 * This Random object will be used to generate all random variables
		 * needed.
		 */
		Random prng = new Random(seed);

		/*
		 * Step 1: Generate a entirely solved Sudoku grid and copy it for
		 * further manipulation.
		 */
		int[][] solvedGrid = generateSolvedGrid(prng);
		int[][] templateSdk = SudokuBuilder.deepCopy(solvedGrid);

		/*
		 * Step 2: Remove 3 elements at random for more randomness in the
		 * generation process (the Sudoku will stay valid).
		 */
		// TODO checking if removed in removerandomElement method.
		for (int i = 0; i < 3;) {
			if (removeRandomElement(templateSdk, prng)) {
				i++;
			}
		}

		/*
		 * Step 3: Cut one clue from every row / column / carree that is still
		 * complete. This results in more randomness in the generating process.
		 */
		cutCompleteStructures(templateSdk, prng);

		/*
		 * Step 4: Cut all clues that satisfy this condition: If the clue is
		 * removed, the only possible candidate for the resulting empty cell is
		 * said clue.
		 */
		cutDeductively(templateSdk);

		/*
		 * Step 5: Cut all clues using the "Neighbor-rule": If every empty cell
		 * neighboring the clue is neighbored by the value of that cell again,
		 * the clue can be cut out. By definition a cell x is neighbored by a
		 * value if another cell in the row, column or carree of the cell x
		 * contains that value.
		 */
		cutWithNeighbourRule(templateSdk);

		// TODO debug ausgaben entfernen
		System.out.println(hasUniqueSolution(templateSdk, Difficulty.HARD)
				+ " has unique solutions");
		int c = getNumberOfClues(templateSdk);

		/*
		 * Step 6: Try to make the resulting Sudoku irreducible using a
		 * "cut-and-test" method.
		 */
		doRandomCutting(templateSdk, diff);
		System.out.println(c - getNumberOfClues(templateSdk));

		return new Sudoku(templateSdk, solvedGrid, diff);
	}

	/**
	 * Generates a solved Sudoku grid.
	 * 
	 * @param prng
	 *            The (P)RNG used to generate the grid.
	 * @return A completely and correctly filled Sudoku grid.
	 */
	private static int[][] generateSolvedGrid(Random prng) {
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
	 *            The sudoku grid to be checked for solutions.
	 * @return True if a solution was found, false if not.
	 */
	private static boolean solve(int i, int j, int[][] cells) {
		if (i == 9) {
			i = 0;
			if (++j == 9)
				return true;
		}
		if (cells[i][j] != 0) // skip filled cells
			return solve(i + 1, j, cells);

		for (int val = 1; val <= 9; ++val) {
			if (Sudoku.legal(i, j, val, cells)) {
				cells[i][j] = val;
				if (solve(i + 1, j, cells))
					return true;
			}
		}
		cells[i][j] = 0; // reset on backtrack
		return false;
	}

	/**
	 * Tries to remove a random element from the Sudoku grid.
	 * 
	 * @param sudoku
	 *            The Sudoku grid which gets modified.
	 * @param prng
	 *            The Random Number Generator used to generate the random
	 *            variables.
	 * @return
	 */
	private static boolean removeRandomElement(int[][] sudoku, Random prng) {
		int x = prng.nextInt(Sudoku.SIZE);
		int y = prng.nextInt(Sudoku.SIZE);
		if (sudoku[y][x] != 0) {
			sudoku[y][x] = 0;
			return true;
		}
		return false;
	}

	/**
	 * Cuts out one element from the Sudoku grid.
	 * 
	 * @param sudoku
	 *            The Sudoku which gets modified.
	 * @param prng
	 *            The Random Number Generator used to generate the random
	 *            variables.
	 */
	private static void cutCompleteStructures(int[][] sudoku, Random prng) {
		// erst die zeilen
		for (int i = 0; i < Sudoku.SIZE; i++) {
			boolean lineComplete = true;
			for (int e = 0; e < Sudoku.SIZE; e++) {
				if (sudoku[i][e] == 0) {
					lineComplete = false;
					break;
				}
			}
			if (lineComplete) {
				int x = prng.nextInt(Sudoku.SIZE);
				sudoku[i][x] = 0;
			}
		}

		// dann die spalten
		for (int i = 0; i < Sudoku.SIZE; i++) {
			boolean columnComplete = true;
			for (int e = 0; e < Sudoku.SIZE; e++) {
				if (sudoku[e][i] == 0) {
					columnComplete = false;
					break;
				}
			}
			if (columnComplete) {
				int y = prng.nextInt(Sudoku.SIZE);
				sudoku[y][i] = 0;
			}
		}

		// Aus jedem vollstaendigen Carre eine zahl entfernen
		for (int i = 0; i < Sudoku.SIZE; i = i + Sudoku.CARREE_SIZE) {
			for (int e = 0; e < Sudoku.SIZE; e = e + Sudoku.CARREE_SIZE) {
				boolean carreeComplete = true;
				for (int xos = 0; xos < Sudoku.CARREE_SIZE; xos++) { // x Offset
					for (int yos = 0; yos < Sudoku.CARREE_SIZE; yos++) { // y
																			// Offset
						if (sudoku[i + yos][e + xos] == 0) {
							carreeComplete = false;
							break; // Lohnt sich das hier? Breaked nur die
									// innerste Schleife
						}
					}
				}
				if (carreeComplete) {
					int x = prng.nextInt(Sudoku.CARREE_SIZE);
					int y = prng.nextInt(Sudoku.CARREE_SIZE);
					sudoku[i + y][e + x] = 0;
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
	 * "cut out a number if all possible values are already used in its neighbouring cells"
	 * 
	 * @param sudoku
	 *            The Sudoku grid to be cut.
	 */
	private static void cutDeductively(int[][] sudoku) {
		for (int x = 0; x < Sudoku.SIZE; x++) {
			for (int y = 0; y < Sudoku.SIZE; y++) {
				int save = sudoku[y][x];
				sudoku[y][x] = 0;
				for (int i = 1; i <= Sudoku.SIZE; i++) {
					if (i != save && Sudoku.legal(y, x, i, sudoku)) {
						sudoku[y][x] = save;
						break;
					}
				}
			}
		}
	}

	/**
	 * Tries to cut out clues using the deductive cutting-rule
	 * "cut out every number which is neighboured by each of its neighbours".
	 * 
	 * @param sudoku
	 *            The Sudoku grid to be cut.
	 */
	private static void cutWithNeighbourRule(int[][] sudoku) {
		/*
		 * Use the row, column and carrre neighborRule for each value in the
		 * Sudoku grid.
		 */
		for (int x = 0; x < Sudoku.SIZE; x++) {
			for (int y = 0; y < Sudoku.SIZE; y++) {
				if (sudoku[y][x] != 0) {
					cutRowNeighbourRule(x, y, sudoku);
					cutColumnNeighbourRule(x, y, sudoku);
					cutCarreeNeighbourRule(x, y, sudoku);
				}
			}
		}
	}

	/**
	 * Tries to cut out a clue using the neighbouring-rule for rows.
	 * 
	 * @param x
	 *            The x-value of the clue in the grid.
	 * @param y
	 *            The y-value of the clue in the grid.
	 * @param sudoku
	 *            The Sudoku grid.
	 * @return True if the clue has been cut, false if not.
	 */
	private static boolean cutRowNeighbourRule(int x, int y, int[][] sudoku) {
		int cutCandidate = sudoku[y][x];
		sudoku[y][x] = 0;

		for (int x1 = 0; x1 < Sudoku.SIZE; x1++) {
			if (sudoku[y][x1] == 0 && x1 != x
					&& !isNeighbouredBy(x1, y, cutCandidate, sudoku)) {
				sudoku[y][x] = cutCandidate;
				return false;
			}
		}
		return true;
	}

	/**
	 * Tries to cut out a clue using the neighbouring-rule for columns.
	 * 
	 * @param x
	 *            The x-value of the clue in the grid.
	 * @param y
	 *            The y-value of the clue in the grid.
	 * @param sudoku
	 *            The Sudoku grid.
	 * @return True if the clue has been cut, false if not.
	 */
	private static boolean cutColumnNeighbourRule(int x, int y, int[][] sudoku) {
		int cutCandidate = sudoku[y][x];
		sudoku[y][x] = 0;

		for (int y1 = 0; y1 < Sudoku.SIZE; y1++) {
			if (sudoku[y1][x] == 0 && y1 != y
					&& !isNeighbouredBy(x, y1, cutCandidate, sudoku)) {
				sudoku[y][x] = cutCandidate;
				return false;
			}
		}
		return true;
	}

	/**
	 * Tries to cut out a clue using the neighbouring-rule for carrees.
	 * 
	 * @param x
	 *            The x-value of the clue in the grid.
	 * @param y
	 *            The y-value of the clue in the grid.
	 * @param sudoku
	 *            The Sudoku grid.
	 * @return True if the clue has been cut, false if not.
	 */
	private static boolean cutCarreeNeighbourRule(int x, int y, int[][] sudoku) {
		int cutCandidate = sudoku[y][x];
		sudoku[y][x] = 0;
		int xos = (x / Sudoku.CARREE_SIZE) * Sudoku.CARREE_SIZE;
		int yos = (y / Sudoku.CARREE_SIZE) * Sudoku.CARREE_SIZE;

		for (int y1 = 0; y1 < Sudoku.CARREE_SIZE; y1++) {
			for (int x1 = 0; x1 < Sudoku.CARREE_SIZE; x1++) {
				if (sudoku[yos + y1][xos + x1] == 0) {
					if (xos + x1 != x || yos + y1 != y) {
						if (!isNeighbouredBy(xos + x1, yos + y1, cutCandidate,
								sudoku))
							;
						{
							sudoku[y][x] = cutCandidate;
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	/**
	 * Shows whether a certain cell in a Sudoku grid has a neighbouring cell
	 * containing a given value.
	 * 
	 * @param x
	 *            The x-value of the cell in the Sudoku grid.
	 * @param y
	 *            The y-value of the cell in the Sudoku grid.
	 * @param val
	 *            The value which is searched for in the neighbouring cells.
	 * @param sudoku
	 *            The Sudoku grid.
	 * @return True if there is a neighbour with the given value, false if not.
	 */
	private static boolean isNeighbouredBy(int x, int y, int val, int[][] sudoku) {
		for (int i = 0; i < Sudoku.SIZE; i++) {
			if (sudoku[i][x] == val || sudoku[y][i] == val) {
				return true;
			}
		}

		int xos = (x / Sudoku.CARREE_SIZE) * Sudoku.CARREE_SIZE;
		int yos = (y / Sudoku.CARREE_SIZE) * Sudoku.CARREE_SIZE;

		for (int x1 = 0; x1 < Sudoku.CARREE_SIZE; x1++) {
			for (int y1 = 0; y1 < Sudoku.CARREE_SIZE; y1++) {
				if (sudoku[yos + y1][xos + x1] == val) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Tries to cut out values which are not cuttable by deduction. Always
	 * checks whether this results in multiple solutions.
	 * 
	 * @param sudoku
	 *            The Sudoku grid to be cut.
	 * @param maxCuts
	 *            The maximum number of clues that get cut out.
	 */
	private static void doRandomCutting(int[][] sudoku, Difficulty diff) {
		for (int x = 0; x < Sudoku.SIZE; x++) {
			for (int y = 0; y < Sudoku.SIZE; y++) {
				if (sudoku[y][x] != 0) {
					int cutCandidate = sudoku[y][x];
					sudoku[y][x] = 0;
					if (!hasUniqueSolution(sudoku, diff)) {
						sudoku[y][x] = cutCandidate;
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
	 * @param diff
	 * 			  The Difficulty determines the maximum recursion depth. 
	 * @return True if only one solution exists, false if there are none or
	 *         multiple solutions.
	 */
	private static boolean hasUniqueSolution(int[][] sudoku, Difficulty diff) {
		int[][] copy = SudokuBuilder.deepCopy(sudoku);
		return (checkSolutions(0, 0, copy, 0, diff.maxRecursionDepth()) == 1);
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
	 *            The sudoku grid to be checked for solutions.
	 * @param solutionsFound
	 *            The amount of solutions found so far.
	 * @return 0 if no solutions exist, 1 if the sudoku has a unique solutions,
	 *         >1 if multiple solutions where found (not neccesarily the actual
	 *         amount of solutions).
	 */
	private static int checkSolutions(int x, int y, int[][] sudoku,
			int solutionsFound, int maxRecursionDepth) {
		if (x == Sudoku.SIZE) {
			x = 0;
			if (++y == Sudoku.SIZE)
				return 1 + solutionsFound;
		}
		if (maxRecursionDepth == 0) {
			return 2;
		}

		if (sudoku[x][y] != 0) // skip filled cells
			return checkSolutions(x + 1, y, sudoku, solutionsFound,
					maxRecursionDepth--);

		for (int val = 1; val <= Sudoku.SIZE && solutionsFound < 2; ++val) {
			if (Sudoku.legal(x, y, val, sudoku)) {
				sudoku[x][y] = val;
				solutionsFound = checkSolutions(x + 1, y, sudoku,
						solutionsFound, maxRecursionDepth--);
			}
		}
		sudoku[x][y] = 0; // reset on backtrack
		return solutionsFound;
	}
	
	public static int[][] deepCopy(int [][] template){
		int[][] copy = new int[template.length][0];
		for(int i = 0; i < template.length; i++){
			copy[i] = Arrays.copyOf(template[i], template[i].length);
		}
		return copy;
	}
}
