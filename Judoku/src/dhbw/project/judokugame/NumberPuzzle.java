package dhbw.project.judokugame;

/**
 * Every class implementing this interface can be displayed by the GUI. This
 * makes it easier to implement other NumberPuzzles like Str8ts.
 * 
 * @author Dennis Uteg, Florian Steurer, Markus Wingler, Michael Jauch
 */
public interface NumberPuzzle {

	/**
	 * @return The recent state as a 2-dimensional array.
	 */
	public int[][] getRecentGrid();

	/**
	 * @return The start state as a 2-dimensional array.
	 */
	public int[][] getStartGrid();

	/**
	 * @return The solution as a 2-dimensional array.
	 */
	public int[][] getSolvedGird();

	/**
	 * Sets a value in the recentGrid inside the NumberPuzzle.
	 * 
	 * @param x
	 *            The x-value of the cell.
	 * @param y
	 *            The y-value of the cell.
	 * @param value
	 *            The value which is to be set.
	 */
	public void setValue(int x, int y, int value);

	/**
	 * Sets a useful hint in the recentGrid within the NumberPuzzle.
	 */
	public void giveHint();

	/**
	 * Searches for wrong entries in the NumberPuzzle.
	 * 
	 * @return An array of size 2 with the x and y value of the mistake. An
	 *         empty array if there are no mistakes.
	 */
	public int[] searchMistake();

	/**
	 * Resets the NumberPuzzle to the initial state.
	 */
	public void reset();

	/**
	 * Sets the NumberPuzzle to the previous state.
	 */
	public void undo();

	/**
	 * Enables / disables the undo button.
	 */
	public boolean undoPossible();

	/**
	 * Sets the NumberPuzzle to a state before an undo()-call.
	 */
	public void redo();

	/**
	 * Enables / disables the redo button.
	 */
	public boolean redoPossible();
}
