import java.util.Arrays;
import java.util.Random;

public class Sudoku implements INumberPuzzle {
	private int[][] startGrid;
	private int[][] solvedGrid;
	private int[][] recentGrid;
	final Difficulty DIFFICULTY;
	final static int SIZE = 9;
	
	public Sudoku(long seed) {
		this.DIFFICULTY = generate(seed);
	}

	public Sudoku(Difficulty dif){
		Difficulty realDiff;
		int i = 0;
		do{
			Random prng = new Random();
			realDiff = generate(prng.nextLong());
			i++;
		}while(realDiff != dif && i < 5);
		
		this.DIFFICULTY = realDiff;
	}
	
	private Difficulty generate(long seed) {
		Random prng = new Random(seed);
		int[][] buildArray = new int[SIZE][SIZE];

		// Schritt 1: Setze die Zahlen 1 bis 8 an beliebige Pl�tze
		for (int i = 1; i <= 8; i++) {
			int x = prng.nextInt(SIZE);
			int y = prng.nextInt(SIZE);
			while (buildArray[y][x] != 0) {
				x = prng.nextInt();
				y = prng.nextInt();
			}
			buildArray[y][x] = i;
		}

		// Schritt 2: Finde m�gliche L�sung per Backtracking

		startGrid = buildArray;
		this.solve();
		int[][] solved = Arrays.copyOf(startGrid, startGrid.length);

		// Schritt 3 entferne genau 3 beliebige elemente
		for (int i = 0; i < 3; i++) {
			int x = prng.nextInt(SIZE);
			int y = prng.nextInt(SIZE);
			solved[y][x] = 0;
		}

		// Schritt 4 entferne aus jeder vollst�ndigen Spalte, Zeile und Karee
		// genau ein Element

		// erst die zeilen
		for (int i = 0; i < SIZE; i++) {
			boolean lineComplete = true;
			for (int e = 0; e < SIZE; e++) {
				if (solved[i][e] == 0) {
					lineComplete = false;
					break;
				}
			}
			if (lineComplete) {
				int x = prng.nextInt(SIZE);
				solved[i][x] = 0;
			}
		}

		// dann die spalten
		for (int i = 0; i < SIZE; i++) {
			boolean columnComplete = true;
			for (int e = 0; e < SIZE; e++) {
				if (solved[e][i] == 0) {
					columnComplete = false;
					break;
				}
			}
			if (columnComplete) {
				int y = prng.nextInt(SIZE);
				solved[y][i] = 0;
			}
		}

		// Aus jedem vollst�ndigen Carre eine zahl entfernen
		for (int i = 0; i < SIZE; i = i + 3) {
			for (int e = 0; e < SIZE; e = e + 3) {
				boolean carreeComplete = true;
				for (int xos = 0; xos < 3; xos++) { // x Offset
					for (int yos = 0; yos < 3; yos++) { // y Offset
						if (solved[i + yos][e + xos] == 0) {
							carreeComplete = false;
							break; // Lohnt sich das hier? Breaked nur die
									// innerste Schleife
						}
					}
				}
				if (carreeComplete) {
					int x = prng.nextInt(3);
					int y = prng.nextInt(3);
					solved[i + y][e + x] = 0;
				}
			}
		}

		// Schritt 5 Regelbasiertes Streichen

		// Schneide jede Zahl die in ihren nachbarzellen alle anderen werte
		// schon vergeben hat
		for (int x = 0; x < SIZE; x++) {
			for (int y = 0; y < SIZE; y++) {
				int save = solved[y][x];
				solved[y][x] = 0;
				for (int i = 1; i < SIZE; i++) {
					if (i != save && legal(x, y, i, solved)) {
						solved[y][x] = save;
						break;
					}
				}
			}
		}

		// Schneide jede Zahl die in allen anderen leeren Zellen ihrer Zeile /
		// Spalte / Karree ausgeschlossen ist.
		for (int x = 0; x < SIZE; x++) {
			for (int y = 0; y < SIZE; y++) {
				if (solved[y][x] != 0) {
					int cutCandidate = solved[y][x];
					solved[y][x] = 0;

					boolean rowCuttable = true;
					boolean columnCuttable = true;
					boolean carreeCuttable = true;

					// Zeilen
					for (int x1 = 0; x1 < SIZE; x1++) {
						if (solved[y][x1] == 0
								&& x1 != x
								&& !isNeighbouredBy(x1, y, cutCandidate, solved)) {
							rowCuttable = false;
							break;
						}
					}

					// Spalten
					for (int y1 = 0; y1 < SIZE; y1++) {
						if (solved[y1][x] == 0
								&& y1 != y
								&& !isNeighbouredBy(x, y1, cutCandidate, solved)) {
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
						solved[y][x] = cutCandidate;
					}
				}
			}
		}

		// Zufallsbasiertes Schneiden

		for (int x = 0; x < SIZE; x++) {
			for (int y = 0; y < SIZE; y++) {
				if (solved[y][x] != 0) {
					int cutCandidate = solved[y][x];
					solved[y][x] = 0;
					int[][] copy = new int[SIZE][SIZE];
					for (int i = 0; i < SIZE; i++) {
						copy[i] = Arrays.copyOf(solved[i], solved[i].length);
					}
					if (hasMultipleSolutions(0, 0, copy, 0) != 1) {
						solved[y][x] = cutCandidate;
					}
				}
			}
		}

		// Idee f�r difficulty -> Leicht ohne zufallsschneiden, mittel mit 1-2x
		// zufallsschneiden und schwer mit viel zufallsschneiden, extrem testen
		// ob nicht zu lange zum generieren braucht

		this.startGrid = solved;
		return Difficulty.EASY;
	}

	private static boolean isNeighbouredBy(int x, int y, int val, int[][] sudoku) {
		for (int i = 0; i < SIZE; i++) {
			if (sudoku[i][x] == val || sudoku[y][i] == val) {
				return true;
			}
		}

		int xos = (x / 3) * 3;
		int yos = (y / 3) * 3;

		for (int x1 = 0; x1 < 3; x1++) {
			for (int y1 = 0; y1 < 3; y1++) {
				if (sudoku[yos + y1][xos + x1] == val) {
					return true;
				}
			}
		}

		return false;
	}

	private static int hasMultipleSolutions(int x, int y, int[][] sudoku, int solutionsFound) {
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
				solutionsFound = hasMultipleSolutions(x + 1, y, sudoku, solutionsFound);
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

		int boxRowOffset = (x / 3) * 3;
		int boxColOffset = (y / 3) * 3;
		for (int k = 0; k < 3; ++k)
			// box
			for (int m = 0; m < 3; ++m)
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

	public int[] markMistake() {
		return new int[2];
	}

	public void giveHint() {

	}
}
