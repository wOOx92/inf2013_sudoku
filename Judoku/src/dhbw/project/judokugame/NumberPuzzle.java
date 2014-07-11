package dhbw.project.judokugame;
/**
 * Every class implementing this interface can be presented by the Gui.
 * 
 * @author Dennis Uteg, Florian Steurer, Markus Wingler, Michael Jauch
 */
public interface NumberPuzzle {
	
	/**
	 * Returns the recent state of the INumberPuzzle.
	 * 
	 * @return The recent state as a 2-dimensional array.
	 */
	public int[][] getRecentGrid();
	
	/**
	 * Returns the start state of the INumberPuzzle.
	 * 
	 * @return The recent state as a 2-dimensional array.
	 */
	public int[][] getStartGrid();

	public int[][] getSolvedGird();
	
	/**
	 * Tries to set a value in a certain cell in the NumberPuzzle.
	 * 
	 * @param x
	 *            The x-value of the cell.
	 * @param y
	 *            The y-value of the cell.
	 * @param value
	 *            The value which is tried to set.
	 * @return True if the value was set, false if it was not successful (no
	 *         change in the current state).
	 */
	public boolean trySetValue(int x, int y, int value);

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
	 * Sets the NumberPuzzle to a state before an undo()-call.
	 */
	public void redo();

}
