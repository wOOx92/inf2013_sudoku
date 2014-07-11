package dhbw.project.JudokuGame;
/**
 * This enum contains the difficulties which Sudokus can be classified in. A sudokus' difficulty is determined by a) the number of clues and b) the amount of random cutting done during its generation.
 * @author Dennis Uteg, Florian Steurer, Markus Wingler, Michael Jauch
 * 
 */
public enum Difficulty{
	EASY,
	MEDIUM,
	HARD;
	
	/**
	 * Determines how much numbers will be cut out by the "cut-and-test" Method.
	 * @return The integer value of the recursion depth used by the backtracking algorithm.
	 */
	public int maxRecursionDepth(){
		if(this == EASY){
			return 0;
		}
		if(this == MEDIUM){
			return 11;
		}
		return 27;
	}
	
	/**
	 * Returns the minimum number of clues a Sudoku needs to possess the specified Difficulty.
	 * @return The minimum number of clues.
	 */
	public int minNumberOfClues(){
		if(this == EASY){
			return 32;
		}
		if(this == MEDIUM){
			return 25;
		}
		//17 because there are no uniquely solvable Sudokus with less clues;
		return 17;
	}
}
