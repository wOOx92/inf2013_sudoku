package dhbw.project.judokugame;

/**
 * This enumeration contains the difficulties for NumberPuzzles. Difficulties
 * are defined by a recursion depth. Differences in the recursion depth affect
 * the amount of the cutting of clues during the generating of NumberPuzzles
 * (the deeper the recursion, the more cutting can be done, the harder the
 * NumberPuzzle will get).
 * 
 * @author Dennis Uteg, Florian Steurer, Markus Wingler, Michael Jauch
 * 
 */
public enum Difficulty {
	EASY, 
	MEDIUM, 
	HARD,
	/**
	 * This Difficulty's maxRecursionDepth is extremely high. It is only
	 * recommended for testing because the high recursion depth could make the
	 * backtracking too slow for user interaction.
	 */
	UNRESTRICTED;

	/**
	 * The value of the recursion depth will directly influence the amount of
	 * clues cut out by "cut-and-test-backtracking". The higher the value, the
	 * more cutting can be done. Tests show that a recursion depth up to 30
	 * yield an appropriate time.
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
		if (this == HARD) {
			return 29;
		}
		return 10000;
	}
}
