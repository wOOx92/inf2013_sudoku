import java.util.Arrays;
import java.util.Random;
import java.util.Stack;

public class Sudoku implements INumberPuzzle {
	/**
	 * This array saves the state of the Sudoku at the beginning.
	 */
	private int[][] startGrid;
	
	/**
	 * This array saves the state of the solved Sudoku.
	 */
	private int[][] solvedGrid;
	
	/**
	 * This array saves the recent state of the Sudoku.
	 */
	private int[][] recentGrid;
	
	/**
	 * This field saves the rating of the difficulty of this Sudoku.
	 */
	final Difficulty DIFFICULTY;

	/**
	 * This field is the side-length of a carree in a Sudoku. 
	 */
	final static int CARREE_SIZE = 3;
	
	/**
	 *The side-length of a Sudoku grid.
	 */
	final static int SIZE = 9;
	
	/**
	 * The maximum of steps a user can go back and forth.
	 */
	final static int UNDOLIMIT = 5;
	
	/**
	 * Stores the previous states of this Sudoku
	 */
	private Stack<int[][]> undoStorage = new Stack<int[][]>();
	
	/**
	 * Stores the states of the Sudoku for redoing.
	 */
	private Stack<int[][]> redoStorage = new Stack<int[][]>();
	
	/**
	 * Creates an instance of Sudoku.
	 * @param seed The seed for the (P)RNG that is used for constructing this instance.
	 * @param diff The desired difficulty of this Sudoku.
	 */
	public Sudoku(long seed, Difficulty diff) {
		this.DIFFICULTY = generate(seed, diff);
	}

	/**
	 * Creates an instance of Sudoku.
	 * @param diff The desired difficulty.
	 */
	public Sudoku(Difficulty diff) {
		Difficulty realDiff;
		int i = 0;
		do {
			Random prng = new Random();
			realDiff = generate(prng.nextLong(), diff);
			i++;
		} while (realDiff != diff && i < 5);

		this.DIFFICULTY = realDiff;
	}

	/**
	 * Tries to generate a Sudoku of the desired difficulty.
	 * @param seed The seed for the (P)RNG that is used to construct the Sudoku.
	 * @param diff The desired difficulty of the Sudoku.
	 * @return The actual difficulty of the generated Sudoku.
	 */
	private Difficulty generate(long seed, Difficulty diff) {
		Random prng = new Random(seed);

		// Schritt 1 und 2 -> Generiere ein gelöstes Sudoku
		solvedGrid = generateSolvedGrid(prng);

		int[][] templateSdk = Controller.deepCopy(solvedGrid);

		// Schritt 3 entferne genau 3 beliebige elemente
		for (int i = 0; i < 3;) {
			if (removeRandomElement(templateSdk, prng)) {
				i++;
			}
		}

		// Schritt 4 entferne aus jeder vollstaendigen Spalte, Zeile und Karee
		// genau ein Element
		cutCompleteStructures(templateSdk, prng);

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

		//TODO Testen ob IMMER eindeutig bleibt, evtl ist implementierung dieser regel falsch, bei CARREES funktioniert es bisher noch nicht
		
		// Schneide jede Zahl die in allen anderen leeren Zellen ihrer Zeile /
		// Spalte / Karree ausgeschlossen ist.
		/*for (int x = 0; x < SIZE; x++) {
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
					 

					if (!(rowCuttable || columnCuttable || false)) {
						templateSdk[y][x] = cutCandidate;
					}
				}
			}
		}*/

		// Zufallsbasiertes Schneiden
		doRandomCutting(templateSdk, diff.toRandomCuttingIndex());
		
		// Testen welche Schwierigkeit erreicht wurde

		int clues = getNumberOfClues(templateSdk);
		int missingClues = diff.minNumberOfClues() - clues;
		
		this.startGrid = templateSdk;
		addRandomClues(prng, missingClues);
		
		if (clues >= 30) {
			return Difficulty.EASY;
		}
		else if (diff == Difficulty.EASY) {		
			return Difficulty.EASY;
		}
		else if (diff == Difficulty.MEDIUM) {
			return Difficulty.MEDIUM;
		}
		else if (clues >= 20) {
			return Difficulty.EXTREME;
		}
		return Difficulty.HARD;
	}

	/**
	 * Generates a solved Sudoku grid.
	 * @param prng The (P)RNG used to generate the grid.
	 * @return A completely and correctly filled Sudoku grid.
	 */
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

	/**
	 * Sets a random element from the sudoku grid to 0
	 * @param sudoku The sudoku grid which gets modified.
	 * @param prng The Random Number Generator used to generate the random variables.
	 * @return
	 */
	private static boolean removeRandomElement(int[][] sudoku, Random prng) {
		int x = prng.nextInt(SIZE);
		int y = prng.nextInt(SIZE);
		if (sudoku[y][x] != 0) {
			sudoku[y][x] = 0;
			return true;
		}
		return false;
	}

	/**
	 * Cuts out one element from the Sudoku grid.
	 * @param sudoku The Sudoku which gets modified.
	 * @param prng The Random Number Generator used to generate the random variables.
	 */
	private static void cutCompleteStructures(int[][] sudoku, Random prng) {
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
							break; // Lohnt sich das hier? Breaked nur die
									// innerste Schleife
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

	/**
	 * Calculates the number of clues in a Sudoku grid.
	 * @param sudoku The Sudoku grid.
	 * @return The amount of given clues.
	 */
	public static int getNumberOfClues(int[][] sudoku){
		int clues = 0;
		for(int x = 0; x < 9; x++){
			for(int y = 0; y < 9; y++){
				if(sudoku[y][x] != 0){
					clues++;
				}
			}
		}
		return clues;
	}
	
	/**
	 * Shows wether a certain cell in a Sudoku grid has a neighbouring cell containing a given value.
	 * @param x The x-value of the cell in the Sudoku grid.
	 * @param y The y-value of the cell in the Sudoku grid.
	 * @param val The value which is searched for in the neighbouring cells.
	 * @param sudoku The Sudoku grid.
	 * @return True if there is a neighbour with the given value, false if not.
	 */
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

	/**
	 * Trys to cut out values which are not cuttable by deduction. Always checks wether this results in multiple solutions.
	 * @param sudoku The sudoku grid to be cut.
	 * @param maxCuts The maximum number of clues that get cut out.
	 */
	private static void doRandomCutting(int[][] sudoku, int maxCuts){
		for (int x = 0; x < SIZE && maxCuts > 0; x++) {
			for (int y = 0; y < SIZE && maxCuts > 0; y++) {
				if (sudoku[y][x] != 0) {
					int cutCandidate = sudoku[y][x];
					sudoku[y][x] = 0;
					if (!hasUniqueSolution(sudoku)) {
						sudoku[y][x] = cutCandidate;
					}
					else{
						maxCuts--;
					}
				}
			}
		}
	}
	
	/**
	 * Adds random clues to make the Sudoku easier.
	 * @param prng The Random Number Generator used to generate the random variables.
	 * @param number The total amount of clues to add.
	 */
	private void addRandomClues(Random prng, int number){
		while(number > 0){
			int x = prng.nextInt(SIZE);
			int y = prng.nextInt(SIZE);
			if(this.startGrid[y][x] == 0){
				startGrid[y][x] = solvedGrid[y][x];
				number--;
			}
		}
	}

	private static boolean hasUniqueSolution(int[][] sudoku){
		int[][] copy = Controller.deepCopy(sudoku);
		return (checkSolutions(0,0,copy,0) == 1);
	}
	
	/**
	 * Checks wether a given Sudoku has none, one or mutliple Solutions per Backtracking. The Sudoku grid will get modified.
	 * @param x The x-value of the cell where possibilities will be applied.
	 * @param y The y-value of the cell where possibilities will be applied.
	 * @param sudoku The sudoku grid to be checked for solutions.
	 * @param solutionsFound The amount of solutions found so far.
	 * @return 0 if no solutions exist, 1 if the sudoku has a unique solutions, >1 if multiple solutions where found (not neccesarily the actual amount of solutions).
	 */
	private static int checkSolutions(int x, int y, int[][] sudoku,
			int solutionsFound) {
		if (x == SIZE) {
			x = 0;
			if (++y == SIZE)
				return 1 + solutionsFound;
		}
		if (sudoku[x][y] != 0) // skip filled cells
			return checkSolutions(x + 1, y, sudoku, solutionsFound);

		for (int val = 1; val <= SIZE && solutionsFound < 2; ++val) {
			if (legal(x, y, val, sudoku)) {
				sudoku[x][y] = val;
				solutionsFound = checkSolutions(x + 1, y, sudoku,
						solutionsFound);
			}
		}
		sudoku[x][y] = 0; // reset on backtrack
		return solutionsFound;
	}

	//TODO: nicht mehr gebraucht?
	private boolean solve() {
		int[][] buffer = Arrays.copyOf(this.startGrid, this.startGrid.length);
		if (solve(0, 0, buffer)) {
			this.solvedGrid = buffer;
			return true;
		}
		return false;
	}

	/**
	 * Tries to find the first element in all the possible solutions of this Sudoku per Backtracking.
	 * @param x The x-value of the cell where possibilities will be applied.
	 * @param y The y-value of the cell where possibilities will be applied.
	 * @param sudoku The sudoku grid to be checked for solutions.
	 * @return True if a solution was found, false if not.
	 */
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

	/**
	 * Checks wether a value is a possible candidate in a certain cell in a Sudoku grid.
	 * @param x The x-value of the cell where the value is checked.
	 * @param y The y-value of the cell where the value is checked.
	 * @param val The value which is checked.
	 * @param sudoku The Sudoku in which the value is checked.
	 * @return True if the value is a possible candidate at this cell, false if not.
	 */
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
		undoStorage.push(Controller.deepCopy(recentGrid));
		if(undoStorage.size() > UNDOLIMIT){
			undoStorage.remove(redoStorage.size()-1);
		}
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
		undoStorage.push(Controller.deepCopy(recentGrid));
		if(undoStorage.size() > UNDOLIMIT){
			undoStorage.remove(redoStorage.size()-1);
		}
		this.recentGrid[y][x] = val;
		return true;
	}

	public int[][] getRecentGrid() {
		return this.recentGrid;
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

	}

	public void undo(){
		if(!undoStorage.empty()){
			redoStorage.push(Controller.deepCopy(recentGrid));
			if(redoStorage.size() > UNDOLIMIT){
				redoStorage.remove(redoStorage.size()-1);
			}
			recentGrid = undoStorage.pop();
			
		}
	}
	
	public void redo(){
		if(!redoStorage.empty()){
			undoStorage.push(Controller.deepCopy(recentGrid));
			if(undoStorage.size() > UNDOLIMIT){
				undoStorage.remove(undoStorage.size()-1);
			}
			recentGrid = redoStorage.pop();
		}
	}
}

//TODO prüfen ob undo limit erreich in extra methode
