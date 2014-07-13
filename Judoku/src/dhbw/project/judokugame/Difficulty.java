package dhbw.project.judokugame;

/**
 * This enumeration contains the difficulties for NumberPuzzles. Difficulties
 * are defined by a recursion depth. Difference in the recursion depth affects
 * the amount of the cutting of clues during the generation of NumberPuzzles
 * (the deeper the recursion, the more cutting can be done, the harder the
 * NumberPuzzle will get).
 * 
 * @author Dennis Uteg, Florian Steurer, Markus Wingler, Michael Jauch
 * 
 */
public enum Difficulty {
	EASY, 
	MEDIUM, 
	HARD;

	/**
	 * The value of the recursion depth will directly influence the amount of
	 * clues cut out by "cut-and-test-backtracking". The higher the value, the
	 * more cutting can be done. Tests show that a recursion depth up to 30
	 * yield a appropriate time.
	 * 
	 * @return The positive integer that is used as an upper limit for the
	 *         recursion depth.
	 */
	public int maxRecursionDepth() {
		if (this == EASY) {
			return 0;
		}
		if (this == MEDIUM) {
			return 11;
		}
		return 28;
	}
}
