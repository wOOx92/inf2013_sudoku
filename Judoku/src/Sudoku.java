import java.util.Random;
import java.util.Stack;

/**
 * This class represents Sudokus.
 * @author Dennis Uteg, Florian Steurer, Markus Wingler, Michael Jauch
 * The generate and solving algorithms used are originate from:
 * https://www.hochschule-trier.de/uploads/tx_rfttheses/Eckart_Sussenburger_-_Loesungs-_und_Generierungsalgorithmen_fuer_Sudoku.pdf
 */
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
		this.recentGrid = this.startGrid;
	}

	/**
	 * Creates an instance of Sudoku.
	 * @param diff The desired difficulty.
	 */
	public Sudoku(Difficulty diff) {
		Difficulty realDiff;
		Random prng = new Random();
		int i = 0;
		do {
			realDiff = generate(prng.nextLong(), diff);
			i++;
		} while (realDiff != diff && i < 20);

		this.recentGrid = this.startGrid;
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

		// Generiere ein gelöstes Sudoku
		solvedGrid = generateSolvedGrid(prng);
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
		System.out.println(getNumberOfClues(templateSdk)-clues1 + "removed deductively");
		
		clues1 = getNumberOfClues(templateSdk);
		cutWithNeighbourRule(templateSdk);
		System.out.println(getNumberOfClues(templateSdk) - clues1 + " removed neighbour");
		System.out.println(hasUniqueSolution(templateSdk) + " has unique solutions");
		
		clues1 = getNumberOfClues(templateSdk);
		doRandomCutting(templateSdk, diff.toRandomCuttingIndex());
		System.out.println(clues1 - getNumberOfClues(templateSdk) + " removed by rnd cutting");
		
		// Testen welche Schwierigkeit erreicht wurde
		int clues = getNumberOfClues(templateSdk);
		int missingClues = diff.minNumberOfClues() - clues;
		
		this.startGrid = templateSdk;
		addRandomClues(prng, missingClues);
		
		//no random cutting has been done so its easy
		if (clues >= Difficulty.EASY.minNumberOfClues()) {
			return Difficulty.EASY;
		}
		//if random cutting has been done and it has less clues than a medium rated Sudoku
		else if (clues < Difficulty.MEDIUM.minNumberOfClues() && diff == Difficulty.HARD){
			return Difficulty.HARD;
		}
		//If its neither easy nor hard its medium
		return Difficulty.MEDIUM;
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
				x = prng.nextInt(SIZE);
				y = prng.nextInt(SIZE);
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
	private static int getNumberOfClues(int[][] sudoku){
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
	 * Tries to cut out clues using the deductive cutting-rule "cut out a number if all possible values are already used in its neighbouring cells"
	 * @param sudoku The Sudoku grid to be cut.
	 */
	private static void cutDeductively(int[][] sudoku){
		for (int x = 0; x < SIZE; x++) {
			for (int y = 0; y < SIZE; y++) {
				int save = sudoku[y][x];
				sudoku[y][x] = 0;
				for (int i = 1; i <= SIZE; i++) {
					if (i != save && legal(y, x, i, sudoku)) {
						sudoku[y][x] = save;
						break;
					}
				}
			}
		}		
	}
	
	/**
	 * Tries to cut out clues using the deductive cutting-rule "cut out every number which is neighboured by each of its neighbours".
	 * @param sudoku The Sudoku grid to be cut.
	 */
	private static void cutWithNeighbourRule(int[][] sudoku){	
		// Schneide jede Zahl die in allen anderen leeren Zellen ihrer Zeile /
		// Spalte / Karree ausgeschlossen ist.
		for (int x = 0; x < SIZE; x++) {
			for (int y = 0; y < SIZE; y++) {
				if (sudoku[y][x] != 0) {
					int cutCandidate = sudoku[y][x];
					sudoku[y][x] = 0;

					boolean rowCuttable = true;
					boolean columnCuttable = true;
					boolean carreeCuttable = true;

					// Zeilen
					for (int x1 = 0; x1 < SIZE; x1++) {
						if (sudoku[y][x1] == 0
								&& x1 != x
								&& !isNeighbouredBy(x1, y, cutCandidate,
										sudoku)) {
							rowCuttable = false;
							break;
						}
					}

					// Spalten
					for (int y1 = 0; y1 < SIZE; y1++) {
						if (sudoku[y1][x] == 0
								&& y1 != y
								&& !isNeighbouredBy(x, y1, cutCandidate,
										sudoku)) {
							columnCuttable = false;
							break;
						}
					}
					
					  //Carrees 
					//TODO FUNKTIONIERT NICHT
					int xos = (x/3)*3; 
					int yos = (y/3)*3;
					  
					 for(int x1 = 0; x1 < 3; x1++){ for(int y1 = 0; y1 < 3;
					  y1++){ if(sudoku[yos+y1][xos+x1] == 0 && xos+x1 != x &&
					  yos+y1 != y &&
					  !isNeighbouredBy(xos+x1,yos+y1,cutCandidate,sudoku)){
					  carreeCuttable = false; break; } } }
					 

					if (!(rowCuttable || columnCuttable || false)) {
						sudoku[y][x] = cutCandidate;
					}
				}
			}
		}	
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

	/**
	 * Checks wether a given Sudoku is uniquely solvable.
	 * @param sudoku The sudoku to be checked.
	 * @return True if only one solution exists, false if there are none or mutliple solutions.
	 */
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

	/**
	 * Tries to find the first element in all the possible solutions of this Sudoku per Backtracking.
	 * @param x The x-value of the cell where possibilities will be applied.
	 * @param y The y-value of the cell where possibilities will be applied.
	 * @param sudoku The sudoku grid to be checked for solutions.
	 * @return True if a solution was found, false if not.
	 */
    private static boolean solve(int i, int j, int[][] cells) {
        if (i == 9) {
            i = 0;
            if (++j == 9)
                return true;
        }
        if (cells[i][j] != 0)  // skip filled cells
            return solve(i+1,j,cells);

        for (int val = 1; val <= 9; ++val) {
            if (legal(i,j,val,cells)) {
                cells[i][j] = val;
                if (solve(i+1,j,cells))
                    return true;
            }
        }
        cells[i][j] = 0; // reset on backtrack
        return false;
    }

    private static boolean legal(int i, int j, int val, int[][] cells) {
        for (int k = 0; k < 9; ++k)  // row
            if (val == cells[k][j])
                return false;

        for (int k = 0; k < 9; ++k) // col
            if (val == cells[i][k])
                return false;

        int boxRowOffset = (i / 3)*3;
        int boxColOffset = (j / 3)*3;
        for (int k = 0; k < 3; ++k) // box
            for (int m = 0; m < 3; ++m)
                if (val == cells[boxRowOffset+k][boxColOffset+m])
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
