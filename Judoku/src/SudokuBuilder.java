import java.util.Arrays;
import java.util.Random;

public class SudokuBuilder {

	public Sudoku newSudoku(Difficulty diff) {
		return newSudoku(new Random().nextInt(), diff);
	}

	public Sudoku newSudoku(long seed, Difficulty diff) {
		Random prng = new Random(seed);

		// Generiere ein gel�stes Sudoku
		int[][] solvedGrid = generateSolvedGrid(prng);
		int[][] templateSdk = Controller.deepCopy(solvedGrid);

		// Entferne genau 3 beliebige elemente
		for (int i = 0; i < 3;) {
			if (removeRandomElement(templateSdk, prng)) {
				i++;
			}
		}

		cutCompleteStructures(templateSdk, prng);

		int clues1 = getNumberOfClues(templateSdk);
		cutDeductively(templateSdk);
		System.out.println(getNumberOfClues(templateSdk) - clues1
				+ "removed deductively");

		clues1 = getNumberOfClues(templateSdk);
		cutWithNeighbourRule(templateSdk);
		System.out.println(getNumberOfClues(templateSdk) - clues1
				+ " removed neighbour");
		System.out.println(hasUniqueSolution(templateSdk)
				+ " has unique solutions");

		clues1 = getNumberOfClues(templateSdk);
		doRandomCutting(templateSdk, diff.toRandomCuttingIndex());
		System.out.println(clues1 - getNumberOfClues(templateSdk)
				+ " removed by rnd cutting");

		// Testen welche Schwierigkeit erreicht wurde
		int clues = getNumberOfClues(templateSdk);
		Sudoku build;
		// no random cutting has been done so its easy
		if (clues >= Difficulty.EASY.minNumberOfClues()) {
			build = new Sudoku(templateSdk, solvedGrid, Difficulty.EASY);
		}
		// if random cutting has been done and it has less clues than a medium
		// rated Sudoku
		else if (clues < Difficulty.MEDIUM.minNumberOfClues()
				&& diff == Difficulty.HARD) {
			build = new Sudoku(templateSdk, solvedGrid, Difficulty.HARD);
		}
		// If its neither easy nor hard its medium
		else {
			build = new Sudoku(templateSdk, solvedGrid, Difficulty.MEDIUM);
		}

		int missingClues = diff.minNumberOfClues() - clues;
		for (int j = 0; j < missingClues; j++) {
			build.giveHint();
		}
		return build;
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

		// Schritt 1: Setze die Zahlen 1 bis 8 an beliebige Plaetze
		for (int i = 1; i <= 8; i++) {
			int x = prng.nextInt(Sudoku.SIZE);
			int y = prng.nextInt(Sudoku.SIZE);
			while (solvedGrid[y][x] != 0) {
				x = prng.nextInt(Sudoku.SIZE);
				y = prng.nextInt(Sudoku.SIZE);
			}
			solvedGrid[y][x] = i;
		}

		// Schritt 2: Finde ein Element aus der Menge aller m�glichen L�sungen
		// per Backtracking
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
	 * Sets a random element from the Sudoku grid to 0
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
		// Schneide jede Zahl die in allen anderen leeren Zellen ihrer Zeile /
		// Spalte / Karree ausgeschlossen ist.
		for (int x = 0; x < Sudoku.SIZE; x++) {
			for (int y = 0; y < Sudoku.SIZE; y++) {
				if (sudoku[y][x] != 0) {
					int cutCandidate = sudoku[y][x];
					sudoku[y][x] = 0;

					boolean rowCuttable = true;
					boolean columnCuttable = true;
					boolean carreeCuttable = true;

					// Zeilen
					for (int x1 = 0; x1 < Sudoku.SIZE; x1++) {
						if (sudoku[y][x1] == 0
								&& x1 != x
								&& !isNeighbouredBy(x1, y, cutCandidate, sudoku)) {
							rowCuttable = false;
							break;
						}
					}

					// TODO jede zahl die hier geschnitte3n wird wird bereits
					// durch die zeilen regel geschnitten
					// Spalten
					for (int y1 = 0; y1 < Sudoku.SIZE; y1++) {
						if (sudoku[y1][x] == 0
								&& y1 != y
								&& !isNeighbouredBy(x, y1, cutCandidate, sudoku)) {
							columnCuttable = false;
							break;
						}
					}

					for (int[] a : sudoku) {
						System.out.println(Arrays.toString(a));
					}

					// Carrees
					// TODO FUNKTIONIERT NICHT
					int xos = (x / 3) * 3;
					int yos = (y / 3) * 3;

					for (int x1 = 0; x1 < 3; x1++) {
						for (int y1 = 0; y1 < 3; y1++) {
							if (sudoku[yos + y1][xos + x1] == 0
									&& xos + x1 != x
									&& yos + y1 != y
									&& !isNeighbouredBy(xos + x1, yos + y1,
											cutCandidate, sudoku)) {
								carreeCuttable = false;
								break;
							}
						}
					}

					if (!(rowCuttable || columnCuttable || false)) {
						sudoku[y][x] = cutCandidate;
					}
				}
			}
		}
	}

	/**
	 * Shows wether a certain cell in a Sudoku grid has a neighbouring cell
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
	 * Trys to cut out values which are not cuttable by deduction. Always checks
	 * wether this results in multiple solutions.
	 * 
	 * @param sudoku
	 *            The sudoku grid to be cut.
	 * @param maxCuts
	 *            The maximum number of clues that get cut out.
	 */
	private static void doRandomCutting(int[][] sudoku, int maxCuts) {
		for (int x = 0; x < Sudoku.SIZE && maxCuts > 0; x++) {
			for (int y = 0; y < Sudoku.SIZE && maxCuts > 0; y++) {
				if (sudoku[y][x] != 0) {
					int cutCandidate = sudoku[y][x];
					sudoku[y][x] = 0;
					if (!hasUniqueSolution(sudoku)) {
						sudoku[y][x] = cutCandidate;
					} else {
						maxCuts--;
					}
				}
			}
		}
	}

	/**
	 * Checks wether a given Sudoku is uniquely solvable.
	 * 
	 * @param sudoku
	 *            The sudoku to be checked.
	 * @return True if only one solution exists, false if there are none or
	 *         mutliple solutions.
	 */
	private static boolean hasUniqueSolution(int[][] sudoku) {
		int[][] copy = Controller.deepCopy(sudoku);
		return (checkSolutions(0, 0, copy, 0) == 1);
	}

	/**
	 * Checks wether a given Sudoku has none, one or mutliple Solutions per
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
			int solutionsFound) {
		if (x == Sudoku.SIZE) {
			x = 0;
			if (++y == Sudoku.SIZE)
				return 1 + solutionsFound;
		}
		if (sudoku[x][y] != 0) // skip filled cells
			return checkSolutions(x + 1, y, sudoku, solutionsFound);

		for (int val = 1; val <= Sudoku.SIZE && solutionsFound < 2; ++val) {
			if (Sudoku.legal(x, y, val, sudoku)) {
				sudoku[x][y] = val;
				solutionsFound = checkSolutions(x + 1, y, sudoku,
						solutionsFound);
			}
		}
		sudoku[x][y] = 0; // reset on backtrack
		return solutionsFound;
	}
}
