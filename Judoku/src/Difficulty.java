/**
 * This enum contains the difficulties which Sudokus can be classified in. A sudokus' difficulty is determined by a) the number of clues and b) the amount of random cutting done during its generation.
 * @author Dennis Uteg, Florian Steurer, Markus Wingler, Michael Jauch
 * 
 */
public enum Difficulty{
	EASY,		//Logisch geschnittene Sudokus mit 30 oder mehr Vorgaben
	MEDIUM,		//Logisch geschnittene Sudokus mit 25-30 Vorgaben und 2x Zufallsschneiden
	HARD;		//Vollstaendig geschnittene Sudokus mit 21-25 Vorgaben
	
	/**
	 * Determines how much random cutting is allowed in a Sudoku of the specified Difficulty.
	 * @return The maximum number of random cuts allowed in this Difficulty.
	 */
	public int maxRecursionDepth(){
		if(this == EASY){
			return 0;
		}
		if(this == MEDIUM){
			return 20;
		}
		return 75;
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
