/**
 * Every class implementing this interface can be presented by the Gui.
 * @author Dennis Uteg, Florian Steurer, Markus Wingler, Michael Jauch
 */
public interface INumberPuzzle {
	// Contains all methods, which are used by the Gui
	
	/**
	 * Returns the recent state of the INumberPuzzle.
	 * @return The recent state as a 2-dimensional array.
	 */
	public int[][] getRecentGrid();

	/**
	 * Tries to set a value in a certain cell in the INumberPuzzle.
	 * @param x The x-value of the cell.
	 * @param y The y-value of the cell.
	 * @param value The value which is tried to set.
	 * @return True if the value was set, false if it was not successfull (no change in the current state).
	 */
	public boolean trySetValue(int x, int y, int value);

	/**
	 * Sets a useful hint in the recentGrid within the INumberPuzzle.
	 */
	public void giveHint(); //Fill out an useful number
	
	/**
	 * Searches for wrong entries in the INumberPuzzle.
	 * @return An array of size 2 with the x and y value of the mistake. An empty array if there are no mistakes.
	 */
	public int[] searchMistake(); //Check mistakes, if mistake found -> X and Y value; empty array means no mistakes. Check impl. with tupel
	
	/**
	 * Resets the INumberPuzzle to the initial state.
	 */
	public void reset();
	
	/**
	 * Sets the INumberPuzzle to the previous state.
	 */
	public void undo();
	
	/**
	 * Sets the INumberPuzzle to a state before an undo()-call.
	 */
	public void redo();
	
}
