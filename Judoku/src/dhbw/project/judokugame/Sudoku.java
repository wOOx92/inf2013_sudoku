package dhbw.project.judokugame;
import java.util.Random;
import java.util.Stack;

/**
 * This class represents Sudokus. It implements NumberPuzzle.
 * @author Dennis Uteg, Florian Steurer, Markus Wingler, Michael Jauch
 * The generate and solving algorithms used originate from:
 * https://www.hochschule-trier.de/uploads/tx_rfttheses/Eckart_Sussenburger_-_Loesungs-_und_Generierungsalgorithmen_fuer_Sudoku.pdf
 * http://www.sudokuwiki.org/sudoku.htm
 */
public class Sudoku implements NumberPuzzle {	
	
	/**
	 * This field is the side-length of a carree in a Sudoku. 
	 */
	public final static int CARREE_SIZE = 3;
	
	/**
	 *The side-length of a Sudoku grid.
	 */
	public final static int SIZE = 9;
	
	/**
	 * The maximum number of steps a user can use {@link Sudoku#undo()} and {@link Sudoku#undo()}.
	 */
	public final static int UNDOLIMIT = 5;
	
	/**
	 * The difficulty rating of the Sudoku.
	 */
	public final Difficulty DIFFICULTY;
	
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
	 * @param sudokuGrid The 9x9 grid of the Sudoku.
	 * @param The complete solution in a 9x9 grid.
	 * @param diff The estimated difficulty.
	 */
	public Sudoku(int[][] sudokuGrid, int[][] solvedGrid, Difficulty diff){
		this.DIFFICULTY = diff;
		this.startGrid = sudokuGrid;
		this.recentGrid = SudokuBuilder.deepCopy(startGrid);
		this.solvedGrid = solvedGrid;
	}

	/**
	 * Checks whether it is possible to place a given value at a certain point in an Sudoku grid, or if this would result in an illogical Sudoku.
	 * @param i
	 * @param j
	 * @param val
	 * @param cells
	 * @return
	 */
    public static boolean legal(int i, int j, int val, int[][] cells) {
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
		undoStorage.push(SudokuBuilder.deepCopy(recentGrid));
		limitStack(undoStorage);
		this.recentGrid = SudokuBuilder.deepCopy(this.startGrid);
		
	}

	public boolean trySetValue(int x, int y, int val) {
		if (val < 0 || val > SIZE) {
			return false;
		}
		if (x < 0 || x >= SIZE) {
			return false;
		}
		if (y < 0 || y >= SIZE) { 
			return false;
		}
		if (this.startGrid[y][x] != 0) {
			return false;
		}
		undoStorage.push(SudokuBuilder.deepCopy(recentGrid));
		limitStack(undoStorage);
		this.recentGrid[y][x] = val;
		redoStorage.clear();
		return true;
	}

	public int[][] getStartGrid() {
		return this.startGrid;
	}
	public int[][] getRecentGrid() {
		return this.recentGrid;
	}

	public int[][] getSolvedGird() {
		return this.solvedGrid;
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
		//TODO theoretisch unendliche laufzeit wegen random
		undoStorage.push(SudokuBuilder.deepCopy(recentGrid));
		Random prng = new Random();
		while(SudokuBuilder.getNumberOfClues(recentGrid) < 81){
			//todo: alle leeren Felder speichern, und DANN erst zufällig eines auswählen (so dass, der Random nicht mehr auf ausgefällt stoßt).
			int x = prng.nextInt(SIZE);
			int y = prng.nextInt(SIZE);
			if(recentGrid[y][x] == 0){
				recentGrid[y][x] = solvedGrid[y][x];
				break;
			}
		}
		
		limitStack(undoStorage);
	}

	public void undo(){
		if(!undoStorage.empty()){
			redoStorage.push(SudokuBuilder.deepCopy(recentGrid));
			limitStack(redoStorage);
			recentGrid = undoStorage.pop();
		}
	}
	

	public void redo(){
		if(!redoStorage.empty()){
			undoStorage.push(SudokuBuilder.deepCopy(recentGrid));
			limitStack(undoStorage);
			recentGrid = redoStorage.pop();
		}
	}
	
	private void limitStack(Stack<?> undoRedoStack){
		if(undoRedoStack.size() > UNDOLIMIT){
			
			undoRedoStack.remove(0);
		}
	}
}