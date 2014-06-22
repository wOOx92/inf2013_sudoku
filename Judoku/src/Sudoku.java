import java.util.Arrays;
import java.util.Random;

public class Sudoku implements INumberPuzzle {
	private int[][] startGrid;
	private int[][] solvedGrid;
	private int[][] recentGrid;
	final Difficulty DIFFICULTY;

	// CARREE_SIZE eines Sudoku = sqrt(SIZE)
	final static int CARREE_SIZE = 3;
	final static int SIZE = 9;

	public Sudoku(long seed) {
		this.DIFFICULTY = generate(seed);
	}

	public Sudoku(Difficulty dif) {
		Difficulty realDiff;
		int i = 0;
		do {
			Random prng = new Random();
			realDiff = generate(prng.nextLong());
			i++;
		} while (realDiff != dif && i < 5);

		this.DIFFICULTY = realDiff;
	}

	private Difficulty generate(long seed) {
		Random prng = new Random(seed);

		// Schritt 1 und 2 -> Generiere ein gelöstes Sudoku
		solvedGrid = generateSolvedGrid(prng);

		int[][] templateSdk = Controller.deepCopy(solvedGrid);

		// Schritt 3 entferne genau 3 beliebige elemente
		for (int i = 0; i < 3;) {
			if(removeRandomElement(templateSdk, prng)){
				i++;
			}
		}

		// Schritt 4 entferne aus jeder vollstaendigen Spalte, Zeile und Karee
		// genau ein Element
		cutCompleteStructures(templateSdk,prng);
		
		// Schritt 5 Regelbasiertes Streichen

		// Schneide jede Zahl die in ihren nachbarzellen alle anderen werte
		// schon vergeben hat
		for (int x = 0; x < SIZE; x++) {
			for (int y = 0; y < SIZE; y++) {
				int save = templateSdk[y][x];
				templateSdk[y][x] = 0;
				for (int i = 1; i < SIZE; i++) {
					if (i != save && legal(x, y, i, templateSdk)) {
						templateSdk[y][x] = save;
						break;
					}
				}
			}
		}

		// Schneide jede Zahl die in allen anderen leeren Zellen ihrer Zeile /
		// Spalte / Karree ausgeschlossen ist.
		for (int x = 0; x < SIZE; x++) {
			for (int y = 0; y < SIZE; y++) {
				if (templateSdk[y][x] != 0) {
					int cutCandidate = templateSdk[y][x];
					templateSdk[y][x] = 0;

					boolean rowCuttable = true;
					boolean columnCuttable = true;
					boolean carreeCuttable = true;

					// Zeilen
					for (int x1 = 0; x1 < SIZE; x1++) {
						if (templateSdk[y][x1] == 0
								&& x1 != x
								&& !isNeighbouredBy(x1, y, cutCandidate,
										templateSdk)) {
							rowCuttable = false;
							break;
						}
					}

					// Spalten
					for (int y1 = 0; y1 < SIZE; y1++) {
						if (templateSdk[y1][x] == 0
								&& y1 != y
								&& !isNeighbouredBy(x, y1, cutCandidate,
										templateSdk)) {
							columnCuttable = false;
							break;
						}
					}

					/*
					 * //Carrees int xos = (x/3)*3; int yos = (y/3)*3;
					 * 
					 * for(int x1 = 0; x1 < 3; x1++){ for(int y1 = 0; y1 < 3;
					 * y1++){ if(solved[yos+y1][xos+x1] == 0 && xos+x1 != x &&
					 * yos+y1 != y &&
					 * !isNeighbouredBy(xos+x1,yos+y1,cutCandidate,solved)){
					 * carreeCuttable = false; break; } } }
					 */

					if (!(rowCuttable || columnCuttable || false)) {
						templateSdk[y][x] = cutCandidate;
					}
				}
			}
		}

		// Zufallsbasiertes Schneiden

		for (int x = 0; x < SIZE; x++) {
			for (int y = 0; y < SIZE; y++) {
				if (templateSdk[y][x] != 0) {
					int cutCandidate = templateSdk[y][x];
					templateSdk[y][x] = 0;
					int[][] copy = new int[SIZE][SIZE];
					for (int i = 0; i < SIZE; i++) {
						copy[i] = Arrays.copyOf(templateSdk[i],
								templateSdk[i].length);
					}
					if (hasMultipleSolutions(0, 0, copy, 0) != 1) {
						templateSdk[y][x] = cutCandidate;
					}
				}
			}
		}

		// Idee fuer difficulty -> Leicht ohne zufallsschneiden, mittel mit
		// 1-2x
		// zufallsschneiden und schwer mit viel zufallsschneiden, extrem testen
		// ob nicht zu lange zum generieren braucht

		this.startGrid = templateSdk;
		return Difficulty.EASY;
	}

	private static int[][] generateSolvedGrid(Random prng) {
		int[][] solvedGrid = new int[SIZE][SIZE];

		// Schritt 1: Setze die Zahlen 1 bis 8 an beliebige Plaetze
		for (int i = 1; i <= 8; i++) {
			int x = prng.nextInt(SIZE);
			int y = prng.nextInt(SIZE);
			while (solvedGrid[y][x] != 0) {
				x = prng.nextInt();
				y = prng.nextInt();
			}
			solvedGrid[y][x] = i;
		}

		// Schritt 2: Finde ein Element aus der Menge aller möglichen Lösungen
		// per Backtracking
		solve(0, 0, solvedGrid);

		return solvedGrid;
	}

	private static boolean removeRandomElement(int[][] sudoku, Random prng){
		int x = prng.nextInt(SIZE);
		int y = prng.nextInt(SIZE);
		if(sudoku[y][x] != 0){
			sudoku[y][x] = 0;
			return true;
		}
		return false;
	}

	private static void cutCompleteStructures(int[][] sudoku, Random prng){
		// erst die zeilen
		for (int i = 0; i < SIZE; i++) {
			boolean lineComplete = true;
			for (int e = 0; e < SIZE; e++) {
				if (sudoku[i][e] == 0) {
					lineComplete = false;
					break;
				}
			}
			if (lineComplete) {
				int x = prng.nextInt(SIZE);
				sudoku[i][x] = 0;
			}
		}

		// dann die spalten
		for (int i = 0; i < SIZE; i++) {
			boolean columnComplete = true;
			for (int e = 0; e < SIZE; e++) {
				if (sudoku[e][i] == 0) {
					columnComplete = false;
					break;
				}
			}
			if (columnComplete) {
				int y = prng.nextInt(SIZE);
				sudoku[y][i] = 0;
			}
		}

		// Aus jedem vollstaendigen Carre eine zahl entfernen
		for (int i = 0; i < SIZE; i = i + CARREE_SIZE) {
			for (int e = 0; e < SIZE; e = e + CARREE_SIZE) {
				boolean carreeComplete = true;
				for (int xos = 0; xos < CARREE_SIZE; xos++) { // x Offset
					for (int yos = 0; yos < CARREE_SIZE; yos++) { // y Offset
						if (sudoku[i + yos][e + xos] == 0) {
							carreeComplete = false;
							break; // Lohnt sich das hier? Breaked nur die innerste Schleife
						}
					}
				}
				if (carreeComplete) {
					int x = prng.nextInt(CARREE_SIZE);
					int y = prng.nextInt(CARREE_SIZE);
					sudoku[i + y][e + x] = 0;
				}
			}
		}
	}

	private static boolean isNeighbouredBy(int x, int y, int val, int[][] sudoku) {
		for (int i = 0; i < SIZE; i++) {
			if (sudoku[i][x] == val || sudoku[y][i] == val) {
				return true;
			}
		}

		int xos = (x / CARREE_SIZE) * CARREE_SIZE;
		int yos = (y / CARREE_SIZE) * CARREE_SIZE;

		for (int x1 = 0; x1 < CARREE_SIZE; x1++) {
			for (int y1 = 0; y1 < CARREE_SIZE; y1++) {
				if (sudoku[yos + y1][xos + x1] == val) {
					return true;
				}
			}
		}

		return false;
	}

	private static int hasMultipleSolutions(int x, int y, int[][] sudoku,
			int solutionsFound) {
		if (x == SIZE) {
			x = 0;
			if (++y == SIZE)
				return 1 + solutionsFound;
		}
		if (sudoku[x][y] != 0) // skip filled cells
			return hasMultipleSolutions(x + 1, y, sudoku, solutionsFound);

		for (int val = 1; val <= SIZE && solutionsFound < 2; ++val) {
			if (legal(x, y, val, sudoku)) {
				sudoku[x][y] = val;
				solutionsFound = hasMultipleSolutions(x + 1, y, sudoku,
						solutionsFound);
			}
		}
		sudoku[x][y] = 0; // reset on backtrack
		return solutionsFound;
	}

	private boolean solve() {
		int[][] buffer = Arrays.copyOf(this.startGrid, this.startGrid.length);
		if (solve(0, 0, buffer)) {
			this.solvedGrid = buffer;
			return true;
		}
		return false;
	}

	private static boolean solve(int x, int y, int[][] sudoku) {
		if (x == SIZE) {
			x = 0;
			if (++y == SIZE)
				return true;
		}
		if (sudoku[x][y] != 0) // skip filled cells
			return solve(x + 1, y, sudoku);

		for (int val = 1; val <= SIZE; ++val) {
			if (legal(x, y, val, sudoku)) {
				sudoku[x][y] = val;
				if (solve(x + 1, y, sudoku))
					return true;
			}
		}
		sudoku[x][y] = 0; // reset on backtrack
		return false;
	}

	private static boolean legal(int x, int y, int val, int[][] sudoku) {
		for (int k = 0; k < SIZE; ++k)
			// row
			if (val == sudoku[k][x])
				return false;

		for (int k = 0; k < SIZE; ++k)
			// col
			if (val == sudoku[y][k])
				return false;

		int boxRowOffset = (x / CARREE_SIZE) * CARREE_SIZE;
		int boxColOffset = (y / CARREE_SIZE) * CARREE_SIZE;
		for (int k = 0; k < CARREE_SIZE; ++k)
			// box
			for (int m = 0; m < CARREE_SIZE; ++m)
				if (val == sudoku[boxRowOffset + k][boxColOffset + m])
					return false;

		return true; // no violations, so it's legal
	}

	public void reset() {
		this.recentGrid = this.startGrid;
	}

	public boolean trySetValue(int x, int y, int val) {
		if (val < 0 || val > SIZE) {
			return false;
		}
		if (x < 0 || x >= SIZE) {
			return false;
		}
		if (x < 0 || x >= SIZE) {
			return false;
		}
		if (this.startGrid[y][x] != 0) {
			return false;
		}
		this.recentGrid[y][x] = val;
		return true;

	}

	public int[][] getRecentGrid() {
		return this.recentGrid;
	}

	public int[] searchMistake() {
		for(int x = 0; x < SIZE; x++){
			for(int y = 0; y < SIZE; y++){
				if(this.recentGrid[y][x] != 0 && this.recentGrid[y][x] != this.solvedGrid[y][x]){
					int[] mistake = new int[2];
					mistake[0] = x;
					mistake[1] = y;
					return mistake;
				}
			}
		}
		
		//No mistake found
		return new int[0];
	}

	public void giveHint() {

	}
}
